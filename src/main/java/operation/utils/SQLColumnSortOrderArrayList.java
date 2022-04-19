package operation.utils;

import java.util.ArrayList;

import basic.reproduce.SimpleReproducible;
import operation.sql.predefined.utils.SqlSortOrderType;

public class SQLColumnSortOrderArrayList implements SimpleReproducible{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8588533637559358621L;
	
	
	//////////////////////////////
	private final ArrayList<SqlSortOrderType> list;
	
	/**
	 * 
	 * @param list
	 */
	public SQLColumnSortOrderArrayList(ArrayList<SqlSortOrderType> list){
		if(list == null) {
			throw new IllegalArgumentException("given list is null!");
		}
		this.list = list;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<SqlSortOrderType> getList() {
		return list;
	}

	
	/**
	 * 
	 */
	@Override
	public SQLColumnSortOrderArrayList reproduce() {
		ArrayList<SqlSortOrderType> ret = new ArrayList<>();
		this.getList().forEach(e->{
			ret.add(e);
		});
		return new SQLColumnSortOrderArrayList(ret);
	}

	////////////////////////////
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
		if (!(obj instanceof SQLColumnSortOrderArrayList))
			return false;
		SQLColumnSortOrderArrayList other = (SQLColumnSortOrderArrayList) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		return true;
	}

	
}
