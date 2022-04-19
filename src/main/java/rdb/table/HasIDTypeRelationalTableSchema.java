package rdb.table;

import java.util.ArrayList;
import java.util.List;

import basic.SimpleName;
import basic.VfNameString;
import basic.lookup.HasID;
import context.project.rdb.VisProjectRDBConstants;

/**
 * base class for a table schema in visframe with a RUID column;
 * 
 * 
 * no VF UDT based columns;
 * all columns are of primitive SQL type;
 * 
 * @author tanxu
 *
 */
public abstract class HasIDTypeRelationalTableSchema<C extends HasIDTypeRelationalTableColumn> extends AbstractRelationalTableSchema<C> implements HasID{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2291943135955169578L;

	
	/**
	 * constructor
	 * @param RDBSchemaName
	 * @param tableName
	 * @param orderedListOfColumn
	 */
	protected HasIDTypeRelationalTableSchema(SimpleName RDBSchemaName, VfNameString tableName,
			List<C> orderedListOfColumn) {
		super(RDBSchemaName, tableName, orderedListOfColumn);
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public abstract HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema<C>> getID();
	
	/**
	 * 
	 * @return
	 */
	public List<C> getOrderedListOfNonRUIDColumn() {
		List<C> ret = new ArrayList<>();
		
		for(C column:this.orderedListOfColumn) {
			if(column.getName().getStringValue().equalsIgnoreCase(VisProjectRDBConstants.RUID_COLUMN_NAME_STRING_VALUE)) {
				continue;
			}
			ret.add(column);
		}
		return ret;
	}
	
	
}
