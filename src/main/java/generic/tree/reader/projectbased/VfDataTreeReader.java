package generic.tree.reader.projectbased;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import context.project.VisProjectDBContext;
import generic.tree.reader.VfTreeReader;
import metadata.DataType;
import metadata.MetadataID;
import metadata.graph.vftree.VfTreeDataMetadata;
import metadata.graph.vftree.VfTreeMandatoryEdgeDataTableSchemaUtils;
import metadata.graph.vftree.VfTreeMandatoryNodeDataTableSchemaUtils;
import metadata.record.RecordDataMetadata;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import sql.ResultSetUtils;
import sql.derby.TableContentSQLStringFactory;

/**
 * a VfTree that is built from a VfTreeDataMetadata of a VisProjectDBContext;
 * 
 * input of tree trimming;
 * 
 * contains the full set of information read from the node and edge record data table;
 * 
 * facilitate simple tree visualization for tree trimming operation;
 * 
 * @author tanxu
 */
public class VfDataTreeReader extends VfTreeReader{
	private final VisProjectDBContext hostVisProjectDBContext; 
	private final MetadataID treeDataMetadataID;
	
	private final LinkedHashSet<DataTableColumnName> nonMandatoryNodeAdditionalFeaturesColNameSetToBeIncluded;
	private final LinkedHashSet<DataTableColumnName> nonMandatoryEdgeAdditionalFeaturesColNameSetToBeIncluded;
	
	/////////////////////
	private VfTreeDataMetadata treeDataMetadata;
	private RecordDataMetadata nodeRecordDataMetadata;
	private RecordDataMetadata edgeRecordDataMetadata;
	private Map<DataTableColumnName, DataTableColumn> nonMandatoryAdditionalNodeFeatureColumnNameMapToBeIncluded;
	private Map<DataTableColumnName, DataTableColumn> nonMandatoryAdditionalEdgeFeatureColumnNameMapToBeIncluded;
	
	
	private VfDataTreeNode rootNode;
	private Map<Integer, VfDataTreeNode> nodeIDMap; //all node constructed from the VfTreeDataMetadata
	
	/**
	 * constructor
	 * @param visProject
	 * @param treeDataMetadataID
	 * @param nonMandatoryNodeAdditionalFeaturesColNameSetToBeIncluded set of non mandatory node additional feature columns names to be included in the built vftree; can not be null; can be empty;
	 * @param nonMandatoryEdgeAdditionalFeaturesColNameSetToBeIncluded set of non mandatory edge additional feature columns names to be included in the built vftree; can not be null; can be empty;
	 */
	public VfDataTreeReader(
			VisProjectDBContext visProject, MetadataID treeDataMetadataID, 
			LinkedHashSet<DataTableColumnName> nonMandatoryNodeAdditionalFeaturesColNameSetToBeIncluded,
			LinkedHashSet<DataTableColumnName> nonMandatoryEdgeAdditionalFeaturesColNameSetToBeIncluded){
		
		if(!treeDataMetadataID.getDataType().equals(DataType.vfTREE)) {
			throw new IllegalArgumentException("given treeDataMetadataID is not of vfTree type!");
		}
		
		this.hostVisProjectDBContext = visProject;
		this.treeDataMetadataID = treeDataMetadataID;
		this.nonMandatoryNodeAdditionalFeaturesColNameSetToBeIncluded = nonMandatoryNodeAdditionalFeaturesColNameSetToBeIncluded;
		this.nonMandatoryEdgeAdditionalFeaturesColNameSetToBeIncluded = nonMandatoryEdgeAdditionalFeaturesColNameSetToBeIncluded;
	}
	
	@Override
	public void perform() throws SQLException {
		this.preprocess();
		
		this.readNodeDataTable();
		
		this.readEdgeDataTable();
		
		this.setChildrenNodeSiblingOrderIndexMap();
	}
	
	
	/**
	 * 
	 * @throws SQLException
	 */
	private void preprocess() throws SQLException {
		this.treeDataMetadata = (VfTreeDataMetadata) this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.treeDataMetadataID);
	
