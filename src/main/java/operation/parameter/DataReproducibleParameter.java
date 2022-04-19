package operation.parameter;

import java.util.function.Predicate;

import basic.SimpleName;
import basic.VfNotes;
import basic.reproduce.DataReproducible;

/**
 * Parameter with value of type {@link DataReproducible} AND {@link #isInputDataTableContentDependent()} being false;
 * 
 * @author tanxu
 * 
 * @param <T>
 */
public class DataReproducibleParameter<T extends DataReproducible> extends AbstractParameter<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3933317092793461176L;
	
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
	public DataReproducibleParameter(
			Class<T> valueTypeClazz, 
			SimpleName name, VfNotes notes, String descriptiveName,
			boolean mandatory, T defaultValue, Predicate<T> nonNullValueAdditionalConstraints
			) {
		super(valueTypeClazz, name, notes, descriptiveName, mandatory, defaultValue, nonNullValueAdditionalConstraints, false);
	}
	
}
