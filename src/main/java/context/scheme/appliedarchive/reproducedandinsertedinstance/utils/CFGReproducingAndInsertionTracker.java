package context.scheme.appliedarchive.reproducedandinsertedinstance.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.lookup.project.type.udt.VisProjectCompositionFunctionGroupManager;
import context.project.VisProjectDBContext;
import context.scheme.VisScheme;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import dependency.cfd.integrated.IntegratedCFDGraphNode;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;

/**
 * 
 * @author tanxu
 *
 */
public class CFGReproducingAndInsertionTracker {
	private final VisSchemeAppliedArchiveReproducerAndInserter hostVisSchemeAppliedArchiveReproducerAndInserter;

	///////////////////////
	private VisScheme appliedVisScheme;
	
	/////////////////////
	/**
	 * map from the original CompositionFunctionGroupID in applied VisScheme
	 * to the map 
	 * 		from the copy index of the original CompositionFunctionGroupID to be reproduced and inserted
	 * 		to the reproduced CompositionFunctionGroup corresponding to the original CompositionFunctionGroupID copy;
	 * 
	 * used to insert the reproduced CFGs into the host VisProjectDBContext;
	 */
	private Map<CompositionFunctionGroupID, Map<Integer, CompositionFunctionGroup>> originalCFGIDCopyIndexReproducedCFGMapMap;
	/**
	 * 
	 */
	private List<CompositionFunctionGroupID> reproducedCFGIDListByInsertionOrder;
	
	//***************************************
	/**
	 * map from orignal CompositionFunctionGroupID in VisScheme 
	 * to map
	 * 		from the copy index of the VCDNode to which the CompositionFunctionGroupID is assigned
	 * 		to the reproduced CompositionFunctionGroupID
	 * 
	 * must be updated whenever a CompositionFunctionGroupID is newly reproduced;
	 * 		invoked from {@link CompositionFunctionGroupID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)}
	 * 
	 * or a CompositionFunctionGroup is rolled back;
	 * 
	 * also used to build the target VisSchemeAppliedArchiveReproducedAndInsertedInstance as constructor parameter;
	 */
	private Map<CompositionFunctionGroupID, Map<Integer,CompositionFunctionGroupID>> originalCFGIDCopyIndexReproducedCFGIDMapMap;
	
	
//	/**
//	 * invoked from {@link CompositionFunctionGroupID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)} whenever a CompositionFunctionGroupID is newly reproduced;
//	 * @param originalID
//	 * @param copyIndex
//	 * @param reproducedID
//	 */
//	public void addToOriginalCFGIDCopyIndexReproducedCFGIDMapMap(
//			CompositionFunctionGroupID originalID, int copyIndex, CompositionFunctionGroupID reproducedID) {
//		if(!this.originalCFGIDCopyIndexReproducedCFGIDMapMap.containsKey(originalID))
//			this.originalCFGIDCopyIndexReproducedCFGIDMapMap.put(originalID, new HashMap<>());
//		
//		this.originalCFGIDCopyIndexReproducedCFGIDMapMap.get(originalID).put(copyIndex, reproducedID);
//	}
	

	
	/**
	 * constructor
	 * @param VSAReproducerAndInserter
	 * @param hostVisProjectDBContext
	 * @param appliedVisScheme
	 * @param applierArchive
	 * @throws SQLException 
	 */
	public CFGReproducingAndInsertionTracker(
			VisSchemeAppliedArchiveReproducerAndInserter hostVisSchemeAppliedArchiveReproducerAndInserter
			) throws SQLException{
			
		this.hostVisSchemeAppliedArchiveReproducerAndInserter = hostVisSchemeAppliedArchiveReproducerAndInserter;
		
		this.appliedVisScheme = this.hostVisSchemeAppliedArchiveReproducerAndInserter.getHostVisProjectDBContext().getHasIDTypeManagerController().getVisSchemeManager().lookup(this.hostVisSchemeAppliedArchiveReproducerAndInserter.getAppliedArchive().getAppliedVisSchemeID());
		
		
		////////////
		this.originalCFGIDCopyIndexReproducedCFGIDMapMap = new HashMap<>();
		this.originalCFGIDCopyIndexReproducedCFGMapMap = new HashMap<>();
		this.reproducedCFGIDListByInsertionOrder = new ArrayList<>();
		
//		this.build();
	}
	
