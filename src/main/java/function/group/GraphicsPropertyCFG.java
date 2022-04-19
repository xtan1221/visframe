package function.group;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.target.LeafGraphicsPropertyCFGTarget;
import graphics.property.node.GraphicsPropertyLeafNode;
import graphics.property.tree.GraphicsPropertyTree;
import metadata.MetadataID;


/**
 * base class for type of {@link CompositionFunctionGroup} whose targets are all of type {@link LeafGraphicsPropertyCFGTarget} and each belonging to a specific {@link GraphicsPropertyTree}
 * 
 * @author tanxu
 *
 */
public abstract class GraphicsPropertyCFG extends AbstractCompositionFunctionGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3572861655138071990L;
	
	///////////////////////
	/**
	 * the map from the target name (full path name of leaf property) to the {@link LeafGraphicsPropertyCFGTarget}
	 */
	private transient Map<SimpleName, LeafGraphicsPropertyCFGTarget<?>> leafGraphicsPropertyCFGTargetFullPathNameOnTreeMap;
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 */
	public GraphicsPropertyCFG(
			CompositionFunctionGroupName name, VfNotes notes,
			MetadataID ownerRecordDataMetadataID) {
		super(name, notes,ownerRecordDataMetadataID);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * return the map from the name of the {@link GraphicsPropertyTree} to each {@link GraphicsPropertyTree};
	 * 
	 * note that the union of {@link LeafGraphicsPropertyCFGTarget}s of all {@link GraphicsPropertyTree} returned by this map consists the full set of the target of this {@link GraphicsPropertyCFG}
	 * @return
	 */
	protected abstract Map<SimpleName , GraphicsPropertyTree> getTargetGraphicsPropertyTreeNameMap();
	
	
	/**
	 * map from the target name (node full path name on GraphicsPropertyTree) to the owner GraphicsPropertyTree;
	 */
	private transient Map<SimpleName , GraphicsPropertyTree> targetNodeFullPathNameGraphicsPropertyTreeMap;
	/**
	 * 
	 * @param targetName
	 * @return
	 */
	public GraphicsPropertyTree getGraphicsPropertyTree(SimpleName targetName) {
		if(this.targetNodeFullPathNameGraphicsPropertyTreeMap == null) {
			this.targetNodeFullPathNameGraphicsPropertyTreeMap = new HashMap<>();
			
			this.getTargetGraphicsPropertyTreeNameMap().forEach((treeName, tree)->{
				tree.getLeafNodeFullPathNameOnTreeMap().keySet().forEach(n->{
					this.targetNodeFullPathNameGraphicsPropertyTreeMap.put(n, tree);
				});
			});
		}
		
		return this.targetNodeFullPathNameGraphicsPropertyTreeMap.get(targetName);
	}
	
	/**
	 * {@inheritDoc}
	 * build and return the map from the target name to each target;
	 */
	@Override
	public Map<SimpleName, LeafGraphicsPropertyCFGTarget<?>> getTargetNameMap(){
		if(this.leafGraphicsPropertyCFGTargetFullPathNameOnTreeMap == null) {
			this.leafGraphicsPropertyCFGTargetFullPathNameOnTreeMap = new LinkedHashMap<>();
			
			for(SimpleName treeName:this.getTargetGraphicsPropertyTreeNameMap().keySet()) {
				GraphicsPropertyTree tree = this.getTargetGraphicsPropertyTreeNameMap().get(treeName);
				
				for(SimpleName nodeFullPathNameOnTree: tree.getLeafNodeFullPathNameOnTreeMap().keySet()) {
					GraphicsPropertyLeafNode<?> leafNode = tree.getLeafNodeFullPathNameOnTreeMap().get(nodeFullPathNameOnTree);
					if(leafNode instanceof GraphicsPropertyLeafNode) {
						this.leafGraphicsPropertyCFGTargetFullPathNameOnTreeMap.put(
								nodeFullPathNameOnTree,
								leafNode.makeLeafGraphicsPropertyCFGTarget(nodeFullPathNameOnTree, treeName)
								);
					}
				}
			}
		}
		
		return this.leafGraphicsPropertyCFGTargetFullPathNameOnTreeMap;
	}
	
	
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced CFG will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this CFG is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract GraphicsPropertyCFG reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
	
}
