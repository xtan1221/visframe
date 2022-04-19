package rdb.table;

import java.io.Serializable;

import basic.HasName;
import basic.VfNameString;
import rdb.sqltype.SQLDataType;

/**
 * base class for relational table column for all types of relational tables in visframe;
 * 
 * data table;
 * lookup table;
 * value table;
 * 
 * @author tanxu
 * 
 */
public abstract class AbstractRelationalTableColumn implements HasName, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5174524857013970993L;
	//////////////////////////////
	protected final VfNameString name;
	protected final SQLDataType sqlDataType;
	private final boolean inPrimaryKey;
	private final Boolean unique;
	private final Boolean notNull;
	
	/**
	 * non-null default value;
	 * if null, no default non-null value;
	 */
	private final String defaultStringValue;
	
	/**
	 * if null, no additional constraints; if non-null, add the constraints at the end of the sql string of this column;
	 */
	private final String additionalConstraints;
	
	/**
	 * constructor
	 * @param name
	 * @param sqlDataType
	 * @param inPrimaryKey
	 * @param unique
	 * @param notNull
	 */
	public AbstractRelationalTableColumn(
//			RelationalTableSchemaID ownerTableID,
			VfNameString name,
//			VfNotes notes,
			SQLDataType sqlDataType,
			boolean inPrimaryKey,
			Boolean unique,
			Boolean notNull,
			String defaultStringValue,
			
			String additionalConstraints
			){
		//TODO validations
		//if defaultStringValue is not null, check if the string value is valid for the sql data type;
		
		//additionalConstraints cannot be empty string;
		
		
		
		
		this.name = name;
//		this.notes = notes;
		this.sqlDataType =sqlDataType;
		this.inPrimaryKey = inPrimaryKey;
		this.unique = unique;
		this.notNull = notNull;
		this.defaultStringValue = defaultStringValue;
		
		this.additionalConstraints = additionalConstraints;
	}
	
	/**
	 * 
	 */
	public abstract VfNameString getName();

	
	public SQLDataType getSqlDataType() {
		return sqlDataType;
	}
	
	public boolean isInPrimaryKey() {
		return inPrimaryKey;
	}

	public Boolean isUnique() {
		return unique;
	}

	public Boolean isNotNull() {
		return notNull;
	}
	
	public String getDefaultStringValue() {
//		return this.getSqlDataType().getDefaultValueString(this.defaultStringValue);
		return defaultStringValue;
	}
	
	public String getAdditionalConstraints() {
		return additionalConstraints;
	}

	

	

	////////////////////////////
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((additionalConstraints == null) ? 0 : additionalConstraints.hashCode());
		result = prime * result + ((defaultStringValue == null) ? 0 : defaultStringValue.hashCode());
		result = prime * result + (inPrimaryKey ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notNull == null) ? 0 : notNull.hashCode());
		result = prime * result + ((sqlDataType == null) ? 0 : sqlDataType.hashCode());
		result = prime * result + ((unique == null) ? 0 : unique.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractRelationalTableColumn))
			return false;
		AbstractRelationalTableColumn other = (AbstractRelationalTableColumn) obj;
		if (additionalConstraints == null) {
			if (other.additionalConstraints != null)
				return false;
		} else if (!additionalConstraints.equals(other.additionalConstraints))
			return false;
		if (defaultStringValue == null) {
			if (other.defaultStringValue != null)
				return false;
		} else if (!defaultStringValue.equals(other.defaultStringValue))
			return false;
		if (inPrimaryKey != other.inPrimaryKey)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (notNull == null) {
			if (other.notNull != null)
				return false;
		} else if (!notNull.equals(other.notNull))
			return false;
		if (sqlDataType == null) {
			if (other.sqlDataType != null)
				return false;
		} else if (!sqlDataType.equals(other.sqlDataType))
			return false;
		if (unique == null) {
			if (other.unique != null)
				return false;
		} else if (!unique.equals(other.unique))
			return false;
		return true;
	}

	
}
