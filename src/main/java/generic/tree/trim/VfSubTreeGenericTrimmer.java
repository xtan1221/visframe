package generic.tree.trim;

import generic.tree.VfTree;
import generic.tree.trim.helper.PositionOnTree;
import generic.tree.trim.helper.VfSubTreeOnExistingNodeTrimmer;
import generic.tree.trim.helper.VfTreeAddNodeTrimmer;

/**
 * extract a subtree from the input vftree rooted on a given position on the tree;
 * @author tanxu
 *
 */
public class VfSubTreeGenericTrimmer extends AbstractVfTreeTrimmer{
	/**
	 * position on tree to subtree from
	 */
	private final PositionOnTree positionOnTree;
	
	/**
	 * constructor
	 * @param inputTree
	 * @param parentNodeID
	 * @param childNodeID
	 * @param newRootNodePos
	 */
	public VfSubTreeGenericTrimmer(
			VfTree inputTree, PositionOnTree positionOnTree) {
		super(inputTree);
		
		if(positionOnTree.getParentNodeID()==null) {//new root is on child node
			if(inputTree.getNodeIDMap().get(positionOnTree.getChildNodeID()).getParentNodeID()==null) {
				throw new IllegalArgumentException("given new root node is the same with existing one;");
			}
		}else {
			if(inputTree.getNodeIDMap().get(positionOnTree.getChildNodeID()).getParentNodeID()!=positionOnTree.getParentNodeID()) {
				throw new IllegalArgumentException("given parentNodeID and childNodeID is not linked by an edge in the input tree!");
			}
		}
		
		if(positionOnTree.isOnRootNode()) {
			throw new IllegalArgumentException("given new root node cannot be on the existing root node;");
		}
		
		
		this.positionOnTree = positionOnTree;
		
	}

	
	/**
	 * create subtree with the help of AddNodeVfTreeTrimmer(if needed) and VfSubTreeOnExistingNodeTrimmer
	 */
	@Override
	public void perform() {
		if(!this.positionOnTree.isOnExistingNode()) {//new node is needed
			VfTreeAddNodeTrimmer addNodeVfTreeTrimmer = new VfTreeAddNodeTrimmer(this.getInputTree(), this.positionOnTree);
			VfSubTreeOnExistingNodeTrimmer vfSubTreeOnExistingNodeTrimmer =
					new VfSubTreeOnExistingNodeTrimmer(addNodeVfTreeTrimmer.getOutputTree(),addNodeVfTreeTrimmer.getNewNodeID());
			this.outputTree = vfSubTreeOnExistingNodeTrimmer.getOutputTree();
		}else {
			VfSubTreeOnExistingNodeTrimmer vfSubTreeOnExistingNodeTrimmer = 
					new VfSubTreeOnExistingNodeTrimmer(this.getInputTree(),this.positionOnTree.getChildNodeID());
			this.outputTree = vfSubTreeOnExistingNodeTrimmer.getOutputTree();
		}
	}

	
}
