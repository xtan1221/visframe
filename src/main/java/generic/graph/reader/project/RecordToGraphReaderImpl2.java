package generic.graph.reader.project;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import context.project.VisProjectDBContext;
import generic.graph.DirectedType;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphEdgeUtils;
import metadata.MetadataID;
import metadata.graph.feature.EdgeDirectednessFeature;
import rdb.table.data.DataTableColumnName;
import sql.ResultSetUtils;
import utils.Pair;


/**
 * implementation of {@link RecordToGraphReader} with one single column whose value is used as the indicator of the directedness of the edge in input edge record data table;
 * 
 * @author tanxu
 *
 */
public class RecordToGraphReaderImpl2 extends RecordToGraphReader {
	/**
	 * can not be null;
	 */
	private final DataTableColumnName edgeDirectednessIndicatorColumnName;
	
	/**
	 * map from the column string value to the indicated DirectedType;
	 * 
	 * note that this map allows multiple type of string values indicating the same DirectedType;
	 * 
	 * also note that this map's key is case sensitive, thus strings with only case differences can be used to indicate different DirectedTypes;
	 * 
	 */
	private final Map<String, DirectedType> colStringValueDirectedTypeMap;
	
	/**
	 * can be null;
	 * if null and the edgeDirectednessIndicatorColumnName contains string values not in the key set of {@link #colStringValueDirectedTypeMap}, the edge will be ignored;
	 */
	private final DirectedType defaultDirectedType;
	
	
	/**
	 * store any VfGraphEdge that has not been returned by the {@link #nextEdge()} method;
	 * only used when a record with DirectedType equal to {@link DirectedType#BI_DIRECTED} is encountered;
	 */
	private LinkedList<VfGraphEdge> queuedEdges = new LinkedList<>();
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param hasVertexDataSourceRecordData
	 * @param vertexDataSourceRecordDataID
	 * @param edgeDataSourceRecordDataID
	 * @param toFilterOutDuplicates
	 * @param vertexIDColumnNameSet
	 * @param vertexAdditionalFeatureColumnNameSet
	 * @param edgeIDColumnNameSet
	 * @param edgeAdditionalFeatureColumnNameSet
	 * @param edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets
	 * @param vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap
	 * @param vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap 
	 * @param edgeDirectednessIndicatorColumnName not null;
	 * @param colStringValueDirectedTypeMap not null, can be empty;
	 * @param defaultDirectedType not null if colStringValueDirectedTypeMap is empty; can be null otherwise;
	 */
	RecordToGraphReaderImpl2(
			VisProjectDBContext hostVisProjectDBContext, boolean hasVertexDataSourceRecordData,
			MetadataID vertexDataSourceRecordDataID,
			MetadataID edgeDataSourceRecordDataID, boolean toFilterOutDuplicates,
			LinkedHashSet<DataTableColumnName> vertexIDColumnNameSet,
			LinkedHashSet<DataTableColumnName> vertexAdditionalFeatureColumnNameSet,
			LinkedHashSet<DataTableColumnName> edgeIDColumnNameSet,
			LinkedHashSet<DataTableColumnName> edgeAdditionalFeatureColumnNameSet,
			boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
			LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
			LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap,
			
			DataTableColumnName edgeDirectednessIndicatorColumnName,
			Map<String, DirectedType> colStringValueDirectedTypeMap,
			DirectedType defaultDirectedType) {
		super(hostVisProjectDBContext, hasVertexDataSourceRecordData, vertexDataSourceRecordDataID, edgeDataSourceRecordDataID, toFilterOutDuplicates,
				vertexIDColumnNameSet, vertexAdditionalFeatureColumnNameSet, edgeIDColumnNameSet,
				edgeAdditionalFeatureColumnNameSet, edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
				vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap, vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap);

		
		if(edgeDirectednessIndicatorColumnName==null) {
			throw new IllegalArgumentException("given edgeDirectednessIndicatorColumnName is null!");
		}
		if(colStringValueDirectedTypeMap==null) {
			throw new IllegalArgumentException("given colStringValueDirectedTypeMap is null!");
		}
		
		
		if(colStringValueDirectedTypeMap.isEmpty() && defaultDirectedType==null) {
			throw new IllegalArgumentException("given defaultDirectedType is null when colStringValueDirectedTypeMap is empty!");
		}
		
		//if DirectedType.BI_DIRECTED is allowed in the edge data table, the edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets must be false, otherwise, it is not possible to distinguish the two opposite directed vertex with the exactly the same set of edge id column;
		if(colStringValueDirectedTypeMap.containsValue(DirectedType.BI_DIRECTED) || (defaultDirectedType!=null&&defaultDirectedType==DirectedType.BI_DIRECTED)) {
			if(edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets) {
				throw new IllegalArgumentException("if DirectedType.BI_DIRECTED is allowed in the edge data table, the edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets must be false, otherwise, it is not possible to distinguish the two opposite directed vertex with the exactly the same set of edge id column;");
			}
		}
		
		
		
		this.edgeDirectednessIndicatorColumnName = edgeDirectednessIndicatorColumnName;
		this.colStringValueDirectedTypeMap = colStringValueDirectedTypeMap;
		this.defaultDirectedType = defaultDirectedType;
	}


