package context.project.rdb;

import java.util.HashSet;
import java.util.Set;

import basic.SimpleName;

/**
 * all constant primary values specifically defined in VisProjectDBContext;
 * 
 * 
 * NOTE that this class should not contain any generic sql constant values;
 * 
 * 
 * @author tanxu
 *
 */
public final class VisProjectRDBConstants {
	/////////rdb schema of VisProjectDBContext
//	/**
//	 * schema for vis project related information such as the notes
//	 */
//	public static final SimpleName PROJECT_INFOR_SCHEMA_NAME = new SimpleName("APP_VF_PROJECT");
	/**
	 * schema for the management system related lookup tables
	 */
	public static final SimpleName MANAGEMENT_SCHEMA_NAME = new SimpleName("APP_VF_MANAGEMENT");
	/**
	 * data tables for record data
	 */
	public static final SimpleName DATA_SCHEMA_NAME = new SimpleName("APP_VF_DATA");
	/**
	 * calculated CF target value tables
	 */
	public static final SimpleName VALUE_SCHEMA_NAME = new SimpleName("APP_VF_VALUE");
	/**
	 * contains temporary Piecewise function index id output index value table and temporary output variable value table for currently calculating CFTargetValueTableRun
	 */
	public static final SimpleName CALCULATION_SCHEMA_NAME = new SimpleName("APP_VF_CALCULATION");
	
	/**
	 * return the full set of schema name defined by VisProject
	 * @return
	 */
	public static Set<SimpleName> getVisProjectSchemaNameSet(){
		Set<SimpleName> ret = new HashSet<>();
		
		ret.add(MANAGEMENT_SCHEMA_NAME);
		ret.add(DATA_SCHEMA_NAME);
		ret.add(VALUE_SCHEMA_NAME);
		ret.add(CALCULATION_SCHEMA_NAME);
		
		return ret;
	}
	
	////////related with all data tables and value tables
	public static final String RUID_COLUMN_NAME_STRING_VALUE = "RUID";
	
	
	
}
