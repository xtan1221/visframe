package context.scheme.appliedarchive.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import metadata.DataType;
import metadata.MetadataID;
import metadata.graph.vftree.VfTreeMandatoryEdgeDataTableSchemaUtils;
import metadata.graph.vftree.VfTreeMandatoryNodeDataTableSchemaUtils;
import metadata.graph.vftree.feature.VfTreeEdgeFeature;
import operation.graph.SingleGenericGraphAsInputOperation;
import operation.vftree.VfTreeTrimmingOperationBase;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableSchemaID;

/**
 * mapping from a vftree metadata (of {@link DataType#vfTREE} type) from host VisProjectDBContext 
 * to a node selected in solution set on the trimmed integrated DOS graph containing metadata of type {@link DataType#vfTREE};
 * 
 * the target vftree metadata must be input metadata of at least one {@link VfTreeTrimmingOperationBase};
 * the target vftree metadata could be input metadata of one or more {@link SingleGenericGraphAsInputOperation};
 * 
 * the mapping should be consistent with the {@link GenericGraphMapping} except for  
 * 1. {@link VfTreeEdgeFeature#isEdgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets()} is always false;
 * 2. the node id columns and mandatory additional feature columns of node record data are fully pre-defined by visframe;
 * 3. the edge id columns and mandatory additional feature columns of edge record data are fully pre-defined by visframe;
 * 
 * thus only the following are explicitly needed in the constructor:
 * 1. the non-mandatory additional feature columns of node record data;
 * 2. the non-mandatory additional feature columns of edge record data;
 * 
 * @author tanxu
 * 
 */
public class VfTreeMapping extends GenericGraphMapping{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8005969510454278151L;
	
	//////////////////////////
	private static final Map<DataTableColumn, DataTableColumn> targetSourceNodeRecordNodeIDColumnMap;
	private static final Map<DataTableColumn, DataTableColumn> targetSourceNodeRecordMandatoryAdditionalFeatureColumnMap;
	
	private static final boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = false;
	private static final Map<DataTableColumn, DataTableColumn> targetSourceEdgeRecordEdgeIDColumnMap;
	private static final Map<DataTableColumn, DataTableColumn> targetSourceEdgeRecordMandatoryAdditionalFeatureColumnMap;
	
	static {
		targetSourceNodeRecordNodeIDColumnMap = new HashMap<>();
		VfTreeMandatoryNodeDataTableSchemaUtils.getIDColumnList().forEach(c->{
			targetSourceNodeRecordNodeIDColumnMap.put(c, c);
		});
		
		targetSourceNodeRecordMandatoryAdditionalFeatureColumnMap = new HashMap<>();
		VfTreeMandatoryNodeDataTableSchemaUtils.getMandatoryAdditionalFeatureColumnList().forEach(c->{
			targetSourceNodeRecordMandatoryAdditionalFeatureColumnMap.put(c, c);
		});
		
		targetSourceEdgeRecordEdgeIDColumnMap = new HashMap<>();
		VfTreeMandatoryEdgeDataTableSchemaUtils.getIDColumnList().forEach(c->{
			targetSourceEdgeRecordEdgeIDColumnMap.put(c, c);
		});
		
		targetSourceEdgeRecordMandatoryAdditionalFeatureColumnMap = new HashMap<>();
		VfTreeMandatoryEdgeDataTableSchemaUtils.getMandatoryAdditionalFeatureColumnList().forEach(c->{
			targetSourceEdgeRecordMandatoryAdditionalFeatureColumnMap.put(c, c);
		});
	}
	
