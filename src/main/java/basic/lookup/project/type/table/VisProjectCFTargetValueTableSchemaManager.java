package basic.lookup.project.type.table;

import basic.lookup.project.type.VisProjectHasIDTypeRelationalTableSchemaManagerBase;
import context.project.VisProjectDBContext;
import rdb.table.value.type.CFTargetValueTableSchema;
import rdb.table.value.type.CFTargetValueTableSchemaID;

public class VisProjectCFTargetValueTableSchemaManager
		extends VisProjectHasIDTypeRelationalTableSchemaManagerBase<CFTargetValueTableSchema, CFTargetValueTableSchemaID> {
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectCFTargetValueTableSchemaManager(VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, CFTargetValueTableSchema.class, CFTargetValueTableSchemaID.class);
	}
	
}
