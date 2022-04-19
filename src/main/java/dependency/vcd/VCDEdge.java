package dependency.vcd;

import java.io.Serializable;
import java.util.Set;

import context.scheme.VSComponent;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import metadata.MetadataID;
import operation.OperationID;
import utils.Pair;

/**
 * interface for VCDEdge on VCDGraph;
 * 
 * note that the dependency between two VCDNodes are fully represented by 
 * @author tanxu
 * 
 */
public interface VCDEdge extends Serializable{
	
	/**
	 * return the source/depending VSComponent linked by this VCDEdge
	 * @return
	 */
	VSComponent getSource();
	
	/**
	 * return the sink/depended VSComponent linked by this VCDEdge
	 * @return
	 */
	VSComponent getSink();
	
	
	///////////////////////////////////
	/**
	 * return the set of dependency contained by this VCDEdge that is based on operation assigned to the source/depending VSComponent and input Metadata assigned to the sink/depended VSComponent;
	 * 
	 * @return
	 */
	Set<Pair<OperationID, MetadataID>> getOperationIDInputMetadataIDDependencyPairSet();
	
	/**
	 * add to the dependency set contained by this VCDEdge that is based on operation assigned to the source/depending VSComponent and input Metadata assigned to the sink/depended VSComponent;
	 * @param dependingID
	 * @param dependedID
	 */
	void addOperationIDInputMetadataIDDependency(OperationID dependingID, MetadataID dependedID);
	
	/**
	 * return the set of dependency contained by this VCDEdge that is based on CFG assigned to the source/depending VSComponent and owner record Metadata assigned to the sink/depended VSComponent;
	 * 
	 * @return
	 */
	Set<Pair<CompositionFunctionGroupID, MetadataID>> getCfgIDOwnerRecordMetadataIDDependencyPairSet();
	
	/**
	 * add to the dependency contained by this VCDEdge that is based on CFG assigned to the source/depending VSComponent and owner record Metadata assigned to the sink/depended VSComponent;
	 *
	 * @param dependingID
	 * @param dependedID
	 */
	void addCfgIDOwnerRecordMetadataIDDependency(CompositionFunctionGroupID dependingID, MetadataID dependedID);
	
	/**
	 * return the set of dependency contained by this VCDEdge that is based on CF assigned to the source/depending VSComponent and depended CF assigned to the sink/depended VSComponent;
	 * 
	 * @return
	 */
	Set<Pair<CompositionFunctionID, CompositionFunctionID>> getCfIDDependedCfIDDependencyPairSet();
	
	/**
	 * add to the dependency contained by this VCDEdge that is based on CF assigned to the source/depending VSComponent and depended CF assigned to the sink/depended VSComponent;
	 * 
	 * @param dependingID
	 * @param dependedID
	 */
	void addCfIDDependedCfIDDependency(CompositionFunctionID dependingID, CompositionFunctionID dependedID);
	
	/**
	 * return the set of dependency contained by this VCDEdge that is based on CF assigned to the source/depending VSComponent and depended record Metadata assigned to the sink/depended VSComponent;
	 * 
	 * @return
	 */
	Set<Pair<CompositionFunctionID, MetadataID>> getCfIDDependedRecordMetadataIDDependencyPairSet();
	
	/**
	 * add to the dependency contained by this VCDEdge that is based on CF assigned to the source/depending VSComponent and depended record Metadata assigned to the sink/depended VSComponent;
	 * 
	 * @param dependingID
	 * @param dependedID
	 */
	void addCfIDDependedRecordMetadataIDDependency(CompositionFunctionID dependingID, MetadataID dependedID);

	
	
	///////////////////////////////////
//	VCDEdge deepClone();
}