	/**
	 * make and return a map of additional feature columns of node record data from target vftree to the source vftree 
	 * including both mandatory ones pre-defined by visframe and non-mandatory ones as the given map;
	 * 
	 * @param targetSourceNodeRecordNonMandatoryAdditionalFeatureColumnMap
	 * @return
	 */
	private static Map<DataTableColumn, DataTableColumn> makeTargetSourceNodeRecordDataAdditionalFeatureColumMap(
			Map<DataTableColumn, DataTableColumn> targetSourceNodeRecordNonMandatoryAdditionalFeatureColumnMap){
		if(targetSourceNodeRecordNonMandatoryAdditionalFeatureColumnMap==null)
			throw new IllegalArgumentException("given targetSourceNodeRecordNonMandatoryFeatureColumnMap cannot be null!");
		
		Map<DataTableColumn, DataTableColumn> ret = new HashMap<>();
		ret.putAll(targetSourceNodeRecordMandatoryAdditionalFeatureColumnMap);
		ret.putAll(targetSourceNodeRecordNonMandatoryAdditionalFeatureColumnMap);
		return ret;
	}
	
	
	/**
	 * make and return a map of additional feature columns including both mandatory ones pre-defined by visframe and non-mandatory ones as the given map;
	 * 
	 * @param targetSourceNodeRecordNonMandatoryAdditionalFeatureColumnMap
	 * @return
	 */
	private static Map<DataTableColumn, DataTableColumn> makeTargetSourceEdgeRecordDataAdditionalFeatureColumMap(
			Map<DataTableColumn, DataTableColumn> targetSourceEdgeRecordNonMandatoryAdditionalFeatureColumnMap){
		if(targetSourceEdgeRecordNonMandatoryAdditionalFeatureColumnMap==null)
			throw new IllegalArgumentException("given targetSourceEdgeRecordNonMandatoryFeatureColumnMap cannot be null!");
		
		Map<DataTableColumn, DataTableColumn> ret = new HashMap<>();
		ret.putAll(targetSourceEdgeRecordMandatoryAdditionalFeatureColumnMap);
		ret.putAll(targetSourceEdgeRecordNonMandatoryAdditionalFeatureColumnMap);
		return ret;
	}
	
	
	///////////////////////////////
	/**
	 * constructor
	 * 
	 * @param targetMetadataID
	 * @param sourceMetadataID
	 * @param targetNodeRecordDataIncluded
	 * @param targetEdgeRecordDataIncluded
	 * @param targetNodeRecordMetadataID
	 * @param targetEdgeRecordMetadataID
	 * @param sourceNodeRecordMetadataID
	 * @param sourceEdgeRecordMetadataID
	 * @param sourceNodeRecordDataTableSchemaID
	 * @param sourceEdgeRecordDataTableSchemaID
	 * @param targetSourceNodeRecordNonMandatoryAdditionalFeatureColumnMap not null if targetNodeRecordDataIncluded is true; null otherwise
	 * @param targetSourceEdgeRecordNonMandatoryAdditionalFeatureColumnMap not null if targetEdgeRecordDataIncluded is true, null otherwise;
	 */
	public VfTreeMapping(
			MetadataID targetMetadataID, MetadataID sourceMetadataID,
			boolean targetNodeRecordDataIncluded,
			boolean targetEdgeRecordDataIncluded,
			
			MetadataID targetNodeRecordMetadataID, MetadataID targetEdgeRecordMetadataID,
			MetadataID sourceNodeRecordMetadataID, MetadataID sourceEdgeRecordMetadataID,
			DataTableSchemaID sourceNodeRecordDataTableSchemaID,
			DataTableSchemaID sourceEdgeRecordDataTableSchemaID,
			
			Map<DataTableColumn, DataTableColumn> targetSourceNodeRecordNonMandatoryAdditionalFeatureColumnMap,
			Map<DataTableColumn, DataTableColumn> targetSourceEdgeRecordNonMandatoryAdditionalFeatureColumnMap
			) {
		super(targetMetadataID, sourceMetadataID, 
				targetNodeRecordDataIncluded,targetEdgeRecordDataIncluded,
				
				targetNodeRecordMetadataID,targetEdgeRecordMetadataID,
				sourceNodeRecordMetadataID,sourceEdgeRecordMetadataID,
				sourceNodeRecordDataTableSchemaID,sourceEdgeRecordDataTableSchemaID,
				
				targetSourceNodeRecordNodeIDColumnMap, //predefined by visframe
				targetNodeRecordDataIncluded?makeTargetSourceNodeRecordDataAdditionalFeatureColumMap(targetSourceNodeRecordNonMandatoryAdditionalFeatureColumnMap):null, //merge with mandatory ones predefined by visframe 
				
				targetEdgeRecordDataIncluded?edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets:null, //always false
				targetEdgeRecordDataIncluded?targetSourceEdgeRecordEdgeIDColumnMap:null, //predefined by visframe
				null, //always null; targetSourceEdgeDataSourceNodeIDColumnMap
				null,//always null; targetSourceEdgeDataSinkNodeIDColumnMap
				targetEdgeRecordDataIncluded?makeTargetSourceEdgeRecordDataAdditionalFeatureColumMap(targetSourceEdgeRecordNonMandatoryAdditionalFeatureColumnMap):null//merge with mandatory ones predefined by visframe 
				);
		// TODO Auto-generated constructor stub
		
	}
	
	@Override
	protected Predicate<DataType> getTargetMetadataTypePredicate() {
		return e->{return e.equals(DataType.vfTREE);};
	}
	
	@Override
	protected Predicate<DataType> getSourceMetadataTypePredicate() {
		return e->{return e.equals(DataType.vfTREE);};
	}

}
