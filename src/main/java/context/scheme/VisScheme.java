package context.scheme;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.VfNotes;
import basic.lookup.VisframeUDT;
import basic.lookup.project.type.udt.VisProjectVisSchemeManager;
import basic.lookup.scheme.type.VisSchemeCompositionFunctionGroupLookup;
import basic.lookup.scheme.type.VisSchemeCompositionFunctionLookup;
import basic.lookup.scheme.type.VisSchemeIndependentFreeInputVariableTypeLookup;
import basic.lookup.scheme.type.VisSchemeMetadataLookup;
import basic.lookup.scheme.type.VisSchemeOperationLookup;
import basic.process.NonReproduceableProcessType;
import context.VisframeContext;
import dependency.cfd.CFDNodeImpl;
import dependency.cfd.SimpleCFDGraph;
import dependency.cfd.SimpleCFDGraphBuilder;
import dependency.dos.SimpleDOSGraph;
import dependency.dos.SimpleDOSGraphBuilder;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroup;
import function.group.CompositionFunctionGroupID;
import metadata.MetadataID;
import metadata.record.RecordDataMetadata;
import rdb.table.data.DataTableSchemaID;


/**
 * 
 * 
 * @author tanxu
 * 
 */
public class VisScheme implements VisframeContext, HasName, HasNotes, VisframeUDT, NonReproduceableProcessType{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8890184777246438783L;
	
	
	//////////////////////////////////////////////////
	private final SimpleName name;
	private final VfNotes notes;
	////////////////
	private final VSComponentPrecedenceList vscomponentPrecedenceList;
	
	/////////////////
	/**
	 * contains the full set of Metadata involved in the CFD and DOS graph;
	 * 
	 * note that the Metadata here may not be necessarily the same with the ones from the host VisProjectDBContext from which this VisScheme is created;
	 * rather, the Metadata are more likely to contain a subset of structure of the original one in terms of the data table columns(RECORD) and feature columns (GRAPH and vfTree);
	 * 
	 * 1. Metadata
	 * 
	 * 2. for Record data
	 * 		data table columns
	 * 			1. primary key columns
	 * 			2. additional columns that are used by CompositionFunction and/or Operation
	 * 
	 * 3. for Graph data
	 * 		node feature
	 * 			1. node record data (see 2)
	 * 			2. id columns
	 * 			3. additional columns that are used by CompositionFunction and/or Operation
	 * 		edge feature
	 * 
	 * 		
	 * 4. for Vftree data
	 * 
	 */
	private final VisSchemeMetadataLookup metadataLookup; //contain all the Metadata involved in the CFD and DOS graphs
	private final VisSchemeOperationLookup operationLookup;//contain all Operation involved in DOS graph
	private final VisSchemeCompositionFunctionGroupLookup compositionFunctionGroupLookup; //contain all 
	private final VisSchemeCompositionFunctionLookup compositionFunctionLookup;
	private final VisSchemeIndependentFreeInputVariableTypeLookup independentFreeInputVariableTypeLookup;
	
	
	///////////////////////////////
	/**
	 * unique ID of this VisScheme in the host VisProjectDBContext;
	 * only relevant when this VisScheme is residing in a host VisProjectDBContext;
	 * should be generated when this VisScheme is imported or created in a host VisProjectDBContext;
	 */
	private Integer UIDInHostVisProject;
	
	/**
	 * whether this VisScheme is imported or created natively in the host VisProjectDBContext;
	 * only relevant when this VisScheme is residing in a host VisProjectDBContext;
	 */
	private Boolean importedInHostVisProject;
	
	
	////////////////////////////////////
	/**
	 * the full set of core ShapeCFG of all VSComponents of this VisScheme;
	 */
	private transient Set<CompositionFunctionGroupID> coreShapeCFGIDSet;
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param precedenceList
	 * @param metadataLookup
	 * @param operationLookup
	 * @param compositionFunctionGroupLookup
	 * @param compositionFunctionLookup
	 * @param independentFreeInputVariableTypeLookup
	 */
	public VisScheme(
			SimpleName name, VfNotes notes,
			VSComponentPrecedenceList precedenceList, 
			
			VisSchemeMetadataLookup metadataLookup,
			VisSchemeOperationLookup operationLookup,
			VisSchemeCompositionFunctionGroupLookup compositionFunctionGroupLookup,
			VisSchemeCompositionFunctionLookup compositionFunctionLookup,
			VisSchemeIndependentFreeInputVariableTypeLookup independentFreeInputVariableTypeLookup
			){
		//TODO 
		//validations
		
		
		
		this.name = name;
		this.notes = notes;
		this.vscomponentPrecedenceList = precedenceList;
		this.metadataLookup = metadataLookup;
		this.independentFreeInputVariableTypeLookup = independentFreeInputVariableTypeLookup;
		this.operationLookup = operationLookup;
		this.compositionFunctionGroupLookup = compositionFunctionGroupLookup;
		this.compositionFunctionLookup = compositionFunctionLookup;
	}
	
	
	/////////////////////////////////////////
	
