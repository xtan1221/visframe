package rdb.table;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import basic.HasName;
import basic.SimpleName;
import basic.VfNameString;

/**
 * base class for a relational table schema in visframe;
 * @author tanxu
 *
 */
public abstract class AbstractRelationalTableSchema<C extends AbstractRelationalTableColumn> implements HasName, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6452237172238060786L;
	
	///////////////////////////////
	private final SimpleName RDBSchemaName;
	protected final VfNameString tableName;
	protected final List<C> orderedListOfColumn;
	
	/**
	 * constructor
	 * @param RDBSchemaName
	 * @param tableName
	 * @param orderedListOfColumn
	 */
	protected AbstractRelationalTableSchema(
			SimpleName RDBSchemaName,
			VfNameString tableName,
			List<C> orderedListOfColumn
			){
		
		//check if there are multiple columns with the same name;
		Set<VfNameString> colNameSet = new HashSet<>();
		for(C col:orderedListOfColumn) {
			if(colNameSet.contains(col.getName())) {
				throw new IllegalArgumentException("multiple columns with the same name found in given orderedListOfColumn:"+col.getName().getStringValue());
			}
			colNameSet.add(col.getName());
		}
		
		
		
		
		this.tableName = tableName;
		this.RDBSchemaName = RDBSchemaName;
		this.orderedListOfColumn = orderedListOfColumn;
	}
	
	
	
	public SimpleName getSchemaName() {
		return this.RDBSchemaName;
	}

	/**
	 * return the index of the column with the given columnName, consistent with java SQL column index;
	 * note that the first parameter is 1, the second is 2, ... 
	 * 
	 * @param columnName
	 * @return
	 */
	public abstract int getColumnIndex(VfNameString columnName);
//	{
//		return this.getOrderListOfColumnName().indexOf(columnName)+1;
//	}
	
//	public abstract AbstractRelationalTableSchemaID getID();
	public abstract List<C> getOrderedListOfColumn();
	public abstract Set<? extends VfNameString> getPrimaryKeyColumnNameSet();
	public abstract List<? extends VfNameString> getOrderListOfColumnName();



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((RDBSchemaName == null) ? 0 : RDBSchemaName.hashCode());
		result = prime * result + ((orderedListOfColumn == null) ? 0 : orderedListOfColumn.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractRelationalTableSchema))
			return false;
		AbstractRelationalTableSchema<?> other = (AbstractRelationalTableSchema<?>) obj;
		if (RDBSchemaName == null) {
			if (other.RDBSchemaName != null)
				return false;
		} else if (!RDBSchemaName.equals(other.RDBSchemaName))
			return false;
		if (orderedListOfColumn == null) {
			if (other.orderedListOfColumn != null)
				return false;
		} else if (!orderedListOfColumn.equals(other.orderedListOfColumn))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}

	

	//////////////////////////////////
	
	
}
