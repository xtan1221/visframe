package rdb.sqltype;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * sql double 
 */
public class SQLDoubleType extends VfDefinedPrimitiveSQLDataType {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2202404291860053886L;
	
	
	//////////////////////////////
	/**
	 * default constructor
	 */
	protected SQLDoubleType() {
		//
	}
	
	
	/**
	 * whether the given source data type can be used as double;
	 */
	@Override
	public boolean isMappableFrom(VfDefinedPrimitiveSQLDataType sourceType) {
		return sourceType.isNumeric();
	}
	

	@Override
	public boolean isValidStringValue(String value) {
		if(value==null)
			return false;
		
		try {
			Double.parseDouble(value);
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}
	
	
	@Override
	public String getSQLString() {
		return "DOUBLE";
	}
	
	@Override
	public boolean isNumeric() {
		return true;
	}
	
	@Override
	public SQLDoubleType reproduce() {
		return this;
	}

	@Override
	public boolean isOfStringType() {
		return false;
	}
	
	
	@Override
	public void setPreparedStatement(PreparedStatement ps, int colIndex, String stringValue) throws SQLException {
		if(stringValue==null) {
			ps.setNull(colIndex, java.sql.Types.DOUBLE);
		}else {
			ps.setDouble(colIndex, Double.parseDouble(stringValue));
		}
		
	}

	@Override
	public Double getDefaultValueObject(String defaultStringValue) {
		return Double.parseDouble(defaultStringValue);
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
		return true;
	}
	
	//////////////////////////////
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SQLDoubleType))
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
		return 23132;
	}

}
