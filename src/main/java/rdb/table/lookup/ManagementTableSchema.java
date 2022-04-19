package rdb.table.lookup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import basic.SimpleName;
import basic.VfNameString;
import context.project.rdb.VisProjectRDBConstants;
import rdb.table.AbstractRelationalTableColumn;
import rdb.table.AbstractRelationalTableSchema;

public class ManagementTableSchema extends AbstractRelationalTableSchema<ManagementTableColumn>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4495351170705652881L;


	/**
	 * constructor
	 * @param tableName
	 * @param orderedListOfColumn
	 */
	public ManagementTableSchema(
			SimpleName tableName,
			List<ManagementTableColumn> orderedListOfColumn
			) {
		super(VisProjectRDBConstants.MANAGEMENT_SCHEMA_NAME, tableName, orderedListOfColumn);
	}
	
	@Override
	public SimpleName getName() {
		return (SimpleName) tableName;
	}
	

	@Override
	public List<ManagementTableColumn> getOrderedListOfColumn() {
		List<ManagementTableColumn> ret = new ArrayList<>();
		
		for(AbstractRelationalTableColumn column:this.orderedListOfColumn) {
			ret.add((ManagementTableColumn)column);
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
