package context.scheme;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import basic.lookup.scheme.type.VisSchemeCompositionFunctionGroupLookup;
import basic.lookup.scheme.type.VisSchemeCompositionFunctionLookup;
import basic.lookup.scheme.type.VisSchemeIndependentFreeInputVariableTypeLookup;
import basic.lookup.scheme.type.VisSchemeMetadataLookup;
import basic.lookup.scheme.type.VisSchemeOperationLookup;
import context.project.VisProjectDBContext;
import dependency.cfd.SimpleCFDGraph;
import dependency.cfd.SimpleCFDGraphBuilder;
import dependency.dos.SimpleDOSGraph;
import dependency.dos.SimpleDOSGraphBuilder;
import exception.VisframeException;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import function.group.ShapeCFG;

/**
 * base builder for a VisScheme with a full defined and validated {@link VSComponentPrecedenceList} in a host {@link VisProjectDBContext};
 * 
 * @author tanxu
 */
public abstract class VisSchemeBuilderBase {
	private final VisProjectDBContext hostVisProjectDBContext;
	
	private final SimpleName name;
	private final VfNotes notes;
	private final VSComponentPrecedenceList componentPrecedenceList;
	
	///////////////////////////////////////
	////
	protected Set<CompositionFunctionID> initialCompositionFunctionIDSet;//CompositionFunctionID of ShapeCFGs in the current VSComponent list
	protected SimpleCFDGraph simpleCFDGraph;
	protected SimpleDOSGraph simpleDOSGraph;
	
