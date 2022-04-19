package rdb.sqltype;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * sql string type either by CHAR or VARCHAR
 * @author tanxu
 *
 */
public class SQLStringType extends VfDefinedPrimitiveSQLDataType {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3112989805156641716L;
	
	
	//////////////////////////
	public static final Integer DEFAULT_VF_STRING_LENGTH = 100;
	
	
	//if null, use the default length?
	private final Integer maxLength;
	private final boolean autoPadding;
	
	
	/**
	 * constructor
	 * @param maxLength
	 */
	public SQLStringType(Integer maxLength,boolean autoPadding) {
		this.maxLength = maxLength;
		this.autoPadding = autoPadding;
	}
	

	/**
	 * return whether the given sourceType can be used as this SQLStringType;
	 * return whether the given sourceType is of SQLStringType and has equal or smaller max length than this one;
	 */
	@Override
	public boolean isMappableFrom(VfDefinedPrimitiveSQLDataType sourceType) {
		if(sourceType instanceof SQLStringType) {
			SQLStringType sourceStringType = (SQLStringType)sourceType;
			return sourceStringType.getMaxLength()<=this.getMaxLength();
		}else {
			return false;
		}
	}
	
	
	@Override
	public boolean isValidStringValue(String value) {
		if(value==null)
			return false;
		
		return value.length()<=this.maxLength;
	}
	
	public Integer getMaxLength() {
		return maxLength==null?DEFAULT_VF_STRING_LENGTH:maxLength;
	}
	
	public boolean isAutoPadding() {
		return autoPadding;
	}

	
	//maximum length for a VARCHAR string is 32,672 characters; no auto padding if data string is shorter than length
	//default length for a CHAR is 1, and the maximum size of length is 254; auto padding of spaces if data string shorter than length
	//
	@Override
	public String getSQLString() {
		 if(autoPadding) {
			 if(this.maxLength!=null) {
				 if(this.maxLength<=254 && this.maxLength>0) {
					 return "CHAR(".concat(this.maxLength.toString()).concat(")");
				 }
			 }
			 
			 return "CHAR(".concat(DEFAULT_VF_STRING_LENGTH.toString()).concat(")");
			 
		 }else {
			 if(this.maxLength!=null) {
				 if(this.maxLength<=32672 && this.maxLength>0) {
					 return "VARCHAR(".concat(this.maxLength.toString()).concat(")");
				 }
			 }
			 
			 return "VARCHAR(".concat(DEFAULT_VF_STRING_LENGTH.toString()).concat(")");
			 
		 }
	}
	
	int getSqlTypeIndicator() {
		if(this.getSQLString().startsWith("CHAR")) {
			return java.sql.Types.CHAR;
		}else {
			return java.sql.Types.VARCHAR;
		}
	}
	
	@Override
	public boolean isNumeric() {
		return false;
	}
	
	
	@Override
	public SQLStringType reproduce() {
		return this;
	}

	@Override
	public boolean isOfStringType() {
		return true;
	}
	
	@Override
	public void setPreparedStatement(PreparedStatement ps, int colIndex, String stringValue) throws SQLException {
		if(stringValue==null) {
			ps.setNull(colIndex, getSqlTypeIndicator());
		}else {
			ps.setString(colIndex, stringValue);
		}
		
		
	}

	@Override
	public String getDefaultValueObject(String defaultStringValue) {
		return defaultStringValue;
	}
	
	@Override
	public boolean isGenericInt() {
		return false;
	}


	@Override
	public boolean isBoolean() {
		return false;
	}

	@Override
	public boolean isDouble() {
		return false;
	}

	
	
	@Override
	public String toString() {
		return "SQLStringType [length=" + maxLength + ", autoPadding=" + autoPadding + "]";
	}

	//////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (autoPadding ? 1231 : 1237);
		result = prime * result + ((maxLength == null) ? 0 : maxLength.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SQLStringType))
			return false;
		SQLStringType other = (SQLStringType) obj;
		if (autoPadding != other.autoPadding)
			return false;
		if (maxLength == null) {
			if (other.maxLength != null)
				return false;
		} else if (!maxLength.equals(other.maxLength))
			return false;
		return true;
	}


}
