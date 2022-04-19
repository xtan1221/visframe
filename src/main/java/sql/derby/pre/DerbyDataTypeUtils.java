/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql.derby.pre;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

import sql.derby.DerbyDBUtils;

/**
 *
 * @author tanxu
 */
public class DerbyDataTypeUtils {
    //sql varchar, char ===> java string
    //sql BOOLEAN  ====> java boolean
    //int, integer ====> java integer
    //sql DOUBLE ===> java double
    //sql REAL ===> java float
    //sql BIGINT ===> java long
    //sql SMALLINT??TINYINT ===> java short
    //sql NUMERIC ===> java BigDecimal
    //sql NULL
    //sql TINYINT ====> java byte
    /**
     * set the parameter value at the index location of the given PreparedStatement to the given value string by using the corresponding PreparedStatement setter methods;
     * The setter methods (setShort, setString, and so on) for setting attribute value must specify types that are compatible with the defined SQL type of the input parameter.the data type should be given by using a standard SQL data type such as VARCHAR(100), INTEGER;
 
 if both SQLDataType and jdbcTypeConstant are given, use the SQLDataType;
     * @param ps
     * @param SQLDataType
     * @param index 
     * @param value 
     * @param isJavaObjectUDT whether or not the data type is java class based UDT
     */
    public static void setPreparedStatementParameterValueForPrimitiveTypeAttribute(PreparedStatement ps, int index, String SQLDataType, Object value, boolean isJavaObjectUDT){
        
        if(SQLDataType!=null){
            if(isJavaObjectUDT){
                try {
                    ps.setObject(index, value);
                    return;
                } catch (SQLException ex) {
                    Logger.getLogger(DerbyDataTypeUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
            String valueString = (String) value;
            try {
                if(isJavaString(SQLDataType)){
                    ps.setString(index, valueString);
                }else if(isJavaBoolean(SQLDataType)){
                    ps.setBoolean(index, Boolean.parseBoolean(valueString));
                }else if(isJavaInteger(SQLDataType)){
                    ps.setInt(index, Integer.parseInt(valueString));
                }else if(isJavaDouble(SQLDataType)){
                    ps.setDouble(index, Double.parseDouble(valueString));
                }else if(isJavaFloat(SQLDataType)){
                    ps.setFloat(index, Float.parseFloat(valueString));
                }else if(isJavaByte(SQLDataType)){
                    ps.setByte(index, Byte.parseByte(valueString));
                }else if(isJavaBigDecimal(SQLDataType)){
                    ps.setBigDecimal(index, new BigDecimal(valueString));
                }else if(isJavaBigInteger(SQLDataType)){
                    ps.setLong(index, Long.parseLong(valueString));

                }else{
                    throw new IllegalArgumentException("given SQLdataType not recognized:"+SQLDataType);
                }


            } catch (SQLException ex) {
                Logger.getLogger(DerbyDataTypeUtils.class.getName()).log(Level.SEVERE, null, ex);
                DerbyDBUtils.printSQLException(ex);
            }
        
        }else{
            throw new IllegalArgumentException("no data type indicator is given in either string value or constaint integer value");
        }
    }
    
    
    /**
     * set the parameter as SQL null value; 
     * @param ps
     * @param SQLdataType
     * @param index 
     * @param isJavaObjectUDTType 
     */
    public static void setPreparedStatementParameterAsNullValue(PreparedStatement ps, int index, String SQLdataType, boolean isJavaObjectUDTType){
        try {
            ps.setNull(index, getJDBCDataTypeIntegerConstantFromSQLTypeString(SQLdataType, isJavaObjectUDTType));
        } catch (SQLException ex) {
            Logger.getLogger(DerbyDataTypeUtils.class.getName()).log(Level.SEVERE, null, ex);
            DerbyDBUtils.printSQLException(ex);
        }
    }
    
    
    /**
     * return the integer constants in class Types that represents each type of data in sql, used to identify generic SQL types, called JDBC types.note that this method is only tested for primitive data types;
        for UDT type, the integer constant is 2000
        https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.sql.Types.VARCHAR
     * @param SQLDataType
     * @param isJavaObjectUDTType whether or not the type is udt
     * @return the constant of the given type, or null if the given type is not recognized;
     */
    public static Integer getJDBCDataTypeIntegerConstantFromSQLTypeString(String SQLDataType, boolean isJavaObjectUDTType){
        if(isJavaObjectUDTType){
            return 2000;
        }
        Integer ret = null;
        for(Field field:Types.class.getDeclaredFields()){
            String fieldName = field.getName();
            
            if (Modifier.isStatic(field.getModifiers())&&Modifier.isFinal(field.getModifiers())){ //static final fields of Types class
                if(SQLDataType.toUpperCase().startsWith(fieldName)){ //this is for char/varchar and cases like this
                    try {
                        ret = (Integer)field.get(null);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        Logger.getLogger(DerbyDataTypeUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if(fieldName.startsWith(SQLDataType.toUpperCase())){//this is for data type = INT, the builtin filed name in Types is INTEGER
                    try {
                        ret = (Integer)field.get(null);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        Logger.getLogger(DerbyDataTypeUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
//                try {
//                    System.out.println(field.getName()+"=="+field.get(null));
//                } catch (IllegalArgumentException ex) {
//                    Logger.getLogger(DerbyDataTypeUtils.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (IllegalAccessException ex) {
//                    Logger.getLogger(DerbyDataTypeUtils.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }
        
        return ret;
    }
    
    /**
     * return the SQL Data Type String corresponding to the given integer constants in jdbc;
     * https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.sql.Types.VARCHAR
     * @param inputInt
     * @return the SQL Data Type String corresponding to the given integer constants in jdbc, null if the given integer is not a jdbc defined constant for sql type
     */
    public static String getSQLDataTypeStringFromJDBCIntegerConstant(Integer inputInt){
        String ret = null;
        for(Field field:Types.class.getDeclaredFields()){
            String fieldName = field.getName();
            
            if (Modifier.isStatic(field.getModifiers())&&Modifier.isFinal(field.getModifiers())){
                try {
                    int jdbcInt = (int) field.get(null);
                    if(jdbcInt == inputInt){
                        ret = fieldName;
                        break;
                    }
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(DerbyDataTypeUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
        
        if(ret.equalsIgnoreCase("JAVA_OBJECT")){
            System.out.println("warning: JAVA_OBJECT is found");
        }
        return ret;
    }
    
    
    
    /**
     * if true, use PreparedStatement.setString() method and the parameter should be String;
     * ???? how about CHAR??
     * @param SQLDataType
     * @return 
     */
    public static boolean isJavaString(String SQLDataType){
        return SQLDataType.contains("VARCHAR")||SQLDataType.contains("CHAR")||SQLDataType.contains("LONGVARCHAR");
    }
    
    
    /**
     * if true, use PreparedStatement.setBoolean() method and the parameter should be boolean;
     * @param SQLDataType
     * @return 
     */
    public static boolean isJavaBoolean(String SQLDataType){
        return SQLDataType.equalsIgnoreCase("BOOLEAN")||SQLDataType.equalsIgnoreCase("BIT");
    }
    
    
    /**
     * if true, use PreparedStatement.setInt() method and the parameter should be int;
     * @param SQLDataType
     * @return 
     */
    public static boolean isJavaInteger(String SQLDataType){
        return SQLDataType.toUpperCase().startsWith("INT");
    }
    
    /**
     * if true, use PreparedStatement.setDouble() method and the parameter should be double;
     * @param SQLDataType
     * @return 
     */
    public static boolean isJavaDouble(String SQLDataType){
        return SQLDataType.equalsIgnoreCase("DOUBLE");
    }
    
    /**
     * if true, use PreparedStatement.setFloat() method and the parameter should be float;
     * @param SQLDataType
     * @return 
     */
    public static boolean isJavaFloat(String SQLDataType){
        return SQLDataType.equalsIgnoreCase("REAL");
    }
    
    /**
     * if true, use PreparedStatement.setByte() method and the parameter should be byte;
     * @param SQLDataType
     * @return 
     */
    public static boolean isJavaByte(String SQLDataType){
        return SQLDataType.equalsIgnoreCase("TINYINT");
    }
    
    
    /**
     * if true, use PreparedStatement.setBigDecimal() method and the parameter should be math.BigDecimal;
     * @param SQLDataType
     * @return 
     */
    public static boolean isJavaBigDecimal(String SQLDataType){
        return SQLDataType.equalsIgnoreCase("NUMERIC");
    }
    
    /**
     * if true, use PreparedStatement.setLong() method and the parameter should be long;
     * @param SQLDataType
     * @return 
     */
    public static boolean isJavaBigInteger(String SQLDataType){
        return SQLDataType.equalsIgnoreCase("BIGINT");
    }
    
    
    
    /**
     * check if the given sql data type string is numeric;
     * 
     * @param sqlDataType
     * @return 
     */
    public static boolean isNumericType(String sqlDataType){
        return isJavaInteger(sqlDataType)||isJavaDouble(sqlDataType)||isJavaFloat(sqlDataType)||isJavaBigDecimal(sqlDataType)||isJavaBigInteger(sqlDataType);
        
    }
    
    
    
    
    
    
    
    public static boolean isValidSQLType(String sqlDataType){
        
        //todo
        return true;
    }
    
    
    
    public static void main(String[] args){
        System.out.println(getJDBCDataTypeIntegerConstantFromSQLTypeString("int", false));
    }
}
