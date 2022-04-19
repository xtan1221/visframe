package basic;

import basic.reproduce.SimpleReproducible;
import rdb.table.data.DataTableColumnName;

/**
 * the string is directly cloned when implementing the reproduce() method;
 * 
 * used to represent names of entity that are not to be modified by the visscheme related applier and the copy index;
 * 
 * for example, data table column name, Operation's Paramter name, Variable's alias name of composition function evaluators
 * 
 * @author tanxu
 */
public class SimpleName extends VfNameString implements SimpleReproducible{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8308384033922538083L;
	
	//////////////////////////
	/**
	 * constructor
	 * @param stringValue
	 */
	public SimpleName(String stringValue) {
		super(stringValue);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * make a new DataTableColumnName based on this SimpleName
	 * @return
	 */
	public DataTableColumnName toDataTableColumnName() {
		return new DataTableColumnName(this.getStringValue());
	}
	
	/**
	 * reproduce and return a new SimpleName of this one;
	 * simply return a new SimpleName with the same string value of this one;
	 * or return the original SimpleName
	 */
	@Override
	public SimpleName reproduce() {
		return this;
	}
	
}
