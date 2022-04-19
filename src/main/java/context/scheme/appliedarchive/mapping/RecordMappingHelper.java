package context.scheme.appliedarchive.mapping;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import context.scheme.appliedarchive.TrimmedIntegratedDOSAndCFDGraphUtils;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import function.composition.CompositionFunction;
import metadata.DataType;
import metadata.MetadataID;
import metadata.record.RecordDataMetadata;
import operation.Operation;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;

/**
 * find out the set of non-primary key column set of the target record metadata that need to be mapped from non-primary key columns of the source record metadata;
 * 
 * facilitate building a {@link RecordMapping}
 * 
 * 
 * @author tanxu
 *
 */
public class RecordMappingHelper {
	private final TrimmedIntegratedDOSAndCFDGraphUtils trimmedIntegratedDOSAndCFDGraphUtils;
	private final IntegratedDOSGraphNode targetRecordDataContainingNode;
	
	//////////////////////
	/**
	 * the record MetadataID of the target record data in the {@link #targetRecordDataContainingNode}
	 */
	private MetadataID targetRecordMetadataID;
	/**
	 * the set of primary key columns of the target record metadata;
	 * the full set of primary key columns should be mapped one-by-one from each primary key column of source record Metadata;
	 * 		thus the primary key column set size of the target and source record Metadata should be the same!
	 * 
	 * can never be null or empty;
	 */
	private Set<DataTableColumn> targetRecordDataPrimaryKeyColumnSetToBeMapped;
	/**
	 * the set of non-primary key columns of the target record Metadata such that
	 * 1. used as input column of one or more operations in the {@link #trimmedIntegratedDOSGraph}
	 * 		{@link Operation#getInputRecordMetadataIDInputColumnNameSetMap()}
	 * 2. used as input columns of input variables of one or more CompositionFunction on the {@link #trimmedIntegratedCFDGraph}
	 * 		{@link CompositionFunction#getDependedRecordMetadataIDInputColumnNameSetMap(context.VisframeContext)}
	 * 
	 * this set is a subset of the non-primary key column set of the target record data and can be empty;
	 * 
	 * this set of columns must be one-to-one mapped from one distinct non-primary key column of the source record Metadata;
	 */
	private Set<DataTableColumn> targetRecordDataNonPrimaryKeyColumnSetToBeMapped;
	
	
	/**
	 * 
	 * @param visScheme
	 * @param trimmedIntegratedCFDGraph
	 * @param trimmedIntegratedDOSGraph
	 * @param targetRecordDataContainingNode
	 */
	public RecordMappingHelper(
			TrimmedIntegratedDOSAndCFDGraphUtils trimmedIntegratedDOSAndCFDGraphUtils,
			IntegratedDOSGraphNode targetRecordDataContainingNode
			){
		//TODO
		if(!targetRecordDataContainingNode.getMetadataID().getDataType().equals(DataType.RECORD))
			throw new IllegalArgumentException("given targetRecordDataContainingNode does not contain a record type data!");
		
		
		this.trimmedIntegratedDOSAndCFDGraphUtils = trimmedIntegratedDOSAndCFDGraphUtils;
		
		this.targetRecordDataContainingNode = targetRecordDataContainingNode;
		
		//////////
		this.detect();
	}
	
	
	private void detect() {
		///////////////
		this.targetRecordMetadataID = this.targetRecordDataContainingNode.getMetadataID();
		
		////////////////////
		Set<DataTableColumnName> inputColumnNameSet = new HashSet<>();
		//
		inputColumnNameSet.addAll(
				this.trimmedIntegratedDOSAndCFDGraphUtils.getRecordDataContainingIntegratedDOSGraphNodeInputColumnNameSetMap()
				.get(this.targetRecordDataContainingNode));
		
		
		///////////////
		RecordDataMetadata targetRecordMetadata = 
				(RecordDataMetadata)this.trimmedIntegratedDOSAndCFDGraphUtils.getVisScheme().getMetadataLookup().lookup(this.targetRecordMetadataID);
		
		this.targetRecordDataPrimaryKeyColumnSetToBeMapped = new LinkedHashSet<>();
		this.targetRecordDataNonPrimaryKeyColumnSetToBeMapped = new LinkedHashSet<>();
		
		targetRecordMetadata.getDataTableSchema().getOrderedListOfNonRUIDColumn().forEach(c->{
			if(c.isInPrimaryKey())
				this.targetRecordDataPrimaryKeyColumnSetToBeMapped.add(c);
			else //non-primary key column
				if(inputColumnNameSet.contains(c.getName()))
					this.targetRecordDataNonPrimaryKeyColumnSetToBeMapped.add(c);
		});
	}
	
	/////////////////////////////////

	/**
	 * @return the targetRecordMetadataID
	 */
	public MetadataID getTargetRecordMetadataID() {
		return targetRecordMetadataID;
	}


	/**
	 * @return the targetRecordDataPrimaryKeyColumnSetToBeMapped
	 */
	public Set<DataTableColumn> getTargetRecordDataPrimaryKeyColumnSetToBeMapped() {
		return targetRecordDataPrimaryKeyColumnSetToBeMapped;
	}


	/**
	 * @return the targetRecordDataNonPrimaryKeyColumnSetToBeMapped
	 */
	public Set<DataTableColumn> getTargetRecordDataNonPrimaryKeyColumnSetToBeMapped() {
		return targetRecordDataNonPrimaryKeyColumnSetToBeMapped;
	}


}
