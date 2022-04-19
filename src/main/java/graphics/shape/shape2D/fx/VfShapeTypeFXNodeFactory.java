package graphics.shape.shape2D.fx;

import java.util.Map;
import basic.SimpleName;
import graphics.property.node.GraphicsPropertyLeafNode;
import graphics.shape.VfShapeType;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

/**
 * interface for factory of a javafx {@link Node} instance based on property values of a {@link VfShapeType} defined in visframe;
 * 
 * @author tanxu
 *
 * @param <T>
 */
public interface VfShapeTypeFXNodeFactory<N extends Node, T extends VfShapeType> {
	/**
	 * return the VfShapeType
	 * @return
	 */
	T getVfShapeType();
	
	
	/**
	 * create a JAVAFX shape Node object with the given leafGraphicsPropertyFullPathNameOnTreeStringValueMap;
	 * 
	 * 0. invoke {@link #initialize()}
	 * 
	 * 1. initialize a javafx shape Node object of the corresponding type;
	 * 
	 * 2. set the type specific properties with {@link #setTypeSpecificTreeProperty(Node)} method;
	 * 
	 * if all mandatory type specific properties are successfully set ({@link #setTypeSpecificTreeProperty(Node)} method returns true), then
	 * 
	 * 3. set the transform related properties with {@link #setTransform(Node)};
	 *
	 * @param leafGraphicsPropertyFullPathNameOnTreeStringValueMap map from leaf node full path name on tree to the string value for the shape instance to be created, which may contain null valued map value;
	 * @param nonMandatoryTreeNameSetWithOneOrMoreLeafNodeAssignedExplicitValue set of name of property tree with at least one leaf property assigned an explicit value in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
	 * @return
	 */
	N makeFXNode(Map<SimpleName,String> leafGraphicsPropertyFullPathNameOnTreeStringValueMap);

	/**
	 * initialize all fields to default value of the corresponding {@link GraphicsPropertyLeafNode};
	 * 
	 * note that this will initialize properties with default value being null to null;
	 * 
	 * must be invoked for each specific shape instance to reset the properties values from any previously set values;
	 */
	void initialize();
	
	
	/**
	 * set the javafx shape {@link Node} properties specific to the shape type;
	 * 
	 * return true if all of the type specific properties are successfully set; false otherwise;
	 */
	boolean setTypeSpecificTreeProperty(N node);
	
	
	/**
	 * set all {@link Transform} related properties if needed;
	 * return true if all properties are successfully set; false otherwise
	 */
	boolean setTransform(N node);
}
