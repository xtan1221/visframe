package fileformat.graph.gexf;

import java.util.HashMap;
import java.util.Map;

import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.SQLStringType;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * transformation of data type of attributes
 * 		GEXF data type string -> visframe sql data type
 * 		"integer" -> INTEGER
 * 		"long" -> LONG
 * 		"double" and "float" -> DOUBLE
 * 		"boolean" -> BOOLEAN
 * 		other types including "string" and "liststring" and "anyURI" -> VARCHAR[50]
 * @author tanxu
 *
 */
public class GEXFAttributeDataTypeUtils {
	private static Map<String, VfDefinedPrimitiveSQLDataType> GEXFDataTypeStringSQLDataTypeMap;
	private static final VfDefinedPrimitiveSQLDataType OTHER_TYPE = new SQLStringType(50, false);
	
	
	static void initialize() {
		GEXFDataTypeStringSQLDataTypeMap = new HashMap<>();
		GEXFDataTypeStringSQLDataTypeMap.put("integer", SQLDataTypeFactory.integerType());
		GEXFDataTypeStringSQLDataTypeMap.put("long", SQLDataTypeFactory.longIntegerType());
		
		GEXFDataTypeStringSQLDataTypeMap.put("double", SQLDataTypeFactory.doubleType());
		GEXFDataTypeStringSQLDataTypeMap.put("float", SQLDataTypeFactory.doubleType());
		
		GEXFDataTypeStringSQLDataTypeMap.put("boolean", SQLDataTypeFactory.booleanType());
	}
	
	
	/**
	 * return the corresponding SQLDataType of the given GEXFDataTypeString
	 * @param GEXFDataTypeString
	 * @return
	 */
	public static VfDefinedPrimitiveSQLDataType getType(String GEXFDataTypeString) {
		if(GEXFDataTypeStringSQLDataTypeMap == null) {
			initialize();
		}
		
		if(GEXFDataTypeStringSQLDataTypeMap.containsKey(GEXFDataTypeString)) {
			return GEXFDataTypeStringSQLDataTypeMap.get(GEXFDataTypeString);
		}else {
			return OTHER_TYPE;
		}
	}
	
	public static String stringType(){
		return "string";
	}
	
	public static String integerType(){
		return "integer";
	}
	
	public static String dobuleType(){
		return "double";
	}
	
	public static String booleanType(){
		return "boolean";
	}
	
}
