package rdb.sqltype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * container class for a customized sql data type string that is not checked by visframe;
 * 
 * must be of primitive type
 * 
 * @author tanxu
 *
 */
public class UncheckedPrimitiveSQLDataType implements SQLDataType{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5017186443725326151L;
	
	///////////////////////
	private final String sqlTypeString; //cannot be null or empty;
	private final boolean numeric;
	private final boolean integer;
	private final boolean ofStringType;
	private final boolean booleanType;
	private final boolean doubleType;
	
	/**
	 * constructor
	 * @param sqlTypeString
	 * @param numeric
	 * @param integer is of integer type or not
	 * @param ofStringType
	 */
	public UncheckedPrimitiveSQLDataType(String sqlTypeString, boolean numeric, boolean integer, boolean ofStringType,
			boolean booleanType, boolean doubleType) {
		this.sqlTypeString = sqlTypeString;
		this.numeric = numeric;
		this.integer = integer;
		this.ofStringType = ofStringType;
		this.booleanType = booleanType;
		this.doubleType = doubleType;
	}
	
	
	@Override
	public String getSQLString() {
		return this.sqlTypeString;
	}
	

	@Override
	public boolean isNumeric() {
		return numeric;
	}

	@Override
	public boolean isOfStringType() {
		return ofStringType;
	}
	

	
	
	/**
	 * reproduce and return a new UncheckedSQLDataType of this one;
	 */
	@Override
	public UncheckedPrimitiveSQLDataType reproduce() {
		return new UncheckedPrimitiveSQLDataType(this.sqlTypeString, this.numeric, this.integer, this.ofStringType, this.booleanType, this.doubleType);
	}

	
	@Override
	public boolean isPrimitive() {
		return true;
	}


	
	@Override
	public void setPreparedStatement(PreparedStatement ps, int colIndex, String stringValue) throws SQLException {
		throw new UnsupportedOperationException();
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
		return this.integer;
	}


	@Override
	public boolean isBoolean() {
		return this.booleanType;
	}


	@Override
	public boolean isDouble() {
		return this.doubleType;
	}

	
}
