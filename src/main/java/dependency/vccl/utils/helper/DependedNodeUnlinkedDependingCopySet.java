package dependency.vccl.utils.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dependency.vccl.utils.NodeCopy;
import utils.Pair;

public class DependedNodeUnlinkedDependingCopySet<V> {
	/**
	 * a list of ...
	 * the order in this list is trivial;
	 */
	private final List<Pair<NodeCopy<V>, V>> dependingNodeUnlinkedCopyDependedNodePairList;
	/**
	 * map from the depended node to the copy number of it;
	 * 
	 * note that copy index start from 1 rather than 0;
	 */
	private final Map<V, Integer> dependedNodeCopyNumberMap;
	
	/**
	 * map from the depended Node 
	 * to the list of copy of it;
	 * 
	 * the key set of this map must be the same with {@link #dependedNodeUnlinkedDependingNodeCopySetMapMap}
	 */
	private final Map<V, Map<Integer,NodeCopy<V>>> dependedNodeCopyIndexMapMap;
	//////////////////////////////
	
	/**
	 * list of copy number of depended node corresponding to each element in {@link #dependingNodeUnlinkedCopyDependedNodePairList};
	 */
	private List<Integer> dependedNodeCopyNumberList;
	
	/**
	 * iterator of all possible combinations of depended node copy index corresponding to the {@link #dependingNodeUnlinkedCopyDependedNodePairList};
	 */
	private Iterator<List<Integer>> dependedCopyIndexListIterator;
	
	
	/**
	 * 
	 * @param dependingNodeUnlinkedCopyDependedNodePairList
	 * @param dependedNodeCopyNumberMap
	 * @param dependedNodeCopyIndexMapMap
	 */
	public DependedNodeUnlinkedDependingCopySet(
			List<Pair<NodeCopy<V>, V>> dependingNodeUnlinkedCopyDependedNodePairList,
			Map<V, Integer> dependedNodeCopyNumberMap,
			Map<V, Map<Integer,NodeCopy<V>>> dependedNodeCopyIndexMapMap
			){
		//
		
		this.dependingNodeUnlinkedCopyDependedNodePairList = dependingNodeUnlinkedCopyDependedNodePairList;
		this.dependedNodeCopyNumberMap = dependedNodeCopyNumberMap;
		this.dependedNodeCopyIndexMapMap = dependedNodeCopyIndexMapMap;
		
		
		this.preprocess();
	}
	
	/**
	 * initialize and build
	 * {@link #dependedNodeCopyNumberList} and {@link #dependedCopyIndexListIterator}
	 */
	private void preprocess() {
		//
		this.dependedNodeCopyNumberList = new ArrayList<>();
		
		this.dependingNodeUnlinkedCopyDependedNodePairList.forEach(pair->{
			this.dependedNodeCopyNumberList.add(this.dependedNodeCopyNumberMap.get(pair.getSecond()));
		});
		

		///
	 	//the list of all copy index list corresponding to dependedNodeCopyNumberList
		List<List<Integer>> dependedNodeCopyIndexListList = new ArrayList<>();
		this.dependedNodeCopyNumberList.forEach(copyNumber->{
			List<Integer> copyIndexList = new ArrayList<>();
			for(int i=1;i<=copyNumber;i++) {
				copyIndexList.add(i);
			}
			dependedNodeCopyIndexListList.add(copyIndexList);
		});
		
		//
		this.dependedCopyIndexListIterator = CartesianProductUtils.cartesianProduct(dependedNodeCopyIndexListList).iterator();
		
	}
	
	
	////////////////////////////////////////////
	/**
	 * find out the next group of copy links containing exactly one copy link from each node copy of depending node to one copy of the depended node in {@link #dependedNodeUnlinkedDependingNodeCopySetMapMap};
	 * 
	 * if there is no further group, return null;
	 * 
	 * @return
	 */
	public Set<CopyLink<V>> nextCopylinkGroup(){
		
		if(this.dependedCopyIndexListIterator.hasNext()) {
			List<Integer> list = this.dependedCopyIndexListIterator.next();
			
			Set<CopyLink<V>> ret = new HashSet<>();
			
			for(int i=0;i<list.size();i++) {
				int dependedNodeCopyIndex = list.get(i);
				Pair<NodeCopy<V>, V> dependingNodeUnlinkedCopyDependedNodePair = 
						dependingNodeUnlinkedCopyDependedNodePairList.get(i);
				
				CopyLink<V> copylink = 
						new CopyLink<>(
								dependingNodeUnlinkedCopyDependedNodePair.getFirst(), 
								dependedNodeCopyIndexMapMap.get(dependingNodeUnlinkedCopyDependedNodePair.getSecond()).get(dependedNodeCopyIndex)
								);
				
				ret.add(copylink);
			}
			
			return ret;
		}else {
			return null;
		}
	}
	
}
