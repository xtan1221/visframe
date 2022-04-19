package context.scheme.appliedarchive;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import context.scheme.VisScheme;
import context.scheme.appliedarchive.mapping.MetadataMapping;
import dependency.cfd.integrated.IntegratedCFDGraphEdge;
import dependency.cfd.integrated.IntegratedCFDGraphNode;
import dependency.dos.DOSEdge.DOSEdgeType;
import dependency.dos.integrated.IntegratedDOSGraphEdge;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import dependency.vccl.VSCopy;
import dependency.vcd.VCDGraph;
import dependency.vcd.VCDNodeImpl;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import metadata.DataType;
import metadata.MetadataID;
import metadata.graph.GraphDataMetadata;
import operation.Operation;
import operation.graph.SingleGenericGraphAsInputOperation;
import operation.vftree.VfTreeTrimmingOperationBase;
import rdb.table.data.DataTableColumnName;

/**
 * step of solution set selection and mapping of VisScheme applying;
 * 
 * after integrated CFD graph and DOS graph are built and trimmed and before Operation reproducing and insertion;
 * 
 * @author tanxu
 *
 */
public class SolutionSetSelectorAndMapper {
	private final VisScheme appliedVisScheme;
	/**
	 * VCD graph for assignment of Metadata and CompositionFunctionGroup to VCD node;
	 */
	private final VCDGraph vcdGraph;
	private final SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> trimmedIntegratedDOSGraph;
	private final SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> trimmedIntegratedCFDGraph;
	
	////////////////////////////////
	///solution path and solution set related
	/**
	 * map from unique index to the {@link SolutionRootPath} on the {@link #trimmedIntegratedDOSGraph};
	 * 		
	 * note that for each root node on the {@link #trimmedIntegratedDOSGraph}, it starts a unique {@link SolutionRootPath};
	 * also some {@link IntegratedDOSGraphNode}s on the {@link #trimmedIntegratedDOSGraph} may be on multiple {@link SolutionRootPath}s;
	 */
	private Map<Integer, SolutionRootPath> solutionRootPathIndexMap;
	/**
	 * the map from the {@link IntegratedDOSGraphNode} on the {@link #trimmedIntegratedDOSGraph} to the set of index of the {@link SolutionRootPath} it is on;
	 * for {@link IntegratedDOSGraphNode}s not on any of the {@link SolutionRootPath}s, they are not in this map's key set;
	 * note that it is possible for a {@link IntegratedDOSGraphNode} to be on multiple SolutionRootPath if it is the shared descendant node of those SolutionRootPaths!!!
	 */
	private Map<IntegratedDOSGraphNode, Set<Integer>> integratedDOSNodeHostSolutionRootPathIndexSetMap;
	
	/**
	 * set of index of SolutionRootPath without any IntegratedDOSNode selected as part of the solution set in {@link #selectedSolutionSetNodeMappingMap};
	 */
	private Set<Integer> unrepresentedSolutionRootPahtIndexSet;

