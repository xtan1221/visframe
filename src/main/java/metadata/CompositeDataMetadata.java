package metadata;

import java.util.Set;

import basic.VfNotes;
import operation.OperationID;

/**
 * base class for composite type Metadata that is composed of one or more component Metadata objects organized with a specific structure;
 * 
 * @author tanxu
 *
 */
public abstract class CompositeDataMetadata extends AbstractMetadata {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5433363272070732807L;
	/////////////////////////////////
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param sourceType
	 * @param sourceCompositeDataMetadataID
	 * @param sourceOperationID
	 */
	public CompositeDataMetadata(
			MetadataName name, VfNotes notes, SourceType sourceType,
			MetadataID sourceCompositeDataMetadataID, OperationID sourceOperationID) {
		super(name, notes, sourceType, sourceCompositeDataMetadataID, sourceOperationID);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * return the DataType of basic component data objects of this CompositeDataMetadata; 
	 * @return
	 */
	public abstract DataType getComponentDataType();
	/**
	 * return the set of component Metadata's MetadataIDs
	 * @return
	 */
	public abstract Set<MetadataID> getComponentRecordDataMetadataIDSet();

	
	
}
