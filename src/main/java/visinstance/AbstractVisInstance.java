package visinstance;

import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;

public abstract class AbstractVisInstance implements VisInstance{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 708553622487286792L;
	
	
	///////////////////////////////////////
	private final int UID;
	private final SimpleName name;
	private final VfNotes notes;
	/**
	 * 
	 */
	private final Set<CompositionFunctionGroupID> coreShapeCFGIDSet;
	/**
	 * see {@link VisInstance#getCoreShapeCFGCFIDSet()}
	 */
	private final Map<CompositionFunctionGroupID, Set<CompositionFunctionID>> coreShapeCFGIDIncludedCFIDSetMap;
	
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param UID
	 * @param coreShapeCFGIDSet
	 * @param coreShapeCFGIDIncludedCFIDSetMap
	 */
	protected AbstractVisInstance(
			SimpleName name, VfNotes notes, int UID, 
			Set<CompositionFunctionGroupID> coreShapeCFGIDSet,
			Map<CompositionFunctionGroupID, Set<CompositionFunctionID>> coreShapeCFGIDIncludedCFIDSetMap){
		if(name == null)
			throw new IllegalArgumentException("given name cannot be null!");
		if(notes == null)
			throw new IllegalArgumentException("given notes cannot be null!");
		
		if(coreShapeCFGIDSet==null||coreShapeCFGIDSet.isEmpty())
			throw new IllegalArgumentException("given coreShapeCFGIDSet cannot be null or empty!");
		
		if(coreShapeCFGIDIncludedCFIDSetMap==null||coreShapeCFGIDIncludedCFIDSetMap.isEmpty())
			throw new IllegalArgumentException("given coreShapeCFGIDIncludedCFIDSetMap cannot be null or empty!");
		
		if(!coreShapeCFGIDSet.equals(coreShapeCFGIDIncludedCFIDSetMap.keySet()))
			throw new IllegalArgumentException("given coreShapeCFGIDSet must be equal to the key set of given coreShapeCFGIDIncludedCFIDSetMap!");
		
		coreShapeCFGIDIncludedCFIDSetMap.values().forEach(v->{
			if(v==null||v.isEmpty())
				throw new IllegalArgumentException("value set of given coreShapeCFGIDIncludedCFIDSetMap cannot be null or empty!");
		});
		
		this.name = name;
		this.notes = notes;
		this.UID = UID;
		this.coreShapeCFGIDSet = coreShapeCFGIDSet;
		this.coreShapeCFGIDIncludedCFIDSetMap = coreShapeCFGIDIncludedCFIDSetMap;
	}
	
	@Override
	public VfNotes getNotes() {
		return notes;
	}
	
	@Override
	public SimpleName getName() {
		return name;
	}
	
	@Override
	public Set<CompositionFunctionGroupID> getCoreShapeCFGIDSet() {
		return coreShapeCFGIDSet;
	}

	@Override
	public Map<CompositionFunctionGroupID, Set<CompositionFunctionID>> getCoreShapeCFGIDIncludedCFIDSetMap(){
		return this.coreShapeCFGIDIncludedCFIDSetMap;
	}
	
	
	@Override
	public int getUID() {
		return UID;
	}
	
	////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + UID;
		result = prime * result
				+ ((coreShapeCFGIDIncludedCFIDSetMap == null) ? 0 : coreShapeCFGIDIncludedCFIDSetMap.hashCode());
		result = prime * result + ((coreShapeCFGIDSet == null) ? 0 : coreShapeCFGIDSet.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractVisInstance))
			return false;
		AbstractVisInstance other = (AbstractVisInstance) obj;
		if (UID != other.UID)
			return false;
		if (coreShapeCFGIDIncludedCFIDSetMap == null) {
			if (other.coreShapeCFGIDIncludedCFIDSetMap != null)
				return false;
		} else if (!coreShapeCFGIDIncludedCFIDSetMap.equals(other.coreShapeCFGIDIncludedCFIDSetMap))
			return false;
		if (coreShapeCFGIDSet == null) {
			if (other.coreShapeCFGIDSet != null)
				return false;
		} else if (!coreShapeCFGIDSet.equals(other.coreShapeCFGIDSet))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		return true;
	}
}
