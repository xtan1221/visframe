package visinstance;


import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.scheme.VisSchemeID;
import context.scheme.appliedarchive.VisSchemeAppliedArchive;
import context.scheme.appliedarchive.VisSchemeAppliedArchiveID;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstanceID;
import dependency.vccl.VSCopy;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;

/**
 * 122220-update
 * 
 * VisInstance based on a {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance};
 * 
 * the core ShapeCFG set of this type VisInstance can be of any subset of VSCopies of the VCCL graph of the corresponding {@link VisSchemeAppliedArchive} of the {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance};
 * 
 * specifically, for the selected VSCopy set, all the core ShapeCFGs of the owner VCDNode are included in the core ShapeCFG set of this type of VisInstance;
 * 
 * normally, for {@link VisSchemeBasedVisInstance}, 
 * 		all leaf VSCopies on the VCCL graph should be included for the core ShapeCFG set, while non-leaf VSCopies are optional;
 * 		this is based on the purpose of VisScheme and the design logic behind it;
 * 
 * However, the above strategy is NOT mandatory, any subset of VSCopies should be allowed to make a {@link VisSchemeBasedVisInstance} for maximal flexibility and convenience;
 * 
 * @author tanxu
 *
 */
public class VisSchemeBasedVisInstance extends AbstractVisInstance{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4151451522644299644L;
	
	/////////////////////////////////////////////
	private final VisSchemeID visSchemeID;
	private final VisSchemeAppliedArchiveID visSchemeAppliedArchiveID;
	private final VisSchemeAppliedArchiveReproducedAndInsertedInstanceID visSchemeAppliedArchiveReproducedAndInsertedInstanceID;
	
	/**
	 * set of VSCopy whose core ShapeCFGs are to be included in the core ShapeCFG set of this {@link VisSchemeBasedVisInstance};
	 * 
	 * cannot be null or empty;
	 * 
	 * must be consistent with the {@link #getCoreShapeCFGCFIDSet()};
	 */
	private final Set<VSCopy> selectedVSCopySetInCoreShapeCFGSet;
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param UID
	 * @param coreShapeCFGIDSet
	 * @param visSchemeApplierArchive
	 */
	public VisSchemeBasedVisInstance(
			SimpleName name, VfNotes notes, int UID,
			Set<CompositionFunctionGroupID> coreShapeCFGIDSet,
			Map<CompositionFunctionGroupID, Set<CompositionFunctionID>> coreShapeCFGIDCFIDSetMap,
			
			//////////////
			VisSchemeID visSchemeID,
			VisSchemeAppliedArchiveID visSchemeAppliedArchiveID,
			VisSchemeAppliedArchiveReproducedAndInsertedInstanceID visSchemeAppliedArchiveReproducedAndInsertedInstanceID,
			Set<VSCopy> selectedVSCopySetInCoreShapeCFGSet
			) {
		super(name, notes, UID, coreShapeCFGIDSet,coreShapeCFGIDCFIDSetMap);
		if(visSchemeID==null)
			throw new IllegalArgumentException("given visSchemeID cannot be null or empty!");
		if(visSchemeAppliedArchiveID==null)
			throw new IllegalArgumentException("given visSchemeAppliedArchiveID cannot be null or empty!");
		if(visSchemeAppliedArchiveReproducedAndInsertedInstanceID==null)
			throw new IllegalArgumentException("given visSchemeAppliedArchiveReproducedAndInsertedInstanceID cannot be null or empty!");
		if(selectedVSCopySetInCoreShapeCFGSet==null || selectedVSCopySetInCoreShapeCFGSet.isEmpty())
			throw new IllegalArgumentException("given selectedVSCopySetInCoreShapeCFGSet cannot be null or empty!");
		//
		if(coreShapeCFGIDSet==null||coreShapeCFGIDSet.isEmpty())
			throw new IllegalArgumentException("given coreShapeCFGIDSet cannot be null or empty!");
		if(coreShapeCFGIDCFIDSetMap==null||coreShapeCFGIDCFIDSetMap.isEmpty())
			throw new IllegalArgumentException("given coreShapeCFGIDCFIDSetMap cannot be null or empty!");
		if(!coreShapeCFGIDSet.equals(coreShapeCFGIDCFIDSetMap.keySet()))
			throw new IllegalArgumentException("given coreShapeCFGIDCFIDSetMap's key set is not equal to given coreShapeCFGIDSet!");
		coreShapeCFGIDCFIDSetMap.forEach((id,set)->{
			if(set==null||set.isEmpty())
				throw new IllegalArgumentException("given coreShapeCFGIDCFIDSetMap's value set cannot be null or empty!");
		});
		
		
		//
		this.visSchemeID = visSchemeID;
		this.visSchemeAppliedArchiveID = visSchemeAppliedArchiveID;
		this.visSchemeAppliedArchiveReproducedAndInsertedInstanceID = visSchemeAppliedArchiveReproducedAndInsertedInstanceID;
		this.selectedVSCopySetInCoreShapeCFGSet = selectedVSCopySetInCoreShapeCFGSet;
	}
	

