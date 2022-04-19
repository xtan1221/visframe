package metadata.graph.vftree.feature;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import generic.graph.DirectedType;
import metadata.graph.feature.EdgeDirectednessFeature;
import metadata.graph.feature.GraphEdgeFeature;
import metadata.graph.vftree.VfTreeMandatoryEdgeDataTableSchemaUtils;
import metadata.graph.vftree.VfTreeMandatoryNodeDataTableSchemaUtils;
import rdb.table.data.DataTableColumnName;

public class VfTreeEdgeFeature extends GraphEdgeFeature{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3677608241847879679L;
	
	///////////////////
	private static final LinkedHashMap<DataTableColumnName, DataTableColumnName> sourceNodeIDColumnNameEdgeColumnNameMap;
	private static final LinkedHashMap<DataTableColumnName, DataTableColumnName> sinkNodeIDColumnNameEdgeColumnNameMap;
	
	static {
		sourceNodeIDColumnNameEdgeColumnNameMap = new LinkedHashMap<>();
		sourceNodeIDColumnNameEdgeColumnNameMap.put(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName(), VfTreeMandatoryEdgeDataTableSchemaUtils.parentNodeIDColumn().getName());
		
		sinkNodeIDColumnNameEdgeColumnNameMap = new LinkedHashMap<>();
		sinkNodeIDColumnNameEdgeColumnNameMap.put(VfTreeMandatoryNodeDataTableSchemaUtils.nodeIDColumn().getName(), VfTreeMandatoryEdgeDataTableSchemaUtils.childNodeIDColumn().getName());
	}
	
	/**
	 * the EdgeDirectednessFeature of VfTreeEdgeFeature pre-defined by visframe
	 */
	static final EdgeDirectednessFeature edgeDirectednessFeature = 
			new EdgeDirectednessFeature(
					false, //boolean hasDirectednessIndicatorColumn
					null,//DataTableColumnName directednessIndicatorColumnName
					DirectedType.UNDIRECTED,//DirectedType defaultDirectedType
					null//Map<String, DirectedType> columnValueStringDirectedTypeMap
					);
	
	
	////////////////////////
	private transient LinkedHashSet<DataTableColumnName> nonMandatoryAdditionalColumnNameSet;
	
	/**
	 * constructor
	 * @param IDColumnNameSet
	 * @param nonMandatoryAdditionalFeatureColumnNameSet
	 * @param edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets
	 * @param nodeIDColumnNameEdgeSourceNodeIDColumnNameMap
	 * @param nodeIDColumnNameEdgeSinkNodeIDColumnNameMap
	 */
	public VfTreeEdgeFeature(
//			LinkedHashSet<DataTableColumnName> IDColumnNameSet,
			LinkedHashSet<DataTableColumnName> nonMandatoryAdditionalFeatureColumnNameSet
			) {
		super(
				VfTreeMandatoryEdgeDataTableSchemaUtils.getIDColumnNameList(), 
				VfTreeMandatoryEdgeDataTableSchemaUtils.makeAdditionalFeatureColumnsNameList(nonMandatoryAdditionalFeatureColumnNameSet), 
				false,//edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
				sourceNodeIDColumnNameEdgeColumnNameMap, 
				sinkNodeIDColumnNameEdgeColumnNameMap,
				edgeDirectednessFeature
				);
	}
	
	/**
	 * return the name set of non-mandatory additional feature columns of VfTree node record data
	 * @return
	 */
	public LinkedHashSet<DataTableColumnName> getNonMandatoryAdditionalColumnNameSet(){
		if(this.nonMandatoryAdditionalColumnNameSet == null) {
			this.nonMandatoryAdditionalColumnNameSet = new LinkedHashSet<>();
			for(DataTableColumnName col:this.getAdditionalFeatureColumnNameSet()) {
				if(!this.getMandatoryAdditionalColumnNameSet().contains(col)) {
					this.nonMandatoryAdditionalColumnNameSet.add(col);
				}
			}
		}
		return this.nonMandatoryAdditionalColumnNameSet;
	}
	
	/**
	 * return the name set of mandatory additional features columns
	 * @return
	 */
	LinkedHashSet<DataTableColumnName> getMandatoryAdditionalColumnNameSet(){
		return VfTreeMandatoryEdgeDataTableSchemaUtils.getMandatoryAdditionalFeatureColumnNameList();
	}
}
