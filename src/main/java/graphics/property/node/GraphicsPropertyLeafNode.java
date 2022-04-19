package graphics.property.node;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import basic.SimpleName;
import basic.VfNotes;
import basic.attribute.VfAttribute;
import basic.serialization.SerializablePredicate;
import function.target.LeafGraphicsPropertyCFGTarget;
import graphics.property.VfGraphicsPropertyUtils;


/**
 * leaf type of a {@link GraphicsPropertyNode} that is also a primitive type attribute;
 * 
 * delegate to a {@link VfAttribute} with the attribute name being the simple node name of the {@link GraphicsPropertyLeafNode} rather than the full path name on tree(which is undefined when a GraphicsPropertyLeafNode is defined);
 * 
 * in visframe, all types of GraphicsPropertyLeafNode are fully predefined by visframe api; user-defined GraphicsPropertyLeafNode is not supported;
 * 
 * @author tanxu
 * 
 */
public class GraphicsPropertyLeafNode<T extends Serializable> implements GraphicsPropertyNode {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1545458389820025310L;
	
	//////////////////////
	/**
	 * node name is the simple name, NOT the full path name!
	 */
	private final VfAttribute<T> propertyAttribute;
	
	/**
	 * constructor
	 * @param nodeName
	 * @param notes
	 * @param SQLDataType
	 * @param domain
	 * @param defaultStringValue
	 */
	public GraphicsPropertyLeafNode(VfAttribute<T> propertyAttribute) {
		this.propertyAttribute = propertyAttribute;
	}
	
	public VfAttribute<T> getPropertyAttribute(){
		return this.propertyAttribute;
	}
	
	public T getDefaultValue() {
		return this.getPropertyAttribute().getDefaultValue();
	}
	
	public boolean canBeNull() {
		return this.getPropertyAttribute().canBeNull();
	}
	
	public SerializablePredicate<T> getNonNullValueConstraints(){
		return this.getPropertyAttribute().getNonNullValueConstraints();
	}
	
	public boolean isValidValue(Object o) {
		return this.getPropertyAttribute().isValidValue(o);
	}
	
	/**
	 * build and return a LeafGraphicsPropertyCFGTarget with this {@link GraphicsPropertyLeafNode} with the given full path name on the tree with the given tree name;
	 * 
	 * @return
	 */
	public LeafGraphicsPropertyCFGTarget<T> makeLeafGraphicsPropertyCFGTarget(SimpleName fullPathNameOnTree, SimpleName treeName){
		return new LeafGraphicsPropertyCFGTarget<>(
				fullPathNameOnTree,
//				new VfAttributeImpl<>(
//						fullPathNameOnTree, //SimpleName name, 
//						this.getPropertyAttribute().getNotes(),//VfNotes notes,
//						this.getPropertyAttribute().getValueType(),//Class<T> valueType, 
//						this.getPropertyAttribute().getToStringFunction(),
//						this.getPropertyAttribute().getFromStringFunction(),
//						this.getPropertyAttribute().getNonNullValueConstraints(),//Predicate<T> domain, 
//						this.getPropertyAttribute().getSQLDataType(),//SQLDataType SQLDataType,
//						this.getPropertyAttribute().getDefaultValue(),//T defaultValue, 
//						this.getPropertyAttribute().canBeNull()//boolean canBeNull
//						),//this.getPropertyAttribute(),
				treeName,
				this
				);
	}
	////////////////////////////////////
	
	@Override
	public SimpleName getName() {
		return this.getPropertyAttribute().getName();
	}

	@Override
	public VfNotes getNotes() {
		return this.getPropertyAttribute().getNotes();
	}
	
	
	@Override
	public Map<SimpleName, GraphicsPropertyLeafNode<T>> getDescendantNodeFullPathNameOnTreeMap(String parentNodeFullPathNameOnTree) {
		Map<SimpleName, GraphicsPropertyLeafNode<T>> ret = new HashMap<>();
		ret.put(
				VfGraphicsPropertyUtils.buildFullPathNameOnTree(parentNodeFullPathNameOnTree, this.getName()),
				this
				);
		
		return ret;
	}


	
	/////////////////////////////////////////
	@Override
	public GraphicsPropertyLeafNode<T> reproduce() {
		return new GraphicsPropertyLeafNode<>(this.getPropertyAttribute().reproduce());
	}
	
	
	/////////////////////////////////////////////
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((propertyAttribute == null) ? 0 : propertyAttribute.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GraphicsPropertyLeafNode<?>))
			return false;
		GraphicsPropertyLeafNode<?> other = (GraphicsPropertyLeafNode<?>) obj;
		if (propertyAttribute == null) {
			if (other.propertyAttribute != null)
				return false;
		} else if (!propertyAttribute.equals(other.propertyAttribute))
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getPropertyAttribute().getName().getStringValue());
		sb.append(";");
		sb.append(this.getPropertyAttribute().getSQLDataType().getSQLString());
		
		return sb.toString();
	}
}
