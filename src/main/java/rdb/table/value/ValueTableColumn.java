package rdb.table.value;

import basic.SimpleName;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;
import rdb.table.HasIDTypeRelationalTableColumn;

public class ValueTableColumn extends HasIDTypeRelationalTableColumn{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1110536729102872920L;

	/**
	 * constructor
	 * @param name
	 * @param sqlDataType
	 * @param inPrimaryKey
	 * @param unique
	 * @param notNull
	 * @param defaultStringValue
	 */
	public ValueTableColumn(
			SimpleName name, 
			VfDefinedPrimitiveSQLDataType sqlDataType, 
			boolean inPrimaryKey, 
			Boolean unique,
			Boolean notNull,
			String defaultStringValue
			) {
		super(name, sqlDataType, inPrimaryKey, unique, notNull, defaultStringValue, null); //null for (String additionalConstraints)
	}
	
	@Override
	public SimpleName getName() {
		return (SimpleName) this.name;
	}
}