	//////////////////////
	/**
	 * 
	 */
	private Map<IntegratedDOSGraphNode, MetadataMapping> selectedSolutionSetNodeMappingMap;
	//////////////////
	/**
	 * 
	 * @param appliedVisScheme
	 * @param vcdGraph
	 * @param trimmedIntegratedDOSGraph
	 * @param trimmedIntegratedCFDGraph
	 * @throws SQLException
	 */
	SolutionSetSelectorAndMapper(
			VisScheme appliedVisScheme,
			VCDGraph vcdGraph,
			SimpleDirectedGraph<IntegratedDOSGraphNode, IntegratedDOSGraphEdge> trimmedIntegratedDOSGraph,
			SimpleDirectedGraph<IntegratedCFDGraphNode, IntegratedCFDGraphEdge> trimmedIntegratedCFDGraph
			) throws SQLException{
		//validations TODO
		
		
		this.appliedVisScheme = appliedVisScheme;
		this.vcdGraph = vcdGraph;
		this.trimmedIntegratedDOSGraph = trimmedIntegratedDOSGraph;
		this.trimmedIntegratedCFDGraph = trimmedIntegratedCFDGraph;
		
		//
		this.identifyAllSolutionRootPaths();
		this.preprocessIntegratedDOSNodes();
		this.initializeSolutionSetAndMapping();
		
	}
	
	
	/**
	 * find out all SolutionRootPaths on the {@link #trimmedIntegratedDOSGraph};
	 * initialize and build the {@link #solutionRootPathIndexMap} and {@link #integratedDOSNodeHostSolutionRootPathIndexMap};
	 * 
	 * must be invoked as a pre-processing step;
	 */
	private void identifyAllSolutionRootPaths() {
		this.solutionRootPathIndexMap = new HashMap<>();
		this.integratedDOSNodeHostSolutionRootPathIndexSetMap = new HashMap<>();
		
		int index = 0;
		for(IntegratedDOSGraphNode node:this.trimmedIntegratedDOSGraph.vertexSet()) {
			if(this.trimmedIntegratedDOSGraph.outDegreeOf(node)==0) {//root node
				//iteratively find out all nodes on the path
				List<IntegratedDOSGraphNode> nodeList = new ArrayList<>();
				IntegratedDOSGraphNode currentNode = node;
				nodeList.add(currentNode);
				while(this.trimmedIntegratedDOSGraph.inDegreeOf(currentNode)==1) {//a single child/depending node
					currentNode = this.trimmedIntegratedDOSGraph.getEdgeSource(
							this.trimmedIntegratedDOSGraph.incomingEdgesOf(currentNode).iterator().next());
					nodeList.add(currentNode);
				}
				
				//update solutionRootPathIndexMap and integratedDOSNodeHostSolutionRootPathIndexMap;
				this.solutionRootPathIndexMap.put(index, new SolutionRootPath(index, nodeList));
				for(IntegratedDOSGraphNode n:nodeList){
					if(!this.integratedDOSNodeHostSolutionRootPathIndexSetMap.containsKey(n))
						this.integratedDOSNodeHostSolutionRootPathIndexSetMap.put(n, new HashSet<>());
					this.integratedDOSNodeHostSolutionRootPathIndexSetMap.get(n).add(index);
				}
				
				index++;
			}
		}
	}
	
