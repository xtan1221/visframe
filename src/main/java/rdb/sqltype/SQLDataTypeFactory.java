package rdb.sqltype;

import rdb.sqltype.SQLIntegerType.IntegerType;

/**
 * holds singleton object for VfDefinedSQLPrimitiveTypes except for SQLStringType 
 * @author tanxu
 *
 */
public class SQLDataTypeFactory {
	private static SQLBooleanType BOOLEAN_TYPE;
	private static SQLDoubleType DOUBLE_TYPE;
	
	private static SQLIntegerType INTEGER_TYPE;
	private static SQLIntegerType SHORT_INTEGER_TYPE;
	private static SQLIntegerType LONG_INTEGER_TYPE;
	
	public static SQLBooleanType booleanType() {
		if(BOOLEAN_TYPE==null) {
			BOOLEAN_TYPE = new SQLBooleanType();
		}
		return BOOLEAN_TYPE;
	}
	
	public static SQLDoubleType doubleType() {
		if(DOUBLE_TYPE==null) {
			DOUBLE_TYPE = new SQLDoubleType();
		}
		return DOUBLE_TYPE;
	}
	
	public static SQLIntegerType integerType() {
		if(INTEGER_TYPE==null) {
			INTEGER_TYPE = new SQLIntegerType(IntegerType.INT);
		}
		return INTEGER_TYPE;
	}
	public static SQLIntegerType shortIntegerType() {
		if(SHORT_INTEGER_TYPE==null) {
			SHORT_INTEGER_TYPE = new SQLIntegerType(IntegerType.SHORT);
		}
		return SHORT_INTEGER_TYPE;
	}
	public static SQLIntegerType longIntegerType() {
		if(LONG_INTEGER_TYPE==null) {
			LONG_INTEGER_TYPE = new SQLIntegerType(IntegerType.LONG);
		}
		return LONG_INTEGER_TYPE;
	}
	
	
	public static SQLIntegerType genericIntegerType(IntegerType type) {
		if(type == IntegerType.INT) {
			return integerType();
		}else if(type == IntegerType.LONG) {
			return longIntegerType();
		}else if(type == IntegerType.SHORT) {
			return shortIntegerType();
		}else {
			throw new IllegalArgumentException("unrecognized IntegerType");
		}
	}
	

	
}
