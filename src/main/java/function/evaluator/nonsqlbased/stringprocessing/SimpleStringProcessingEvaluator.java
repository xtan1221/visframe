package function.evaluator.nonsqlbased.stringprocessing;

import java.sql.SQLException;

import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;

/**
 * no regular expression involved
 * 
 * @author tanxu
 * 
 */
public abstract class SimpleStringProcessingEvaluator extends StringProcessingEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3064063640332765022L;

	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param notes
	 */
	SimpleStringProcessingEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int indexID,
			VfNotes notes 
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract SimpleStringProcessingEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;

}