	///////////////////////////////
	/**
	 * @return the visSchemeID
	 */
	public VisSchemeID getVisSchemeID() {
		return visSchemeID;
	}
	
	/**
	 * @return the visSchemeAppliedArchiveID
	 */
	public VisSchemeAppliedArchiveID getVisSchemeAppliedArchiveID() {
		return visSchemeAppliedArchiveID;
	}
	

	/**
	 * @return the visSchemeApplierArchiveInstanceID
	 */
	public VisSchemeAppliedArchiveReproducedAndInsertedInstanceID getVisSchemeAppliedArchiveReproducedAndInsertedInstanceID() {
		return visSchemeAppliedArchiveReproducedAndInsertedInstanceID;
	}


	/**
	 * @return the selectedVSCopySetInCoreShapeCFGSet
	 */
	public Set<VSCopy> getSelectedVSCopySetInCoreShapeCFGSet() {
		return selectedVSCopySetInCoreShapeCFGSet;
	}


	
	/////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((selectedVSCopySetInCoreShapeCFGSet == null) ? 0 : selectedVSCopySetInCoreShapeCFGSet.hashCode());
		result = prime * result + ((visSchemeAppliedArchiveID == null) ? 0 : visSchemeAppliedArchiveID.hashCode());
		result = prime * result + ((visSchemeAppliedArchiveReproducedAndInsertedInstanceID == null) ? 0
				: visSchemeAppliedArchiveReproducedAndInsertedInstanceID.hashCode());
		result = prime * result + ((visSchemeID == null) ? 0 : visSchemeID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof VisSchemeBasedVisInstance))
			return false;
		VisSchemeBasedVisInstance other = (VisSchemeBasedVisInstance) obj;
		if (selectedVSCopySetInCoreShapeCFGSet == null) {
			if (other.selectedVSCopySetInCoreShapeCFGSet != null)
				return false;
		} else if (!selectedVSCopySetInCoreShapeCFGSet.equals(other.selectedVSCopySetInCoreShapeCFGSet))
			return false;
		if (visSchemeAppliedArchiveID == null) {
			if (other.visSchemeAppliedArchiveID != null)
				return false;
		} else if (!visSchemeAppliedArchiveID.equals(other.visSchemeAppliedArchiveID))
			return false;
		if (visSchemeAppliedArchiveReproducedAndInsertedInstanceID == null) {
			if (other.visSchemeAppliedArchiveReproducedAndInsertedInstanceID != null)
				return false;
		} else if (!visSchemeAppliedArchiveReproducedAndInsertedInstanceID
				.equals(other.visSchemeAppliedArchiveReproducedAndInsertedInstanceID))
			return false;
		if (visSchemeID == null) {
			if (other.visSchemeID != null)
				return false;
		} else if (!visSchemeID.equals(other.visSchemeID))
			return false;
		return true;
	}

}
