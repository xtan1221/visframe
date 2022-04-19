package function.evaluator.nonsqlbased.rng;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.input.InputVariable;
import function.variable.output.OutputVariable;
import function.variable.output.type.ValueTableColumnOutputVariable;

/**
 * generate a random number between the value of two input variable of numeric type based on a uniform distribution;
 * 
 * ==========================
 * how to deal with null value of input variable for some record:
 * 		if any of the {@link #range1InputVariable} or {@link #range2InputVariable} has null value for some record, the output variable will be evaluated to null;
 * 		
 * @author tanxu
 *
 */
public class SimpleRNGEvaluator extends RNGEvaluator {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5104327181718273168L;
	
	
	/////////////////////////
	/**
	 * must be of numeric type; cannot be null;
	 * 
	 * if null valued for some record, output variable value will be evaluated to null for such record;
	 */
	private final InputVariable range1InputVariable;
	/**
	 * must be of numeric type; cannot be null;
	 * 
	 * if null valued for some record, output variable value will be evaluated to null for such record
	 */
	private final InputVariable range2InputVariable;
	
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param notes
	 * @param range1InputVariable not null; must be of numeric type
	 * @param range2InputVariable not null; must be of numeric type;
	 * @param outputVariable not null; must be of double type;
	 */
	public SimpleRNGEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID, 
			int indexID,
			VfNotes notes,
			ValueTableColumnOutputVariable outputVariable,
			
			InputVariable range1InputVariable,
			InputVariable range2InputVariable
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes, outputVariable);
		// validations
		
		if(range1InputVariable==null)
			throw new IllegalArgumentException("given range1InputVariable cannot be null!");
		if(!range1InputVariable.getSQLDataType().isNumeric())
			throw new IllegalArgumentException("given range1InputVariable must be of numeric type!");
		if(range2InputVariable==null)
			throw new IllegalArgumentException("given range2InputVariable cannot be null!");
		if(!range2InputVariable.getSQLDataType().isNumeric())
			throw new IllegalArgumentException("given range2InputVariable must be of numeric type!");
		

		///input variables must have different alias names
		if(range1InputVariable.getAliasName().equals(range2InputVariable.getAliasName()))
			throw new IllegalArgumentException("given range1InputVariable and range2InputVariable must have different alias names!");
		
		
		
		this.range1InputVariable = range1InputVariable;
		this.range2InputVariable = range2InputVariable;
		
	}

	public InputVariable getRange1InputVariable() {
		return range1InputVariable;
	}
	
	public InputVariable getRange2InputVariable() {
		return range2InputVariable;
	}

	////////////////////////////////////////////
	@Override
	public Map<SimpleName, InputVariable> getInputVariableAliasNameMap() {
//		if(this.inputVariableAliasNameMap==null) {
		Map<SimpleName, InputVariable> ret = new HashMap<>();
			
		ret.put(this.range1InputVariable.getAliasName(), this.range1InputVariable);
		ret.put(this.range2InputVariable.getAliasName(), this.range2InputVariable);
			
//		}
		return ret;
	}
	

	///////////////////////////////////
	private transient Random rand;
	private Random getRand() {
		if(this.rand == null)
			this.rand = new Random();
		
		return this.rand;
	}
	@Override
	public Map<OutputVariable, String> evaluate(Map<SimpleName, String> inputVariableAliasNameStringValueMap){
		Map<OutputVariable, String> ret = new HashMap<>();
		
		if(inputVariableAliasNameStringValueMap.get(this.range1InputVariable.getAliasName())==null||
				inputVariableAliasNameStringValueMap.get(this.range2InputVariable.getAliasName())==null) {
			ret.put(this.getOutputVariable(), null);
			return ret;
		}
		
		double range1 = Double.parseDouble(inputVariableAliasNameStringValueMap.get(this.range1InputVariable.getAliasName()));
		double range2 = Double.parseDouble(inputVariableAliasNameStringValueMap.get(this.range2InputVariable.getAliasName()));
		
		
		double value = range1+(range2-range1)*this.getRand().nextDouble();
		
		
		ret.put(this.getOutputVariable(), Double.toString(value));
		
		return ret;
	}


	
	//////////////////////////////
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public SimpleRNGEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		CompositionFunctionID reproducedHostCompositionFunctionID = 
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedIndexID = this.getIndexID();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		ValueTableColumnOutputVariable reproducedOutputVariable = 
				this.getOutputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		InputVariable range1InputVariable = this.getRange1InputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		InputVariable range2InputVariable = this.getRange2InputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		///////////////////////
		return new SimpleRNGEvaluator(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				reproducedOutputVariable,
				range1InputVariable,
				range2InputVariable
				);
	}

	
	/////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((range1InputVariable == null) ? 0 : range1InputVariable.hashCode());
		result = prime * result + ((range2InputVariable == null) ? 0 : range2InputVariable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof SimpleRNGEvaluator))
			return false;
		SimpleRNGEvaluator other = (SimpleRNGEvaluator) obj;
		if (range1InputVariable == null) {
			if (other.range1InputVariable != null)
				return false;
		} else if (!range1InputVariable.equals(other.range1InputVariable))
			return false;
		if (range2InputVariable == null) {
			if (other.range2InputVariable != null)
				return false;
		} else if (!range2InputVariable.equals(other.range2InputVariable))
			return false;
		return true;
	}

	
	
}