	protected VisSchemeMetadataLookup metadataLookup; //contain all the Metadata involved in the CFD and DOS graphs
	protected VisSchemeOperationLookup operationLookup;//contain all Operation involved in DOS graph
	protected VisSchemeCompositionFunctionGroupLookup compositionFunctionGroupLookup; //contain all 
	protected VisSchemeCompositionFunctionLookup compositionFunctionLookup;
	protected VisSchemeIndependentFreeInputVariableTypeLookup independentFreeInputVariableTypeLookup;
	
	
	////
	private VisScheme visScheme;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param name
	 * @param notes 
	 * @param componentPrecedenceList not null or empty
	 */
	VisSchemeBuilderBase(VisProjectDBContext hostVisProjectDBContext, SimpleName name, VfNotes notes, List<VSComponent> componentPrecedenceList){
		if(hostVisProjectDBContext==null)
			throw new IllegalArgumentException("given hostVisProjectDBContext cannot be null!");
		if(name==null)
			throw new IllegalArgumentException("given name cannot be null!");
		if(notes==null)
			throw new IllegalArgumentException("given notes cannot be null!");
		if(componentPrecedenceList==null || componentPrecedenceList.isEmpty())
			throw new IllegalArgumentException("given componentPrecedenceList cannot be null or empty!");
		
		//check if the VSComponentPrecedenceList is valid
		//1. every VSComponent contains a disjoint non-empty set of core ShapeCFG from others
		//2. every core ShapeCFG have all of its mandatory targets assigned to one CompositionFunction
		Set<CompositionFunctionGroupID> coreShapeCFGIDSet = new HashSet<>();
		componentPrecedenceList.forEach(c->{
			c.getCoreShapeCFGIDSet().forEach(id->{
				CompositionFunctionGroup cfg = null;
				try {
					cfg = hostVisProjectDBContext.getHasIDTypeManagerController().getCompositionFunctionGroupManager().lookup(id);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(cfg==null)
					throw new IllegalArgumentException("one of the core shapeCFG is not found in the given hostVisProjectDBContext!");
				
				
				if(cfg instanceof ShapeCFG) {
					ShapeCFG shapeCFG = (ShapeCFG)cfg;
					try {
						Map<SimpleName, CompositionFunctionID> targetNameAssignedCFIDMap = hostVisProjectDBContext.getHasIDTypeManagerController().getCompositionFunctionManager().getTargetNameAssignedCFIDMap(id);
						
						shapeCFG.getMandatoryTargetNameSet().forEach(e->{
							if(!targetNameAssignedCFIDMap.keySet().contains(e))
								throw new IllegalArgumentException("at least one mandatory target of selected core shapeCFG is not assigned to a CompositionFunction!");
						});
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new IllegalArgumentException(e.getMessage());
					}
				}else {
					throw new IllegalArgumentException("one of the core shapeCFG is not of ShapeCFG type!");
				}
				
				
				if(coreShapeCFGIDSet.contains(id))
					throw new IllegalArgumentException("duplciate core shapeCFG is found in more than one VSComponents!");
				
				coreShapeCFGIDSet.add(id);
			});
			
			
		});
		
		
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		
		this.name = name;
		this.notes = notes;
		this.componentPrecedenceList = new VSComponentPrecedenceList(componentPrecedenceList);
		
		
		try {
			this.build();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("SQLException thrown!"+e.getMessage());
		}
	}
	
	/**
	 * @return the hostVisProjectDBContext
	 */
	public VisProjectDBContext getHostVisProjectDBContext() {
		return hostVisProjectDBContext;
	}


	/**
	 * @return the name
	 */
	public SimpleName getName() {
		return name;
	}

	/**
	 * @return the notes
	 */
	public VfNotes getNotes() {
		return notes;
	}

	/**
	 * @return the componentPrecedenceList
	 */
	public VSComponentPrecedenceList getComponentPrecedenceList() {
		return componentPrecedenceList;
	}

	/**
	 * @return the cFGDraphBuilder
	 */
	public SimpleCFDGraph getCFGDraph() {
		return simpleCFDGraph;
	}

	/**
	 * @return the dOSGraphBuilder
	 */
	public SimpleDOSGraph getDOSGraph() {
		return simpleDOSGraph;
	}

	/**
	 * return the built VisScheme
	 * @return the visScheme
	 */
	public VisScheme getVisScheme() {
		return visScheme;
	}
	
	
	//////////////////////////////////////////////////////
	/**
	 * @throws SQLException 
	 * 
	 */
	private void build() throws SQLException {
		this.buildCFDAndDOSGraph();
		//implemented in subclass
		this.buildMetadataLookup();
		this.buildOperationLookup();
		this.buildCompositionFunctionGroupLookup();
		this.buildCompositionFunctionLookup();
		this.buildIndependentFreeInputVariableTypeLookup();
		//
		this.buildVisScheme();
	}
	
	/**
	 * build the {@link #simpleCFDGraph} and {@link #simpleDOSGraph} for the full set of core ShapeCFGs as a pre-processing step;
	 * @throws SQLException 
	 */
	private void buildCFDAndDOSGraph() throws SQLException {
		//first find out the set of initial CompositionFunctionID
		this.initialCompositionFunctionIDSet = new HashSet<>();
		
		this.getComponentPrecedenceList().getList().forEach(c->{
			c.getCoreShapeCFGIDSet().forEach(cfgID->{
				try {
					this.initialCompositionFunctionIDSet.addAll(
							this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCompositionFunctionManager().getCompositionFunctionIDSetOfGroupID(cfgID)
					);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new VisframeException(e.getMessage());
				}
			});
		});
		
		SimpleCFDGraphBuilder CFDGraphBuilder = new SimpleCFDGraphBuilder(this.getHostVisProjectDBContext(), this.initialCompositionFunctionIDSet);
		this.simpleCFDGraph = CFDGraphBuilder.getBuiltGraph();
		
		
		//find out the depended record data MetadataID set of the built CFD graph
		SimpleDOSGraphBuilder DOSGraphBuilder = new SimpleDOSGraphBuilder(this.getHostVisProjectDBContext(),
				CFDGraphBuilder.getBuiltGraph().getDependedRecordMetadataIDInputVariableDataTableColumnNameSetMap().keySet());
		this.simpleDOSGraph = DOSGraphBuilder.getBuiltGraph();
	}
	
	
	/**
	 * build the {@link #metadataLookup}
	 * @throws SQLException 
	 * 
	 * 
	 */
	protected abstract void buildMetadataLookup() throws SQLException;

	/**
	 * build the {@link #operationLookup}
	 * @throws SQLException 
	 */
	protected abstract void buildOperationLookup() throws SQLException;

	/**
	 * build the {@link #compositionFunctionGroupLookup}
	 * @throws SQLException 
	 */
	protected abstract void buildCompositionFunctionGroupLookup() throws SQLException;
	
	/**
	 * build the {@link #compositionFunctionLookup}
	 */
	protected abstract void buildCompositionFunctionLookup();
	
	
	/**
	 * build the {@link #independentFreeInputVariableTypeLookup}
	 */
	protected abstract void buildIndependentFreeInputVariableTypeLookup();
	
	
	
	////////////////////////
	/**
	 * build the {@link #visScheme} with all the built data;
	 */
	private void buildVisScheme() {
		this.visScheme = new VisScheme(
				this.getName(), this.getNotes(), this.getComponentPrecedenceList(),
				this.metadataLookup, this.operationLookup, 
				this.compositionFunctionGroupLookup, this.compositionFunctionLookup, this.independentFreeInputVariableTypeLookup
				);
	}
}
