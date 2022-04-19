package metadata;

import basic.VfNameString;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeReproducerUtils;


/**
 * marker class for VfNameString of a Metadata;
 * 
 * reproduce a MetadataName involves lookup whether the Metadata is related with the SSS or not;
 * 
 * reproduce a MetadataName should be done with {@link VisSchemeReproducerUtils#reproduceMetadataName(MetadataID, int)} method outside of DataTableName class (normally where the reproducing of MetadataName is needed); 
 * 
 * @author tanxu
 * 
 */
public class MetadataName extends VfNameString{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3064243105938026561L;
	
	/**
	 * constructor
	 * @param stringValue
	 */
	public MetadataName(String stringValue) {
		super(stringValue);
	}
//
//	/**
//	 * need to find out whether the IntegratedDOSGraphNode containing the MetadataID is selected in the solution set of the visSchemeApplierArchive or not;
//	 * 
//	 * 1. find out the IntegratedDOSGraphNode containing the owner MetadataID on the trimmed integrated DOS graph;
//	 * 
//	 * 2. if the IntegratedDOSGraphNode is in the solution set
//	 * 
//	 * 3. if not in the solution set
//	 */
//	@Override
//	public MetadataName reproduce(
//			VisSchemeAppliedArchive visSchemeApplierArchive, 
//			int visSchemeAppliedArchiveReproducedAndInsertedInstanceUID, 
//			MetadataID ownerMetadataID, 
//			int ownerMetadataCopyIndex) throws SQLException {
//		
//		
//		
//		return new MetadataName(
//				VisSchemeReproducerUtils.makeNameStringForReproducedVisframeUDTEntity(
//						this.getStringValue(), visSchemeApplierArchive.getUID(), visSchemeAppliedArchiveReproducedAndInsertedInstanceUID, copyIndex));
//	}
	
}
