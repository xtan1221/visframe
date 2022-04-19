package rdb.table;

import basic.VfNameString;
import rdb.sqltype.SQLDataType;

/**
 * base class for column of HasIDTypeRelationalTableSchema;
 * 
 * note that all columns are of primitive SQL type (and not UDT type);
 * 
 * @author tanxu
 *
 */
public abstract class HasIDTypeRelationalTableColumn extends AbstractRelationalTableColumn{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4236008447765838945L;

	
	public HasIDTypeRelationalTableColumn(
			VfNameString name, SQLDataType sqlDataType, boolean inPrimaryKey,
			Boolean unique, Boolean notNull, String defaultStringValue, String additionalConstraints) {
		super(name, sqlDataType, inPrimaryKey, unique, notNull, defaultStringValue, additionalConstraints);
		// TODO Auto-generated constructor stub
	}

}
