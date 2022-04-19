package context.scheme;

import java.io.Serializable;
import java.util.Set;

import basic.VfNotes;
import function.group.CompositionFunctionGroupID;

/**
 * contains a set of CompositionFunctionGroupID for ShapeCFGs that are to be calculated and visualized;
 * 
 * 
 * @author tanxu
 *
 */
public final class VSComponent implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8185384228356326076L;
	
	////////////////////
	private final VfNotes notes;
	private final Set<CompositionFunctionGroupID> coreShapeCFGIDSet;
	
	/**
	 * constructor
	 * @param coreShapeCFGIDSet
	 */
	public VSComponent(VfNotes notes, Set<CompositionFunctionGroupID> coreShapeCFGIDSet){
		//validations
		//not null or empty
		if(coreShapeCFGIDSet == null||coreShapeCFGIDSet.isEmpty()) {
			throw new IllegalArgumentException("coreShapeCFGIDSet cannot be null or empty");
		}
		
		
		this.notes = notes;
		this.coreShapeCFGIDSet = coreShapeCFGIDSet;
	}
	
	public Set<CompositionFunctionGroupID> getCoreShapeCFGIDSet() {
		return coreShapeCFGIDSet;
	}

	public VfNotes getNotes() {
		return notes;
	}

	//////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coreShapeCFGIDSet == null) ? 0 : coreShapeCFGIDSet.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VSComponent))
			return false;
		VSComponent other = (VSComponent) obj;
		if (coreShapeCFGIDSet == null) {
			if (other.coreShapeCFGIDSet != null)
				return false;
		} else if (!coreShapeCFGIDSet.equals(other.coreShapeCFGIDSet))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		return true;
	}
	
	
}
