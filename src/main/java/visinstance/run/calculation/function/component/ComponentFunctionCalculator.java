package visinstance.run.calculation.function.component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import function.component.ComponentFunction;
import function.evaluator.Evaluator;
import visinstance.run.calculation.evaluator.updator.utils.ComponentFunctionValueTableUpdator;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

/**
 * base class for calculator of a ComponentFunction started by a {@link CFTargetValueTableRunCalculator}
 * 
 * @author tanxu
 *
 */
public abstract class ComponentFunctionCalculator<T extends ComponentFunction, U extends ComponentFunctionValueTableUpdator<T>> {
	private final CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator;
	private final T targetComponentFunction;
	private final U componentFunctionValueTableUpdator;
	/**
	 * constructor
	 * @param CFTargetValueTableRunCalculator
	 */
	ComponentFunctionCalculator(
			CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator,
			T targetComponentFunction,
			U componentFunctionValueTableUpdator){
		
		this.CFTargetValueTableRunCalculator = CFTargetValueTableRunCalculator;
		this.targetComponentFunction = targetComponentFunction;
		this.componentFunctionValueTableUpdator = componentFunctionValueTableUpdator;
	}
	
	
	protected CFTargetValueTableRunCalculator getCFTargetValueTableRunCalculator() {
		return CFTargetValueTableRunCalculator;
	}
	
	/**
	 * return the target ComponentFunction of this {@link ComponentFunctionCalculator}
	 * @return
	 */
	protected T getTargetComponentFunction() {
		return this.targetComponentFunction;
	}



	/**
	 * @return the componentFunctionValueTableUpdator
	 */
	protected U getComponentFunctionValueTableUpdator() {
		return componentFunctionValueTableUpdator;
	}

	//////////////////////////////////////////
	/**
	 * carry out the calculation based on each evaluator;
	 * for each evaluator in the target ComponentFunction,
	 * 1. build the full sql query string;
	 * 2. run the sql query string to get the ResultSet;
	 * 3. process the ResultSet to update value tables;
	 * 
	 * then post-process if necessary;
	 * @throws SQLException 
	 */
	public abstract void calculate() throws SQLException;
	
	
	
	/**
	 * method that process an Evaluator
	 * @param e
	 * @return
	 * @throws SQLException 
	 */
	protected abstract String buildFullSqlQuerString(Evaluator e) throws SQLException;
	
	
	/**
	 * run the given fullSqlQuerString in the host VisProjectDBContext and return the ResultSet;
	 * @param fullSqlQuerString
	 * @return
	 * @throws SQLException 
	 */
	protected ResultSet runFullSqlQueryString(String fullSqlQuerString) throws SQLException {
		
		Statement statement = this.CFTargetValueTableRunCalculator.getHostVisProjectDBContext().getDBConnection().createStatement();
		ResultSet rs = statement.executeQuery(fullSqlQuerString);
		return rs;
		
	}
	
	/**
	 * update the value tables
	 * @param e
	 * @param rs
	 * @throws SQLException 
	 */
	protected abstract void updateResultIntoValueTables(Evaluator e, ResultSet rs) throws SQLException;

}
