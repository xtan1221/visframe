package operation.graph.utils;

import java.io.Serializable;
import java.util.HashMap;
import generic.graph.DirectedType;

/**
 * wrapper class for a Map<String,DirectedType>
 * see {@link BuildGraphFromTwoExistingRecordOperation_pre#DIRECTED_INDICATOR_COLUMN_STRING_VALUE_DIRECTED_TYPE_MAP}
 * @author tanxu
 *
 */
public class DirectedColumnIndicatorStringValueDirectedTypeMap implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4471457133794945308L;
	
	//////////////////////////
	private final HashMap<String,DirectedType> map;
	/**
	 * constructor
	 * @param map
	 */
	public DirectedColumnIndicatorStringValueDirectedTypeMap(HashMap<String,DirectedType> map){
		this.map = map;
	}
	
	public HashMap<String,DirectedType> getMap(){
		return map;
	}

	
	//////////////////////////////////
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
		if (!(obj instanceof DirectedColumnIndicatorStringValueDirectedTypeMap))
			return false;
		DirectedColumnIndicatorStringValueDirectedTypeMap other = (DirectedColumnIndicatorStringValueDirectedTypeMap) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}
	
	
}
