package rdb.sqltype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import basic.SimpleName;


public class UDTDataType implements SQLDataType{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6219600551136206311L;
	
	//////////////////////////
	private final SimpleName schemaName;
	private final String udtTypeString;
	
	/**
	 * constructor
	 * @param schemaName
	 * @param udtTypeString
	 */
	public UDTDataType(SimpleName schemaName, String udtTypeString){
		this.schemaName = schemaName;
		this.udtTypeString = udtTypeString;
	}
	
	
	public SimpleName getSchemaName() {
		return schemaName;
	}
	
	public String getUdtTypeString() {
		return udtTypeString;
	}

	
	/////////////////////////////////////
	@Override
	public String getSQLString() {
		return this.getSchemaName().getStringValue().concat(".").concat(this.getUdtTypeString());
	}
	
	@Override
	public boolean isNumeric() {
		return false;
	}

	@Override
	public boolean isOfStringType() {
		return false;
	}
	
	@Override
	public UDTDataType reproduce() {
		return new UDTDataType(this.getSchemaName(),this.getUdtTypeString());
	}


	@Override
	public boolean isPrimitive() {
		return false;
	}
	
	/**
	 * 
	 */
	@Override
	public void setPreparedStatement(PreparedStatement ps, int colIndex, String stringValue) throws SQLException {
		throw new UnsupportedOperationException("do not use this method for UDT type column, use the PreparedStatement.setObject(...) method instead");
	}


	@Override
	public Object getDefaultValueObject(String defaultStringValue) {
		throw new UnsupportedOperationException();
	}


	@Override
	public String getStringValue(ResultSet rs, String colName) {
		throw new UnsupportedOperationException();
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
	
}
