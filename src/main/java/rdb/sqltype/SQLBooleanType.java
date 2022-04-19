package rdb.sqltype;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLBooleanType extends VfDefinedPrimitiveSQLDataType {
	/**
	 * 
	 */
	private static final long serialVersionUID = -859575720216083422L;

	/**
	 * default constructor
	 */
	protected SQLBooleanType() {
		//
	}
	
	@Override
	public boolean isValidStringValue(String value) {
		if(value==null)
			return false;
		return value.equalsIgnoreCase("TRUE")||value.equalsIgnoreCase("FALSE");
	}
	
	
	@Override
	public String getSQLString() {
		return "BOOLEAN";
	}
	
	@Override
	public boolean isNumeric() {
		return false;
	}


	/**
	 * return true only if the given sourceType is also SQLBooleanType;
	 */
	@Override
	public boolean isMappableFrom(VfDefinedPrimitiveSQLDataType sourceType) {
		return sourceType instanceof SQLBooleanType;
	}
	
	
	/**
	 * directly return this SQLBooleanType since it is logically singleton
	 */
	@Override
	public SQLBooleanType reproduce() {
		return this;
	}

	
	@Override
	public boolean isOfStringType() {
		return false;
	}
	
//	@Override
//	public String getDefaultValueString(String plainString) {
//		if(plainString.equalsIgnoreCase("true")) {
//			return "1";
//		}else if(plainString.equalsIgnoreCase("false")){
//			return "0";
//		}else {
//			throw new IllegalArgumentException("unrecognized default value string for SQLBooleanType:"+plainString);
//		}
//	}
	

	/**
	 * given value object is a string
	 */
	@Override
	public void setPreparedStatement(PreparedStatement ps, int colIndex, String stringValue) throws SQLException {
		if(stringValue==null) {
			ps.setNull(colIndex, java.sql.Types.BOOLEAN);
		}else {
			ps.setBoolean(colIndex, Boolean.parseBoolean(stringValue));
		}
		
	}

	@Override
	public Boolean getDefaultValueObject(String defaultStringValue) {
		return Boolean.parseBoolean(defaultStringValue);
	}
	
	@Override
	public boolean isGenericInt() {
		return false;
	}

	@Override
	public boolean isBoolean() {
		return true;
	}

	@Override
	public boolean isDouble() {
		return false;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SQLBooleanType))
			return false;
		
		return true;
//		SQLDoubleType other = (SQLDoubleType) obj;
//		
//		//both are double
//		if(this.isDouble() && other.isDouble()) {
//			return true;
//		}
//		
//		return false;
	}
	
	@Override
	public int hashCode() {
		return 3267;
	}

}
