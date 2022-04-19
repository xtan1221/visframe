package generic.tree.trim;

import generic.tree.VfTree;
import generic.tree.trim.helper.PositionOnTree;
import generic.tree.trim.helper.VfRerootTreeOnExistingNodeTrimmer;
import generic.tree.trim.helper.VfTreeAddNodeTrimmer;

/**
 * perform the reroot trimming on an input VfTree on an arbitrary location on the tree including an existing node and on a specific location of a branch;
 * 
 * 
 * @author tanxu
 *
 */
public class VfRerootTreeGenericTrimmer extends AbstractVfTreeTrimmer{
	/**
	 * position on tree to reroot from
	 */
	private final PositionOnTree positionOnTree;
	
	/**
	 * constructor
	 * @param inputTree
	 * @param parentNodeID
	 * @param childNodeID
	 * @param newRootNodePos
	 */
	public VfRerootTreeGenericTrimmer(
			VfTree inputTree, 
			PositionOnTree positionOnTree
			) {
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
	 * reroot tree with the help of AddNodeVfTreeTrimmer(if needed) and VfRerootExistingNodeTreeTrimmer
	 */
	@Override
	public void perform() {
		if(!this.positionOnTree.isOnExistingNode()) {//new node is needed
			VfTreeAddNodeTrimmer addNodeVfTreeTrimmer = new VfTreeAddNodeTrimmer(this.getInputTree(), this.positionOnTree);
			VfRerootTreeOnExistingNodeTrimmer vfRerootExistingNodeTreeTrimmer = new VfRerootTreeOnExistingNodeTrimmer(addNodeVfTreeTrimmer.getOutputTree(),addNodeVfTreeTrimmer.getNewNodeID());
			this.outputTree = vfRerootExistingNodeTreeTrimmer.getOutputTree();
		}else {
			VfRerootTreeOnExistingNodeTrimmer vfRerootExistingNodeTreeTrimmer = 
					new VfRerootTreeOnExistingNodeTrimmer(this.getInputTree(),this.positionOnTree.getChildNodeID());//!!!!!child node is the new root
			this.outputTree = vfRerootExistingNodeTreeTrimmer.getOutputTree();
		}
		
	}
	
}
