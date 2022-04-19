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
 * 
 * an implementation of {@link RecordToGraphReader} with all edges treated as the same default {@link DirectedType} without any indicator column; 
 * 
 * @author tanxu
 *
 */
public class RecordToGraphReaderImpl extends RecordToGraphReader {
	/**
	 * 
	 */
	private final DirectedType defaultDirectedType;
	
	
	///////////////////////////
	/**
	 * store any VfGraphEdge that has not been returned by the {@link #nextEdge()} method;
	 * only used when a record with DirectedType equal to {@link DirectedType#BI_DIRECTED} is encountered;
	 */
	private LinkedList<VfGraphEdge> queuedEdges = new LinkedList<>();
	
	
	/**
	 * 
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
	 * @param defaultDirectedType
	 */
	RecordToGraphReaderImpl(
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
			DirectedType defaultDirectedType) {
		super(hostVisProjectDBContext, hasVertexDataSourceRecordData, 
				vertexDataSourceRecordDataID, edgeDataSourceRecordDataID, toFilterOutDuplicates, vertexIDColumnNameSet,
				vertexAdditionalFeatureColumnNameSet, edgeIDColumnNameSet, edgeAdditionalFeatureColumnNameSet,
				edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
				vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap, vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap);
		
		
		if(defaultDirectedType==null) {
			throw new IllegalArgumentException("given defaultDirectedType is null!");
		}
		
		//if DirectedType.BI_DIRECTED is allowed in the edge data table, the edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets must be false, otherwise, it is not possible to distinguish the two opposite directed vertex with the exactly the same set of edge id column;
		if(defaultDirectedType==DirectedType.BI_DIRECTED) {
			if(edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets) {
				throw new IllegalArgumentException("if DirectedType.BI_DIRECTED is allowed in the edge data table, the edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets must be false, otherwise, it is not possible to distinguish the two opposite directed vertex with the exactly the same set of edge id column;");
			}		
		}
	
		
		this.defaultDirectedType = defaultDirectedType;
	}
	
	
	/**
	 * 
	 * build a VfGraphEdge from the {@link #edgeRecordDataTableResultSet} if it is not empty;
	 * 
	 * the built edge's directed-ness should be the same with the {@link #directed};
	 * 
	 * @throws SQLException 
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
			
			if(defaultDirectedType == DirectedType.UNDIRECTED) {
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
				
			}else if(defaultDirectedType == DirectedType.DIRECTED_FORWARD) {
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
			}else if(defaultDirectedType == DirectedType.DIRECTED_BACKWARD) {
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
				
			}else if(defaultDirectedType == DirectedType.BI_DIRECTED) {
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
				throw new IllegalArgumentException("unrecognized default directedType:"+defaultDirectedType);
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
				false, //boolean hasDirectednessIndicatorColumn,
				null,///DataTableColumnName directednessIndicatorColumnName,
				this.defaultDirectedType,//DirectedType defaultDirectedType,
				null//Map<String, DirectedType> columnValueStringDirectedTypeMap
				);
	}

	
	
	/////////////////////////
//	public DirectedType getDefaultDirectedType() {
//		return defaultDirectedType;
//	}
	
}
