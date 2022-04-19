package operation.parameter;

import java.util.function.Predicate;

import basic.SimpleName;
import basic.VfNotes;
import basic.reproduce.SimpleReproducible;

/**
 * parameters whose value type is of type SimpleReproducible AND {@link #isInputDataTableContentDependent()} being false;
 * @author tanxu
 *
 * @param <T>
 */
public class SimpleReproducibleParameter<T extends SimpleReproducible> extends AbstractParameter<T>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4272753381813290279L;
	
	/**
	 * constructor
	 * @param valueTypeClazz
	 * @param name
	 * @param notes
	 * @param descriptiveName
	 * @param mandatory
	 * @param defaultValue
	 * @param nonNullValueAdditionalConstraints
	 */
	public SimpleReproducibleParameter(
			Class<T> valueTypeClazz, SimpleName name, VfNotes notes, String descriptiveName,
			boolean mandatory, T defaultValue, Predicate<T> nonNullValueAdditionalConstraints
			) {
		super(valueTypeClazz, name, notes, descriptiveName, mandatory, defaultValue, nonNullValueAdditionalConstraints, false);
		// TODO Auto-generated constructor stub
	}
	
}
