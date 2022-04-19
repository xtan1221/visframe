package context.project.process.logtable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import basic.lookup.HasID;
import basic.lookup.ID;
import context.scheme.VisSchemeID;
import context.scheme.appliedarchive.VisSchemeAppliedArchiveID;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstanceID;
import fileformat.FileFormatID;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import function.variable.independent.IndependentFreeInputVariableTypeID;
import importer.DataImporterID;
import metadata.MetadataID;
import operation.OperationID;
import visinstance.VisInstanceID;
import visinstance.run.VisInstanceRunID;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunID;
import visinstance.run.layoutconfiguration.VisInstanceRunLayoutConfigurationID;

/**
 * a collection of ID set organized in each type;
 * 
 * used to group a set of IDs that are to be manipulated together;
 * 
 * =========================================
 * 102120-update
 * note that table schema IDs related with CFTargetValueTableRun should not be included in VfIDCollection;
 * 1. CFTargetValueTableSchemaID should be explicitly and directly controlled by the CFTargetValueTableRun
 * 		1. when CFTargetValueTableRun is inserted and calculated, the CFTargetValueTableSchemaID is created and inserted;
 * 		2. when the CFTargetValueTableRun is removed, the CFTargetValueTableSchema is removed;
 * 				this should be implemented in the {@link VisProjectCFTargetValueTableRunManager#delete()} method, 
 * 				thus will be triggered in the rollback after crash;
		
 * 2. for PiecewiseFunctionIndexIDOutputIndexValueTableSchemaID and TemporaryOutputVariableValueTableSchemaID,
 * 		they should be removed once the CFTargetValueTableRun calculation is done;
 * 		for rollback after crash, simply remove all tables in the CALCULATION schema
 * 
 * 
 * ==========================================
 * 111620-
 * DataTableSchemaID is removed;
 * 
 * @author tanxu
 *	
 */
public class VfIDCollection implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -663701584155023270L;

	/**
	 * 
	 */
	private final Map<Class<? extends ID<? extends HasID>>, Set<ID<? extends HasID>>> idTypeSetMap;
	
	
	/////////////
	private transient Set<ID<? extends HasID>> currentFullSet;
	
	/**
	 * constructor
	 */
	public VfIDCollection(){
		this.idTypeSetMap = new HashMap<>();
		
		this.idTypeSetMap.put(FileFormatID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(DataImporterID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(MetadataID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(OperationID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(CompositionFunctionGroupID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(CompositionFunctionID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(IndependentFreeInputVariableTypeID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(VisSchemeID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(VisSchemeAppliedArchiveID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(VisSchemeAppliedArchiveReproducedAndInsertedInstanceID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(VisInstanceID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(VisInstanceRunID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(CFTargetValueTableRunID.class, new LinkedHashSet<ID<? extends HasID>>());
		this.idTypeSetMap.put(VisInstanceRunLayoutConfigurationID.class, new LinkedHashSet<ID<? extends HasID>>());
		
		///////////////
//		this.idTypeSetMap.put(DataTableSchemaID.class, new HashSet<ID<? extends HasID>>());
		
//		this.idTypeSetMap.put(CFTargetValueTableSchemaID.class, new HashSet<ID<? extends HasID>>());
//		this.idTypeSetMap.put(PiecewiseFunctionIndexIDOutputIndexValueTableSchemaID.class, new HashSet<ID<? extends HasID>>());
//		this.idTypeSetMap.put(TemporaryOutputVariableValueTableSchemaID.class, new HashSet<ID<? extends HasID>>());

	}
	
	/**
	 * add a new ID to the collection;
	 * @param id
	 */
	public void addID(ID<? extends HasID> id) {
		this.idTypeSetMap.get(id.getClass()).add(id);
		if(this.currentFullSet!=null) {
			this.getAllIDs().add(id);
		}
	}
	
	
	/**
	 * remove the ID from the collection
	 * @param id
	 */
	public void removeID(ID<? extends HasID> id) {
		this.idTypeSetMap.get(id.getClass()).remove(id);
		if(this.currentFullSet!=null) {
			this.getAllIDs().remove(id);
		}
	}
	
	
	
	/**
	 * return the full set of IDs in this collection;
	 * @return
	 */
	public Set<ID<? extends HasID>> getAllIDs(){
		if(this.currentFullSet == null) {
			this.currentFullSet = new HashSet<>();
			for(Class<? extends ID<? extends HasID>> type:this.idTypeSetMap.keySet()) {
				this.currentFullSet.addAll(this.idTypeSetMap.get(type));
			}
		}
		return this.currentFullSet;
	}
	
	/**
	 * return the set of IDs of the given type;
	 * @param idType
	 * @return
	 */
	public Set<ID<? extends HasID>> getIDSet(Class<? extends ID<? extends HasID>> idType){
		return this.idTypeSetMap.get(idType);
	}
	/**
	 * return whether this {@link VfIDCollection} is empty or not;
	 * @return
	 */
	public boolean isEmpty() {
		return this.getAllIDs().isEmpty();
	}


	///////////////////////////////////////////////
	@Override
	public String toString() {
		String ret = "";
		
		boolean nothingAddedYetOuter = true;
		for(Class<? extends ID<? extends HasID>> type:this.idTypeSetMap.keySet()) {
			////
			if(this.idTypeSetMap.get(type).isEmpty()) {
				continue;
			}
			
			String typeString = type.getSimpleName().concat("[");
			boolean nothingAddedYetInner = true;
			for(ID<? extends HasID> id:this.idTypeSetMap.get(type)) {
				if(nothingAddedYetInner) {
					nothingAddedYetInner = false;
				}else {
					typeString = typeString.concat(",");
				}
				typeString = typeString.concat(id.toString());
			}
			typeString = typeString.concat("]");
			//////////
			
			if(nothingAddedYetOuter) {
				nothingAddedYetOuter = false;
			}else {
				ret = ret.concat(System.lineSeparator());
			}
			
			ret = ret.concat(typeString);
		}
		
		
//		return "VfIDCollection [idTypeSetMap=" + ret + "]";
		return ret;
	}

	////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idTypeSetMap == null) ? 0 : idTypeSetMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VfIDCollection))
			return false;
		VfIDCollection other = (VfIDCollection) obj;
		if (idTypeSetMap == null) {
			if (other.idTypeSetMap != null)
				return false;
		} else if (!idTypeSetMap.equals(other.idTypeSetMap))
			return false;
		return true;
	}
}
