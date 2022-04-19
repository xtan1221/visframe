package context.scheme;

import java.io.Serializable;
import java.util.List;

/**
 * class for a precedence list of VSComponents;
 * 
 * also provide a set of utility methods to process the list;
 * 
 * @author tanxu
 *
 */
public final class VSComponentPrecedenceList implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5050072137079335923L;
	
	////////////
	private final List<VSComponent> list;
	
	/**
	 * constructor
	 * 
	 * @param list the list of VSComponent that has been validated so that each VSComponent contains a disjoint set of core ShapeCFG with all mandatory target assigned to a CompositionFunction
	 */
	public VSComponentPrecedenceList(List<VSComponent> list){
		if(list==null || list.isEmpty())
			throw new IllegalArgumentException("given list cannot be null or empty!");
		
		
		this.list = list;
	}

	public List<VSComponent> getList() {
		return list;
	}

	//////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VSComponentPrecedenceList))
			return false;
		VSComponentPrecedenceList other = (VSComponentPrecedenceList) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		return true;
	}
	
}
