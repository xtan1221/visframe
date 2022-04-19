package graphics.property.node;

import java.util.Map;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.reproduce.SimpleReproducible;

/**
 * base class for a property node;
 * 
 * all property nodes of the same parent node should have their own unique {@link SimpleName};
 * 
 * ========================
 * all types of {@link GraphicsPropertyNode} are pre-defined by visframe;
 * 		also, each pre-defined {@link GraphicsPropertyNode} type has a SINGLETON instance maintained by visframe;
 * see factory classes in graphics.property.shape2D.factory package for a full list of pre-defined {@link GraphicsPropertyNode};
 * 
 * 
 * ======================
 * same {@link GraphicsPropertyNode} can be used to construct different {@link GraphicsPropertyTree}s
 * 
 * ======================
 * NOTE that the full path name of a {@link GraphicsPropertyNode} is undefined until its parent node's full path name is built and passed to this one;
 * 
 * @author tanxu
 * 
 */
public interface GraphicsPropertyNode extends HasName, HasNotes, SimpleReproducible{
	@Override
	SimpleName getName();
	
	/**
	 * build and return the full path name of all descendant nodes on the tree of this node (including this one); recursion
	 * @return
	 */
	Map<SimpleName, ? extends GraphicsPropertyNode> getDescendantNodeFullPathNameOnTreeMap(String parentNodeFullPathNameOnTree);
	
	
	/////////////////////////////////////
	@Override
	public abstract GraphicsPropertyNode reproduce();
}
