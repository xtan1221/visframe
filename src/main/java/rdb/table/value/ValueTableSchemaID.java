package rdb.table.value;

import basic.SimpleName;
import rdb.table.HasIDTypeRelationalTableSchemaID;

public abstract class ValueTableSchemaID<T extends ValueTableSchema> extends HasIDTypeRelationalTableSchemaID<T>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3544326854097113970L;
	
	///////////////////////////////////////////
	private final SimpleName schemaName;
	private final ValueTableName tableName;
	
	
	/**
	 * constructor
	 * @param schemaName
	 * @param tableName
	 */
	public ValueTableSchemaID(SimpleName schemaName, ValueTableName tableName){
		this.schemaName = schemaName;
		this.tableName = tableName;
	}
	
	
	@Override
	public SimpleName getSchemaName() {
		return schemaName;
	}

	
	@Override
	public ValueTableName getTableName() {
		return tableName;
	}

	
	////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((schemaName == null) ? 0 : schemaName.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		ValueTableSchemaID<T> other = (ValueTableSchemaID<T>) obj; //??
		if (schemaName == null) {
			if (other.schemaName != null)
				return false;
		} else if (!schemaName.equals(other.schemaName))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ValueTableSchemaID [tableName=" + tableName + "]";
	}



}
