package basic.attribute;

import java.io.Serializable;
import basic.SimpleName;
import basic.VfNotes;
import basic.serialization.SerializableFunction;
import basic.serialization.SerializablePredicate;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

public class VfAttributeImpl<T extends Serializable> implements VfAttribute<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2093855875950343953L;
	
	////////////////////////////////////////
	
	private final SimpleName name;
	private final VfNotes notes;
	private final Class<T> valueType;
	
	private final SerializableFunction<T, String> toStringFunction;
	private final SerializableFunction<String, T> fromStringFunction;
	
	private final SerializablePredicate<T> nonNullValueConstraints;
	
	private final VfDefinedPrimitiveSQLDataType SQLDataType;
	private final T defaultValue;
	private final boolean canBeNull;
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param valueType
	 * @param toStringFunction
	 * @param fromStringFunction
	 * @param nonNullValueConstraints
	 * @param SQLDataType
	 * @param defaultValue
	 * @param canBeNull
	 */
	public VfAttributeImpl(
			SimpleName name, VfNotes notes,
			Class<T> valueType, 
			SerializableFunction<T, String> toStringFunction,
			SerializableFunction<String, T> fromStringFunction,
			SerializablePredicate<T> nonNullValueConstraints,
			VfDefinedPrimitiveSQLDataType SQLDataType,
			T defaultValue, boolean canBeNull
			){
		this.name = name;
		this.notes = notes;
		this.valueType = valueType;
		this.toStringFunction = toStringFunction;
		this.fromStringFunction = fromStringFunction;
		
		this.nonNullValueConstraints = nonNullValueConstraints;
		this.SQLDataType = SQLDataType;
		this.defaultValue = defaultValue;
		this.canBeNull = canBeNull;
	}
	
	
	@Override
	public SimpleName getName() {
		return name;
	}

	@Override
	public VfNotes getNotes() {
		return notes;
	}

	@Override
	public Class<T> getValueType() {
		return this.valueType;
	}

	@Override
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return this.SQLDataType;
	}

	@Override
	public SerializablePredicate<T> getNonNullValueConstraints() {
		return this.nonNullValueConstraints;
	}

	@Override
	public T getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public boolean canBeNull() {
		return this.canBeNull;
	}


	@Override
	public SerializableFunction<T, String> getToStringFunction() {
		return this.toStringFunction;
	}

	
	@Override
	public SerializableFunction<String, T> getFromStringFunction() {
		return this.fromStringFunction;
	}

	
	/**
	 * 
	 * @param newName
	 * @return
	 */
	@Override
	public VfAttributeImpl<T> renameTo(SimpleName newName){
		return new VfAttributeImpl<>(newName, 
				notes,
				valueType, 
				toStringFunction,
				fromStringFunction,
				nonNullValueConstraints,
				SQLDataType,
				defaultValue, canBeNull
				);
	}
	
	
	
	
	//////////////////////////////////
	@Override
	public VfAttributeImpl<T> reproduce() {
		return new VfAttributeImpl<>(
				this.getName().reproduce(),//SimpleName name, 
				this.getNotes().reproduce(),//VfNotes notes,
				this.getValueType(),//Class<T> valueType, 
				this.getToStringFunction(),//SerializableFunction<T, String> toStringFunction,
				this.getFromStringFunction(),//SerializableFunction<String, T> fromStringFunction,
				this.getNonNullValueConstraints(),//SerializablePredicate<T> nonNullValueConstraints,
				this.getSQLDataType().reproduce(),//VfDefinedPrimitiveSQLDataType SQLDataType,
				this.getDefaultValue(),//T defaultValue, 
				this.canBeNull()//boolean canBeNull
				);
	}


	
	////////////////////////////////////
	
	@Override
	public String toString() {
		return "VfAttributeImpl [name=" + name + ", notes=" + notes + ", valueType=" + valueType + ", toStringFunction="
				+ toStringFunction + ", fromStringFunction=" + fromStringFunction + ", nonNullValueConstraints="
				+ nonNullValueConstraints + ", SQLDataType=" + SQLDataType + ", defaultValue=" + defaultValue
				+ ", canBeNull=" + canBeNull + "]";
	}


	/**
	 * all functional fields are ignored;
	 * functional interface has no valid equals and hashcode methods?
	 * TODO
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((SQLDataType == null) ? 0 : SQLDataType.hashCode());
		result = prime * result + (canBeNull ? 1231 : 1237);
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
//		result = prime * result + ((fromStringFunction == null) ? 0 : fromStringFunction.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
//		result = prime * result + ((nonNullValueConstraints == null) ? 0 : nonNullValueConstraints.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
//		result = prime * result + ((toStringFunction == null) ? 0 : toStringFunction.hashCode());
		result = prime * result + ((valueType == null) ? 0 : valueType.hashCode());
		return result;
	}

	/**
	 * all functional fields are ignored;
	 * functional interface has no valid equals and hashcode methods?
	 * TODO
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VfAttributeImpl<?>))
			return false;
		VfAttributeImpl<?> other = (VfAttributeImpl<?>) obj;
		if (SQLDataType == null) {
			if (other.SQLDataType != null)
				return false;
		} else if (!SQLDataType.equals(other.SQLDataType))
			return false;
		if (canBeNull != other.canBeNull)
			return false;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
//		if (fromStringFunction == null) {
//			if (other.fromStringFunction != null)
//				return false;
//		} else if (!fromStringFunction.equals(other.fromStringFunction))
//			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
//		if (nonNullValueConstraints == null) {
//			if (other.nonNullValueConstraints != null)
//				return false;
//		} else if (!nonNullValueConstraints.equals(other.nonNullValueConstraints))
//			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
//		if (toStringFunction == null) {
//			if (other.toStringFunction != null)
//				return false;
//		} else if (!toStringFunction.equals(other.toStringFunction))
//			return false;
		if (valueType == null) {
			if (other.valueType != null)
				return false;
		} else if (!valueType.equals(other.valueType))
			return false;
		return true;
	}

	
	
}