	/**
	 * set the following values of each IntegratedDOSNode on the {@link #trimmedIntegratedDOSGraph} 
	 * 1. {@link IntegratedDOSGraphNode#setInputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs(Set)}
	 * 		this requires to check both the {@link #trimmedIntegratedCFDGraph} and {@link #trimmedIntegratedDOSGraph};
	 * 2. {@link IntegratedDOSGraphNode#setAllowedSourceMetadataDataTypeSet(Set)};
	 * 		this requires to check the {@link #trimmedIntegratedDOSGraph};
	 * 3. {@link IntegratedDOSGraphNode#setParentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs(IntegratedDOSGraphNode)};
	 * 		this requires to check the {@link #trimmedIntegratedDOSGraph};
	 * 4. {@link IntegratedDOSGraphNode#setNodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs(IntegratedDOSGraphNode)};
	 * 		this requires to check the {@link #trimmedIntegratedDOSGraph};
	 * 5. {@link IntegratedDOSGraphNode#setEdgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs(IntegratedDOSGraphNode)};
	 * 		this requires to check the {@link #trimmedIntegratedDOSGraph};
	 * 
	 * note that this method will set the above values of all IntegratedDOSNodes on the {@link #trimmedIntegratedDOSGraph}, even though only a subset of them are involved in the solution set;
	 * 
	 *
	 * @throws SQLException 
	 * 
	 */
	private void preprocessIntegratedDOSNodes() throws SQLException {
		Map<IntegratedDOSGraphNode, Set<DataTableColumnName>> integratedDOSNodeInputColumnNameSetMap = new HashMap<>();
		
		//check each edge on the trimmedIntegratedDOSGraph and update related features
		this.trimmedIntegratedDOSGraph.edgeSet().forEach(e->{
			if(e.getType().equals(DOSEdgeType.OPERATION)) {
				Operation operation = this.appliedVisScheme.getOperationLookup().lookup(e.getOperationID());
				//the IntegratedDOSNode containing the input metadata of the operation specific to the edge;
				IntegratedDOSGraphNode inputMetadataNode = this.trimmedIntegratedDOSGraph.getEdgeTarget(e);
				
				//1. update the integratedDOSNodeInputColumnNameSetMap
				Map<MetadataID,Set<DataTableColumnName>> inputRecordMetadataIDInputColumnNameSetMap = 
						operation.getInputRecordMetadataIDInputColumnNameSetMap();
				if(inputMetadataNode.getMetadataID().getDataType().equals(DataType.RECORD)) {// the input metadata of the current edge is a record data;
					if(!integratedDOSNodeInputColumnNameSetMap.containsKey(inputMetadataNode))
						integratedDOSNodeInputColumnNameSetMap.put(inputMetadataNode, new HashSet<>());
					integratedDOSNodeInputColumnNameSetMap.get(inputMetadataNode).addAll(inputRecordMetadataIDInputColumnNameSetMap.get(inputMetadataNode.getMetadataID()));
				}
				
				//2. update the allowed data type related fields of the inputMetadataNode
				if(operation instanceof VfTreeTrimmingOperationBase) {//input metadata must be mapped by a vftree type data;
					inputMetadataNode.setInputMetadataOfVfTreeTrimmingOperationBase(true);
				}
				if(operation instanceof SingleGenericGraphAsInputOperation) {//input metadata must be mapped by a 
					inputMetadataNode.setInputMetadataOfSingleGenericGraphAsInputOperation(true);
				}
				
			}else {//DOSEdgeType.COMPOSITE_DATA_COMPONENT
				//update the composite-component metadata related fields
				
				IntegratedDOSGraphNode compositeMetadataNode = this.trimmedIntegratedDOSGraph.getEdgeTarget(e);
				IntegratedDOSGraphNode componentMetadataNode = this.trimmedIntegratedDOSGraph.getEdgeSource(e);
				//
				componentMetadataNode.setParentGenericGraphIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs(compositeMetadataNode);
				//
				GraphDataMetadata gdmd = (GraphDataMetadata) this.appliedVisScheme.getMetadataLookup().lookup(compositeMetadataNode.getMetadataID());
				if(gdmd.getNodeRecordMetadataID().equals(componentMetadataNode.getMetadataID())) {//componentMetadataNode is the node record data of compositeMetadataNode
					compositeMetadataNode.setNodeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs(componentMetadataNode);
				}else {//componentMetadataNode is the node record data of compositeMetadataNode
					compositeMetadataNode.setEdgeComponentIntegratedDOSNodeOnTrimmedIntegratedCFDAndDOSGraphs(componentMetadataNode);
				}
			}
		});
		
		
		//check each node on the {@link #trimmedIntegratedCFDGraph} to find out the input columns of depended record metadata as well as their IntegratedDOSNode
		//and add to the integratedDOSNodeInputColumnNameSetMap
		
		for(IntegratedCFDGraphNode dependingIntegratedCFDNode:this.trimmedIntegratedCFDGraph.vertexSet()){
			CompositionFunctionID cfID = dependingIntegratedCFDNode.getCfID();
			CompositionFunction cf = this.appliedVisScheme.getCompositionFunctionLookup().lookup(cfID);
			
			Map<MetadataID, Set<DataTableColumnName>> dependedRecordMetadataIDInputColumnNameSetMap = 
					cf.getDependedRecordMetadataIDInputColumnNameSetMap(this.appliedVisScheme);
			
			//identify the IntegratedDOSNode of each depended record metadata of the IntegratedCFDNode
			for(MetadataID dependedRecordMetadataID:dependedRecordMetadataIDInputColumnNameSetMap.keySet()) {
				VCDNodeImpl cfAssignedVCDNode = this.vcdGraph.getCFIDAssignedVCDNodeMap().get(cfID);
				VCDNodeImpl dependedRecordMetadataAssignedVCDNode = this.vcdGraph.getMetadataIDAssignedVCDNodeMap().get(dependedRecordMetadataID);
				
				IntegratedDOSGraphNode dependedIntegratedDOSNode;
				if(dependedRecordMetadataAssignedVCDNode.equals(cfAssignedVCDNode)) {
					dependedIntegratedDOSNode = 
							dependedRecordMetadataAssignedVCDNode.getVSCopyIndexMap()
							.get(dependingIntegratedCFDNode.getCopyIndex())
							.getAssignedMetadataIDIntegratedDOSNodeMap().get(dependedRecordMetadataID);
				}else {
					//cfg and owner record data are assigned to different VCDNodes
					//find out the copy index of the ownerRecordMetadataAssignedVCDNode to which the copy of the cfgAssignedVCDNode is linked
					VSCopy cfAssignedVCDNodeCopy = cfAssignedVCDNode.getVSCopyIndexMap().get(dependingIntegratedCFDNode.getCopyIndex());
					int dependedRecordMetadataAssignedVCDNodeCopyIndex = 
							cfAssignedVCDNodeCopy.getDependedVCDNodeLinkedCopyMap().get(dependedRecordMetadataAssignedVCDNode).getIndex();
					
					dependedIntegratedDOSNode = 
							dependedRecordMetadataAssignedVCDNode.getVSCopyIndexMap().get(
									dependedRecordMetadataAssignedVCDNodeCopyIndex).getAssignedMetadataIDIntegratedDOSNodeMap().get(dependedRecordMetadataID);
				}
				
				if(!integratedDOSNodeInputColumnNameSetMap.containsKey(dependedIntegratedDOSNode))
					integratedDOSNodeInputColumnNameSetMap.put(dependedIntegratedDOSNode, new HashSet<>());
				integratedDOSNodeInputColumnNameSetMap.get(dependedIntegratedDOSNode).addAll(dependedRecordMetadataIDInputColumnNameSetMap.get(dependedRecordMetadataID));
			}
			
		}
		
		//update the input column name set of each IntegratedDOSNode containing a record metadata with the integratedDOSNodeInputColumnNameSetMap;
		integratedDOSNodeInputColumnNameSetMap.forEach((k,v)->{
			k.setInputColumnNameSetOnTrimmedIntegratedCFDAndDOSGraphs(v);
		});
		
	}
	//////////////////////////////////////////////
	///methods for solution set selection and mapping
	//note that methods
	/**
	 * initialize related fields for solution set selection and mapping;
	 */
	public void initializeSolutionSetAndMapping() {
		this.selectedSolutionSetNodeMappingMap = new HashMap<>();
		this.unrepresentedSolutionRootPahtIndexSet = new HashSet<>();
		this.unrepresentedSolutionRootPahtIndexSet.addAll(this.solutionRootPathIndexMap.keySet());
	}
	
