package graphics.property.tree;

import java.util.LinkedHashMap;
import java.util.Map;
import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.VfNotes;
import basic.reproduce.SimpleReproducible;
import function.group.IndependentGraphicsPropertyCFG;
import graphics.property.node.GraphicsPropertyNode;
import graphics.shape.VfShapeType;
import graphics.property.node.GraphicsPropertyLeafNode;

/**
 * base class for a set of closely related graphics properties of type {@link GraphicsPropertyNode} organized in a rooted tree structure;
 * 
 * {@link GraphicsPropertyTree} is the base component for {@link VfShapeType}, each {@link VfShapeType} has its own set of {@link GraphicsPropertyTree} each with a unique tree name different from others of the same {@link VfShapeType};
 * 
 * {@link GraphicsPropertyTree} can also be used as independent property tree for {@link IndependentGraphicsPropertyCFG};
 * 
 * @author tanxu
 * 
 */
public class GraphicsPropertyTree implements HasName, HasNotes, SimpleReproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7086823768708739273L;
	//////////////////
	
	private final SimpleName name;
	private final VfNotes notes;
	private final GraphicsPropertyNode rootNode;
	
	////////////
	private transient Map<SimpleName, ? extends GraphicsPropertyNode> nodeFullPathNameOnTreeMap;
	private transient Map<SimpleName, GraphicsPropertyLeafNode<?>> leafNodeFullPathNameOnTreeMap;
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param rootNode
	 * @param mandatory
	 */
	public GraphicsPropertyTree(
			SimpleName name, 
			GraphicsPropertyNode rootNode,
			VfNotes notes
			){
		this.name = name;
		this.notes = notes;
		this.rootNode = rootNode;
	}
	
	
	public GraphicsPropertyNode getRootNode() {
		return rootNode;
	}


	/**
	 * build and return the nodeFullPathNameOnTreeMap
	 * @return
	 */
	public Map<SimpleName, ? extends GraphicsPropertyNode> getNodeFullPathNameOnTreeMap() {
		if(this.nodeFullPathNameOnTreeMap == null) {
			this.nodeFullPathNameOnTreeMap = this.rootNode.getDescendantNodeFullPathNameOnTreeMap(this.getName().getStringValue());
		}
		return nodeFullPathNameOnTreeMap;
	}
	
	/**
	 * return the map from the full path name on tree of the leaf node graphics property to the VfPrimitiveGraphicsProperty node
	 * @return
	 */
	public Map<SimpleName, GraphicsPropertyLeafNode<?>> getLeafNodeFullPathNameOnTreeMap() {
		if(this.leafNodeFullPathNameOnTreeMap == null) {
			this.leafNodeFullPathNameOnTreeMap = new LinkedHashMap<>();
			for(SimpleName nodeFullPathNameOnTree:this.getNodeFullPathNameOnTreeMap().keySet()) {
				GraphicsPropertyNode node = this.getNodeFullPathNameOnTreeMap().get(nodeFullPathNameOnTree);
				
				if(node instanceof GraphicsPropertyLeafNode) {
					this.leafNodeFullPathNameOnTreeMap.put(nodeFullPathNameOnTree, (GraphicsPropertyLeafNode<?>)node);
				}
			}
		}
		
		return leafNodeFullPathNameOnTreeMap;
	}
	
	////////////////////////////////
	
	@Override
	public VfNotes getNotes() {
		return this.notes;
	}
	
	
	@Override
	public SimpleName getName() {
		return this.name;
	}

	
	///////////////////////////////////
	@Override
	public GraphicsPropertyTree reproduce() {
		return new GraphicsPropertyTree(
				this.getName().reproduce(),//SimpleName name,
				this.getRootNode().reproduce(),//GraphicsPropertyNode rootNode
				this.getNotes().reproduce()//VfNotes notes, 
				);
	}

	
	////////////////////////////
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((rootNode == null) ? 0 : rootNode.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GraphicsPropertyTree))
			return false;
		GraphicsPropertyTree other = (GraphicsPropertyTree) obj;
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
		if (rootNode == null) {
			if (other.rootNode != null)
				return false;
		} else if (!rootNode.equals(other.rootNode))
			return false;
		return true;
	}
	


}