		this.nodeRecordDataMetadata = (RecordDataMetadata) this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.treeDataMetadata.getNodeRecordMetadataID());
		
		this.edgeRecordDataMetadata = (RecordDataMetadata) this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.treeDataMetadata.getEdgeRecordMetadataID());
		
		///
		this.nonMandatoryAdditionalNodeFeatureColumnNameMapToBeIncluded = new LinkedHashMap<>();
		
		for(DataTableColumn col:this.nodeRecordDataMetadata.getDataTableSchema().getOrderedListOfNonRUIDColumn()) {
			if(!VfTreeMandatoryNodeDataTableSchemaUtils.getMandatoryAttributeColumnNameList().contains(col.getName())) {
				if(this.nonMandatoryNodeAdditionalFeaturesColNameSetToBeIncluded.contains(col.getName())) {
					this.nonMandatoryAdditionalNodeFeatureColumnNameMapToBeIncluded.put(col.getName(), col);
				}
			}
		}
		
		this.nonMandatoryAdditionalEdgeFeatureColumnNameMapToBeIncluded = new LinkedHashMap<>();
		
		for(DataTableColumn col:this.edgeRecordDataMetadata.getDataTableSchema().getOrderedListOfNonRUIDColumn()) {
			if(!VfTreeMandatoryEdgeDataTableSchemaUtils.getMandatoryAttributeColumnNameList().contains(col.getName())) {
				if(this.nonMandatoryEdgeAdditionalFeaturesColNameSetToBeIncluded.contains(col.getName())) {
					this.nonMandatoryAdditionalEdgeFeatureColumnNameMapToBeIncluded.put(col.getName(), col);
				}
			}
		}
	}
	
	/**
	 * read the node data table and create a VfDataTreeNode using each record in the table;
	 * 
	 * @throws SQLException 
	 */
	private void readNodeDataTable() throws SQLException {
		this.nodeIDMap = new HashMap<>();
		
		Set<String> selectedColNameSet = new LinkedHashSet<>();
		Map<DataTableColumnName, DataTableColumn> colNameMap = new HashMap<>();
		for(DataTableColumn col:this.nodeRecordDataMetadata.getDataTableSchema().getOrderedListOfNonRUIDColumn()) {
			selectedColNameSet.add(col.getName().getStringValue());
			colNameMap.put(col.getName(),col);
		}
		
		String sqlQueryString = TableContentSQLStringFactory.buildSelectSQLString(
				this.nodeRecordDataMetadata.getDataTableSchema().getSchemaName().getStringValue(), 
				this.nodeRecordDataMetadata.getDataTableSchema().getName().getStringValue(), 
				selectedColNameSet, 
				null); //condition string
		
		Statement statement = this.getHostVisProjectDBContext().getDBConnection().createStatement();
		
		ResultSet rs = statement.executeQuery(sqlQueryString);
		
		while(rs.next()) {
			Map<DataTableColumnName, String> nodeAttributeNameStringValueMap = ResultSetUtils.getCurrentRecordColumnNameStringValueMap(rs, colNameMap);
			
			//extract primary attributes and leave all derivate attributes alone;
			int ID = Integer.parseInt(nodeAttributeNameStringValueMap.get(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName()));
			
			Integer parentNodeID = nodeAttributeNameStringValueMap.get(VfTreeMandatoryNodeDataTableSchemaUtils.nodeParentIDColumn().getName())==null?
					null:Integer.parseInt(nodeAttributeNameStringValueMap.get(VfTreeMandatoryNodeDataTableSchemaUtils.nodeParentIDColumn().getName()));
			
			int siblingOrderIndex =  Integer.parseInt(nodeAttributeNameStringValueMap.get(VfTreeMandatoryNodeDataTableSchemaUtils.nodeSiblingIndexColumn().getName()));
			
			double leafIndex = Double.parseDouble(nodeAttributeNameStringValueMap.get(VfTreeMandatoryNodeDataTableSchemaUtils.leafIndexColumn().getName()));
			
			int edgeNumToRoot = Integer.parseInt(nodeAttributeNameStringValueMap.get(VfTreeMandatoryNodeDataTableSchemaUtils.edgeNumToRootColumn().getName()));
			
			Double distToRoot = nodeAttributeNameStringValueMap.get(VfTreeMandatoryNodeDataTableSchemaUtils.nodeDistanceToRootColumn().getName())==null?
					null:Double.parseDouble(nodeAttributeNameStringValueMap.get(VfTreeMandatoryNodeDataTableSchemaUtils.nodeDistanceToRootColumn().getName()));

			Map<DataTableColumnName, String> nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap = new HashMap<>();
			for(DataTableColumnName colName:this.nonMandatoryNodeAdditionalFeaturesColNameSetToBeIncluded) {
				nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap.put(colName, nodeAttributeNameStringValueMap.get(colName));
			}
			
			VfDataTreeNode node = new VfDataTreeNode(
					ID, parentNodeID, siblingOrderIndex, leafIndex, edgeNumToRoot, distToRoot,
					nonMandatoryAdditionalNodeFeatureColumnNameValueStringMap);
			
			this.nodeIDMap.put(node.getID(), node);
			
			if(parentNodeID==null) {
				this.rootNode = node;
			}
		}
		
		
		rs.close();
		
	}
	
	/**
	 * read the edge data table and parse out the following information for each edge and add to the corresponding child node
	 * 1. bootstrap value
	 * 2. length
	 * 3. non mandatory additional attributes
	 * 
	 * must be invoked after {@link #readNodeDataTable()} method
	 * @throws SQLException 
	 */
	private void readEdgeDataTable() throws SQLException {
		Set<String> selectedColNameSet = new LinkedHashSet<>();
		Map<DataTableColumnName, DataTableColumn> colNameMap = new HashMap<>();
		for(DataTableColumn col:this.edgeRecordDataMetadata.getDataTableSchema().getOrderedListOfNonRUIDColumn()) {
			selectedColNameSet.add(col.getName().getStringValue());
			colNameMap.put(col.getName(),col);
		}
		
		String sqlQueryString = TableContentSQLStringFactory.buildSelectSQLString(
				this.edgeRecordDataMetadata.getDataTableSchema().getSchemaName().getStringValue(), 
				this.edgeRecordDataMetadata.getDataTableSchema().getName().getStringValue(), 
				selectedColNameSet, 
				null); //condition string
		
		Statement statement = this.getHostVisProjectDBContext().getDBConnection().createStatement();
		
		ResultSet rs = statement.executeQuery(sqlQueryString);
		
		while(rs.next()) {
			Map<DataTableColumnName, String> edgeAttributeNameStringValueMap = ResultSetUtils.getCurrentRecordColumnNameStringValueMap(rs, colNameMap);
			
			//extract primary attributes and leave all derived attributes alone;
			int childNodeID = Integer.parseInt(edgeAttributeNameStringValueMap.get(VfTreeMandatoryEdgeDataTableSchemaUtils.childNodeIDColumn().getName()));
			
			Object lengthStringObject = edgeAttributeNameStringValueMap.get(VfTreeMandatoryEdgeDataTableSchemaUtils.lengthColumn().getName());
			Double length = lengthStringObject==null?null:Double.parseDouble(lengthStringObject.toString());
			
			Object bootstrapStringObject = edgeAttributeNameStringValueMap.get(VfTreeMandatoryEdgeDataTableSchemaUtils.bootstrapColumn().getName());
			Integer bootstrap = bootstrapStringObject==null?null:Integer.parseInt(bootstrapStringObject.toString());
			
			Map<DataTableColumnName, String> nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap = new HashMap<>();
			for(DataTableColumnName colName:this.nonMandatoryEdgeAdditionalFeaturesColNameSetToBeIncluded) {
				nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap.put(colName, edgeAttributeNameStringValueMap.get(colName));
			}
			
			VfDataTreeNode node = this.nodeIDMap.get(childNodeID);
			
			node.setDistanceToParentNode(length);
			node.setBootstrapValueToParentNode(bootstrap);
			node.setNonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap(nonMandatoryAdditionalEdgeFeatureColumnNameValueStringMap);
		}
		
		
		rs.close();
	}

	/**
	 * set the childrenNodeSiblingOrderIndexMap of each node on the tree
	 */
	private void setChildrenNodeSiblingOrderIndexMap() {
		for(Integer nodeID:this.nodeIDMap.keySet()) {
			VfDataTreeNode node = this.nodeIDMap.get(nodeID);
			if(node.getParentNodeID()==null) {
				//skip root node
			}else {
				VfDataTreeNode parentNode = this.nodeIDMap.get(node.getParentNodeID());
				parentNode.addChildNode(node);
			}
		}
	}
	
	
	public VisProjectDBContext getHostVisProjectDBContext() {
		return hostVisProjectDBContext;
	}

	public VfTreeDataMetadata getTreeDataMetadata() {
		return treeDataMetadata;
	}
	
	
	///////////////////////////////////////////
	@Override
	public VfDataTreeNode getRootNode() {
		return this.rootNode;
	}
	
	@Override
	public Map<DataTableColumnName, DataTableColumn> getNonMandatoryAdditionalNodeFeatureColumnNameMap() {
		return this.nonMandatoryAdditionalNodeFeatureColumnNameMapToBeIncluded;
	}

	@Override
	public Map<DataTableColumnName, DataTableColumn> getNonMandatoryAdditionalEdgeFeatureColumnNameMap() {
		return this.nonMandatoryAdditionalEdgeFeatureColumnNameMapToBeIncluded;
	}
	
}
