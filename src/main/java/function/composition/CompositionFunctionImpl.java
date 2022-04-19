package function.composition;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.component.ComponentFunction;
import function.component.PiecewiseFunction;
import function.component.SimpleFunction;
import function.group.CompositionFunctionGroupID;
import function.variable.input.InputVariable;
import function.variable.output.OutputVariable;
import metadata.MetadataID;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

public class CompositionFunctionImpl implements CompositionFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4058012001254386603L;
	
	////////////////////////////
	private final int indexID;
	private final VfNotes notes;
	
	private final MetadataID ownerRecordDataMetadataID;
	private final CompositionFunctionGroupID groupID;
	private final Set<SimpleName> assignedTargetNameSet;
	private final ComponentFunction root;
	
	/**
	 * constructor
	 * @param indexID
	 * @param notes
	 * @param groupID
	 * @param assignedTargetNameSet not null or empty
	 * @param rootFunction not null
	 */
	public CompositionFunctionImpl(
			int indexID, VfNotes notes,
			MetadataID ownerRecordDataMetadataID,
			CompositionFunctionGroupID groupID, 
			Set<SimpleName> assignedTargetNameSet,
			ComponentFunction rootFunction
			){
		if(notes==null)
			throw new IllegalArgumentException("given notes cannot be null!");
		
		if(ownerRecordDataMetadataID==null)
			throw new IllegalArgumentException("given ownerRecordDataMetadataID cannot be null!");
		
		if(groupID==null)
			throw new IllegalArgumentException("given groupID cannot be null!");
		
		if(assignedTargetNameSet==null||assignedTargetNameSet.isEmpty())
			throw new IllegalArgumentException("given assignedTargetNameSet cannot be null or empty!");
		
		if(rootFunction==null)
			throw new IllegalArgumentException("given rootFunction cannot be null!");
		
		
		this.indexID = indexID;
		this.notes = notes;
		this.ownerRecordDataMetadataID = ownerRecordDataMetadataID;
		this.groupID = groupID;
		this.assignedTargetNameSet = assignedTargetNameSet;
		this.root = rootFunction;
	}
	
	
	@Override
	public VfNotes getNotes() {
		return notes;
	}
	
	@Override
	public MetadataID getOwnerRecordDataMetadataID() {
		return this.ownerRecordDataMetadataID;
	}
	
	
	@Override
	public CompositionFunctionID getID() {
		return new CompositionFunctionID(this.getHostCompositionFunctionGroupID(),this.getIndexID());
	}

	@Override
	public int getIndexID() {
		return indexID;
	}
	
	@Override
	public CompositionFunctionGroupID getHostCompositionFunctionGroupID() {
		return groupID;
	}
	
	@Override
	public Set<SimpleName> getAssignedTargetNameSet() {
		return assignedTargetNameSet;
	}
	
	@Override
	public ComponentFunction getRootFunction() {
		return root;
	}
	

	@Override
	public Set<InputVariable> getInputVariableSet() {
		return this.getRootFunction().getInputVariableSetOfThisAndAllDownstreamComponentFunctions();
	}
	
	@Override
	public Set<OutputVariable> getOutputVariableSet() {
		return this.getRootFunction().getOutputVariableSetOfThisAndAllDownstreamComponentFunctions();
	}
	
	


	/////////////////////////////////
	/**
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public CompositionFunctionImpl reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		int reproducedIndexID = this.getIndexID();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		//note that CF is assigned to the same VCDNode with the host CFG, thus the copy index should be the same
		CompositionFunctionGroupID reproducedGroupID = 
				this.getHostCompositionFunctionGroupID().reproduce(
						hostVisProjctDBContext, 
						VSAArchiveReproducerAndInserter, 
						copyIndex);
		
		//find out the copy index of the VCDNode to which the owner record Metadata is assigned
		int copyIndexOfOwnerRecordMetadata = 
				VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOwnerRecordMetadata(this.getHostCompositionFunctionGroupID(), copyIndex);
		
		MetadataID reproducedOwnerRecordDataMetadataID = 
				this.getOwnerRecordDataMetadataID().reproduce(
						hostVisProjctDBContext, 
						VSAArchiveReproducerAndInserter,
						copyIndexOfOwnerRecordMetadata);//find out the copy index of owner record data
		
		
		///
		Set<SimpleName> reproducedAssignedTargetNameSet = new HashSet<>();
		
		for(SimpleName target:this.getAssignedTargetNameSet()) {
			reproducedAssignedTargetNameSet.add(target.reproduce());
		}
		
		//
		ComponentFunction reproducedRootFunction = 
				this.getRootFunction().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		
		/////
		return new CompositionFunctionImpl(
				reproducedIndexID,
				reproducedNotes,
				reproducedOwnerRecordDataMetadataID,
				reproducedGroupID,
				reproducedAssignedTargetNameSet,
				reproducedRootFunction
				);
	}

	//////////////////////////////////////
	//CF target value table calculation
	
	
	
	@Override
	public Map<Integer, ComponentFunction> getComponentFunctionIndexIDMap() {
		Map<Integer, ComponentFunction> ret = new LinkedHashMap<>();
		
		ret.put(this.getRootFunction().getIndexID(), this.getRootFunction());
		
		this.getRootFunction().getAllDownstreamComponentFunctionSet().forEach(e->{
			ret.put(e.getIndexID(),e);
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	private transient Map<ComponentFunction, ComponentFunction> componentFunctionPreviousComponentFunctionMap;
	/**
	 * 
	 */
	@Override
	public ComponentFunction getPreviousComponentFunction(ComponentFunction cf) {
		if(componentFunctionPreviousComponentFunctionMap==null) {
			this.componentFunctionPreviousComponentFunctionMap = new HashMap<>();
			
			this.getComponentFunctionIndexIDMap().forEach((id, comp)->{
				if(comp instanceof SimpleFunction) {
					SimpleFunction sf = (SimpleFunction)comp;
					if(sf.getNext()!=null)
						this.componentFunctionPreviousComponentFunctionMap.put(sf.getNext(), comp);
				}else {
					PiecewiseFunction pf = (PiecewiseFunction)comp;
					
					this.componentFunctionPreviousComponentFunctionMap.put(pf.getDefaultNextFunction(), comp);
					pf.getConditionPrecedenceIndexNextFunctionMap().forEach((index, next)->{
						this.componentFunctionPreviousComponentFunctionMap.put(next, comp);
					});
				}
				
			});
		}
		return componentFunctionPreviousComponentFunctionMap.get(cf);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap() {
		this.getRootFunction().buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap(
				new HashMap<>(), null, null);
	}
	
	
	/**
	 * {@inheritDoc}
	 * @throws SQLException 
	 */
	@Override
	public void calculate(CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator) throws SQLException {
		this.buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap();
		
		this.getRootFunction().calculate(CFTargetValueTableRunCalculator);
	}

	
	
	///////////////////////////////////////////
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignedTargetNameSet == null) ? 0 : assignedTargetNameSet.hashCode());
		result = prime * result + ((groupID == null) ? 0 : groupID.hashCode());
		result = prime * result + indexID;
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((ownerRecordDataMetadataID == null) ? 0 : ownerRecordDataMetadataID.hashCode());
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CompositionFunctionImpl))
			return false;
		CompositionFunctionImpl other = (CompositionFunctionImpl) obj;
		if (assignedTargetNameSet == null) {
			if (other.assignedTargetNameSet != null)
				return false;
		} else if (!assignedTargetNameSet.equals(other.assignedTargetNameSet))
			return false;
		if (groupID == null) {
			if (other.groupID != null)
				return false;
		} else if (!groupID.equals(other.groupID))
			return false;
		if (indexID != other.indexID)
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (ownerRecordDataMetadataID == null) {
			if (other.ownerRecordDataMetadataID != null)
				return false;
		} else if (!ownerRecordDataMetadataID.equals(other.ownerRecordDataMetadataID))
			return false;
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		return true;
	}


}