	/**
	 * build VfGraphEdge from the input edge record data;
	 * the directed-ness of edge of each record should be processed accordingly based on the {@link #colStringValueDirectedTypeMap} and {@link #defaultDirectedType} 
	 */
	@Override
	public VfGraphEdge nextEdge() throws SQLException {
		if(!this.vertexDone) {
			throw new UnsupportedOperationException();
		}
		if(this.edgeDone) {
			throw new UnsupportedOperationException();
		}
		
		if(this.edgeRecordDataTableResultSet==null) {
			this.queryEdgeDataTable();
		}
		
		//return an edge from the queuedEdges if any;
		if(!this.queuedEdges.isEmpty()) {
			return this.queuedEdges.poll();
		}
		
		//
		while(this.edgeRecordDataTableResultSet.next()) {
			Map<DataTableColumnName, String> colNameStringValueMap = ResultSetUtils.getCurrentRecordColumnNameStringValueMap(this.edgeRecordDataTableResultSet, this.getEdgeAttributeColNameMap());
			
			DirectedType directedType = this.getColStringValueDirectedTypeMap().get(
					colNameStringValueMap.get(this.getEdgeDirectednessIndicatorColumnName()));
			
			if(directedType==null) {//use the default type
				if(this.getDefaultDirectedType()==null) {//
					System.out.println("Warning: unrecognized edge directedness indicator column string value is found, but defaultDirectedType is null; record is skipped!");
					continue;
				}else {
					directedType = this.getDefaultDirectedType();
				}
			} 
			
			/////////////////////////////////
			Map<DataTableColumnName, String> IDAttributeNameStringValueMap = new HashMap<>();
			for(DataTableColumnName colName:this.getEdgeIDColumnNameSet()) {
				IDAttributeNameStringValueMap.put(colName, colNameStringValueMap.get(colName));
			}
			
			Map<DataTableColumnName, String> sourceVertexIDAttributeNameStringValueMap = new HashMap<>();
			for(DataTableColumnName colName:this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap().values()) {
				sourceVertexIDAttributeNameStringValueMap.put(colName, colNameStringValueMap.get(colName));
			}
			Map<DataTableColumnName, String> sinkVertexIDAttributeNameStringValueMap = new HashMap<>();
			for(DataTableColumnName colName:this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap().values()) {
				sinkVertexIDAttributeNameStringValueMap.put(colName, colNameStringValueMap.get(colName));
			}
			
			Map<DataTableColumnName, String> additionalAttributeNameStringValueMap = new HashMap<>();
			for(DataTableColumnName colName:this.getEdgeAdditionalFeatureColumnNameSet()) {
				additionalAttributeNameStringValueMap.put(colName, colNameStringValueMap.get(colName));
			}
			
			if(directedType == DirectedType.UNDIRECTED) {
				VfGraphEdge ret = new VfGraphEdge(
						IDAttributeNameStringValueMap, 
						this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets(),
						this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap(),
						sourceVertexIDAttributeNameStringValueMap,
						this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap(),
						sinkVertexIDAttributeNameStringValueMap,
						additionalAttributeNameStringValueMap,
						false);
				return ret;
				
			}else if(directedType == DirectedType.DIRECTED_FORWARD) {
				VfGraphEdge ret = new VfGraphEdge(
						IDAttributeNameStringValueMap, 
						this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets(),
						this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap(),
						sourceVertexIDAttributeNameStringValueMap,
						this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap(),
						sinkVertexIDAttributeNameStringValueMap,
						additionalAttributeNameStringValueMap,
						true);
				return ret;
			}else if(directedType == DirectedType.DIRECTED_BACKWARD) {
				Pair<Map<DataTableColumnName, String>, Map<DataTableColumnName, String>> swapped = 
						VfGraphEdgeUtils.swap(
								this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap(), this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap(), 
								sourceVertexIDAttributeNameStringValueMap, sinkVertexIDAttributeNameStringValueMap);
				
				VfGraphEdge ret = new VfGraphEdge(
						IDAttributeNameStringValueMap, 
						this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets(),
						this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap(),
						swapped.getFirst(),
						this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap(),
						swapped.getSecond(),
						additionalAttributeNameStringValueMap,
						true);
				return ret;
				
			}else if(directedType == DirectedType.BI_DIRECTED) {
				VfGraphEdge forward = new VfGraphEdge(
						IDAttributeNameStringValueMap, 
						this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets(),
						this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap(),
						sourceVertexIDAttributeNameStringValueMap,
						this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap(),
						sinkVertexIDAttributeNameStringValueMap,
						additionalAttributeNameStringValueMap,
						true);
				
				Pair<Map<DataTableColumnName, String>, Map<DataTableColumnName, String>> swapped = 
						VfGraphEdgeUtils.swap(
								this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap(), this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap(), 
								sourceVertexIDAttributeNameStringValueMap, sinkVertexIDAttributeNameStringValueMap);
				
				VfGraphEdge backward = new VfGraphEdge(
						IDAttributeNameStringValueMap, 
						this.isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets(),
						this.getVertexIDColumnNameEdgeSourceVertexIDColumnNameMap(),
						swapped.getFirst(),
						this.getVertexIDColumnNameEdgeSinkVertexIDColumnNameMap(),
						swapped.getSecond(),
						additionalAttributeNameStringValueMap,
						true);
				
				this.queuedEdges.add(backward);
				return forward;
				
			}else {
				throw new IllegalArgumentException("unrecognized directedType:"+directedType);
			}
			
		}
		
		this.edgeRecordDataTableResultSet.close();
		
		
		this.edgeDone = true;
		
		
		return null;
	}
	
	


	@Override
	public EdgeDirectednessFeature getEdgeDirectednessFeature() {
		// TODO Auto-generated method stub
		return new EdgeDirectednessFeature(
				true,//boolean hasDirectednessIndicatorColumn,
				this.edgeDirectednessIndicatorColumnName,//DataTableColumnName directednessIndicatorColumnName,
				this.defaultDirectedType,//DirectedType defaultDirectedType,
				this.colStringValueDirectedTypeMap//Map<String, DirectedType> columnValueStringDirectedTypeMap
				);
	}

	////////////////////////////
	/**
	 * @return the edgeDirectednessIndicatorColumnName
	 */
	public DataTableColumnName getEdgeDirectednessIndicatorColumnName() {
		return edgeDirectednessIndicatorColumnName;
	}


	/**
	 * @return the colStringValueDirectedTypeMap
	 */
	public Map<String, DirectedType> getColStringValueDirectedTypeMap() {
		return colStringValueDirectedTypeMap;
	}


	/**
	 * @return the defaultDirectedType
	 */
	public DirectedType getDefaultDirectedType() {
		return defaultDirectedType;
	}

	
}
