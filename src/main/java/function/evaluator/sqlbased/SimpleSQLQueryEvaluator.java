package function.evaluator.sqlbased;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.CanBeUsedForPiecewiseFunctionConditionEvaluatorType;
import function.evaluator.sqlbased.utils.SelectElementExpression;
import function.evaluator.sqlbased.utils.WhereConditionExpression;
import function.variable.input.InputVariable;
import function.variable.output.OutputVariable;
import function.variable.output.type.CFGTargetOutputVariable;


//The runnable sql query must be of 
//SELECT … FROM … WHERE


/**
 * simple SqlQueryBasedEvaluator;
 * 
 * =======================================091320
 * 1. regarding constraints and rules on how to create a SimpleSqlQueryEvaluator:
 * 
 * Only allow user-defined select and where clause, FROM clause is automatically generated;
 * 
 * select terms must be non-empty while where clause can be null;
 * 
 * 
 * each term in the select clause is corresponding to column in the resulted table view thus, corresponding to an output variable as well;
 * 
 * 
 * ============================================091320
 * 2. constraints on input variables of type {@link RecordwiseInputVariable}
 * if a {@link RecordwiseInputVariable} (also input column) in resulted SELECT clause is from the tables (set T1) with same set of RUID column values of the owner record data's data table of this evaluator 
 * 		for example, owner data table of the host CFG of the host CF of this evaluator or value table of the CF with the same host CFG of this one
 * 		the default is to extract the value for the same record unless an explicit condition is given in the Where clause;
 * 
 * if a {@link RecordwiseInputVariable} (also input column)  in resulted SELECT clause is from tables (set T2) with a different set of RUID column values of the owner record data's data table of this evaluator
 * 		for example, data table other than the owner record data's data table or a CF target value table of a CF of a different CFG from the host CFG of this evaluator 
 * 		conditions MUST be given explicitly between the every T2 table and at least one of the tables in T1, otherwise the sql query is invalid;
 * 
 * ===================================
 * 3. regarding performing the calculation of this type of evaluator:
 * 
 * to build the full SELECT clause string, need to add the RUID column of the owner record data table as the first selected term;
 * 
 * to build the full WHERE clause string, 
 * 		1. need to add the RUID column equity conditions between the tables in T1 with AND logic operator;
 * 				specifically, add an equity condition between owner data table's RUID column and every other table's RUID column in T1 and connect them with 'AND'
 * 		2. also need to add the upstream PicewiseFunction output index conditions
 * 				Specifically, add an equity condition for every upstream PiecewiseFunction regarding the output index that lead to the host ComponentFunction of this evaluator on the hierarchy connected with 'AND';
 * 
 * to build the full FROM clause string, need to include all tables in T1 and T2 and the CFPiecewiseFunctionIndexIDOutputIndexValueTable 
 * 
 * 
 * for a record of the owner record data, there are 0, 1, or multiple rows produced by the sql query; 
 * thus to facilitate extract output from the resulted ResultSet, need to add some type of sorting approach to deal with multiple rows for the same owner data table RUID column value;
 * 		add ORDER BY clause with owner data table RUID column;
 * 		add GROPU By clause with owner data table RUID column;
 * 		add DISINCT key word to the owner data table RUILD column in SELECT clause;
 * which one is the most computationally efficient?
 * 
 * ======================================091320
 * {@link InputVariable}s in different {@link SelectElementExpression}s and {@link WhereConditionExpression} are independent from each other;
 * thus only those in the same {@link SelectElementExpression} or {@link WhereConditionExpression} should have unique alias name;
 * 		the {@link InputVariable}s are used to built the sql query string, alias names only help to distinguish {@link InputVariable}s in the same {@link VfSQLExpression}
 * 	
 * {@link OutputVariable}s in each {@link SelectElementExpression}, on the contrary, should have different alias names, since alias names are used in the resulted value table as column names;
 * 
 * @author tanxu
 *
 */
public class SimpleSQLQueryEvaluator extends SQLQueryBasedEvaluator implements CanBeUsedForPiecewiseFunctionConditionEvaluatorType{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1568711144824178969L;
	
	///////////////////
	/**
	 * an ordered list of SelectColumnExpressions corresponding to the output columns of the sql query built by this SimpleSqlQueryEvaluator;
	 * 
	 */
	private final List<SelectElementExpression> selectClauseExpressionList;
	
	/**
	 * expression for where conditional clause;
	 * 
	 * if null, no condition;
	 */
	private final WhereConditionExpression whereConditionExpression;
	
	////////////////////////////////////////////////
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param indexID
	 * @param notes
	 * @param selectClauseExpressionList not null or empty
	 * @param whereConditionExpression be null if no where condition for the sql query;
	 */
	public SimpleSQLQueryEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID, 
			int indexID,
			VfNotes notes,
			
