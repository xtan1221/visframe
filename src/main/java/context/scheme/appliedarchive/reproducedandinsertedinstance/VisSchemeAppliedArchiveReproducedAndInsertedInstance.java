package context.scheme.appliedarchive.reproducedandinsertedinstance;

import java.util.List;
import java.util.Map;
import basic.HasNotes;
import basic.SimpleName;
import basic.VfNotes;
import basic.lookup.VisframeUDT;
import basic.process.NonReproduceableProcessType;
import context.scheme.VisSchemeID;
import context.scheme.appliedarchive.VisSchemeAppliedArchiveID;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import operation.OperationID;

/**
 * VisframeUDT class for a reproduced and inserted instance of a VisSchemeAppliedArchive;
 * 
 * for VisSchemeAppliedArchive without any Operation with parameter dependent on input data table content, 
 * there could be only one single reproduced and inserted instance;
 * 
 * for VisSchemeAppliedArchive with one or more Operation with parameter dependent on input data table content,
 * there could be multiple instances;
 * 		however, it is not checked whether different instances have the same set of assigned values for such parameters;
 * 		thus it is possible there are multiple VisSchemeAppliedArchiveReproducedAndInsertedInstances with exactly the same set of assigned values for such parameters for each copy of such Operations;
 * 
 * @author tanxu
 *
 */
public class VisSchemeAppliedArchiveReproducedAndInsertedInstance implements HasNotes, VisframeUDT, NonReproduceableProcessType{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1108516554010701487L;
	
	////////////////////////////////
	private final VfNotes notes;
	private final VisSchemeID appliedVisSchemeID;
	private final VisSchemeAppliedArchiveID applierArchiveID;
	private final int UID;
	
	
	/////////////
	/**
	 * map from OperationID in VisScheme with parameter dependent on input data table content 
	 * to the map 
	 * 		from the copy index of the Operation
	 * 		to the map
	 * 			from the name of parameters dependent on input data table content
	 * 			to the assigned object value (may be null)
	 * 
	 * cannot be null;
	 * can be empty if there is no such Operations on the trimmed integrated DOS graph of the VisSchemeApplierArchive;
	 * 
	 */
	private final Map<OperationID, Map<Integer,Map<SimpleName, Object>>> originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap;
	
	/**
	 * map from the OperationID in the VisScheme
	 * to the map
	 * 		from the copy index of the VCDNode to which the OperationID is assigned
	 * 		to the reproduced and inserted OperationID corresponding to the original OperationID and the copy index;
	 */
	private final Map<OperationID, Map<Integer, OperationID>> originalOperationIDCopyIndexReproducedOperationIDMapMap;
	
	/**
	 * the list of reproduced and inserted OperationID in the insertion order;
	 */
	private final List<OperationID> reproducedOperationIDListByInsertionOrder;
	
	
	/**
	 * map from the CompositionFunctionGroupID in VisScheme
	 * to the map
	 * 		from the copy index
	 * 		to the reproduced and inserted CompositionFunctionGroupID corresponding to the CompositionFunctionGroupID in VisScheme and the copy index;
	 * 
	 * facilitate lookup of reproduced CFGIDs based on original CFGID and copy index;
	 */
	private final Map<CompositionFunctionGroupID, Map<Integer, CompositionFunctionGroupID>> originalCFGIDCopyIndexReproducedCFGIDMapMap;
	/**
	 * the list of reproduced and inserted CompositionFunctionGroupID in the insertion order
	 */
	private final List<CompositionFunctionGroupID> reproducedCFGIDListByInsertionOrder;
	


	/**
	 * map from the CompositionFunctionID in VisScheme
	 * to the map
	 * 		from the copy index
	 * 		to the reproduced and inserted CompositionFunctionID corresponding to the CompositionFunctionID in VisScheme and the copy index;
	 * 
	 * facilitate lookup of reproduced CFIDs based on original CFGID and copy index;
	 */
	private final Map<CompositionFunctionID, Map<Integer, CompositionFunctionID>> originalCFIDCopyIndexReproducedCFIDMapMap;
	/**
	 * the list of reproduced and inserted CompositionFunctionID in the insertion order
	 */
	private final List<CompositionFunctionID> reproducedCFIDListByInsertionOrder;
	
