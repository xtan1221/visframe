package function.variable.independent;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;


/**
 * class for a type of FIV of a CF that can be used by any CompositionFunction in its FreeInputVariable
 */
public class IndependentFreeInputVariableTypeImpl implements IndependentFreeInputVariableType{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3224684798695436538L;
	
	
	////////////////////////////////////////////
	private final SimpleName name;
	private final VfNotes notes;
	private final CompositionFunctionID ownerCompositionFunctionID;
	private final VfDefinedPrimitiveSQLDataType SQLDataType;
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param ownerCompositionFunctionID
	 * @param SQLDataType
	 */
	public IndependentFreeInputVariableTypeImpl(
			SimpleName name, VfNotes notes,
			CompositionFunctionID ownerCompositionFunctionID,
			VfDefinedPrimitiveSQLDataType SQLDataType){
		this.name = name;
		this.notes = notes;
		this.ownerCompositionFunctionID = ownerCompositionFunctionID;
		this.SQLDataType = SQLDataType;
		
	}

	@Override
	public CompositionFunctionID getOwnerCompositionFunctionID() {
		return ownerCompositionFunctionID;
	}

	
	@Override
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return SQLDataType;
	}

	@Override
	public VfNotes getNotes() {
		return notes;
	}

	
	@Override
	public SimpleName getName() {
		return name;
	}
	
	
	/**
	 * reproduce and return a new IndependentFreeInputVariableType of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which the owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public IndependentFreeInputVariableTypeImpl reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		return new IndependentFreeInputVariableTypeImpl(
				this.getName().reproduce(),
				this.getNotes().reproduce(),
				this.ownerCompositionFunctionID.reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.getSQLDataType().reproduce()
				);
	}

	
	/////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((SQLDataType == null) ? 0 : SQLDataType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((ownerCompositionFunctionID == null) ? 0 : ownerCompositionFunctionID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IndependentFreeInputVariableTypeImpl))
			return false;
		IndependentFreeInputVariableTypeImpl other = (IndependentFreeInputVariableTypeImpl) obj;
		if (SQLDataType == null) {
			if (other.SQLDataType != null)
				return false;
		} else if (!SQLDataType.equals(other.SQLDataType))
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
		if (ownerCompositionFunctionID == null) {
			if (other.ownerCompositionFunctionID != null)
				return false;
		} else if (!ownerCompositionFunctionID.equals(other.ownerCompositionFunctionID))
			return false;
		return true;
	}

	
}
