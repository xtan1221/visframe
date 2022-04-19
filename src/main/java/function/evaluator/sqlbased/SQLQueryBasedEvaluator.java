package function.evaluator.sqlbased;

import java.sql.SQLException;

import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.AbstractEvaluator;
import function.evaluator.sqlbased.utils.VfSQLExpression;

/**
 * 
 * output variables are evaluated by running a sql query for all records together;
 * 
 * {@link RecordwiseInputVariable} can be of the same or different owner record data with the one of the host CompositionFunctionGroup; 
 * 
 * A post-processing needs to be performed to extract one record (among possibly multiple record) from the resulted table of the sql query for each qualified record (with the same RUID column value)
 * It is possible for some qualified records, there is no result in the sql query output table;
 * note that RecordwiseInputVariables of this evaluator type can be of the any data table column or its CFG target
 * 
 * Do not allow aggregate function, if needed, use a {@link SQLAggregateFunctionBasedInputVariable}
 * 
 * 
 * ====================================
 * for those in the same {@link SQLQueryBasedEvaluator}, alias name uniqueness are required for each {@link VfSQLExpression};
 * @author tanxu
 *
 */
public abstract class SQLQueryBasedEvaluator extends AbstractEvaluator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2392496810492490631L;

	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param notes
	 */
	SQLQueryBasedEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID, 
			int indexID,
			VfNotes notes) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * reproduce and return a new SqlQueryBasedEvaluator of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract SQLQueryBasedEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;

	
}