	public VSComponentPrecedenceList getVSComponentPrecedenceList() {
		return vscomponentPrecedenceList;
	}
	
	public Integer getUID() {
		return UIDInHostVisProject;
	}

	/**
	 * set the UID of this VisScheme before it is inserted into a host {@link VisProjectDBContext};
	 * 
	 * this should be done for both VisSchemes imported from a serialized file and created from scratch;
	 * 
	 * the uid can be generated by {@link VisProjectVisSchemeManager#findNextAvaiableUID()} method of the host {@link VisProjectDBContext};
	 * 
	 * for a residing VisScheme in a host {@link VisProjectDBContext}, if it is to be exported to a serialized file, the uid need to be set to null before serialization;
	 * 
	 * @param uID
	 */
	public void setUID(Integer uID) {
		UIDInHostVisProject = uID;
	}
	
	/**
	 * whether this VisScheme is imported from a serialized file or not; only relevant when this VisScheme is residing in a host {@link VisProjectDBContext};
	 * @return
	 */
	public Boolean isImported() {
		return importedInHostVisProject;
	}
	
	/**
	 * 
	 * @param imported
	 */
	public void setImported(Boolean imported) {
		this.importedInHostVisProject = imported;
	}
	
	
	/////////////////////////////////////////
	
	@Override
	public VfNotes getNotes() {
		return notes;
	}
	
	@Override
	public SimpleName getName() {
		return name;
	}
	
	@Override
	public VisSchemeID getID() {
		return new VisSchemeID(this.getUID());
	}
	
	//////////////////////////////////////
	@Override
	public VisSchemeMetadataLookup getMetadataLookup() {
		return this.metadataLookup;
	}
	
	@Override
	public VisSchemeOperationLookup getOperationLookup() {
		return this.operationLookup;
	}

	@Override
	public VisSchemeCompositionFunctionGroupLookup getCompositionFunctionGroupLookup() {
		return this.compositionFunctionGroupLookup;
	}
	
	@Override
	public VisSchemeCompositionFunctionLookup getCompositionFunctionLookup() {
		return this.compositionFunctionLookup;
	}
	
	

	@Override
	public VisSchemeIndependentFreeInputVariableTypeLookup getIndependentFreeInputVariableTypeLookup() {
		return independentFreeInputVariableTypeLookup;
	}
	
	
	//////////////////////////////////////////////////////
	@Override
	public CompositionFunctionID getCompositionFuncitionID(CompositionFunctionGroupID cfgID, SimpleName targetName) {
		return this.getCompositionFunctionLookup().getCompositionFunctionGroupIDTargetNameAssignedCFIDMapMap().get(cfgID).get(targetName);
	}
	

	
	@Override
	public Set<CompositionFunctionID> getCompositionFunctionIDSetOfGroupID(CompositionFunctionGroupID cfgID) {
		return new HashSet<>(this.getCompositionFunctionLookup().getCompositionFunctionGroupIDTargetNameAssignedCFIDMapMap().get(cfgID).values());
	}



	@Override
	public DataTableSchemaID getDataTableSchemaID(MetadataID recordMetadataID) {
		RecordDataMetadata rmd = (RecordDataMetadata)this.getMetadataLookup().lookup(recordMetadataID);
		
		return rmd.getDataTableSchema().getID();
	}

	
	
	@Override
	public DataTableSchemaID getOwnerRecordDataTableSchemaID(CompositionFunctionGroupID cfgID) {
		
		return this.getDataTableSchemaID(this.getCompositionFunctionGroupLookup().lookup(cfgID).getOwnerRecordDataMetadataID());
	}

	
	//////////////////////////////////////////////
	