	/**
	 * select the given unselected and selectable IntegratedDOSNode in the solution set;
	 * 
	 * if the given IntegratedDOSNode is not selectable or the IntegratedDOSNode is already selected, throw exception;
	 * 
	 * @param node
	 */
	public void selectIntegratedDOSNode(IntegratedDOSGraphNode node) {
		if(!this.integratedDOSNodeHostSolutionRootPathIndexSetMap.containsKey(node)) //not selectable
			throw new IllegalArgumentException("given IntegratedDOSNode is not on any SolutionRootPath!");
		this.integratedDOSNodeHostSolutionRootPathIndexSetMap.get(node).forEach(i->{//not selectable
			if(!this.unrepresentedSolutionRootPahtIndexSet.contains(i))
				throw new IllegalArgumentException("at least one SolutionRootPath on which the given IntegratedDOSNode is already represented by a selected IntegratedDOSNode!");
		});
		if(this.selectedSolutionSetNodeMappingMap.containsKey(node))//already selected
			throw new IllegalArgumentException("given IntegratedDOSNode is already selected in solution set!");
		
		///update the selectedSolutionSetNodeMappingMap and unrepresentedSolutionRootPahtIndexSet
		this.selectedSolutionSetNodeMappingMap.put(node, null);
		this.integratedDOSNodeHostSolutionRootPathIndexSetMap.get(node).forEach(i->{
			this.unrepresentedSolutionRootPahtIndexSet.remove(i);
			this.solutionRootPathIndexMap.get(i).setRepresented(true);
		});
	}
	
	/**
	 * deselect the given selected IntegratedDOSNode from the solution set;
	 * 
	 * if the given IntegratedDOSNode is not already selected in the solution set, throw exception;
	 * @param node
	 */
	public void deselectIntegratedDOSNode(IntegratedDOSGraphNode node) {
		if(!this.selectedSolutionSetNodeMappingMap.containsKey(node))
			throw new IllegalArgumentException("given IntegratedDOSNode is not selected yet!");
		
		///update the selectedSolutionSetNodeMappingMap and unrepresentedSolutionRootPahtIndexSet
		this.selectedSolutionSetNodeMappingMap.remove(node);
		this.integratedDOSNodeHostSolutionRootPathIndexSetMap.get(node).forEach(i->{
			this.unrepresentedSolutionRootPahtIndexSet.add(i);
			this.solutionRootPathIndexMap.get(i).setRepresented(false);
		});
	}
	
