package generic.graph.reader.project;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import context.project.VisProjectDBContext;
import generic.graph.DirectedType;
import metadata.MetadataID;
import rdb.table.data.DataTableColumnName;

public class RecordToGraphReaderFactory{
	private final VisProjectDBContext hostVisProjectDBContext;
	private final boolean hasVertexDataSourceRecordData;
	private final MetadataID vertexDataSourceRecordDataID;
	private final MetadataID edgeDataSourceRecordDataID;
	
	/**
	 * whether to filter out duplicate vertex and edge when parsing the data tables;
	 * 
	 * if false, the duplicates should be dealt with by downstream step, which normally is a {@link GraphBuilder}
	 */
	private final boolean toFilterOutDuplicates;
	
	/////////////////////
	/**
	 * this column set must be from the {@link #vertexDataSourceRecordDataID} data table schema and can include either primary key column or non primary key columns
	 */
	private final LinkedHashSet<DataTableColumnName> vertexIDColumnNameSet;
	/**
	 * this column set must be from the {@link #vertexDataSourceRecordDataID} data table schema and can include either primary key column or non primary key columns
	 */
	private final LinkedHashSet<DataTableColumnName> vertexAdditionalFeatureColumnNameSet;
	
	/**
	 * this column set must be from the {@link #edgeDataSourceRecordDataID} data table schema and can include either primary key column or non primary key columns
	 */
	private final LinkedHashSet<DataTableColumnName> edgeIDColumnNameSet;
	/**
	 * this column set must be from the {@link #edgeDataSourceRecordDataID} data table schema and can include either primary key column or non primary key columns
	 */
	private final LinkedHashSet<DataTableColumnName> edgeAdditionalFeatureColumnNameSet;
	
	private final boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
	/**
	 * this column set must be from the {@link #edgeDataSourceRecordDataID} data table schema and can include either primary key column or non primary key columns
	 */
	private final LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap;
	/**
	 * this column set must be from the {@link #edgeDataSourceRecordDataID} data table schema and can include either primary key column or non primary key columns
	 */
	private final LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap;
	
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
	 */
	public RecordToGraphReaderFactory(
			VisProjectDBContext hostVisProjectDBContext, 
			boolean hasVertexDataSourceRecordData,
			MetadataID vertexDataSourceRecordDataID,
			MetadataID edgeDataSourceRecordDataID, boolean toFilterOutDuplicates,
			LinkedHashSet<DataTableColumnName> vertexIDColumnNameSet,
			LinkedHashSet<DataTableColumnName> vertexAdditionalFeatureColumnNameSet,
			LinkedHashSet<DataTableColumnName> edgeIDColumnNameSet,
			LinkedHashSet<DataTableColumnName> edgeAdditionalFeatureColumnNameSet,
			boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
			LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
			LinkedHashMap<DataTableColumnName, DataTableColumnName> vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap) {
		
		//validations?
		
		
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		this.hasVertexDataSourceRecordData = hasVertexDataSourceRecordData;
		this.vertexDataSourceRecordDataID = vertexDataSourceRecordDataID;
		this.edgeDataSourceRecordDataID = edgeDataSourceRecordDataID;
		this.toFilterOutDuplicates = toFilterOutDuplicates;
		
		this.vertexIDColumnNameSet = vertexIDColumnNameSet;
		this.vertexAdditionalFeatureColumnNameSet = vertexAdditionalFeatureColumnNameSet;
		
		this.edgeIDColumnNameSet = edgeIDColumnNameSet;
		this.edgeAdditionalFeatureColumnNameSet = edgeAdditionalFeatureColumnNameSet;
		this.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
		this.vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap = vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap;
		this.vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap = vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap;
	}
	
	
	/**
	 * build a {@link RecordToGraphReader} without a {@link DirectedType} indicator column but treat all edges as the same {@link DirectedType} as the given one;
	 * 
	 * @param defaultDirectedType
	 * @return
	 */
	public RecordToGraphReader build(DirectedType defaultDirectedType) {
		
		return new RecordToGraphReaderImpl(hostVisProjectDBContext, hasVertexDataSourceRecordData, 
				vertexDataSourceRecordDataID, edgeDataSourceRecordDataID, toFilterOutDuplicates, vertexIDColumnNameSet,
				vertexAdditionalFeatureColumnNameSet, edgeIDColumnNameSet, edgeAdditionalFeatureColumnNameSet,
				edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
				vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap, vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap,
				defaultDirectedType);
	}
	
	
	/**
	 * build a {@link RecordToGraphReader} with a {@link DirectedType} indicator column and the map from the string value to a specific {@link DirectedType}
	 * @param edgeDirectednessIndicatorColumnName
	 * @param colStringValueDirectedTypeMap
	 * @param defaultDirectedType
	 * @return
	 */
	public RecordToGraphReader build(
			DataTableColumnName edgeDirectednessIndicatorColumnName,
			Map<String, DirectedType> colStringValueDirectedTypeMap,
			DirectedType defaultDirectedType) {
		
		return new RecordToGraphReaderImpl2(hostVisProjectDBContext, hasVertexDataSourceRecordData,
				vertexDataSourceRecordDataID, edgeDataSourceRecordDataID, toFilterOutDuplicates, vertexIDColumnNameSet,
				vertexAdditionalFeatureColumnNameSet, edgeIDColumnNameSet, edgeAdditionalFeatureColumnNameSet,
				edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
				vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap, vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap,
				edgeDirectednessIndicatorColumnName,
				colStringValueDirectedTypeMap,
				defaultDirectedType);

	}
	
	
	
}
