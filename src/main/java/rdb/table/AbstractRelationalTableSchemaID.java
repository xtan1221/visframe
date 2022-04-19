package rdb.table;

import basic.SimpleName;
import basic.VfNameString;

/**
 * abstract class for ID of a AbstractRelationalTableSchema
 * @author tanxu
 *
 */
public abstract class AbstractRelationalTableSchemaID{
	public abstract SimpleName getSchemaName();
	public abstract VfNameString getTableName();
}