	/**
	 * constructor
	 * @param notes
	 * @param appliedVisSchemeID
	 * @param applierArchiveID
	 * @param UID
	 * @param operationWithParameterDependentOnInputDataTableContentParameterNameAssignedStringValueMapMap
	 */
	public VisSchemeAppliedArchiveReproducedAndInsertedInstance(
			VfNotes notes,
			VisSchemeID appliedVisSchemeID,
			VisSchemeAppliedArchiveID applierArchiveID,
			int UID,
			Map<OperationID, Map<Integer,Map<SimpleName, Object>>> originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap,
			Map<OperationID, Map<Integer, OperationID>> originalOperationIDCopyIndexReproducedOperationIDMapMap,
			List<OperationID> reproducedOperationIDListByInsertionOrder,
			Map<CompositionFunctionGroupID, Map<Integer, CompositionFunctionGroupID>> originalCFGIDCopyIndexReproducedCFGIDMapMap,
			List<CompositionFunctionGroupID> reproducedCFGIDListByInsertionOrder,
			Map<CompositionFunctionID, Map<Integer, CompositionFunctionID>> originalCFIDCopyIndexReproducedCFIDMapMap,
			List<CompositionFunctionID> reproducedCFIDListByInsertionOrder
			){
		// validations
		if(notes == null)
			throw new IllegalArgumentException("given notes cannot be null!");
		if(appliedVisSchemeID == null)
			throw new IllegalArgumentException("given appliedVisSchemeID cannot be null!");
		if(applierArchiveID == null)
			throw new IllegalArgumentException("given applierArchiveID cannot be null!");
		if(originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap == null)
			throw new IllegalArgumentException("given operationWithParameterDependentOnInputDataTableContentParameterNameAssignedStringValueMapMap cannot be null!");
		
		this.notes = notes;
		this.appliedVisSchemeID = appliedVisSchemeID;
		this.applierArchiveID = applierArchiveID;
		this.UID = UID;
		
		this.originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap = 
				originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap;
		this.originalOperationIDCopyIndexReproducedOperationIDMapMap = originalOperationIDCopyIndexReproducedOperationIDMapMap;
		this.reproducedOperationIDListByInsertionOrder = reproducedOperationIDListByInsertionOrder;
		
		this.originalCFGIDCopyIndexReproducedCFGIDMapMap = originalCFGIDCopyIndexReproducedCFGIDMapMap;
		this.reproducedCFGIDListByInsertionOrder = reproducedCFGIDListByInsertionOrder;
		
		this.originalCFIDCopyIndexReproducedCFIDMapMap = originalCFIDCopyIndexReproducedCFIDMapMap;
		this.reproducedCFIDListByInsertionOrder = reproducedCFIDListByInsertionOrder;
	}
	
	
	@Override
	public VisSchemeAppliedArchiveReproducedAndInsertedInstanceID getID() {
		return new VisSchemeAppliedArchiveReproducedAndInsertedInstanceID(this.UID);
	}
	
	@Override
	public VfNotes getNotes() {
		return this.notes;
	}
	
	/////////////////////////////////////
	/**
	 * @return the appliedVisSchemeID
	 */
	public VisSchemeID getAppliedVisSchemeID() {
		return appliedVisSchemeID;
	}

	/**
	 * @return the applierArchiveID
	 */
	public VisSchemeAppliedArchiveID getApplierArchiveID() {
		return applierArchiveID;
	}

	/**
	 * @return the uID
	 */
	public int getUID() {
		return UID;
	}


	/**
	 * @return the operationWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap
	 */
	public Map<OperationID, Map<Integer,Map<SimpleName, Object>>> getOperationWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap() {
		return originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap;
	}

	/**
	 * @return the originalOperationIDCopyIndexReproducedOperationIDMapMap
	 */
	public Map<OperationID, Map<Integer, OperationID>> getOriginalOperationIDCopyIndexReproducedOperationIDMapMap() {
		return originalOperationIDCopyIndexReproducedOperationIDMapMap;
	}
	
	/**
	 * @return the originalCFGIDCopyIndexReproducedCFGIDMapMap
	 */
	public Map<CompositionFunctionGroupID, Map<Integer, CompositionFunctionGroupID>> getOriginalCFGIDCopyIndexReproducedCFGIDMapMap() {
		return originalCFGIDCopyIndexReproducedCFGIDMapMap;
	}


	/**
	 * @return the originalCFIDCopyIndexReproducedCFIDMapMap
	 */
	public Map<CompositionFunctionID, Map<Integer, CompositionFunctionID>> getOriginalCFIDCopyIndexReproducedCFIDMapMap() {
		return originalCFIDCopyIndexReproducedCFIDMapMap;
	}

	/**
	 * @return the reproducedOperationIDListByInsertionOrder
	 */
	public List<OperationID> getReproducedOperationIDListByInsertionOrder() {
		return reproducedOperationIDListByInsertionOrder;
	}


