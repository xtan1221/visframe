/**
 * 
 */
package function.group;

import java.sql.SQLException;
import java.util.Map;
import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import graphics.property.tree.GraphicsPropertyTree;
import graphics.shape.VfShapeType;
import metadata.MetadataID;

/**
 * a sub-type of {@link GraphicsPropertyCFG} with the full set of targets defined by a specific {@link VfShapeType};
 * 
 * the difference between {@link IndependentGraphicsPropertyCFG} and {@link ShapeCFG} is how the set of target {@link GraphicsPropertyTree} is created;
 * 1. for {@link IndependentGraphicsPropertyCFG}, the {@link GraphicsPropertyTree}s are arbitrary;
 * 2. for {@link ShapeCFG}, the {@link GraphicsPropertyTree}s are predefined by the specific {@link VfShapeType}
 * @author tanxu
 *
 */
public final class ShapeCFG extends GraphicsPropertyCFG {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3289686134405004118L;
	
	////////////////////////
	public static final SimpleName TYPE_NAME = new SimpleName("ShapeCFG");
	public static final VfNotes TYPE_NOTES = new VfNotes();
	
	////////////////
	/**
	 * set of non-mandatory tree of the shape type with at least one leaf node assigned to one of the CF;
	 * facilitate generating visualization object;
	 */
	private final VfShapeType shapeType;
	
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param ownerRecordDataMetadataID
	 * @param shapeType
	 */
	public ShapeCFG(
			CompositionFunctionGroupName name, VfNotes notes,
			MetadataID ownerRecordDataMetadataID,
			
			VfShapeType shapeType) {
		super(name, notes, ownerRecordDataMetadataID);
		
		if(shapeType==null)
			throw new IllegalArgumentException("given shapeType cannot be null!");
		
		this.shapeType = shapeType;
	}
	
	/**
	 * return the {@link VfShapeType}
	 * @return
	 */
	public VfShapeType getShapeType() {
		return shapeType;
	}
	
	
	//////////////////////////////////////////
	@Override
	public SimpleName getTypeName() {
		return TYPE_NAME;
	}
	
	@Override
	public VfNotes getTypeNotes() {
		return TYPE_NOTES;
	}
	
	@Override
	protected Map<SimpleName, GraphicsPropertyTree> getTargetGraphicsPropertyTreeNameMap() {
		return this.getShapeType().getGraphicsPropertyTreeNameMap();
	}
	
	/**
	 * reproduce and return a new ShapeCFG of this one;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced CFG will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this CFG is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public ShapeCFG reproduce(
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
		
		return new ShapeCFG(
				reproducedCFGName,
				reproducedNotes,
				reproducedOwnerRecordDataMetadataID,
				this.shapeType
				);
	}

	
	//////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((shapeType == null) ? 0 : shapeType.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ShapeCFG))
			return false;
		ShapeCFG other = (ShapeCFG) obj;
		if (shapeType == null) {
			if (other.shapeType != null)
				return false;
		} else if (!shapeType.equals(other.shapeType))
			return false;
		return true;
	}


}
