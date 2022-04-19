package function.evaluator.nonsqlbased.rng;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.evaluator.nonsqlbased.NonSQLQueryBasedEvaluator;
import function.variable.output.OutputVariable;
import function.variable.output.type.PFConditionEvaluatorBooleanOutputVariable;
import function.variable.output.type.ValueTableColumnOutputVariable;
import rdb.sqltype.SQLDataTypeFactory;

/**
 * base class for evaluator that calculate a random integer value for a single output variable of double type
 * 
 * @author tanxu
 *
 */
public abstract class RNGEvaluator extends NonSQLQueryBasedEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6937639151253765160L;

	/**
	 * must be of double type; cannot be null;
	 * 
	 * thus cannot be {@link PFConditionEvaluatorBooleanOutputVariable} type
	 */
	private final ValueTableColumnOutputVariable outputVariable;
	
	
	/**
	 * 
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param indexID
	 * @param notes
	 * @param outputVariable must be of double type;
	 */
	protected RNGEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID, 
			int indexID,
			VfNotes notes,
			
			ValueTableColumnOutputVariable outputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		
		//
		if(outputVariable==null)
			throw new IllegalArgumentException("given outputVariable cannot be null!");
		if(!outputVariable.getSQLDataType().equals(SQLDataTypeFactory.doubleType()))
			throw new IllegalArgumentException("given outputVariable must be of double type!");
		
		
		///
		this.outputVariable = outputVariable;
	}


	public ValueTableColumnOutputVariable getOutputVariable() {
		return outputVariable;
	}
	
	
	@Override
	public Map<SimpleName, OutputVariable> getOutputVariableAliasNameMap(){
//		if(this.outputVariableAliasNameMap == null) {
		Map<SimpleName, OutputVariable> ret = new HashMap<>();
		ret.put(this.outputVariable.getAliasName(), this.outputVariable);
//		}
		
		return ret;
	}
	
	
	/////////////////////////
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract RNGEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;


	
	/////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((outputVariable == null) ? 0 : outputVariable.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RNGEvaluator))
			return false;
		RNGEvaluator other = (RNGEvaluator) obj;
		if (outputVariable == null) {
			if (other.outputVariable != null)
				return false;
		} else if (!outputVariable.equals(other.outputVariable))
			return false;
		return true;
	}
	
	
	
}
