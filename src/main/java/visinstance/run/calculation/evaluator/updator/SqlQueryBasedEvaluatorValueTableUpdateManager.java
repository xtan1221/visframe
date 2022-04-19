package visinstance.run.calculation.evaluator.updator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import function.component.ComponentFunction;
import function.evaluator.sqlbased.SimpleSQLQueryEvaluator;
import function.evaluator.sqlbased.SQLQueryBasedEvaluator;
import function.variable.output.OutputVariable;
import visinstance.run.calculation.evaluator.updator.utils.ComponentFunctionValueTableUpdator;
import visinstance.run.calculation.function.composition.CFTargetValueTableRunCalculator;

/**
 * base class for updator of value tables for a {@link SQLQueryBasedEvaluator};
 * 
 * 1. retrieve a random row from ResultSet for each distinct RUID value;
 * 
 * 2. insert into the value tables with UPDATE clause;
 * 
 * @author tanxu
 *
 */
public class SqlQueryBasedEvaluatorValueTableUpdateManager<C extends ComponentFunction,U extends ComponentFunctionValueTableUpdator<C>> extends EvaluatorValueTableUpdateManager<C,SQLQueryBasedEvaluator,U> {
	
	////////////////
	private Integer currentGroupRUID;
	
	/**
	 * constructor
	 * @param CFTargetValueTableRunCalculator
	 * @param hostComponentFunction
	 * @param evaluator
	 * @param resultSet
	 * @param componentFunctionValueTableUpdator
	 * @throws SQLException 
	 */
	public SqlQueryBasedEvaluatorValueTableUpdateManager(
			CFTargetValueTableRunCalculator CFTargetValueTableRunCalculator,
			SQLQueryBasedEvaluator evaluator, 
			ResultSet resultSet,
			U componentFunctionValueTableUpdator) throws SQLException {
		super(CFTargetValueTableRunCalculator, evaluator, resultSet, componentFunctionValueTableUpdator);
		// TODO Auto-generated constructor stub
	}


	/**
	 * retrieve the next row in the ResultSet with a new RUID column value;
	 * note that the RUID column's value is the first element in the returned list;
	 * 
	 */
	@Override
	protected List<Object> getNextRUIDObjectValueList() throws SQLException {
		while(this.resultSet.next()) {
			int currentRowRUID = (Integer)this.resultSet.getObject(1); //note that the first column in ResultSet has index 1 rather than 0
			
			if(this.currentGroupRUID==null) {//first row
				this.currentGroupRUID = currentRowRUID;
				
				List<Object> ret = new ArrayList<>();
				
				for(int i=1;i<=this.columnNum;i++) {
					ret.add(this.resultSet.getObject(i));
				}
				
				return ret;
				
			}else {
				if(currentRowRUID == this.currentGroupRUID) {
					//still the same RUID, skip and keep reading next row in ResultSet
				}else {//a new RUID
					this.currentGroupRUID = currentRowRUID;
					
					List<Object> ret = new ArrayList<>();
					
					for(int i=1;i<=this.columnNum;i++) {
						ret.add(this.resultSet.getObject(i));
					}
					
					return ret;
					
				}
			}
			
			
		}
		//reach end of ResultSet
		return null;
	}

	
	
	/**
	 * 
	 */
	@Override
	public void perform() throws SQLException {
		List<Object> next;
//		int num=0;
		while((next = this.getNextRUIDObjectValueList())!=null) {
			Map<OutputVariable, String> outputVariableCalculatedStringValueMap = new HashMap<>();
			
			if(this.getEvaluator() instanceof SimpleSQLQueryEvaluator) {
				SimpleSQLQueryEvaluator sse = (SimpleSQLQueryEvaluator) this.getEvaluator();
				
				for(int i=0;i<sse.getSelectClauseExpressionList().size();i++) {
					OutputVariable ov = sse.getSelectClauseExpressionList().get(i).getOutputVariable();
					Object objectValue = next.get(i+1); //note that the first column is the RUID column, need to add 1
					
					outputVariableCalculatedStringValueMap.put(
							ov, 
							objectValue==null?null:objectValue.toString()
									);
					
				}
				
//				System.out.println(num);
//				num++;
				
				this.updateValueTables(
						outputVariableCalculatedStringValueMap,
						(Integer)next.get(0)
						);
				
			}else {
				throw new IllegalArgumentException();
			}
			
		}
		//
		this.componentFunctionValueTableUpdator.updateLastBatch();
		//post processing?????
		//TODO
	}

	
	
	
	
}
