package basic.reproduce;

import java.util.LinkedHashMap;

public abstract class DataReproducibleKeyValueLinkedHashMap<K extends DataReproducible, V extends DataReproducible> implements DataReproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2034518434890786893L;
	
	/////////////////////////////
	private final LinkedHashMap<K,V> map;
	
	protected DataReproducibleKeyValueLinkedHashMap(LinkedHashMap<K,V> map){
		this.map = map;
	}
	
	public LinkedHashMap<K,V> getMap() {
		return map;
	}

	
	////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DataReproducibleKeyValueLinkedHashMap<?,?>))
			return false;
		DataReproducibleKeyValueLinkedHashMap<?,?> other = (DataReproducibleKeyValueLinkedHashMap<?,?>) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}
	
	
	
}
