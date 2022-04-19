package function.evaluator.nonsqlbased.stringprocessing;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.input.InputVariable;
import function.variable.input.nonrecordwise.type.ConstantValuedInputVariable;
import function.variable.output.OutputVariable;
import function.variable.output.type.CFGTargetOutputVariable;
import function.variable.output.type.ValueTableColumnOutputVariable;

/**
 * split the string value of the target input variable into two substrings by the string value of the splitter input variable;
 * if there is 0 appearance of splitter string, the first output variable will be the same with input target, second output variable will be empty string;
 * if there is 1 appearance, trivial;
 * if there are multiple appearance,  the target string will be split by the first appearance;
 * 
 * 
 * ======================================================
 * how to deal with null value of input variable for some record:
 * 
 * 		if any of {@link #targetInputVariable}, {@link #splitterStringInputVariable}'s string value is null for some record, 
 * 			the output variable will be null;
 * 
 * @author tanxu
 *
 */
public class StringSplitToTwoBySplitterStringEvaluator extends SimpleStringProcessingEvaluator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4567309292474903374L;
	
	/////////////////////////////
	/**
	 * input variable whose value string will be split; 
	 * 
	 * if null valued for some record, output variable's value will be null for such records;
	 * can be of any data type; if not string type, string format will be used;
	 * can be of any type including {@link ConstantValuedInputVariable} type; 
	 */
	private final InputVariable targetInputVariable;
	/**
	 * string value is used as splitter string to split the target input variable’s string value; 
	 * 
	 * if null valued for some record, output variable's value will be null for such records;
	 * 
	 * can be of any data type; if not string type, string format will be used;
	 * can be of any type including {@link ConstantValuedInputVariable} type; 
	 * 
	 * note that splitter string can be empty string;
	 */
	private final InputVariable splitterStringInputVariable;
	
	/**
	 * output variable for the concatenated string; must be of string type;
	 * since it is not boolean type, thus cannot be used for PiecewiseFunction's condition evaluator, thus ValueTableColumnOutputVariable type rather than EvaluatorSpecificOutputVariable
	 */
	private final ValueTableColumnOutputVariable outputVariable1;
	
	/**
	 * output variable for the concatenated string; must be of string type;
	 * since it is not boolean type, thus cannot be used for PiecewiseFunction's condition evaluator, thus ValueTableColumnOutputVariable type rather than EvaluatorSpecificOutputVariable
	 */
	private final ValueTableColumnOutputVariable outputVariable2;
	
	
	/**
	 * 
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param indexID
	 * @param notes
	 * @param targetInputVariable not null;
	 * @param splitterStringInputVariable not null;
	 * @param outputVariable1 not null; must be of string type;
	 * @param outputVariable2 not null; must be of string type;
	 */
	public StringSplitToTwoBySplitterStringEvaluator(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID, 
			int indexID,
			VfNotes notes,
			
			InputVariable targetInputVariable,
			InputVariable splitterStringInputVariable,
			ValueTableColumnOutputVariable outputVariable1,
			ValueTableColumnOutputVariable outputVariable2
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, indexID, notes);
		
		//validations
		if(targetInputVariable==null)
			throw new IllegalArgumentException("given targetInputVariable cannot be null!");
		
		if(splitterStringInputVariable==null)
			throw new IllegalArgumentException("given splitterStringInputVariable cannot be null!");
		
		if(outputVariable1==null)
			throw new IllegalArgumentException("given outputVariable1 cannot be null!");
		
		if(outputVariable2==null)
			throw new IllegalArgumentException("given outputVariable2 cannot be null!");
		
		if(!outputVariable1.getSQLDataType().isOfStringType())
			throw new IllegalArgumentException("given outputVariable1 must be of string type!");
		if(!outputVariable2.getSQLDataType().isOfStringType())
			throw new IllegalArgumentException("given outputVariable2 must be of string type!");
		
		//input variables must have different alias names
		Set<SimpleName> inputVariableAliasNameSet = new HashSet<>();
		inputVariableAliasNameSet.add(targetInputVariable.getAliasName());
		
		if(inputVariableAliasNameSet.contains(splitterStringInputVariable.getAliasName()))
			throw new IllegalArgumentException("duplicate alias name is found for multiple input variables:"+splitterStringInputVariable.getAliasName().getStringValue());
		
		//outputVariable1 and outputVariable2 should have different alias names
		if(outputVariable1.getAliasName().equals(outputVariable2.getAliasName()))
			throw new IllegalArgumentException("alias names of outputVariable1 and outputVariable2 cannot be the same!");
		
		
		//if both outputVariable1 and outputVariable2 are assigned to CFGTargets, the targets must be different!; 
		if(outputVariable1 instanceof CFGTargetOutputVariable && outputVariable2 instanceof CFGTargetOutputVariable) {
			CFGTargetOutputVariable ov1 = (CFGTargetOutputVariable)outputVariable1;
			CFGTargetOutputVariable ov2 = (CFGTargetOutputVariable)outputVariable2;
			if(ov1.getTargetName().equals(ov2.getTargetName()))
				throw new IllegalArgumentException("given outputVariable1 and outputVariable2 are both CFGTargetOutputVariable type and assigned with the same CFGTarget!");
		}
		
		
		this.targetInputVariable = targetInputVariable;
		this.splitterStringInputVariable = splitterStringInputVariable;
		this.outputVariable1 = outputVariable1;
		this.outputVariable2 = outputVariable2;
	}
	
	
	/**
	 * @return the targetInputVariable
	 */
	public InputVariable getTargetInputVariable() {
		return targetInputVariable;
	}


	/**
	 * @return the splitterStringInputVariable
	 */
	public InputVariable getSplitterStringInputVariable() {
		return splitterStringInputVariable;
	}


	/**
	 * @return the outputVariable1
	 */
	public ValueTableColumnOutputVariable getOutputVariable1() {
		return outputVariable1;
	}


	/**
	 * @return the outputVariable2
	 */
	public ValueTableColumnOutputVariable getOutputVariable2() {
		return outputVariable2;
	}

	//////////////////////////
	@Override
	public Map<SimpleName, InputVariable> getInputVariableAliasNameMap() {
//		if(this.inputVariableAliasNameMap==null) {
		Map<SimpleName, InputVariable> ret = new HashMap<>();
		ret.put(this.targetInputVariable.getAliasName(), this.targetInputVariable);
		ret.put(this.splitterStringInputVariable.getAliasName(), this.splitterStringInputVariable);
//		}
		return ret;
	}
	
	@Override
	public Map<SimpleName, OutputVariable> getOutputVariableAliasNameMap(){
//		if(this.outputVariableAliasNameMap == null) {
		Map<SimpleName, OutputVariable> ret = new HashMap<>();
		ret.put(this.outputVariable1.getAliasName(), this.outputVariable1);
		ret.put(this.outputVariable2.getAliasName(), this.outputVariable2);
//		}
		
		return ret;
	}
	
	
	//////////////////////////////
	@Override
	public Map<OutputVariable, String> evaluate(Map<SimpleName, String> inputVariableAliasNameStringValueMap) {
		Map<OutputVariable, String> ret = new HashMap<>();
		
		//if one of targetInputVariable, targetSubstringInputVariable or replacingStringInputVariable's string value is null, the output variable will be null;
		if(inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName())==null&&!(this.targetInputVariable instanceof ConstantValuedInputVariable)
			||
			inputVariableAliasNameStringValueMap.get(this.splitterStringInputVariable.getAliasName())==null&&!(this.splitterStringInputVariable instanceof ConstantValuedInputVariable)){
			
			ret.put(this.outputVariable1, null);
			ret.put(this.outputVariable2, null);
			return ret;
		}
		
		////////
		String targetString;
		if(this.targetInputVariable instanceof ConstantValuedInputVariable) {
			targetString = ((ConstantValuedInputVariable)targetInputVariable).getValueString();
		}else {
			targetString = inputVariableAliasNameStringValueMap.get(this.targetInputVariable.getAliasName());
		}
		
		String splitterString=null;
		if(this.splitterStringInputVariable instanceof ConstantValuedInputVariable) {
			splitterString = ((ConstantValuedInputVariable)splitterStringInputVariable).getValueString();
		}else {
			splitterString = inputVariableAliasNameStringValueMap.get(this.splitterStringInputVariable.getAliasName());
		}
		
		//////
		String[] splits = targetString.split(splitterString, 2);
		
		ret.put(this.outputVariable1, splits[0]);
		ret.put(this.outputVariable2, splits.length==2?splits[1]:"");
		
		return ret;
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
	public StringSplitToTwoBySplitterStringEvaluator reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		////
		CompositionFunctionID reproducedHostCompositionFunctionID = 
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedIndexID = this.getIndexID();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		InputVariable targetInputVariable = 
				this.getTargetInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		InputVariable splitterStringInputVariable = 
				this.getSplitterStringInputVariable().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		ValueTableColumnOutputVariable outputVariable1 = 
				this.getOutputVariable1().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		ValueTableColumnOutputVariable outputVariable2 = 
				this.getOutputVariable2().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		///////////////////////
		return new StringSplitToTwoBySplitterStringEvaluator(
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedIndexID,
				reproducedNotes,
				targetInputVariable,
				splitterStringInputVariable,
				outputVariable1,
				outputVariable2
			);
	}

	
	//////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((outputVariable1 == null) ? 0 : outputVariable1.hashCode());
		result = prime * result + ((outputVariable2 == null) ? 0 : outputVariable2.hashCode());
		result = prime * result + ((splitterStringInputVariable == null) ? 0 : splitterStringInputVariable.hashCode());
		result = prime * result + ((targetInputVariable == null) ? 0 : targetInputVariable.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StringSplitToTwoBySplitterStringEvaluator))
			return false;
		StringSplitToTwoBySplitterStringEvaluator other = (StringSplitToTwoBySplitterStringEvaluator) obj;
		if (outputVariable1 == null) {
			if (other.outputVariable1 != null)
				return false;
		} else if (!outputVariable1.equals(other.outputVariable1))
			return false;
		if (outputVariable2 == null) {
			if (other.outputVariable2 != null)
				return false;
		} else if (!outputVariable2.equals(other.outputVariable2))
			return false;
		if (splitterStringInputVariable == null) {
			if (other.splitterStringInputVariable != null)
				return false;
		} else if (!splitterStringInputVariable.equals(other.splitterStringInputVariable))
			return false;
		if (targetInputVariable == null) {
			if (other.targetInputVariable != null)
				return false;
		} else if (!targetInputVariable.equals(other.targetInputVariable))
			return false;
		return true;
	}
	
	
}