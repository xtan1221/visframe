package function.group;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import graphics.property.tree.GraphicsPropertyTree;
import metadata.MetadataID;


/**
 * a sub-type of {@link GraphicsPropertyCFG} with an arbitrary set of target {@link GraphicsPropertyTree}s, 
 * each of which is assigned an unique tree name and a root {@link GraphicsPropertyNode} predefined by visframe;
 *  
 * the difference between {@link IndependentGraphicsPropertyCFG} and {@link ShapeCFG} is how the set of target {@link GraphicsPropertyTree} is created;
 * 1. for {@link IndependentGraphicsPropertyCFG}, the {@link GraphicsPropertyTree}s are arbitrary;
 * 2. for {@link ShapeCFG}, the {@link GraphicsPropertyTree}s are predefined by the specific {@link VfShapeType}
 * 
 * 3. the calculated values for {@link ShapeCFG} can be used to directly make Shapes for visualization layout, 
 * 		while {@link IndependentGraphicsPropertyCFG} can only be used to facilitate calculation of other {@link CompositionFunctionGroup}s
 * @author tanxu
 *
 */
public final class IndependentGraphicsPropertyCFG extends GraphicsPropertyCFG {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1196058489297159674L;
	
	/////////////////////////
	public static final SimpleName TYPE_NAME = new SimpleName("IndependentGraphicsPropertyCFG");
	public static final VfNotes TYPE_NOTES = new VfNotes();
	
	
	/////////////////
	/**
	 * map from name of {@link GraphicsPropertyTree} to the {@link GraphicsPropertyTree};
	 * note that all {@link GraphicsPropertyTree}s of the same {@link IndependentGraphicsPropertyCFG} should have their own unique tree name;
	 */
	private final Map<SimpleName, GraphicsPropertyTree> targetGraphicsPropertyTreeNameMap;
	
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param ownerRecordDataMetadataID
	 * @param targetGraphicsPropertyTreeNameMap not null, not empty;
	 */
	public IndependentGraphicsPropertyCFG(
			CompositionFunctionGroupName name, VfNotes notes,
			MetadataID ownerRecordDataMetadataID,
			
			Set<GraphicsPropertyTree> targetGraphicsPropertyTreeSet) {
		
		super(name, notes,ownerRecordDataMetadataID);
		
		if(targetGraphicsPropertyTreeSet==null||targetGraphicsPropertyTreeSet.isEmpty()) {
			throw new IllegalArgumentException("given targetGraphicsPropertyTreeSet cannot be null or empty");
		}
		
		
		
		targetGraphicsPropertyTreeNameMap = new LinkedHashMap<>();
		
		targetGraphicsPropertyTreeSet.forEach(e->{
			if(targetGraphicsPropertyTreeNameMap.containsKey(e.getName())) {
				throw new IllegalArgumentException("duplicate tree name found in given targetGraphicsPropertyTreeSet!");
			}
			targetGraphicsPropertyTreeNameMap.put(e.getName(), e);
		});
		
	}

	
	@Override
	public SimpleName getTypeName() {
		return TYPE_NAME;
	}
	
	@Override
	public VfNotes getTypeNotes() {
		return TYPE_NOTES;
	}
	
	@Override
	public Map<SimpleName, GraphicsPropertyTree> getTargetGraphicsPropertyTreeNameMap() {
		return targetGraphicsPropertyTreeNameMap;
	}

	
	///////////////////////////////
	/**
	 * reproduce and return a new IndependentGraphicsPropertyCFG of this one;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced CFG will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this CFG is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public IndependentGraphicsPropertyCFG reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		CompositionFunctionGroupName reproducedCFGName = this.getID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex).getName();
		
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		//find out the copy index of the VCDNode to which the owner record Metadata is assigned
		int copyIndexOfOwnerRecordMetadata = 
				VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOwnerRecordMetadata(this.getID(), copyIndex);
		
		MetadataID reproducedOwnerRecordDataMetadataID = 
				this.getOwnerRecordDataMetadataID().reproduce(
						hostVisProjctDBContext, 
						VSAArchiveReproducerAndInserter,
						copyIndexOfOwnerRecordMetadata);//find out the copy index of owner record data
		
		//
		Set<GraphicsPropertyTree> targetGraphicsPropertyTreeSet = new HashSet<>();
		for(SimpleName treeName:this.getTargetGraphicsPropertyTreeNameMap().keySet()) {
			targetGraphicsPropertyTreeSet.add(this.getTargetGraphicsPropertyTreeNameMap().get(treeName).reproduce());
		}
		
		return new IndependentGraphicsPropertyCFG(
				reproducedCFGName,
				reproducedNotes,
				reproducedOwnerRecordDataMetadataID,
				targetGraphicsPropertyTreeSet
				);
	}
	

	/////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((targetGraphicsPropertyTreeNameMap == null) ? 0 : targetGraphicsPropertyTreeNameMap.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof IndependentGraphicsPropertyCFG))
			return false;
		IndependentGraphicsPropertyCFG other = (IndependentGraphicsPropertyCFG) obj;
		if (targetGraphicsPropertyTreeNameMap == null) {
			if (other.targetGraphicsPropertyTreeNameMap != null)
				return false;
		} else if (!targetGraphicsPropertyTreeNameMap.equals(other.targetGraphicsPropertyTreeNameMap))
			return false;
		return true;
	}

}
