package operation.parameter;

import java.util.function.Predicate;

import basic.SimpleName;
import basic.VfNotes;
import basic.reproduce.DataReproducible;
import basic.reproduce.Reproducible;
import basic.reproduce.SimpleReproducible;

/**
 * all types of parameters that are 
 * 
 * 1. non-reproducible
 * 		not of any of {@link Reproducible}, {@link DataReproducible} and {@link SimpleReproducible}
 * 2. non-primitive type AND
 * 
 * 3. {@link #isInputDataTableContentDependent()} being true; 
 * 
 * @author tanxu
 * @param <T> 
 */
public class MiscTypeParameter<T> extends AbstractParameter<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7861404600526086904L;

	
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
	public MiscTypeParameter(
			Class<T> valueTypeClazz, SimpleName name, VfNotes notes, String descriptiveName, 
			boolean mandatory, T defaultValue, Predicate<T> nonNullValueAdditionalConstraints) {
		super(valueTypeClazz, name, notes, descriptiveName, mandatory, defaultValue, nonNullValueAdditionalConstraints, true);
		
		//validations
		//value type cannot be Reproducible!!!!!
		if(Reproducible.class.isAssignableFrom(valueTypeClazz)) {
			throw new IllegalArgumentException("value object type of MiscTypeParameter cannot be of type Reproducible");
		}
		//value type cannot be DataReproducible!!!!!
		if(DataReproducible.class.isAssignableFrom(valueTypeClazz)) {
			throw new IllegalArgumentException("value object type of MiscTypeParameter cannot be of type DataReproducible");
		}
		//value type cannot be SimpleReproducible!!!!!
		if(SimpleReproducible.class.isAssignableFrom(valueTypeClazz)) {
			throw new IllegalArgumentException("value object type of MiscTypeParameter cannot be of type SimpleReproducible");
		}
		//not primitive
		if(valueTypeClazz.isPrimitive()) {
			throw new IllegalArgumentException("value object type of MiscTypeParameter cannot be primitive type");
		}
	}
	
}
