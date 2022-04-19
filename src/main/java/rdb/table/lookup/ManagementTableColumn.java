package rdb.table.lookup;

import basic.SimpleName;
import rdb.sqltype.SQLDataType;
import rdb.table.AbstractRelationalTableColumn;

public class ManagementTableColumn extends AbstractRelationalTableColumn {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6281311313692395457L;

	/**
	 * constructor
	 * @param name
	 * @param sqlDataType
	 * @param inPrimaryKey
	 * @param unique
	 * @param notNull
	 * @param additionalConstraints
	 * @param defaultStringValue
	 */
	public ManagementTableColumn(
			SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			Boolean notNull, String additionalConstraints, String defaultStringValue
			) {
		super(name, sqlDataType, inPrimaryKey, unique, notNull, additionalConstraints, defaultStringValue);
	}
	
	@Override
	public SimpleName getName() {
		return (SimpleName)this.name;
	}

}
