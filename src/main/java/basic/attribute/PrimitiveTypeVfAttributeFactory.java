package basic.attribute;

import basic.SimpleName;
import basic.VfNotes;
import basic.serialization.SerializablePredicate;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * static factory methods for building primitive type {@link VfAttribute}
 * @author tanxu
 *
 */
public class PrimitiveTypeVfAttributeFactory {
	
	/**
	 * build and return a double type {@link VfAttribute}
	 * @param name
	 * @param notes
	 * @param nonNullValueConstraints
	 * @param SQLDataType
	 * @param defaultValue
	 * @param canBeNull
	 * @return
	 */
	public static VfAttributeImpl<Double> doubleTypeVfAttribute(
			SimpleName name, VfNotes notes,
			SerializablePredicate<Double> nonNullValueConstraints,
			Double defaultValue, boolean canBeNull
			){
		return new VfAttributeImpl<Double>(
				name, notes,
				Double.class, 
				d->{return Double.toString(d);},//toStringFunction
				s->{return Double.parseDouble(s);},//fromStringFunction
				nonNullValueConstraints,
				SQLDataTypeFactory.doubleType(),
				defaultValue, canBeNull);
	}
	
	/**
	 * build and return a integer type {@link VfAttribute}
	 * @param name
	 * @param notes
	 * @param nonNullValueConstraints
	 * @param SQLDataType
	 * @param defaultValue
	 * @param canBeNull
	 * @return
	 */
	public static VfAttributeImpl<Integer> intTypeVfAttribute(
			SimpleName name, VfNotes notes,
			SerializablePredicate<Integer> nonNullValueConstraints,
			VfDefinedPrimitiveSQLDataType SQLDataType,
			Integer defaultValue, boolean canBeNull
			){
		return new VfAttributeImpl<Integer>(
				name, notes,
				Integer.class, 
				i->{return Integer.toString(i);},//toStringFunction
				s->{return Integer.parseInt(s);},//fromStringFunction
				nonNullValueConstraints,
				SQLDataType,
				defaultValue, canBeNull);
	}
	
	/**
	 * build and return a boolean type {@link VfAttribute}
	 * @param name
	 * @param notes
	 * @param nonNullValueConstraints
	 * @param SQLDataType
	 * @param defaultValue
	 * @param canBeNull
	 * @return
	 */
	public static VfAttributeImpl<Boolean> booleanTypeVfAttribute(
			SimpleName name, VfNotes notes,
			SerializablePredicate<Boolean> nonNullValueConstraints,
			Boolean defaultValue, boolean canBeNull
			){
		return new VfAttributeImpl<Boolean>(
				name, notes,
				Boolean.class, 
				i->{return Boolean.toString(i);},//toStringFunction
				s->{return Boolean.parseBoolean(s);},//fromStringFunction
				nonNullValueConstraints,
				SQLDataTypeFactory.booleanType(),
				defaultValue, canBeNull);
	}
	
	/**
	 * build and return a string type {@link VfAttribute}
	 * @param name
	 * @param notes
	 * @param nonNullValueConstraints
	 * @param SQLDataType
	 * @param defaultValue
	 * @param canBeNull
	 * @return
	 */
	public static VfAttributeImpl<String> stringTypeVfAttribute(
			SimpleName name, VfNotes notes,
			SerializablePredicate<String> nonNullValueConstraints,
			VfDefinedPrimitiveSQLDataType SQLDataType,
			String defaultValue, boolean canBeNull
			){
		return new VfAttributeImpl<String>(
				name, notes,
				String.class, 
				i->{return i;},//toStringFunction
				s->{return s;},//fromStringFunction
				nonNullValueConstraints,
				SQLDataType,
				defaultValue, canBeNull);
	}
	
	
	
}