	/**
	 * set the mapping for the given IntegratedDOSNode selected in the solution set as the given MetadataMapping, which may be null;
	 * 
	 * if the given IntegratedDOSNode is not already selected, throw exception;
	 * 
	 * if the given MetadataMapping is not null and is not consistent with the given IntegratedDOSNode in terms of the data type, throw exception;
	 * 
	 * if the given MetadataMapping is null, simply remove the current MetadataMapping of the given IntegratedDOSNode from the {@link #selectedSolutionSetNodeMappingMap}
	 * 
	 * @param node
	 * @param mapping
	 */
	public void setMapping(IntegratedDOSGraphNode node, MetadataMapping mapping) {
		if(!this.selectedSolutionSetNodeMappingMap.containsKey(node))
			throw new IllegalArgumentException("given IntegratedDOSNode is not selected yet!");
		
		if(mapping!=null) {
			if(!node.getAllowedSourceMetadataDataTypeSet().contains(mapping.getSourceMetadataID().getDataType()))
				throw new IllegalArgumentException("given MetadataMapping's source metadata's data type is not valid!");
			
			if(node.getMetadataID().equals(mapping.getTargetMetadataID()))
				throw new IllegalArgumentException("given MetadataMapping's target MetadataID is not the same with the contained MetadataID of the given IntegratedDOSNode!");
		}
		/////
		this.selectedSolutionSetNodeMappingMap.put(node, mapping);
	}
	
	/**
	 * return whether the solution set is fully selected;
	 * which is equivalent to whether every SolutionRootPath is represented by exactly one selected IntegratedDOSNode;
	 * @return
	 */
	public boolean solutionSetSelectionIsDone() {
		return this.unrepresentedSolutionRootPahtIndexSet.isEmpty();
	}
	
	/**
	 * return whether the solution set is fully selected and every selected IntegratedDOSNode has been assigned a non-null valid MetadataMapping;
	 * @return
	 */
	public boolean mappingIsDone() {
		if(!solutionSetSelectionIsDone())
			return false;
		
		for(IntegratedDOSGraphNode node:this.selectedSolutionSetNodeMappingMap.keySet()){
			if(this.selectedSolutionSetNodeMappingMap.get(node)==null)
				return false;
		}
		
		return true;
	}
	
	
	
	///////////////////////////////////////
	/**
	 * @return the selectedSolutionSetNodeMappingMap
	 */
	public Map<IntegratedDOSGraphNode, MetadataMapping> getSelectedSolutionSetNodeMappingMap() {
		return selectedSolutionSetNodeMappingMap;
	}


	/////////////////////////////////////////////
	/**
	 * class for a path on the trimmed integrated dos graph IntegratedDOSNode containing a list of IntegratedDOSNodes starting from a root node (no outgoing/depended node) 
	 * to the first descendant node with two or more children/depending nodes;
	 * 
	 * the set of SolutionRootPath is fixed once the trimmed integrated dos graph is built;
	 * 
	 * @author tanxu
	 *
	 */
	static class SolutionRootPath{
		/**
		 * unique index of this SolutionRootPath among all SolutionRootPath of the host trimmed integrated dos graph;
		 */
		private final int index;
		/**
		 * list of IntegratedDOSNode starting from the root node to the descendant node with two or more children/depending nodes;
		 * cannot be null or empty;
		 */
		private final List<IntegratedDOSGraphNode> integratedDOSNodeListOnPath;
		
	
		private boolean represented;
		/**
		 * 
		 * @param index
		 * @param integratedDOSNodeListOnPath
		 */
		SolutionRootPath(int index, List<IntegratedDOSGraphNode> integratedDOSNodeListOnPath){
			//
			if(integratedDOSNodeListOnPath==null||integratedDOSNodeListOnPath.isEmpty())
				throw new IllegalArgumentException("given integratedDOSNodeListOnPath cannot be null or empty!");
			
			
			this.index = index;
			this.integratedDOSNodeListOnPath = integratedDOSNodeListOnPath;
		}

		/**
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * @return the integratedDOSNodeListOnPath
		 */
		public List<IntegratedDOSGraphNode> getIntegratedDOSNodeListOnPath() {
			return integratedDOSNodeListOnPath;
		}
		
		////////////////////
		/**
		 * @return the represented
		 */
		protected boolean isRepresented() {
			return represented;
		}

		/**
		 * @param represented the represented to set
		 */
		protected void setRepresented(boolean represented) {
			this.represented = represented;
		}

	}
}
