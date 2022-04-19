package visinstance.run.calculation.evaluator.updator.utils;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import function.component.ComponentFunction;
import function.variable.output.OutputVariable;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

/**
 * base class to update rows in the value tables of a ComponentFunction based on the output variable values of an evaluator;
 * 
 * utility class to facilitate {@link EvaluatorValueTableUpdateManager};
 * 
 * @author tanxu
 *
 * @param <C>
 */
public abstract class ComponentFunctionValueTableUpdator<C extends ComponentFunction> {
	/**
	 * number of maximal rows to be updated in a batch;
	 */
	protected final static int BATCH_UPDATE_LIMIT = 1000;
	
	///////////////////////////////////
	private final CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator;
	
	//////////////////////////////////

	protected Statement statement;
	protected int currentBatchSize = 0;
	/**
	 * constructor
	 * @param CFTargetValueTableRunCalculator
	 * @throws SQLException 
	 */
	protected ComponentFunctionValueTableUpdator(CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator) throws SQLException{
		this.CFTargetValueTableRunCalculator = CFTargetValueTableRunCalculator;

		this.statement = this.getCFTargetValueTableRunCalculator().getHostVisProjectDBContext().getDBConnection().createStatement();
	}
	

	/**
	 * @return the cFTargetValueTableRunCalculator
	 */
	public CFTargetValueTableRunCalculator getCFTargetValueTableRunCalculator() {
		return CFTargetValueTableRunCalculator;
	}
	
	
	/**
	 * update the columns of the row with the given RUID value and corresponding to the given OutputVariable with the given values;
	 * 
	 * @param outputVariableAliasNameCalculatedStringValueMap
	 * @param RUID
	 * @throws SQLException
	 */
	public abstract void updateValueTables(Map<OutputVariable, String> outputVariableAliasNameCalculatedStringValueMap, int RUID) throws SQLException;
	
	
	/**
	 * perform the last batch of update if there is any;
	 * must be invoked from the invoker of this ComponentFunctionValueTableUpdator at the end of the updating;
	 * @throws SQLException
	 */
	public void updateLastBatch() throws SQLException {
		if(this.currentBatchSize>0) {
			statement.executeBatch();
			this.currentBatchSize = 0;
		}
	}
}
