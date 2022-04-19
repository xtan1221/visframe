package basic.reproduce;

import java.util.LinkedHashSet;

public abstract class DataReproducibleLinkedHashSet<E extends DataReproducible> implements DataReproducible {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4974043828029955321L;
	
	///////////////////////
	private final LinkedHashSet<E> set;
	/**
	 * constructor
	 * @param set
	 */
	protected DataReproducibleLinkedHashSet(LinkedHashSet<E> set){
		this.set = set;
	}

	public LinkedHashSet<E> getSet() {
		return set;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((set == null) ? 0 : set.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DataReproducibleLinkedHashSet<?>))
			return false;
		DataReproducibleLinkedHashSet<?> other = (DataReproducibleLinkedHashSet<?>) obj;
		if (set == null) {
			if (other.set != null)
				return false;
		} else if (!set.equals(other.set))
			return false;
		return true;
	}
	
	
	
}
