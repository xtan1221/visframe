package operation.parameter.primitive;

import java.util.function.Predicate;

import basic.SimpleName;
import basic.VfNotes;
import operation.parameter.AbstractParameter;
/**
 * integer type and {@link #isInputDataTableContentDependent()} can be either true or false;
 * @author tanxu
 *
 */
public class IntegerParameter extends AbstractParameter<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6664131719246876134L;
	
	/**
	 * 
	 * @param name
	 * @param notes
	 * @param descriptiveName
	 * @param mandatory
	 * @param defaultValue
	 * @param nonNullValueAdditionalConstraints
	 * @param inputDataTableContentDependent
	 */
	public IntegerParameter(
			SimpleName name, VfNotes notes, String descriptiveName, boolean mandatory,
			Integer defaultValue, Predicate<Integer> nonNullValueAdditionalConstraints,
			boolean inputDataTableContentDependent) {
		super(Integer.class, name, notes, descriptiveName, mandatory, defaultValue, nonNullValueAdditionalConstraints, inputDataTableContentDependent);
	}

}
