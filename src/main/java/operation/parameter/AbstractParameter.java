package operation.parameter;

import java.util.function.Predicate;

import basic.SimpleName;
import basic.VfNotes;

/**
 * 
 * Abstract class of {@link Parameter}
 * 
 * @author tanxu
 *
 * @param <T>
 */
public abstract class AbstractParameter<T> implements Parameter<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6854963534002513431L;
	
	
	/////////////////////////
	private final Class<T> valueType;
	private final SimpleName name;
	private final VfNotes notes;
	private final String descriptiveName;
	private final boolean mandatory;
	private final T defaultValue;
	private final Predicate<T> nonNullValueAdditionalConstraints;
	private final boolean inputDataTableContentDependent;
	
	
	/**
	 * constructor
	 * @param valueTypeClazz
	 * @param name
	 * @param notes
	 * @param descriptiveName
	 * @param mandatory
	 * @param defaultValue
	 * @param inputDataTableContentDependent
	 */
	public AbstractParameter(
			Class<T> valueTypeClazz,
			SimpleName name,
			VfNotes notes,
			String descriptiveName,
			boolean mandatory,
			T defaultValue,
			Predicate<T> nonNullValueAdditionalConstraints,
			boolean inputDataTableContentDependent
			){
		
		
		this.valueType = valueTypeClazz;
		this.name = name;
		this.notes = notes;
		this.descriptiveName = descriptiveName;
		this.mandatory = mandatory;
		this.defaultValue = defaultValue;
		this.nonNullValueAdditionalConstraints = nonNullValueAdditionalConstraints;
		this.inputDataTableContentDependent = inputDataTableContentDependent;
	}
	
	@Override
	public Class<T> getValueType(){
		return this.valueType;
	}
	
	
	@Override
	public SimpleName getName() {
		return this.name;
	}
	@Override
	public VfNotes getNotes() {
		return notes;
	}
	@Override
	public String getDescriptiveName() {
		return this.descriptiveName;
	}
	
	@Override
	public Boolean isMandatory() {
		return this.mandatory;
	}
	
	@Override
	public T getDefaultValue() {
		return this.defaultValue;
	}
	
	@Override
	public Predicate<T> getNonNullValueAdditionalConstraints(){
		return this.nonNullValueAdditionalConstraints;
	}
	
	@Override
	public boolean isInputDataTableContentDependent() {
		return this.inputDataTableContentDependent;
	}

}
