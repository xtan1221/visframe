package rdb.table.value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import basic.SimpleName;
import basic.VfNameString;
import rdb.table.AbstractRelationalTableColumn;
import rdb.table.HasIDTypeRelationalTableSchema;

/**
 * for value tables including:
 * 1. CF target value table;
 * 2. Piecewise index ID output Index value table;
 * 3. temporary output variable value table;
 * 
 * @author tanxu
 *
 */
public abstract class ValueTableSchema extends HasIDTypeRelationalTableSchema<ValueTableColumn> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8126212045182610288L;

	//////////////////////////////////////
	/**
	 * constructor
	 * @param RDBSchemaName
	 * @param tableName
	 * @param orderedListOfColumn
	 */
	protected ValueTableSchema(
			SimpleName RDBSchemaName, ValueTableName tableName,
			List<ValueTableColumn> orderedListOfColumn) {
		super(RDBSchemaName, tableName, orderedListOfColumn);
	}
	
	
	@Override
	public abstract ValueTableSchemaID<? extends ValueTableSchema> getID();
	
	
	@Override
	public ValueTableName getName() {
		return (ValueTableName)this.tableName;
	}
	
	
	@Override
	public List<ValueTableColumn> getOrderedListOfColumn() {
		List<ValueTableColumn> ret = new ArrayList<>();
		
		for(AbstractRelationalTableColumn column:this.orderedListOfColumn) {
			ret.add((ValueTableColumn)column);
		}
		return ret;
	}
	
	
	@Override
	public Set<SimpleName> getPrimaryKeyColumnNameSet() {
		Set<SimpleName> ret = new HashSet<>();
		
		for(AbstractRelationalTableColumn column:this.orderedListOfColumn) {
			if(column.isInPrimaryKey()) {
				ret.add((SimpleName)column.getName());
			}
		}
		return ret;
	}
	
	
	@Override
	public List<SimpleName> getOrderListOfColumnName() {
		List<SimpleName> ret = new ArrayList<>();
		
		for(AbstractRelationalTableColumn column:this.orderedListOfColumn) {
			ret.add((SimpleName)column.getName());
		}
		
		return ret;
	}

	
	@Override
	public int getColumnIndex(VfNameString columnName) {
		if(columnName instanceof SimpleName) {
			return this.getOrderListOfColumnName().indexOf((SimpleName)columnName)+1;
		}else {
			throw new IllegalArgumentException("given columnName is not of SimpleName type");
		}
	}
}
