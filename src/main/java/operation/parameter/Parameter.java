package operation.parameter;

import java.io.Serializable;
import java.util.function.Predicate;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.reproduce.Reproducible;

/**
 * interface for a parameter for a sub-type of {@link Operation};
 * 
 * shared by all instances of the same type of {@link Operation};
 * @author tanxu
 *
 * @param <T>
 */
public interface Parameter<T> extends HasName, HasNotes, Serializable{
	
	/**
	 * returns the name of the parameter; parameters of the same Operation class must have unique names
	 */
	SimpleName getName();
	
	/**
	 * return a full descriptive name of this parameter; may be used by GUI features
	 * @return
	 */
	String getDescriptiveName();
	
	/**
	 * return the type of the value of this parameter
	 * @return
	 */
	Class<T> getValueType();
	
	/**
	 * whether the value for this parameter is mandatory;
	 * if true, a non-null value must be set for an instance of this type parameter;
	 * @return
	 */
	Boolean isMandatory();
	
	/**
	 * return the default value;
	 * if null, no default value is available; 
	 * @return
	 */
	T getDefaultValue();
	

	
	/**
	 * whether or not the value of this parameter can be null or not 
	 * 
	 * if resultedFromReproducing is true, it will allow parameters dependent on input data table to be null;
	 * if resultedFromReproducing is false, it will only consider whether the parameter is mandatory and whether the default value is null;
	 * 
	 * 
	 * this is invoked by constructor to validate the parameters;
	 * 
	 * @return
	 * @param toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent
	 */
	default boolean canHaveNullValueObject(boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent) {
		if(toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent) {//if true, always check the parameter value;
			return this.isMandatory()?getDefaultValue()==null?false:true:true;
		}else {//if false
			return this.isInputDataTableContentDependent()? //if dependent on input data table content, do not check the constraint;
					true:this.isMandatory()?
								getDefaultValue()==null?false:true
								:true;
		}
	}
	
	
	
	/**
	 * returns whether the type of value object is primitive type
	 * see {@link Class#isPrimitive()} for details;
	 * @return
	 */
	default boolean isOfPrimitiveType() {
		return this.getValueType().isPrimitive();
	}
	
	/**
	 * whether this parameter is dependent on the content of input Metadata's data tables;
	 * 
	 * NOTE that parameters are either specific to the data content of input Metadata or not;
	 * 
	 * for example, for those dependent on data content,
	 * 		parameter of tree trimming
	 * 		for Parameters of algorithm parameter that are normally set to the specific input data;
	 * 
	 * for those not dependent on data content, they are normally related with the structure of the data such as table columns;
	 * 
	 * ================================
	 * if true, the value of this parameter cannot be directly reproduced, but need to be explicitly assigned based on the input Metadata's content;
	 * 		also in the {@link Operation#reproduce(context.scheme.applier.VisSchemeApplierArchive, int)} method of the owner Operation class, 
	 * 		set the value of this parameter to null for the reproduced copy; 
	 * 
	 * otherwise, the value of this parameter can be directly reproduced accordingly 
	 * 		based on whether parameter value type is {@link Reproducible} or {@link SimpleReproducible}
	 * @return
	 */
	boolean isInputDataTableContentDependent();
	
	/**
	 * return the Function that validate any additional constraints of a non-null object value of this Parameter;
	 * if there is none of such constraints, return null;
	 * 
	 * @return
	 */
	Predicate<T> getNonNullValueAdditionalConstraints();
	
	/**
	 * check if the given value object of this parameter is valid defined by this parameter;
	 * 1. if null, returns {@link #canHaveNullValueObject()};
	 * 2. if non-null, check if the type of the object is consistent with this parameter's value type
	 * 
	 * MUST be invoked in the constructor of {@link Operation} subclass when assigning a object to a Parameter
	 * 
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked")
	default boolean validateObjectValue(Object o, boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent) {
		if(o==null) {
			return canHaveNullValueObject(toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent);
		}else {
			if(!this.getValueType().isAssignableFrom(o.getClass())) {//given object's type is not valid
				return false;
			}
			
			if(this.getNonNullValueAdditionalConstraints()!=null) {//there is additional constraints on the value of this parameter
				return this.getNonNullValueAdditionalConstraints().test((T)o);
			}else {//no further constraints
				return true;
			}
		}
	}
	
	
}
