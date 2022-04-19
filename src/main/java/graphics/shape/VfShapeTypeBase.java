package graphics.shape;

import java.util.HashMap;
import java.util.Map;
import basic.SimpleName;
import basic.VfNotes;
import function.target.LeafGraphicsPropertyCFGTarget;
import graphics.property.node.GraphicsPropertyLeafNode;
import graphics.property.tree.GraphicsPropertyTree;
import javafx.scene.transform.Transform;

import static graphics.property.tree.VfTransformPropertyTreeFactory.*;

/**
 * base AbstractVfShapeType class with transform graphics property trees;
 * 
 * note that transform graphics properties are basic properties shared by types of shape defined in visframe;
 * 
 * definition of transform in visframe is similar to the {@link Transform} of javafx;
 * 
 * @author tanxu
 * 
 */
public abstract class VfShapeTypeBase implements VfShapeType {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5019924379143146524L;
	
	/////////////////
	private final SimpleName name;//
	private final VfNotes notes;
	private final Map<SimpleName, GraphicsPropertyTree> graphicsPropertyTreeNameMap;
	
	
	///////////////////////
	private transient Map<SimpleName, LeafGraphicsPropertyCFGTarget<?>> leafGraphicsPropertyCFGTargetNameMap;
	
	
	/**
	 * 
	 * @param name
	 * @param notes
	 * @param shapeSpecificGraphicsPropertyTreeNameMap
	 */
	protected VfShapeTypeBase(SimpleName name, VfNotes notes, Map<SimpleName, GraphicsPropertyTree> shapeSpecificGraphicsPropertyTreeNameMap){
		
		if(shapeSpecificGraphicsPropertyTreeNameMap==null||shapeSpecificGraphicsPropertyTreeNameMap.isEmpty()) {
			throw new IllegalArgumentException("given shapeSpecificGraphicsPropertyTreeNameMap cannot be null or empty!");
		}
		
		this.name = name;
		this.notes = notes;
		
		this.graphicsPropertyTreeNameMap=new HashMap<>();
		this.graphicsPropertyTreeNameMap.putAll(shapeSpecificGraphicsPropertyTreeNameMap);
		
		///add the transform graphics property trees
		this.graphicsPropertyTreeNameMap.put(TRANSLATE_TREE.getName(), TRANSLATE_TREE);
		this.graphicsPropertyTreeNameMap.put(SCALE_TREE.getName(), SCALE_TREE);
		this.graphicsPropertyTreeNameMap.put(SHEAR_TREE.getName(), SHEAR_TREE);
		this.graphicsPropertyTreeNameMap.put(ROTATE_TREE.getName(), ROTATE_TREE);
	}
	
	@Override
	public VfNotes getNotes() {
		return notes;
	}

	@Override
	public SimpleName getName() {
		return name;
	}


	@Override
	public Map<SimpleName, GraphicsPropertyTree> getGraphicsPropertyTreeNameMap() {
		return this.graphicsPropertyTreeNameMap;
	}

//	@Override
//	public Set<SimpleName> getMandatoryShapeGraphicsPropertyTreeNameSet() {
//		if(this.mandatoryShapeGraphicsPropertyTreeNameSet==null) {
//			mandatoryShapeGraphicsPropertyTreeNameSet = new HashSet<>();
//			this.getGraphicsPropertyTreeNameMap().values().forEach(e->{
//				if(e.isMandatory()) {
//					mandatoryShapeGraphicsPropertyTreeNameSet.add(e.getName());
//				}
//			});
//		}
//		return mandatoryShapeGraphicsPropertyTreeNameSet;
//	}

	/**
	 * build and return the map from the full path name on tree of all {@link GraphicsPropertyLeafNode} on this {@link VfShapeTypeBase} 
	 * to the {@link LeafGraphicsPropertyCFGTarget} built based on the {@link GraphicsPropertyLeafNode};
	 */
	@Override
	public Map<SimpleName, LeafGraphicsPropertyCFGTarget<?>> getLeafGraphicsPropertyCFGTargetNameMap() {
		if(this.leafGraphicsPropertyCFGTargetNameMap == null) {
			this.leafGraphicsPropertyCFGTargetNameMap = new HashMap<>();
			
			for(SimpleName treeName:this.getGraphicsPropertyTreeNameMap().keySet()) {
				GraphicsPropertyTree tree = this.getGraphicsPropertyTreeNameMap().get(treeName);
				
				for(SimpleName leafNodeFullPathNameOnTree: tree.getLeafNodeFullPathNameOnTreeMap().keySet()) {
					GraphicsPropertyLeafNode<?> leafNode = tree.getLeafNodeFullPathNameOnTreeMap().get(leafNodeFullPathNameOnTree);
					leafGraphicsPropertyCFGTargetNameMap.put(
							leafNodeFullPathNameOnTree, 
							leafNode.makeLeafGraphicsPropertyCFGTarget(leafNodeFullPathNameOnTree, treeName)
							);
				}
			}
		}
		
		return leafGraphicsPropertyCFGTargetNameMap;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((graphicsPropertyTreeNameMap == null) ? 0 : graphicsPropertyTreeNameMap.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VfShapeTypeBase))
			return false;
		VfShapeTypeBase other = (VfShapeTypeBase) obj;
		if (graphicsPropertyTreeNameMap == null) {
			if (other.graphicsPropertyTreeNameMap != null)
				return false;
		} else if (!graphicsPropertyTreeNameMap.equals(other.graphicsPropertyTreeNameMap))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		return true;
	}

	
}
