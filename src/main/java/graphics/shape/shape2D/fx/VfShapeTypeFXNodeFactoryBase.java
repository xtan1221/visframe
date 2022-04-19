package graphics.shape.shape2D.fx;

import java.util.HashMap;
import java.util.Map;
import basic.SimpleName;
import graphics.shape.VfShapeType;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

/**
 * 1222720-update
 * base class for {@link VfShapeTypeFXNodeFactory} with a set of methods to facilitate creating a javafx {@link Node} instance;
 * 
 * rule of processing mandatory and non mandatory properties 
 * 
 * for mandatory properties, 
 * 		if no valid value is calculated, the Shape instance cannot be created;
 * 
 * for non-mandatory properties, 
 * 		1. if non-null value is explicitly calculated and given in the {@link #leafGraphicsPropertyFullPathNameOnTreeStringValueMap}
 * 			if the calculated value is invalid, the shape instance cannot be created;
 * 
 * 		2. otherwise, no non-null value of the property is not calculated and given in the {@link #leafGraphicsPropertyFullPathNameOnTreeStringValueMap}
 * 			either do nothing (if the property will be automatically set by the corresponding JAVAFX {@link Shape})
 * 			or set the property value to the default value of the corresponding {@link GraphicsPropertyLeafNode} if not null;
 * 
 * @author tanxu
 *
 * @param <T>
 */
public abstract class VfShapeTypeFXNodeFactoryBase<N extends Node, T extends VfShapeType> implements VfShapeTypeFXNodeFactory<N, T> {
	/**
	 * the value string of leaf graphic property of the current shape entity;
	 * note that the value string can be null;
	 * also it is possible that only a subset of leaf graphic properties are in the map;
	 */
	protected Map<SimpleName, String> leafGraphicsPropertyFullPathNameOnTreeStringValueMap;
	
	@Override
	public void initialize() {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap!=null)
			this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.clear();
		else
			this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap = new HashMap<>();
	}
	
	/**
	 * set corresponding {@link Transform} if any of the leaf of that transform type is assigned to a CF
	 */
	@Override
	public boolean setTransform(N node) {
		return FXTransformFactory.singletonInstance().setTransform(node, leafGraphicsPropertyFullPathNameOnTreeStringValueMap);
	}
}
