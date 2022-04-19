package operation.parameter.primitive;

import java.util.function.Predicate;

import basic.SimpleName;
import basic.VfNotes;
import operation.parameter.AbstractParameter;

/**
 * double type and {@link #isInputDataTableContentDependent()} can be either true or false;
 * @author tanxu
 *
 */
public class DoubleParameter extends AbstractParameter<Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3930425901112264592L;
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param descriptiveName
	 * @param mandatory
	 * @param defaultValue
	 * @param nonNullValueAdditionalConstraints
	 * @param inputDataTableContentDependent
	 */
	public DoubleParameter(
			SimpleName name, VfNotes notes, String descriptiveName, boolean mandatory,
			Double defaultValue, Predicate<Double> nonNullValueAdditionalConstraints,
			boolean inputDataTableContentDependent) {
		super(Double.class, name, notes, descriptiveName, mandatory, defaultValue, nonNullValueAdditionalConstraints, inputDataTableContentDependent);
	}
	
}
