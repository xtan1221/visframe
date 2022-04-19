package visinstance;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.VisSchemeAppliedArchive;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;
import dependency.vccl.VSCopy;
import dependency.vcd.VCDNode;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;

/**
 * 122220-update
 * 
 * helper class for a {@link VisSchemeBasedVisInstance} after {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance} and non-empty {@link #selectedVSCopySetInCoreShapeCFGSet};
 * 
 * specifically, this class will build the following two fields needed to construct a {@link VisSchemeBasedVisInstance}:
 * 
 * 1. {@link #coreShapeCFGIDSet}
 * 		only include those core ShapeCFG of the selectedVSCopySetInCoreShapeCFGSet;
 * 2. {@link #coreShapeCFGIDCFIDSetMap}
 * 		only include those CFs of the coreShapeCFGIDSet that are reproduced by the corresponding VisSchemeAppliedArchiveReproducedAndInsertedInstance;
 * 		this is because it is possible some CF of the core ShapeCFGs are created natively after the VisSchemeAppliedArchiveReproducedAndInsertedInstance was built and inserted (thus not reproduced and not a part of the VisSchemeAppliedArchiveReproducedAndInsertedInstance)
 * 			those CFs should not be included!!!
 * 
 * 		note that this concern is not necessary for non-core ShapeCFG's CFs, 
 * 		as long as the CFs of core ShapeCFGs is correctly dealt with (with above strategy), the non-core ShapeCFG's CFs on the induced CFD graph will always be reproduced;
 * 
 *
 * see {@link VisSchemeBasedVisInstance} for more details about these two collections;
 * 
 * @author tanxu
 * 
 */
public class VisSchemeBasedVisInstanceHelper {
	//////////////
	private final VisSchemeAppliedArchive visSchemeAppliedArchive;
	
	private final VisSchemeAppliedArchiveReproducedAndInsertedInstance visSchemeAppliedArchiveReproducedAndInsertedInstance;
	
	private final Set<VSCopy> selectedVSCopySetInCoreShapeCFGSet;
	
	/////////////////////////////
	/**
	 * 
	 */
	private Set<CompositionFunctionGroupID> coreShapeCFGIDSet;
	/**
	 * 
	 */
	private Map<CompositionFunctionGroupID, Set<CompositionFunctionID>> coreShapeCFGIDCFIDSetMap;
	
	/**
	 * 
	 * @param hostVisProjectDBContext
	 * @param visSchemeAppliedArchive
	 * @param visSchemeAppliedArchiveReproducedAndInsertedInstance
	 * @param nonLeafVSCopySetInCoreShapeCFGSet
	 * @throws SQLException
	 */
	public VisSchemeBasedVisInstanceHelper(
			VisSchemeAppliedArchive visSchemeAppliedArchive,
			VisSchemeAppliedArchiveReproducedAndInsertedInstance visSchemeAppliedArchiveReproducedAndInsertedInstance,
			Set<VSCopy> nonLeafVSCopySetInCoreShapeCFGSet
			) throws SQLException{
		/////validations
		if(visSchemeAppliedArchive == null)
			throw new IllegalArgumentException("given visSchemeAppliedArchive cannot be null!");
		if(visSchemeAppliedArchiveReproducedAndInsertedInstance == null)
			throw new IllegalArgumentException("given visSchemeAppliedArchiveReproducedAndInsertedInstance cannot be null!");
		
		if(nonLeafVSCopySetInCoreShapeCFGSet==null||nonLeafVSCopySetInCoreShapeCFGSet.isEmpty())
			throw new IllegalArgumentException("given nonLeafVSCopySetInCoreShapeCFGSet cannot be null or empty!");
		
		
		
		this.visSchemeAppliedArchive = visSchemeAppliedArchive;
		this.visSchemeAppliedArchiveReproducedAndInsertedInstance = visSchemeAppliedArchiveReproducedAndInsertedInstance;
		this.selectedVSCopySetInCoreShapeCFGSet = nonLeafVSCopySetInCoreShapeCFGSet;
		
		////
		this.perform();
	}
	
	
	/**
	 * build the {@link #coreShapeCFGIDSet} and {@link #coreShapeCFGIDCFIDSetMap}
	 * 
	 * 1. first build {@link #coreShapeCFGIDSet} and 
	 * find out the copy index set for each CompositionFunctionGroupID in VisScheme whose reproduced copies are included in the {@link #coreShapeCFGIDSet} ;
	 * 		for each VSCopy on the VCCL graph
	 * 			if the VSCopy is leaf node or is in the {@link #selectedVSCopySetInCoreShapeCFGSet}
	 * 				for each of the core ShapeCFG assigned to the VCDNode of the VSCopy,
	 * 					add the reproduced ShapeCFGs to the {@link #coreShapeCFGIDSet}
	 * 
	 * 2. build {@link #coreShapeCFGIDCFIDSetMap}
	 * @throws SQLException
	 */
	private final void perform() throws SQLException {
		this.coreShapeCFGIDSet = new HashSet<>();
		this.coreShapeCFGIDCFIDSetMap = new HashMap<>();
		
		this.selectedVSCopySetInCoreShapeCFGSet.forEach(copy->{
			VCDNode vcdNode = copy.getOwnerVCDNode();
			int copyIndex = copy.getIndex();
			
			vcdNode.getVSComponent().getCoreShapeCFGIDSet().forEach(coreShapeCFGID->{
				CompositionFunctionGroupID reproducedCoreShapeCFGID = 
						this.visSchemeAppliedArchiveReproducedAndInsertedInstance.getOriginalCFGIDCopyIndexReproducedCFGIDMapMap().get(coreShapeCFGID).get(copyIndex);
				
				this.coreShapeCFGIDSet.add(reproducedCoreShapeCFGID);
				
				//add each cf of the core shape CFG
				Set<CompositionFunctionID> reproducedCFIDSet = new HashSet<>();
				
				this.visSchemeAppliedArchive.getCFGIDTargetAssignedCFIDMapMapInVisScheme().get(reproducedCoreShapeCFGID).forEach((target,cfid)->{
					//note that cf is assigned to the same VCDNode as the owner cfg
					reproducedCFIDSet.add(
							this.visSchemeAppliedArchiveReproducedAndInsertedInstance.getOriginalCFIDCopyIndexReproducedCFIDMapMap().get(cfid).get(copyIndex));
				});
				
				this.coreShapeCFGIDCFIDSetMap.put(reproducedCoreShapeCFGID, reproducedCFIDSet);
			});
			
		});
		
	}
	
	
	////////////////////////////////////
	/**
	 * @return the coreShapeCFGIDSet
	 */
	public Set<CompositionFunctionGroupID> getBuiltCoreShapeCFGIDSet() {
		return coreShapeCFGIDSet;
	}
	

	/**
	 * @return the coreShapeCFGIDCFIDSetMap
	 */
	public Map<CompositionFunctionGroupID, Set<CompositionFunctionID>> getBuiltCoreShapeCFGIDCFIDSetMap() {
		return coreShapeCFGIDCFIDSetMap;
	}


}
