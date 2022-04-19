package generic.tree.trim.helper;


import java.io.Serializable;
import java.util.Map;

/**
 * contains a set of children nodes sibling reorder manipulations;
 * 
 * @author tanxu
 *
 */
public final class SiblingReorderPattern implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2266778890905209539L;
	
	//////////////////////////////
	/**
	 * each set element defines a specific reorder of children nodes of a parent node;
	 * for a set element map, the key is the parent node id, which cannot be null;, the value map is from original children node sibling order index to the reordered sibling order index, which cannot be null or empty;
	 */
	private final Map<Integer,Map<Integer, Integer>> parentNodeIDToOriginalSwappedIndexMapMap;
	
	
	/**
	 * constructor
	 * @param parentNodeID
	 * @param parentNodeIDToOriginalSwappedIndexMapMap cannot be null or empty; each element map must be non-null and non-empty;
	 */
	public SiblingReorderPattern(Map<Integer,Map<Integer, Integer>> parentNodeIDToOriginalSwappedIndexMapMap){
		if(parentNodeIDToOriginalSwappedIndexMapMap==null||parentNodeIDToOriginalSwappedIndexMapMap.isEmpty()) {
			throw new IllegalArgumentException("given parentNodeIDToOriginalSwappedIndexMapMap cannot be null or empty!");
		}
		for(int parentNodeID:parentNodeIDToOriginalSwappedIndexMapMap.keySet()) {
			Map<Integer, Integer> map = parentNodeIDToOriginalSwappedIndexMapMap.get(parentNodeID);
			
			if(map==null||map.isEmpty()) {
				throw new IllegalArgumentException("value map of given parentNodeIDToOriginalSwappedIndexMapMap cannot be null or empty!");
			}
			
			for(int originalIndex:map.keySet()) {
				if(!map.containsValue(originalIndex)) {
					throw new IllegalArgumentException("original sibling order index is not found in the reordered ones!");
				}
			}
		}
		
		this.parentNodeIDToOriginalSwappedIndexMapMap = parentNodeIDToOriginalSwappedIndexMapMap;
	}
	

	public Map<Integer,Map<Integer, Integer>> getParentNodeIDToOriginalSwappedIndexMapMap() {
		return parentNodeIDToOriginalSwappedIndexMapMap;
	}

	/////////////////////
	@Override
	public String toString() {
		return "SiblingReorderPattern [parentNodeIDToOriginalSwappedIndexMapMap="
				+ parentNodeIDToOriginalSwappedIndexMapMap + "]";
	}


	////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parentNodeIDToOriginalSwappedIndexMapMap == null) ? 0
				: parentNodeIDToOriginalSwappedIndexMapMap.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SiblingReorderPattern))
			return false;
		SiblingReorderPattern other = (SiblingReorderPattern) obj;
		if (parentNodeIDToOriginalSwappedIndexMapMap == null) {
			if (other.parentNodeIDToOriginalSwappedIndexMapMap != null)
				return false;
		} else if (!parentNodeIDToOriginalSwappedIndexMapMap.equals(other.parentNodeIDToOriginalSwappedIndexMapMap))
			return false;
		return true;
	}
	
	
	
}