	/**
	 * @return the reproducedCFGIDListByInsertionOrder
	 */
	public List<CompositionFunctionGroupID> getReproducedCFGIDListByInsertionOrder() {
		return reproducedCFGIDListByInsertionOrder;
	}


	/**
	 * @return the reproducedCFIDListByInsertionOrder
	 */
	public List<CompositionFunctionID> getReproducedCFIDListByInsertionOrder() {
		return reproducedCFIDListByInsertionOrder;
	}



	//////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + UID;
		result = prime * result + ((appliedVisSchemeID == null) ? 0 : appliedVisSchemeID.hashCode());
		result = prime * result + ((applierArchiveID == null) ? 0 : applierArchiveID.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((originalCFGIDCopyIndexReproducedCFGIDMapMap == null) ? 0
				: originalCFGIDCopyIndexReproducedCFGIDMapMap.hashCode());
		result = prime * result + ((originalCFIDCopyIndexReproducedCFIDMapMap == null) ? 0
				: originalCFIDCopyIndexReproducedCFIDMapMap.hashCode());
		result = prime * result + ((originalOperationIDCopyIndexReproducedOperationIDMapMap == null) ? 0
				: originalOperationIDCopyIndexReproducedOperationIDMapMap.hashCode());
		result = prime * result
				+ ((originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap == null)
						? 0
						: originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap
								.hashCode());
		result = prime * result
				+ ((reproducedCFGIDListByInsertionOrder == null) ? 0 : reproducedCFGIDListByInsertionOrder.hashCode());
		result = prime * result
				+ ((reproducedCFIDListByInsertionOrder == null) ? 0 : reproducedCFIDListByInsertionOrder.hashCode());
		result = prime * result + ((reproducedOperationIDListByInsertionOrder == null) ? 0
				: reproducedOperationIDListByInsertionOrder.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VisSchemeAppliedArchiveReproducedAndInsertedInstance))
			return false;
		VisSchemeAppliedArchiveReproducedAndInsertedInstance other = (VisSchemeAppliedArchiveReproducedAndInsertedInstance) obj;
		if (UID != other.UID)
			return false;
		if (appliedVisSchemeID == null) {
			if (other.appliedVisSchemeID != null)
				return false;
		} else if (!appliedVisSchemeID.equals(other.appliedVisSchemeID))
			return false;
		if (applierArchiveID == null) {
			if (other.applierArchiveID != null)
				return false;
		} else if (!applierArchiveID.equals(other.applierArchiveID))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (originalCFGIDCopyIndexReproducedCFGIDMapMap == null) {
			if (other.originalCFGIDCopyIndexReproducedCFGIDMapMap != null)
				return false;
		} else if (!originalCFGIDCopyIndexReproducedCFGIDMapMap
				.equals(other.originalCFGIDCopyIndexReproducedCFGIDMapMap))
			return false;
		if (originalCFIDCopyIndexReproducedCFIDMapMap == null) {
			if (other.originalCFIDCopyIndexReproducedCFIDMapMap != null)
				return false;
		} else if (!originalCFIDCopyIndexReproducedCFIDMapMap.equals(other.originalCFIDCopyIndexReproducedCFIDMapMap))
			return false;
		if (originalOperationIDCopyIndexReproducedOperationIDMapMap == null) {
			if (other.originalOperationIDCopyIndexReproducedOperationIDMapMap != null)
				return false;
		} else if (!originalOperationIDCopyIndexReproducedOperationIDMapMap
				.equals(other.originalOperationIDCopyIndexReproducedOperationIDMapMap))
			return false;
		if (originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap == null) {
			if (other.originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap != null)
				return false;
		} else if (!originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap
				.equals(other.originalOperationIDWithParameterDependentOnInputDataTableContentParameterNameAssignedObjectValueMapMap))
			return false;
		if (reproducedCFGIDListByInsertionOrder == null) {
			if (other.reproducedCFGIDListByInsertionOrder != null)
				return false;
		} else if (!reproducedCFGIDListByInsertionOrder.equals(other.reproducedCFGIDListByInsertionOrder))
			return false;
		if (reproducedCFIDListByInsertionOrder == null) {
			if (other.reproducedCFIDListByInsertionOrder != null)
				return false;
		} else if (!reproducedCFIDListByInsertionOrder.equals(other.reproducedCFIDListByInsertionOrder))
			return false;
		if (reproducedOperationIDListByInsertionOrder == null) {
			if (other.reproducedOperationIDListByInsertionOrder != null)
				return false;
		} else if (!reproducedOperationIDListByInsertionOrder.equals(other.reproducedOperationIDListByInsertionOrder))
			return false;
		return true;
	}
}
