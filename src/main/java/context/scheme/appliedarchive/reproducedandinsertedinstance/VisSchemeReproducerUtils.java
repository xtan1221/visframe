package context.scheme.appliedarchive.reproducedandinsertedinstance;

import basic.HasName;
import basic.lookup.VisframeUDT;

/**
 * contains the full
 * @author tanxu
 *
 */
public class VisSchemeReproducerUtils {
	
	/**
	 * make the name for a reproduced entity of {@link VisframeUDT} type that
	 * 
	 * 1. are stored in management tables in host VisProjectDBContext;
	 * 
	 * 2. and implements {@link HasName} interface;
	 * 
	 * note that the names are often a component of the PrimaryKeyID;
	 * 1. OperationID
	 * 		name
	 * 2. CompositionFunctionGroupID
	 * 		name
	 * 3. CompositionFunctionID
	 * 		owner CompositionFunctionGroupID + index
	 * 4. MetadataID
	 * 		name + data type
	 * 		DataTableSchema name is the same with the owner record Metadata name;
	 * 5. IndependentFreeInputVariableTypeID
	 * 		owner CFID + alias name
	 * 		note that IndependentFreeInputVariableType does not implements {@link HasName} interface
	 * 
	 * note that this method will not check if the built name and the primary key is already taken by existing entity of the same type in the host VisProjectDBContext;
	 * 
	 * @param originalNameString
	 * @param visSchemeArchiveUID
	 * @param visSchemeAppliedArchiveReproducedAndInsertedInstanceUID
	 * @param copyIndexOfAssignedVSComponent
	 * @return
	 */
	public static String makeNameStringForReproducedVisframeUDTEntity(
			String originalNameString, int visSchemeArchiveUID, 
			int visSchemeAppliedArchiveReproducedAndInsertedInstanceUID, int copyIndexOfAssignedVSComponent) {
		
		//TODO
		return null;
	}
}
