package function.variable.input;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.AbstractVariable;
import function.variable.independent.IndependentFreeInputVariableType;
import function.variable.input.nonrecordwise.type.FreeInputVariable;

/**
 * input variables that are defined for a specific {@link Evaluator} of a specific {@link ComponentFunction}
 * 
 * another major type of input variable is the {@link FreeInputVariable} which is defined based on a {@link IndependentFreeInputVariableType} that is owned by a {@link CompositionFunction} and
 * can be shared to {@link Evaluator}s of other {@link CompositionFunction}s;
 * 
 * @author tanxu
 *
 */
public abstract class InputVariable extends AbstractVariable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6887785762220802233L;
	
	/////////////////////////////////
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param aliasName
	 * @param notes
	 * @param SQLDataType
	 * @param hostEvaluatorIndexID
	 */
	protected InputVariable(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int hostEvaluatorIndexID,
			SimpleName aliasName, VfNotes notes
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, hostEvaluatorIndexID, aliasName, notes);
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
	public abstract InputVariable reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;


	
	

}
