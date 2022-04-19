package generic.tree.trim;

import java.util.Map;

import generic.tree.VfTree;
import generic.tree.trim.helper.SiblingReorderPattern;


/**
 * trimmer for 
 * @author tanxu
 *
 */
public class VfSilblingReorderTrimmer extends AbstractVfTreeTrimmer {
	
	private final SiblingReorderPattern siblingReorderPattern;

	
	/**
	 * constructor
	 * @param inputTree
	 * @param parentNodeID
	 * @param siblingNodesOldAndNewOrderIndexMap
	 */
	public VfSilblingReorderTrimmer(
			VfTree inputTree,
			SiblingReorderPattern siblingReorderPattern
			) {
		super(inputTree);
		
		if(siblingReorderPattern==null) {
			throw new IllegalArgumentException("give siblingReorderPattern cannot be null!");
		}
		
		
		this.siblingReorderPattern = siblingReorderPattern;
		
	}

	////implement perform method to create the outputTree
	//1. create the output VfTrimTree with the Map<Integer, VfTrimTreeNode> clonedNodeIDMapFromInputVfTree 
	//2. modify the sibling order index accordingly;
	@Override
	public void perform() {
		this.cloneTreeNodes();
		
		this.outputTree = new VfTrimTree(this.getClonedRootNodeFromInputVfTree(), this.getClonedNodeIDMapFromInputVfTree(), 
				this.getInputTree().getNonMandatoryAdditionalNodeFeatureColumnNameMap(), 
				this.getInputTree().getNonMandatoryAdditionalEdgeFeatureColumnNameMap());
		this.outputTree.buildChildrenNodeSiblingOrderIndexMap();
		this.outputTree.recalculateChildrenSiblingOrderIndex();
		
		//
		Map<Integer,Integer> siblingNodesOldAndNewOrderIndexMap;
		for(int parentNodeID:this.getSiblingReorderPattern().getParentNodeIDToOriginalSwappedIndexMapMap().keySet()) {
			siblingNodesOldAndNewOrderIndexMap = this.getSiblingReorderPattern().getParentNodeIDToOriginalSwappedIndexMapMap().get(parentNodeID);
			
			for(int oldIndex:siblingNodesOldAndNewOrderIndexMap.keySet()) {
				int newOrderIndex = siblingNodesOldAndNewOrderIndexMap.get(oldIndex);
				
				VfTrimTreeNode childNode = (VfTrimTreeNode)this.outputTree.getNodeIDMap().get(parentNodeID).getChildrenNodeSiblingOrderIndexMap().get(oldIndex);
				childNode.setSiblingOrderIndex(newOrderIndex);
			}
		}
		
		//reset 
		this.outputTree.buildChildrenNodeSiblingOrderIndexMap();
		this.outputTree.recalculateChildrenSiblingOrderIndex();
		//
	}
	
	
	public SiblingReorderPattern getSiblingReorderPattern() {
		return siblingReorderPattern;
	}

}
