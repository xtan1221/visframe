package graphics.property.node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import basic.SimpleName;
import basic.VfNotes;
import graphics.property.VfGraphicsPropertyUtils;

/**
 * non leaf node that contains at least one children nodes;
 * 
 * 
 * @author tanxu
 *
 */
public class GraphicsPropertyNonLeafNode implements GraphicsPropertyNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4056017517995773080L;
	
	////////////////////////////////////////
	/**
	 * 
	 */
	private final SimpleName name;
	/**
	 * 
	 */
	private final VfNotes notes;
	
	/**
	 * child node name is the simple name not the full path name;
	 * thus, for a non-leaf node, each child node should have a unique name;
	 * further, for VfPrimitiveGraphicsProperty(leaf node) type children, ... ?
	 */
	private final Map<SimpleName, GraphicsPropertyNode> childrenNodeNameMap;
	
	/**
	 * description on how the child node is related with this node;
	 */
	private final Map<SimpleName, String> childrenNodeNameDescriptionStringMap;
	
	/**
	 * constructor
	 * @param nodeName
	 * @param notes
	 * @param childrenNodeNameMap
	 * @param childrenNodeNameDescriptionStringMap
	 */
	public GraphicsPropertyNonLeafNode(
			SimpleName name, VfNotes notes,
			Map<SimpleName, GraphicsPropertyNode> childrenNodeNameMap,
			Map<SimpleName, String> childrenNodeNameDescriptionStringMap
			) {
		
		if(childrenNodeNameMap==null||childrenNodeNameMap.isEmpty()) {
			throw new IllegalArgumentException("given childrenNodeNameMap cannot be null or empty!");
		}
		
		if(childrenNodeNameDescriptionStringMap==null||childrenNodeNameDescriptionStringMap.isEmpty()) {
			throw new IllegalArgumentException("given childrenNodeNameDescriptionStringMap cannot be null or empty!");
		}
		
		if(!childrenNodeNameMap.keySet().equals(childrenNodeNameDescriptionStringMap.keySet())) {
			throw new IllegalArgumentException("given childrenNodeNameMap and childrenNodeNameDescriptionStringMap do not contain the same set of children node name!");
		}
		
		
		this.name = name;
		this.notes = notes;
		
		this.childrenNodeNameMap = childrenNodeNameMap;
		this.childrenNodeNameDescriptionStringMap = childrenNodeNameDescriptionStringMap;
	}

	public Map<SimpleName, GraphicsPropertyNode> getChildrenNodeNameMap() {
		return childrenNodeNameMap;
	}
	
	
	public Map<SimpleName, String> getChildrenNodeNameDescriptionStringMap() {
		return childrenNodeNameDescriptionStringMap;
	}
	

	@Override
	public SimpleName getName() {
		return name;
	}

	@Override
	public VfNotes getNotes() {
		return notes;
	}


	/////////////////////////////////
	@Override
	public Map<SimpleName, ? extends GraphicsPropertyNode> getDescendantNodeFullPathNameOnTreeMap(
			String parentNodeFullPathNameOnTree) {
		
		Map<SimpleName, GraphicsPropertyNode> ret =  new LinkedHashMap<>();
		
		SimpleName fullPathNameOnTreeOfThisNode = VfGraphicsPropertyUtils.buildFullPathNameOnTree(parentNodeFullPathNameOnTree, this.getName());
		ret.put(fullPathNameOnTreeOfThisNode, this);
		
		for(SimpleName childNodeName:this.childrenNodeNameMap.keySet()) {
			GraphicsPropertyNode childNode = this.childrenNodeNameMap.get(childNodeName);
			ret.putAll(childNode.getDescendantNodeFullPathNameOnTreeMap(fullPathNameOnTreeOfThisNode.getStringValue()));
		}
		
		return ret;
	}
	

	/////////////////////////////
	/**
	 * reproduce and return a new VfGraphicsPropertyNonLeafNode of this one;
	 */
	@Override
	public GraphicsPropertyNonLeafNode reproduce() {
		Map<SimpleName, GraphicsPropertyNode> childrenNodeNameMap = new HashMap<>();
		Map<SimpleName, String> childrenNodeNameDescriptionStringMap = new HashMap<>();
		
		for(SimpleName childNodeName:this.getChildrenNodeNameMap().keySet()) {
			childrenNodeNameMap.put(childNodeName.reproduce(), this.getChildrenNodeNameMap().get(childNodeName).reproduce());
			childrenNodeNameDescriptionStringMap.put(childNodeName.reproduce(), this.getChildrenNodeNameDescriptionStringMap().get(childNodeName));
		}
		
		return new GraphicsPropertyNonLeafNode(
				this.getName().reproduce(),
				this.getNotes().reproduce(),
				childrenNodeNameMap,
				childrenNodeNameDescriptionStringMap
				);
	}

	

	
	///////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childrenNodeNameDescriptionStringMap == null) ? 0
				: childrenNodeNameDescriptionStringMap.hashCode());
		result = prime * result + ((childrenNodeNameMap == null) ? 0 : childrenNodeNameMap.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GraphicsPropertyNonLeafNode))
			return false;
		GraphicsPropertyNonLeafNode other = (GraphicsPropertyNonLeafNode) obj;
		if (childrenNodeNameDescriptionStringMap == null) {
			if (other.childrenNodeNameDescriptionStringMap != null)
				return false;
		} else if (!childrenNodeNameDescriptionStringMap.equals(other.childrenNodeNameDescriptionStringMap))
			return false;
		if (childrenNodeNameMap == null) {
			if (other.childrenNodeNameMap != null)
				return false;
		} else if (!childrenNodeNameMap.equals(other.childrenNodeNameMap))
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

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getName().getStringValue());
		
		return sb.toString();
	}
	
}
