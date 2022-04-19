/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql.derby.pre;

import java.util.List;

import basic.VfNameString;


/**
 * 
 * @author tanxu
 */
public class SQLStringUtils {
    
    /**
     * test whether the given name is a valid table or attribute name for sql table;
     * cannot contain any conserved symbol/character of sql;
     * 1. can not contain dot '.', ',', '(',')', ...
     * 
     * @param name
     * @return 
     */
    public static boolean isValidTableOrAttributeName(String name){
        return true;
    }
    
    
    /**
     * 
     * @param attList
     * @return 
     */
    public static String createSQLListOfAttributes(List<? extends VfNameString> attList){
        String ret = "";
        for(VfNameString att:attList){
            if(!ret.isEmpty()){
                ret = ret.concat(",");
            }
            ret = ret.concat(att.toString());
        }
        return ret;
    }
    
    
    public static boolean SQLStringIsValid(String sql){
        //todo
        return true;
    }
    
    /**
     * 
     * @param sql
     * @return 
     */
    public static List<String> parseInputTableNameList(String sql){
        //todo
        return null;
    }
    
    
}
