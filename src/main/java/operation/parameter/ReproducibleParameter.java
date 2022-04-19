package operation.parameter;

import java.util.function.Predicate;

import basic.SimpleName;
import basic.VfNotes;
import basic.reproduce.Reproducible;

/**
 * Parameter with value type of {@link Reproducible} AND {@link #isInputDataTableContentDependent()} being false;
 * 
 * @author tanxu
 *
 * @param <T>
 */
public class ReproducibleParameter<T extends Reproducible> extends AbstractParameter<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6246421738458376308L;
	
	/**
	 * constructor
	 * @param valueTypeClazz
	 * @param name
	 * @param notes
	 * @param descriptiveName
	 * @param mandatory
	 * @param defaultValue
	 */
	public ReproducibleParameter(
			Class<T> valueTypeClazz, SimpleName name, VfNotes notes, 
			String descriptiveName, boolean mandatory, Predicate<T> nonNullValueAdditionalConstraints,
			T defaultValue
			) {
		super(valueTypeClazz, name, notes, descriptiveName, mandatory, defaultValue, nonNullValueAdditionalConstraints, false); //no additional constraints?
		// TODO Auto-generated constructor stub
	}
	
}
