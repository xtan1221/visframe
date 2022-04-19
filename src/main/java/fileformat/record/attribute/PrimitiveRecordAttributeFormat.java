package fileformat.record.attribute;

import basic.SimpleName;
import basic.VfNotes;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * primitive type record attribute that is corresponding to a column of the data table;
 * when used to create data table column, all PrimitiveRecordAttribute are (can be null) and (not unique)  ??
 * @author tanxu
 *
 */
public class PrimitiveRecordAttributeFormat extends AbstractRecordAttributeFormat {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6855617470879592055L;
	
	/////////////////
	private final VfDefinedPrimitiveSQLDataType SQLDataType;
	
	/**
	 * constructor
	 * @param name cannot be null
	 * @param notes cannot be null
	 * @param sqlTypeString cannot be null or empty string
	 */
	public PrimitiveRecordAttributeFormat(
			SimpleName name, VfNotes notes,
			VfDefinedPrimitiveSQLDataType SQLDataType
			) {
		super(name, notes);
		
		if(SQLDataType==null) {
			throw new IllegalArgumentException("given SQLDataType cannot be null");
		}
		if(!SQLDataType.isPrimitive()) {
			throw new IllegalArgumentException("given SQLDataType cannot be non-primitive");
		}
		
		this.SQLDataType = SQLDataType;
	}
	
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return SQLDataType;
	}
	
	
	@Override
	public String toString() {
		return "PrimitiveRecordAttributeFormat [SQLDataType=" + SQLDataType + ", getName()=" + getName() + "]";
	}

	
	//////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((SQLDataType == null) ? 0 : SQLDataType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof PrimitiveRecordAttributeFormat))
			return false;
		PrimitiveRecordAttributeFormat other = (PrimitiveRecordAttributeFormat) obj;
		if (SQLDataType == null) {
			if (other.SQLDataType != null)
				return false;
		} else if (!SQLDataType.equals(other.SQLDataType))
			return false;
		return true;
	}


}
