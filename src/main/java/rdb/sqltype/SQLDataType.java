package rdb.sqltype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import basic.reproduce.SimpleReproducible;

public interface SQLDataType extends SimpleReproducible{
	String getSQLString();
	
	boolean isNumeric();
	boolean isGenericInt();
	
	boolean isOfStringType();
	
	boolean isPrimitive();
	
	boolean isBoolean();
	boolean isDouble();
	
//	/**
//	 * return the default value string of this SQLDataType with the given plain string;
//	 * the return the string will be used to create sql string to create the table schema;
//	 * 
//	 * this must be overridden by subtype SQLDataType 
//	 * @param plainString
//	 * @return
//	 */
//	default String getDefaultValueString(String plainString) {
//		return plainString;
//	}
	
	
	/**
	 * downcast the returned type
	 */
	@Override
	SQLDataType reproduce();
	
	/**
	 * 
	 * @param ps
	 * @param colIndex
	 * @param stringValue
	 * @throws SQLException
	 */
	void setPreparedStatement(PreparedStatement ps, int colIndex, String stringValue) throws SQLException;
	
	/**
	 * return the string value of the given column name in the current record of the given ResultSet;
	 * return null if the value is null;
	 * @param rs
	 * @param colName
	 * @return
	 * @throws SQLException 
	 */
	String getStringValue(ResultSet rs, String colName) throws SQLException;
	
	
	
	Object getDefaultValueObject(String defaultStringValue);
	
	//implement equals() and hashCode method in each final sub class
	//TODO
}
