package generic.graph;

import java.util.Map;

import rdb.table.data.DataTableColumnName;


/**
 * class for an instance of a graph vertex with the string values for a set of ID attributes and additional attributes;
 * 
 * @author tanxu
 *
 */
public class VfGraphVertex {
//	private static int CURRENT_ID;
//	/**
//	 * must be invoked before a new graah is to be built
//	 */
//	public static void reset() {
//		CURRENT_ID = 0;
//	}
//	/**
//	 * invoked after a new VfGraphVertex is created;
//	 */
//	public void setID() {
//		this.id = CURRENT_ID;
//		CURRENT_ID++;
//	}
	
	
	//////////////////////////
	/**
	 * vertex ID attributes that distinguish this vertex from other ones in the same graph;
	 * cannot be null or empty; map values cannot be null;
	 */
	private final Map<DataTableColumnName, String> IDAttributeNameStringValueMap;
	
	/**
	 * cannot be null, can be empty; map value can be null;
	 */
	private final Map<DataTableColumnName, String> additionalAttributeNameStringValueMap;
	
	////////////////////////////////
//	/**
//	 * unique id assigned to each vertex of the same graph to facilitate the UndirectedGraphCycleDetector;
//	 * must be from 0 to (vertex_num - 1)
//	 */
//	private int id;
	
	/**
	 * constructor
	 * @param IDAttributeNameStringValueMap cannot be null or empty; map values cannot be null;
	 * @param additionalAttributeNameStringValueMap cannot be null, can be empty; map value can be null;
	 */
	public VfGraphVertex(Map<DataTableColumnName, String> IDAttributeNameStringValueMap, Map<DataTableColumnName, String> additionalAttributeNameStringValueMap){
//		System.out.println("invoked!!!!!!");
		this.IDAttributeNameStringValueMap = IDAttributeNameStringValueMap;
		this.additionalAttributeNameStringValueMap = additionalAttributeNameStringValueMap;
	}

	
	/**
	 * @return the iDAttributeNameStringValueMap
	 */
	public Map<DataTableColumnName, String> getIDAttributeNameStringValueMap() {
		return IDAttributeNameStringValueMap;
	}


	/**
	 * @return the additionalAttributeNameStringValueMap
	 */
	public Map<DataTableColumnName, String> getAdditionalAttributeNameStringValueMap() {
		return additionalAttributeNameStringValueMap;
	}

	
	@Override
	public String toString() {
		return "VfGraphVertex [IDAttributeNameStringValueMap=" + IDAttributeNameStringValueMap
				+ ", additionalAttributeNameStringValueMap=" + additionalAttributeNameStringValueMap + "]";
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ IDAttributeNameStringValueMap.hashCode(); //
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VfGraphVertex))
			return false;
		VfGraphVertex other = (VfGraphVertex) obj;
		if (IDAttributeNameStringValueMap == null) {
			if (other.IDAttributeNameStringValueMap != null)
				return false;
		} else if (!IDAttributeNameStringValueMap.equals(other.IDAttributeNameStringValueMap))
			return false;
		return true;
	}
	
	//////////////////////////////////////////
//	/**
//	 * @return the id
//	 */
//	public int getId() {
//		return id;
//	}
//
//
//	/**
//	 * @param id the id to set
//	 */
//	public void setId(int id) {
//		this.id = id;
//	}

	
}
