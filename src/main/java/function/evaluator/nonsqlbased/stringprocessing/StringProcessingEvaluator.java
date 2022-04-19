package function.evaluator.nonsqlbased.stringprocessing;

import java.sql.SQLException;

import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.nonsqlbased.NonSQLQueryBasedEvaluator;
import function.variable.input.InputVariable;
import function.variable.input.nonrecordwise.type.ConstantValuedInputVariable;

/**
 * base class for string processing;
 * 
 * 
 * ===========================
 * input variables can be any types of {@link InputVariable} including {@link ConstantValuedInputVariable} type;
 * 
 * note that {@link ConstantValuedInputVariable} can only be used in {@link StringProcessingEvaluator};
 * 
 * @author tanxu
 *
 */
public abstract class StringProcessingEvaluator extends NonSQLQueryBasedEvaluator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6515577578463788340L;

	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param notes
	 */
	StringProcessingEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int indexID,
			VfNotes notes) {
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
	public abstract StringProcessingEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;

}
