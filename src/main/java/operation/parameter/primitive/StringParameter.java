package operation.parameter.primitive;

import java.util.function.Predicate;

import basic.SimpleName;
import basic.VfNotes;
import operation.parameter.AbstractParameter;

/**
 * string type and {@link #isInputDataTableContentDependent()} can be either true or false;
 * @author tanxu
 *
 */
public class StringParameter extends AbstractParameter<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2890540241749488538L;
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param descriptiveName
	 * @param mandatory
	 * @param defaultValue
	 * @param inputDataTableContentDependent
	 */
	public StringParameter(
			SimpleName name, VfNotes notes, String descriptiveName, boolean mandatory,
			String defaultValue, Predicate<String> nonNullValueAdditionalConstraints,
			boolean inputDataTableContentDependent) {
		super(String.class, name, notes, descriptiveName, mandatory, defaultValue, nonNullValueAdditionalConstraints, inputDataTableContentDependent);
	}

	

}
