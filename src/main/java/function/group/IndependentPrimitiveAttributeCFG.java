package function.group;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.target.IndependentPrimitiveAttributeTarget;
import metadata.MetadataID;

/**
 * a sub-type of {@link CompositionFunctionGroup} with all targets of type {@link IndependentPrimitiveAttributeTarget};
 * 
 * the difference of {@link IndependentPrimitiveAttributeCFG} from the {@link GraphicsPropertyCFG} is
 * 		the targets of {@link IndependentPrimitiveAttributeCFG} are fully and explicitly defined by the creator of each instance of {@link IndependentPrimitiveAttributeCFG}
 * 
 * @author tanxu
 *
 */
public final class IndependentPrimitiveAttributeCFG extends AbstractCompositionFunctionGroup {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6297074216431076861L;
	
	/////////////////////
	public static final SimpleName TYPE_NAME = new SimpleName("IndependentAttributeCFG");
	public static final VfNotes TYPE_NOTES = new VfNotes();
	
	
	/////////////////
	/**
	 * map from the name to each target {@link IndependentPrimitiveAttributeTarget}
	 */
	private final Map<SimpleName, IndependentPrimitiveAttributeTarget<?>> targetIndependentPrimitiveAttributeNameMap;
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param targetIndependentAttributeNameMap
	 */
	public IndependentPrimitiveAttributeCFG(
			CompositionFunctionGroupName name, VfNotes notes,
			MetadataID ownerRecordDataMetadataID,
			
			Set<IndependentPrimitiveAttributeTarget<?>> targetIndependentAttributeSet
			) {
		super(name, notes,ownerRecordDataMetadataID);
		
		if(targetIndependentAttributeSet==null||targetIndependentAttributeSet.isEmpty()) {
			throw new IllegalArgumentException("given targetIndependentAttributeSet cannot be null or empty!");
		}
		
		
		
		this.targetIndependentPrimitiveAttributeNameMap = new LinkedHashMap<>();
		
		for(IndependentPrimitiveAttributeTarget<?> target:targetIndependentAttributeSet) {
			if(targetIndependentPrimitiveAttributeNameMap.containsKey(target.getName())) {
				throw new IllegalArgumentException("duplicate target name found in given targetIndependentAttributeSet!");
			}
			targetIndependentPrimitiveAttributeNameMap.put(target.getName(), target);
		}
		
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
	public Map<SimpleName, IndependentPrimitiveAttributeTarget<?>> getTargetNameMap() {
		return targetIndependentPrimitiveAttributeNameMap;
	}
	
	//////////////////////////////////////
	/**
	 * reproduce and return a new IndependentAttributeCFG of this one;
	 * @param copyIndex index of the VCCLNode of the VCDNode to which this CFG is assigned
	 * @throws SQLException 
	 */
	@Override
	public IndependentPrimitiveAttributeCFG reproduce(
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
		
		
		Set<IndependentPrimitiveAttributeTarget<?>> reproducedTargetIndependentAttributeSet = new LinkedHashSet<>();
		for(SimpleName attributeName:this.getTargetNameMap().keySet()) {
			reproducedTargetIndependentAttributeSet.add(
					this.getTargetNameMap().get(attributeName).reproduce()
					);
		}
		
		return new IndependentPrimitiveAttributeCFG(
				reproducedCFGName,
				reproducedNotes,
				reproducedOwnerRecordDataMetadataID,
				reproducedTargetIndependentAttributeSet
				);
		
	}

	//////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((targetIndependentPrimitiveAttributeNameMap == null) ? 0
				: targetIndependentPrimitiveAttributeNameMap.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof IndependentPrimitiveAttributeCFG))
			return false;
		IndependentPrimitiveAttributeCFG other = (IndependentPrimitiveAttributeCFG) obj;
		if (targetIndependentPrimitiveAttributeNameMap == null) {
			if (other.targetIndependentPrimitiveAttributeNameMap != null)
				return false;
		} else if (!targetIndependentPrimitiveAttributeNameMap.equals(other.targetIndependentPrimitiveAttributeNameMap))
			return false;
		return true;
	}
}