	/**
	 * reproduce all CFGs based on current reproduced and inserted Operations;
	 * 
	 * this will initialize and build the 
	 * 		{@link #originalCFGIDCopyIndexReproducedCFGIDMapMap}
	 * 		{@link #originalCFGIDCopyIndexReproducedCFGMapMap}
	 * 
	 * should be invoked only after all Operations are reproduced and inserted into host VisProjectDBContext;
	 * 		note that this is not checked in this method; rather, the invoker should be responsible for this checking;
	 * 
	 * @throws SQLException
	 */
	public void build() throws SQLException {
		//////////////////
		for(IntegratedCFDGraphNode v:this.hostVisSchemeAppliedArchiveReproducerAndInserter.getAppliedArchive().getTrimmedIntegratedCFDGraph().vertexSet()){
			int copyIndex = v.getCopyIndex();
			
			CompositionFunctionGroupID cfgID = v.getCfID().getHostCompositionFunctionGroupID();
			
			if(this.originalCFGIDCopyIndexReproducedCFGMapMap.containsKey(cfgID) && this.originalCFGIDCopyIndexReproducedCFGMapMap.get(cfgID).containsKey(copyIndex)) {
				//already reproduced
			}else {
				if(!this.originalCFGIDCopyIndexReproducedCFGMapMap.containsKey(cfgID)) {
					this.originalCFGIDCopyIndexReproducedCFGMapMap.put(cfgID, new HashMap<>());
					this.originalCFGIDCopyIndexReproducedCFGIDMapMap.put(cfgID, new HashMap<>());
				}
				
				CompositionFunctionGroup originalCFG = this.appliedVisScheme.getCompositionFunctionGroupLookup().lookup(cfgID);
				CompositionFunctionGroup reproducedCFG = originalCFG.reproduce(
						this.hostVisSchemeAppliedArchiveReproducerAndInserter.getHostVisProjectDBContext(), 
						this.hostVisSchemeAppliedArchiveReproducerAndInserter, 
						copyIndex);
				
				this.originalCFGIDCopyIndexReproducedCFGMapMap.get(cfgID).put(copyIndex, reproducedCFG);
				this.originalCFGIDCopyIndexReproducedCFGIDMapMap.get(cfgID).put(copyIndex, reproducedCFG.getID());
				this.reproducedCFGIDListByInsertionOrder.add(reproducedCFG.getID());
			}
		}
	}

	/**
	 * remove all the reproduced CFGs;
	 * 
	 * should be invoked when all reproduced and inserted CFGs are rolled back;
	 * 
	 */
	public void removeAllReproducedCFGs() {
		this.originalCFGIDCopyIndexReproducedCFGIDMapMap.clear();
		this.originalCFGIDCopyIndexReproducedCFGMapMap.clear();
		this.reproducedCFGIDListByInsertionOrder.clear();
	}

	///////////////////////////////
	
	/**
	 * return the reproduced CFGs that need to be inserted into host VisProjectDBContext;
	 * @return the originalCFGIDCopyIndexReproducedCFGMapMap
	 */
	public Map<CompositionFunctionGroupID, Map<Integer, CompositionFunctionGroup>> getOriginalCFGIDCopyIndexReproducedCFGMapMap() {
		return originalCFGIDCopyIndexReproducedCFGMapMap;
	}


	/**
	 * 
	 * @return the originalCFGIDCopyIndexReproducedCFGIDMapMap
	 */
	public Map<CompositionFunctionGroupID, Map<Integer,CompositionFunctionGroupID>> getOriginalCFGIDCopyIndexReproducedCFGIDMapMap() {
		return originalCFGIDCopyIndexReproducedCFGIDMapMap;
	}
	
	/**
	 * @return the reproducedCFGIDListByInsertionOrder
	 */
	public List<CompositionFunctionGroupID> getReproducedCFGIDListByInsertionOrder() {
		return reproducedCFGIDListByInsertionOrder;
	}
	
	/**
	 * check if the given reproduced CompositionFunctionGroupID have already been generated by any previously reproduced CFG;
	 * 
	 * facilitate to reproduce a unique non-occupied CFGID in {@link CompositionFunctionGroupID#reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)};
	 * 
	 * note that different from Operations, CFGs are all reproduced before inserted into host VisProjectDBContext, 
	 * thus in the {@link VisProjectCompositionFunctionGroupManager#buildReproducedID(CompositionFunctionGroupID, VisSchemeAppliedArchiveReproducerAndInserter)},
	 * to check if a CFGID is already occupied or not, need to check both those in the management table and those in the {@link #reproducedCFGIDListByInsertionOrder}
	 * 
	 * @param reproducedID
	 * @return
	 */
	public boolean reproducedCFGIDAlreadyGeneratedByThisRun(CompositionFunctionGroupID reproducedIDToBeChecked) {
		for(CompositionFunctionGroupID reproducedID:this.reproducedCFGIDListByInsertionOrder) {
			if(reproducedIDToBeChecked.equals(reproducedID))
				return true;
		}
		
		return false;
	}
	/////////////////////////

}