	public Set<CompositionFunctionGroupID> getCoreShapeCFGIDSet(){
		if(this.coreShapeCFGIDSet == null) {
			this.coreShapeCFGIDSet = new HashSet<>();
			this.vscomponentPrecedenceList.getList().forEach(c->{
				this.coreShapeCFGIDSet.addAll(c.getCoreShapeCFGIDSet());
			});
		}
		
		return this.coreShapeCFGIDSet;
	}
	
	
	///////////////////////////////
	/**
	 * SimpleCFDGraph of this VisScheme
	 */
	private transient SimpleCFDGraph cfdGraph;
	/**
	 * build (if not yet) and return the SimpleCFDGraph of this VisScheme;
	 * 
	 * note that the initial CF ID set is composed of the CFID set of core ShapeCFGs assigned to each VSComponents (?TODO)
	 * 
	 * @return
	 * @throws SQLException
	 */
	public SimpleCFDGraph getVisSchemeCFDGraph() {
		if(this.cfdGraph==null) {
			Set<CompositionFunctionID> initialCFIDSet = new HashSet<>();
			this.getVSComponentPrecedenceList().getList().forEach(v->{
				v.getCoreShapeCFGIDSet().forEach(cfgid->{
					initialCFIDSet.addAll(
							this.getCompositionFunctionIDSetOfGroupID(cfgid));
				});
			});
			
			SimpleCFDGraphBuilder builder;
			try {
				builder = new SimpleCFDGraphBuilder(this, initialCFIDSet);
				this.cfdGraph = builder.getBuiltGraph();
			} catch (SQLException e) {//SQLException should never be thrown here since no sql query is involved
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return cfdGraph;
	}
	
	/**
	 * SimpleDOSGraph of this VisScheme
	 */
	private transient SimpleDOSGraph dosGraph;
	/**
	 * build(if not yet) and return the SimpleDOSGraph of this VisScheme;
	 * 
	 * note that initial MetadataID set is composed of (TODO check! and validate)
	 * 		1. owner record data of all cfgs in the {@link #cfdGraph}
	 * 		2. depended record data of all CFs in the {@link #cfdGraph};
	 * @return
	 * @throws SQLException 
	 */
	public SimpleDOSGraph getVisSchemeDOSGraph() {
		if(this.dosGraph == null) {
			
			Set<MetadataID> inducingMetadataIDSet = new HashSet<>(); 
			
			try {
				for(CFDNodeImpl v:this.getVisSchemeCFDGraph().getUnderlyingGraph().vertexSet()){
					CompositionFunctionGroup cfg = this.getCompositionFunctionGroupLookup().lookup(v.getCFID().getHostCompositionFunctionGroupID());
					inducingMetadataIDSet.add(cfg.getOwnerRecordDataMetadataID());
					
					CompositionFunction cf = this.getCompositionFunctionLookup().lookup(v.getCFID());
					inducingMetadataIDSet.addAll(cf.getDependedRecordMetadataIDInputColumnNameSetMap(this).keySet());
					
				}
			} catch (SQLException e) {//SQLException should never be thrown here since no sql query is involved
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			SimpleDOSGraphBuilder builder = new SimpleDOSGraphBuilder(this, inducingMetadataIDSet);
			this.dosGraph = builder.getBuiltGraph();
		}
		
		return this.dosGraph;
	}


	///////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((UIDInHostVisProject == null) ? 0 : UIDInHostVisProject.hashCode());
		result = prime * result
				+ ((compositionFunctionGroupLookup == null) ? 0 : compositionFunctionGroupLookup.hashCode());
		result = prime * result + ((compositionFunctionLookup == null) ? 0 : compositionFunctionLookup.hashCode());
		result = prime * result + ((importedInHostVisProject == null) ? 0 : importedInHostVisProject.hashCode());
		result = prime * result + ((independentFreeInputVariableTypeLookup == null) ? 0
				: independentFreeInputVariableTypeLookup.hashCode());
		result = prime * result + ((metadataLookup == null) ? 0 : metadataLookup.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((operationLookup == null) ? 0 : operationLookup.hashCode());
		result = prime * result + ((vscomponentPrecedenceList == null) ? 0 : vscomponentPrecedenceList.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VisScheme))
			return false;
		VisScheme other = (VisScheme) obj;
		if (UIDInHostVisProject == null) {
			if (other.UIDInHostVisProject != null)
				return false;
		} else if (!UIDInHostVisProject.equals(other.UIDInHostVisProject))
			return false;
		if (compositionFunctionGroupLookup == null) {
			if (other.compositionFunctionGroupLookup != null)
				return false;
		} else if (!compositionFunctionGroupLookup.equals(other.compositionFunctionGroupLookup))
			return false;
		if (compositionFunctionLookup == null) {
			if (other.compositionFunctionLookup != null)
				return false;
		} else if (!compositionFunctionLookup.equals(other.compositionFunctionLookup))
			return false;
		if (importedInHostVisProject == null) {
			if (other.importedInHostVisProject != null)
				return false;
		} else if (!importedInHostVisProject.equals(other.importedInHostVisProject))
			return false;
		if (independentFreeInputVariableTypeLookup == null) {
			if (other.independentFreeInputVariableTypeLookup != null)
				return false;
		} else if (!independentFreeInputVariableTypeLookup.equals(other.independentFreeInputVariableTypeLookup))
			return false;
		if (metadataLookup == null) {
			if (other.metadataLookup != null)
				return false;
		} else if (!metadataLookup.equals(other.metadataLookup))
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
		if (operationLookup == null) {
			if (other.operationLookup != null)
				return false;
		} else if (!operationLookup.equals(other.operationLookup))
			return false;
		if (vscomponentPrecedenceList == null) {
			if (other.vscomponentPrecedenceList != null)
				return false;
		} else if (!vscomponentPrecedenceList.equals(other.vscomponentPrecedenceList))
			return false;
		return true;
	}
}
