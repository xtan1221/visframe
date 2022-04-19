package basic.attribute;

import java.io.Serializable;
import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.VfNotes;
import basic.reproduce.SimpleReproducible;
import basic.serialization.SerializableFunction;
import basic.serialization.SerializablePredicate;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;


/**
 * base interface for a visframe attribute that are eventually stored as a column in a table in the RDB of host {@link VisProjectDBContext};
 * 
 * for example, 
 * 1. record data table column
 * 2. cf value table column 
 * 3. piecewise function table column?
 * 
 * 4. graphics shape's primitive property
 * 
 * 
 * thus, for {@link Operation}'s {@link Parameter}, it distinguishes from those features due to the fact that {@link Parameter}s are not stored as column of a table;
 * 
 * 
 * @author tanxu
 * 
 */
public interface VfAttribute<T extends Serializable> extends HasName, HasNotes, SimpleReproducible{
	@Override
	SimpleName getName();
	
	@Override
	VfNotes getNotes();
	
	Class<T> getValueType();
	
	/**
	 * function that transform a value of this {@link VfAttribute} to a string representation;
	 * @return
	 */
	SerializableFunction<T, String> getToStringFunction();
	
	/**
	 * function that transform a string representation to a value type of this {@link VfAttribute}
	 * @return
	 */
	SerializableFunction<String, T> getFromStringFunction();
	
	/**
	 * return the SQLDataType of the value data type T;
	 * 
	 * could be null if there is no corresponding SQLDataType?
	 * @return
	 */
	VfDefinedPrimitiveSQLDataType getSQLDataType();
	
	/**
	 * return a Predicate that can check if a specific value of an instance of non-null {@link VfAttribute} is valid or not;
	 * can be null if there is no constraints on the value of an instance of {@link VfAttribute} ;
	 * @return
	 */
	SerializablePredicate<T> getNonNullValueConstraints();
	
	
	/**
	 * return the default value;
	 * can be null;
	 * 
	 * note that if default value is null and {@link #canBeNull()} returns false, an explicit non-null value should be assigned;
	 * 
	 * if a non-null value is not passing the test of {@link #getNonNullValueConstraints()} method, use this value instead;
	 * @return
	 */
	T getDefaultValue();//can be null
	
	/**
	 * 
	 * @param newName
	 * @return
	 */
	VfAttribute<T> renameTo(SimpleName newName);
	
	/**
	 * return the string representation of the default value of this {@link VfAttribute}
	 * @return
	 */
	default String getDefaultStringValue() {
		if(this.getDefaultValue()==null) {
			return null;
		}
		return this.getToStringFunction().apply(this.getDefaultValue());
	}
	
	
	/**
	 * return whether null value is allowed
	 * @return
	 */
	boolean canBeNull();
	
	////////////////////////////////

	/**
	 * check and return whether the given object value is valid or not;
	 * @param o
	 * @return
	 */
	default boolean isValidValue(Object o) {
		if(o==null) {
			if(this.canBeNull()) {
				return true;
			}else {
				return false;
			}
		}else {
			if(!this.getValueType().isAssignableFrom(o.getClass())) {
				return false;
			}
			
			@SuppressWarnings("unchecked")
			T t = (T)o;
			
			if(this.getNonNullValueConstraints()!=null) {
				return this.getNonNullValueConstraints().test(t);
			}else {
				return true;
			}
		}
	}
	
	
	
	/**
	 * return whether this {@link VfAttribute}'s value type is of visframe defined primitive data types:
	 * 
	 * String, Boolean, Integer, Double
	 * 
	 * @return
	 */
	default boolean isVisframePrimitiveType() {
		return this.getValueType().equals(String.class)
				||this.getValueType().equals(Boolean.class)
				||this.getValueType().equals(Integer.class)
				||this.getValueType().equals(Double.class);
		
	}
	
	
	@Override
	VfAttribute<T> reproduce();

}
