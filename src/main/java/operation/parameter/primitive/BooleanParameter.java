package operation.parameter.primitive;

import basic.SimpleName;
import basic.VfNotes;
import operation.parameter.AbstractParameter;

/**
 * boolean type and {@link #isInputDataTableContentDependent()} can be either true or false;
 * @author tanxu
 *
 */
public class BooleanParameter extends AbstractParameter<Boolean> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3078108588694709642L;
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param descriptiveName
	 * @param mandatory
	 * @param defaultValue
	 * @param inputDataTableContentDependent
	 */
	public BooleanParameter(
			SimpleName name, VfNotes notes, String descriptiveName, boolean mandatory,
			Boolean defaultValue, 
			boolean inputDataTableContentDependent) {
		super(Boolean.class, name, notes, descriptiveName, 
				mandatory, defaultValue, null, //no additional constraints
				inputDataTableContentDependent);
		// TODO Auto-generated constructor stub
	}
	
}