			List<SelectElementExpression> selectClauseExpressionList,
			WhereConditionExpression whereConditionExpression
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);

		//validations
		if(selectClauseExpressionList==null||selectClauseExpressionList.isEmpty())
			throw new IllegalArgumentException("given selectClauseExpressionList cannot be null or empty!");
		
		
		//output variables in selectClauseExpressionList must have different alias names;
		//also if multiple OutputVariables are of CFGTargetOutputVariable type, they must be assigned to different CFGTargets
		Map<SimpleName, OutputVariable> outputVariableAliasNameMap = new HashMap<>();
		Set<SimpleName> assignedCFGTargetNameSet = new HashSet<>();
		selectClauseExpressionList.forEach(e->{
			if(outputVariableAliasNameMap.containsKey(e.getOutputVariable().getAliasName())) {
				throw new IllegalArgumentException("duplicate output varaible alias name found:"+e.getOutputVariable().getAliasName().getStringValue());
			}
			
			if(e.getOutputVariable() instanceof CFGTargetOutputVariable) {
				CFGTargetOutputVariable v = (CFGTargetOutputVariable)e.getOutputVariable();
				if(assignedCFGTargetNameSet.contains(v.getTargetName())) {
					throw new IllegalArgumentException("same CFGTarget is assigned to multiple OutputVariables of given selectClauseExpressionList!");
				}
				assignedCFGTargetNameSet.add(v.getTargetName());
			}
			
			outputVariableAliasNameMap.put(e.getOutputVariable().getAliasName(), e.getOutputVariable());
		});
		
		
		
		
		this.selectClauseExpressionList = selectClauseExpressionList;
		this.whereConditionExpression = whereConditionExpression;
	}
	
	///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	//generate the full sql query string is depending on the specific visinstance run;
	//since the free input variables and non-recordwise input variables should be calculated first
	
	public List<SelectElementExpression> getSelectClauseExpressionList() {
		return selectClauseExpressionList;
	}

	public WhereConditionExpression getWhereConditionExpression() {
		return whereConditionExpression;
	}
	
	
	////////////////////////////
	
	@Override
	public Map<SimpleName, InputVariable> getInputVariableAliasNameMap() {
//		if(this.inputVariableAliasNameMap==null) {
		Map<SimpleName, InputVariable> ret = new HashMap<>();
			
		this.selectClauseExpressionList.forEach(e->{
			ret.putAll(e.getInputVariableAliasNameMap());
		});
//		}
		
		return ret;
	}
	
	
	/**
	 * though initialized in constructor, still need to implement here due to Serialization({@link #outputVariableAliasNameMap} is transient)
	 */
	@Override
	public Map<SimpleName, OutputVariable> getOutputVariableAliasNameMap(){
//		if(this.outputVariableAliasNameMap==null) {
		Map<SimpleName, OutputVariable> ret = new HashMap<>();
			
		selectClauseExpressionList.forEach(e->{
			ret.put(e.getOutputVariable().getAliasName(), e.getOutputVariable());
		});
		
//		}
		return ret;
	}
	
	////////////////////////////////////////
	/**
	 * reproduce and return a new SimpleSqlQueryEvaluator of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public SimpleSQLQueryEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		List<SelectElementExpression> selectClauseExpressionList = new ArrayList<>();
		
		for(SelectElementExpression se:this.getSelectClauseExpressionList()) {
			selectClauseExpressionList.add(se.reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex));
		}
		
		SimpleSQLQueryEvaluator ret = new SimpleSQLQueryEvaluator(
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.getHostComponentFunctionIndexID(),
				this.getIndexID(),
				this.getNotes().reproduce(),
				selectClauseExpressionList,
				this.getWhereConditionExpression().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex)
				);
		
		return ret;
	}
	
	
	///////////////////////////////////////////////

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((selectClauseExpressionList == null) ? 0 : selectClauseExpressionList.hashCode());
		result = prime * result + ((whereConditionExpression == null) ? 0 : whereConditionExpression.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof SimpleSQLQueryEvaluator))
			return false;
		SimpleSQLQueryEvaluator other = (SimpleSQLQueryEvaluator) obj;
		if (selectClauseExpressionList == null) {
			if (other.selectClauseExpressionList != null)
				return false;
		} else if (!selectClauseExpressionList.equals(other.selectClauseExpressionList))
			return false;
		if (whereConditionExpression == null) {
			if (other.whereConditionExpression != null)
				return false;
		} else if (!whereConditionExpression.equals(other.whereConditionExpression))
			return false;
		return true;
	}

	
}
