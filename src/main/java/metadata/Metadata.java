package metadata;

import basic.HasName;
import basic.HasNotes;
import basic.lookup.VisframeUDT;
import basic.process.NonProcessType;
import operation.OperationID;

/**
 * interface that contains full set of annotation and structural information for a type of data with a specific structure and components and supporting data tables;
 * 
 * 
 * @author tanxu
 *
 */
public interface Metadata extends VisframeUDT, HasName, HasNotes, NonProcessType{
	/**
	 * 
	 */
	@Override
	MetadataName getName();
	
	/**
	 * returns the DataType of this Metadata
	 * @return
	 */
	DataType getDataType();
	
	/**
	 * returns the SourceType of this Metadata
	 * @return
	 */
	SourceType getSourceType();
	
	/**
	 * returns the MetadataID of the source CompositeDataMetadata of this Metadata if {@link getSourceType()} method returns {@link SourceType#STRUCTURAL_COMPONENT};
	 * otherwise, returns null; 
	 * @return
	 */
	MetadataID getSourceCompositeDataMetadataID();
	
	/**
	 * returns the OperationID of the Operation that produced this Metadata if {@link getSourceType()} method returns {@link SourceType#RESULT_FROM_OPERATION};
	 * otherwise, returns null;
	 * @return
	 */
	OperationID getSourceOperationID();
	
	
	@Override
	default MetadataID getID() {
		return new MetadataID(this.getName(),this.getDataType());
	}

}
