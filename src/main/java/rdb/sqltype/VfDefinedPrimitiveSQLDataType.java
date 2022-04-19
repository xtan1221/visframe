package rdb.sqltype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class VfDefinedPrimitiveSQLDataType implements SQLDataType {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2198376277278415557L;
	
	
	///////////////////////////////////
	/**
	 * check and return if the given string value is valid for this VfDefinedSQLPrimitiveType;
	 * null value are always invalid;
	 * @param value
	 * @return
	 */
	public abstract boolean isValidStringValue(String value);
	
	/**
	 * whether or not the given sourceType can be mapped to this VfDefinedPrimitiveSQLDataType;
	 * 
	 * specifically used in metadata mapping of selected solution set in VisScheme applying to help check if the source metadata's data table column's data type is valid;
	 * 
	 * @param sourceType
	 * @return
	 */
	public abstract boolean isMappableFrom(VfDefinedPrimitiveSQLDataType sourceType);
	
	
	
	@Override
	public boolean isPrimitive() {
		return true;
	}
	
	
	
	@Override
	public abstract VfDefinedPrimitiveSQLDataType reproduce();
	
	
	
	/**
	 * @throws SQLException 
	 * 
	 */
	@Override
	public abstract void setPreparedStatement(PreparedStatement ps, int colIndex, String stringValue) throws SQLException;
	
	
	@Override
	public String getStringValue(ResultSet rs, String colName) throws SQLException {
		Object objectValue = rs.getObject(colName);
		
		if(objectValue==null) {
			return null;
		}else {
			return objectValue.toString();
		}
	}
	
	/////////////////////////////
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VfDefinedPrimitiveSQLDataType))
			return false;
		VfDefinedPrimitiveSQLDataType other = (VfDefinedPrimitiveSQLDataType) obj;
		
		//both are double
		if(this.isDouble() && other.isDouble()) {
			return true;
		}
		
		//both are boolean
		if(this.isBoolean() && other.isBoolean()) {
			return true;
		}
		
		//for integer type and string type, override this method in SQLIntegerType and SQLStringType classes;
		
		
		return false;
	}

	
}
