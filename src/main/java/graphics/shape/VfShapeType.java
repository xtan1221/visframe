package graphics.shape;

import java.io.Serializable;
import java.util.Map;
import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.VfNotes;
import function.target.LeafGraphicsPropertyCFGTarget;
import graphics.property.tree.GraphicsPropertyTree;

/**
 * interface for a shape type defined in visframe that can be visualized;
 * 
 * all {@link VfShapeType}s in visframe are pre-defined and a singleton instance of each type is maintained;
 * 
 * see classes in graphics.shape.shape2D.fx.type package for predefined visframe shape types;
 * 
 * @author tanxu
 *
 */
public interface VfShapeType extends HasName, HasNotes, Serializable{
	/**
	 * return the type name of this {@link VfShapeType};
	 * each {@link VfShapeType} in visframe must have its unique name;
	 */
	@Override
	SimpleName getName();//
	
	/**
	 * return the notes of this {@link VfShapeType};
	 */
	@Override
	VfNotes getNotes();
	
	/**
	 * return the map from name of {@link ShapeGraphicsPropertyTree} to {@link ShapeGraphicsPropertyTree};
	 * each {@link ShapeGraphicsPropertyTree} of the same {@link VfShapeType} should have a unique name;
	 * all {@link ShapeGraphicsPropertyTree}s together of a {@link VfShapeType} defines the most important feature of a {@link VfShapeType};
	 * 
	 * @return
	 */
	Map<SimpleName, GraphicsPropertyTree> getGraphicsPropertyTreeNameMap();
	
//	
//	/**
//	 * return name set of VfGraphicsPropertyTree that is mandatory of this VfShapeType;
//	 * 
//	 * 
//	 * @return
//	 */
//	Set<SimpleName> getMandatoryShapeGraphicsPropertyTreeNameSet();
//	
	/**
	 * find out and return the GraphicsPropertyTree of the given nodeFullPathName
	 * @param nodeFullPathName
	 * @return
	 */
	default GraphicsPropertyTree getGraphicsPropertyTree(SimpleName nodeFullPathName) {
		for(SimpleName treeName:this.getGraphicsPropertyTreeNameMap().keySet()) {
			if(this.getGraphicsPropertyTreeNameMap().get(treeName).getNodeFullPathNameOnTreeMap().keySet().contains(nodeFullPathName)) {
				return this.getGraphicsPropertyTreeNameMap().get(treeName);
			}
		}
		
		throw new IllegalArgumentException("given nodeFullPathName is not found on any tree of this shape type!");
	}
	/**
	 * 
	 * @return
	 */
	Map<SimpleName, LeafGraphicsPropertyCFGTarget<?>> getLeafGraphicsPropertyCFGTargetNameMap();
	
	
	/**
	 * return the LeafGraphicsPropertyCFGTarget corresponding to the X layout properties of this VfShapeType;
	 * 
	 * @return
	 */
	LeafGraphicsPropertyCFGTarget<?> getXLayoutLeafTarget();
	
	/**
	 * return the LeafGraphicsPropertyCFGTarget corresponding to the Y layout properties of this VfShapeType;
	 * 
	 * @return
	 */
	LeafGraphicsPropertyCFGTarget<?> getYLayoutLeafTarget();
}
