package function.evaluator;

import java.sql.SQLException;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;



/**
 * 
 * @author tanxu
 *
 */
public abstract class AbstractEvaluator implements Evaluator{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7115533138153152090L;
	
	//////////////////////
	private final CompositionFunctionID hostCompositionFunctionID;
	private final int hostComponentFunctionIndexID;
	private final int indexID;
	private final VfNotes notes;
	
	//////////
	
//	protected transient Map<SimpleName, InputVariable> inputVariableAliasNameMap;
//	protected transient Map<SimpleName, OutputVariable> outputVariableAliasNameMap;
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param notes
	 */
	protected AbstractEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int indexID,
			VfNotes notes
			){
		this.hostCompositionFunctionID = hostCompositionFunctionID;
		this.hostComponentFunctionIndexID = hostComponentFunctionIndexID;
		this.indexID = indexID;
		this.notes = notes;
		
	}
	
	
	@Override
	public CompositionFunctionID getHostCompositionFunctionID() {
		return this.hostCompositionFunctionID;
	}
	
	@Override
	public int getHostComponentFunctionIndexID() {
		return hostComponentFunctionIndexID;
	}
	
	@Override
	public int getIndexID() {
		return indexID;
	}
	
	@Override
	public VfNotes getNotes() {
		return notes;
	}
	
	
	
	////////////////////////
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract AbstractEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;


	/////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hostComponentFunctionIndexID;
		result = prime * result + ((hostCompositionFunctionID == null) ? 0 : hostCompositionFunctionID.hashCode());
		result = prime * result + indexID;
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractEvaluator))
			return false;
		AbstractEvaluator other = (AbstractEvaluator) obj;
		if (hostComponentFunctionIndexID != other.hostComponentFunctionIndexID)
			return false;
		if (hostCompositionFunctionID == null) {
			if (other.hostCompositionFunctionID != null)
				return false;
		} else if (!hostCompositionFunctionID.equals(other.hostCompositionFunctionID))
			return false;
		if (indexID != other.indexID)
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		return true;
	}

	
	
	
	
}
