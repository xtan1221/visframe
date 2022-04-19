package function.variable.input.nonrecordwise.type;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.input.nonrecordwise.NonRecordwiseInputVariable;
import function.variable.input.recordwise.RecordwiseInputVariable;
import function.variable.output.type.ValueTableColumnOutputVariable;
import metadata.MetadataID;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * 
 * NonRecordwiseInputVariable whose value is the same for all record of the owner record data of the host CompositionFunction of this input variable;
 * 
 * 1. first select a target record data based on whose data table/CompositionFunctions this {@link SQLAggregateFunctionBasedInputVariable} will be calculated;
 * 2. then select the {@link #VfSQLAggregateFunctionType}
 * 3. create the input based on the selected {@link #VfSQLAggregateFunctionType}
 * 
 * Specifically the value of this input variable is based on 0, 1, or 2 
 * 1. columns of the TARGET record data and/or
 * 2. target of the CompositionFunctionGroup of the target record data and/or
 * 3. the upstream ValueTableColumnOutputVariable of the host Evaluator IF the owner record data of the host CFG of this input variable is the same with the target record data;
 * 
 * @author tanxu
 * 
 */
public class SQLAggregateFunctionBasedInputVariable extends NonRecordwiseInputVariable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3679840253028382696L;
	
	
	////////////////////////
	private final VfSQLAggregateFunctionType aggregateFunctionType;
	
	/**
	 * the record data based on whose related data this {@link SQLAggregateFunctionBasedInputVariable} is CALCULATED;
	 * 
	 * NOT necessarily the same with the owner record data of the host {@link CompositionFunctionGroup} of this {@link SQLAggregateFunctionBasedInputVariable};
	 * could be different or the same;
	 * 
	 * not null;
	 * 
	 * if the VfSQLAggregateFunctionType does not need input columns(for example, COUNT), targetRecordMetadataID will be used to facilitate calculation!
	 */
	private final MetadataID targetRecordMetadataID;
	
	//input 1 and input 2 can be 
	//1. column of the owner record data of required data type
			//column name
	//2. target of a CFG of the owner record data of the required data type
			//cfgID + target name
	//3. if the host CF is of a CFG of the owner record data, upstream ValueTableColumnOutputVariable of the required data type
			//ValueTableColumnOutputVariable
	
	/**
	 * first input;
	 * 
	 * must be null if the {@link #aggregateFunctionType} requires 0 input columns; 
	 * must be non-null otherwise;
	 * 
	 * the owner record data of {@link #firstInputColumn} must the same with {@link #targetRecordMetadataID} if not null
	 * 
	 * possible types of value
	 * 1. data table column of the {@link #targetRecordMetadataID}
	 * 2. {@link CFGTarget} of a {@link CompositionFunctionGroup} of the {@link #targetRecordMetadataID}
	 * 3. {@link ValueTableColumnOutputVariable} of upstream Evaluator of the host {@link Evaluator} of this {@link #SQLAggregateFunctionBasedInputVariable2} ONLY IF 
	 * 		the {@link #targetRecordMetadataID} is the same with the owner record data of the host {@link CompositionFunctionGroup} of this {@link #SQLAggregateFunctionBasedInputVariable2}
	 */
	private final RecordwiseInputVariable recordwiseInputVariable1;
	
	
	/**
	 * second input;
	 * 
	 * must be null if the {@link #aggregateFunctionType} requires 0 or 1 input columns; 
	 * must be non-null if the {@link #aggregateFunctionType} requires 2 input columns; ;
	 * 
	 * the owner record data of {@link #firstInputColumn} must the same with {@link #targetRecordMetadataID} if not null;
	 * 
	 * possible types of value
	 * 1. data table column of the {@link #targetRecordMetadataID}
	 * 2. {@link CFGTarget} of a {@link CompositionFunctionGroup} of the {@link #targetRecordMetadataID}
	 * 3. {@link ValueTableColumnOutputVariable} of upstream Evaluator of the host {@link Evaluator} of this {@link #SQLAggregateFunctionBasedInputVariable2} ONLY IF 
	 * 		the {@link #targetRecordMetadataID} is the same with the owner record data of the host {@link CompositionFunctionGroup} of this {@link #SQLAggregateFunctionBasedInputVariable2}
	 */
	private final RecordwiseInputVariable recordwiseInputVariable2;
	

	/**
	 * constructor
	 * @param aliasName 
	 * @param notes
	 * @param SQLDataType
	 * @param hostComponentFunctionIndexID
	 * @param hostEvaluatorIndexID
	 * @param aggregateFunctionType not null
	 * @param dataTableID cannot be null
	 * @param firstColumnName can be null
	 * @param secondColumnName can be null
	 */
	public SQLAggregateFunctionBasedInputVariable(
			CompositionFunctionID hostCompositionFunctionID, 
			int hostComponentFunctionIndexID,
			int hostEvaluatorIndexID,
			SimpleName aliasName, VfNotes notes,
			
			VfSQLAggregateFunctionType aggregateFunctionType,
			MetadataID targetRecordMetadataID,
			RecordwiseInputVariable recordwiseInputVariable1,
			RecordwiseInputVariable recordwiseInputVariable2
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID,
				hostEvaluatorIndexID, aliasName, notes);
		//validations 
		if(aggregateFunctionType==null)
			throw new IllegalArgumentException("given aggregateFunctionType cannot be null!");
		if(targetRecordMetadataID==null)
			throw new IllegalArgumentException("given targetRecordMetadataID cannot be null!");
		
		if(aggregateFunctionType.noInputColumnIsNeeded()) {//
			if(recordwiseInputVariable1!=null||recordwiseInputVariable2!=null) {
				throw new IllegalArgumentException("recordwiseInputVariable1 and recordwiseInputVariable2 must all be null when aggregateFunctionType does not need input columns");
			}
		
		}else {
			if(aggregateFunctionType.requiredNumOfColumn==1) {
				if(recordwiseInputVariable1==null) {
					throw new IllegalArgumentException("recordwiseInputVariable1 cannot be null when aggregateFunctionType's requiredNumOfColumn = 1");
				}
				
				if(recordwiseInputVariable2!=null)
					throw new IllegalArgumentException("recordwiseInputVariable2 must be null when aggregateFunctionType's requiredNumOfColumn = 1");
				
			}else if(aggregateFunctionType.requiredNumOfColumn==2) {
				if(recordwiseInputVariable1==null||recordwiseInputVariable2 ==null) {
					throw new IllegalArgumentException("recordwiseInputVariable1 and recordwiseInputVariable2 must all be non-null when aggregateFunctionType's requiredNumOfColumn = 2");
				}
//				
//				if(firstColumnName.equals(secondColumnName)) {
//					throw new IllegalArgumentException("firstColumnName and secondColumnName must be different when aggregateFunctionType's requiredNumOfColumn = 2");
//				}
			}
			
			if(aggregateFunctionType.inputColumnsMustBeNumeric) {
				if(!recordwiseInputVariable1.getSQLDataType().isNumeric() || (recordwiseInputVariable2!=null && !recordwiseInputVariable2.getSQLDataType().isNumeric())) {
					throw new IllegalArgumentException("recordwiseInputVariable1 (and recordwiseInputVariable2) must be numeric type when aggregateFunctionType requires numeric input column(s)!");
				}
			}
		}
		
		////
		this.aggregateFunctionType = aggregateFunctionType;
		this.targetRecordMetadataID = targetRecordMetadataID;
		this.recordwiseInputVariable1 = recordwiseInputVariable1;
		this.recordwiseInputVariable2 = recordwiseInputVariable2;
	}

	


	/**
	 * @return the aggregateFunctionType
	 */
	public VfSQLAggregateFunctionType getAggregateFunctionType() {
		return aggregateFunctionType;
	}

	/**
	 * @return the targetRecordMetadataID
	 */
	public MetadataID getTargetRecordMetadataID() {
		return targetRecordMetadataID;
	}


	/**
	 * @return the input1
	 */
	public RecordwiseInputVariable getRecordwiseInputVariable1() {
		return recordwiseInputVariable1;
	}

	/**
	 * @return the input2
	 */
	public RecordwiseInputVariable getRecordwiseInputVariable2() {
		return recordwiseInputVariable2;
	}

	/////////////////////
	@Override
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return aggregateFunctionType.getOutputSqlDataType();
	}
	
	/**
	 * reproduce and return a new SQLAggregateFunctionBasedInputVariable of this one;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException 
	 */
	@Override
	public SQLAggregateFunctionBasedInputVariable reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		//find out the copy index of the VCDNode to which the owner record Metadata is assigned
//		int copyIndexOfOwnerRecordMetadata = 
//				VSAArchiveReproducerAndInserter.getApplierArchive().lookupCopyIndexOfOwnerRecordMetadata(
//						this.getHostCompositionFunctionID().getHostCompositionFunctionGroupID(), copyIndex);
		
//		MetadataID reproducedOwnerRecordDataMetadataID = 
//				this.getOwnerRecordDataMetadataID().reproduce(
//						hostVisProjctDBContext, 
//						VSAArchiveReproducerAndInserter,
//						copyIndexOfOwnerRecordMetadata);//find out the copy index of owner record data
		
		//
		CompositionFunctionID reproducedHostCompositionFunctionID =
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedHostEvaluatorIndexID = this.getHostEvaluatorIndexID();
		SimpleName reproducedAliasName = this.getAliasName().reproduce();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		
		VfSQLAggregateFunctionType aggregateFunctionType = this.getAggregateFunctionType();
		
		//
		//first find out the copy index of the target record Metadata which is a depended record data of the owner cf of SQLAggregateFunctionBasedInputVariable
		int copyIndexOfTargetRecordMetadataID = 
				VSAArchiveReproducerAndInserter.getAppliedArchive().lookupDependedRecordMetadataCopyIndex(
						this.getHostCompositionFunctionID(), copyIndex, this.getTargetRecordMetadataID());
		
		MetadataID targetRecordMetadataID = 
				this.getTargetRecordMetadataID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndexOfTargetRecordMetadataID);
		
		//
		RecordwiseInputVariable recordwiseInputVariable1 = 
				this.getRecordwiseInputVariable1()==null?null:this.getRecordwiseInputVariable1().reproduce(
						hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex); //?
		RecordwiseInputVariable recordwiseInputVariable2 =
				this.getRecordwiseInputVariable2()==null?null:this.getRecordwiseInputVariable2().reproduce(
						hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex); //?
		
		
		return new SQLAggregateFunctionBasedInputVariable(
//				reproducedOwnerRecordDataMetadataID,
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedHostEvaluatorIndexID,
				reproducedAliasName,
				reproducedNotes,
				aggregateFunctionType,
				targetRecordMetadataID,
				recordwiseInputVariable1,
				recordwiseInputVariable2
				);
	}
	
	
	////////////////////////////////////////
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((aggregateFunctionType == null) ? 0 : aggregateFunctionType.hashCode());
		result = prime * result + ((recordwiseInputVariable1 == null) ? 0 : recordwiseInputVariable1.hashCode());
		result = prime * result + ((recordwiseInputVariable2 == null) ? 0 : recordwiseInputVariable2.hashCode());
		result = prime * result + ((targetRecordMetadataID == null) ? 0 : targetRecordMetadataID.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof SQLAggregateFunctionBasedInputVariable))
			return false;
		SQLAggregateFunctionBasedInputVariable other = (SQLAggregateFunctionBasedInputVariable) obj;
		if (aggregateFunctionType != other.aggregateFunctionType)
			return false;
		if (recordwiseInputVariable1 == null) {
			if (other.recordwiseInputVariable1 != null)
				return false;
		} else if (!recordwiseInputVariable1.equals(other.recordwiseInputVariable1))
			return false;
		if (recordwiseInputVariable2 == null) {
			if (other.recordwiseInputVariable2 != null)
				return false;
		} else if (!recordwiseInputVariable2.equals(other.recordwiseInputVariable2))
			return false;
		if (targetRecordMetadataID == null) {
			if (other.targetRecordMetadataID != null)
				return false;
		} else if (!targetRecordMetadataID.equals(other.targetRecordMetadataID))
			return false;
		return true;
	}

	////////////////////////////////////////


	//	public static class Input{
//		private final InputType type;
//		
//		private final SQLDataType dataType;
//		
//		private final RecordDataTableColumnInputVariable recordDataTableColumnInputVariable;
//		
//		private final CFGTargetInputVariable CFGTargetInputVariable;
//		
//		private final ValueTableColumnOutputVariable upstreamValueTableColumnOutputVariable;
//		
//		public Input(InputType type, 
//				SQLDataType dataType,
//				
//				RecordDataTableColumnInputVariable recordDataTableColumnInputVariable,
//				CFGTargetInputVariable CFGTargetInputVariable,
//				
//				ValueTableColumnOutputVariable upstreamValueTableColumnOutputVariable
//				) {
//			//validations
//			
//			
//			this.type = type;
//			this.dataType = dataType;
//			
//			this.recordDataTableColumnInputVariable = recordDataTableColumnInputVariable;
//			
//			this.CFGTargetInputVariable = CFGTargetInputVariable;
//			
//			this.upstreamValueTableColumnOutputVariable = upstreamValueTableColumnOutputVariable;
//			
//		}
//		
//	}
//	
//	public static enum InputType{
//		DATA_TABLE_COLUMN,
//		CFG_TARGET,
//		UPSTREAM_VALUE_TABLE_COLUMN_OUTPUT_VARIABLE;
//	}
	////////////////////////////////////////////
	/**
	 * types of SQL aggregate function implemented in visframe
	 * @author tanxu
	 * 
	 */
	public static enum VfSQLAggregateFunctionType{
		MAX(1, true, SQLDataTypeFactory.doubleType()),
		MIN(1, true, SQLDataTypeFactory.doubleType()),
		AVG(1, true, SQLDataTypeFactory.doubleType()),
		SUM(1, true, SQLDataTypeFactory.doubleType()),
		COUNT(null, null, SQLDataTypeFactory.integerType()),//input is a table
		CORRELATE(2, true, SQLDataTypeFactory.doubleType()), //two input columns
		CONTAINING_NULL(1, false, SQLDataTypeFactory.booleanType()); //whether a specific column contains null values or not
		
		private final Integer requiredNumOfColumn;
		private final Boolean inputColumnsMustBeNumeric;
		private final VfDefinedPrimitiveSQLDataType outputSqlDataType;
		
		VfSQLAggregateFunctionType(Integer requiredNumOfColumn, Boolean inputColumnsMustBeNumeric, VfDefinedPrimitiveSQLDataType outputSqlDataType){
			this.requiredNumOfColumn = requiredNumOfColumn;
			this.inputColumnsMustBeNumeric = inputColumnsMustBeNumeric;
			this.outputSqlDataType = outputSqlDataType;
		}
		
		/**
		 * whether or not no input columns is needed for this aggregate function type;
		 * if true, the only needed information is the input table;
		 * @return
		 */
		public boolean noInputColumnIsNeeded() {
			return this.requiredNumOfColumn==null&&this.inputColumnsMustBeNumeric==null;
		}
		
		public Boolean getInputColumnsMustBeNumeric() {
			return inputColumnsMustBeNumeric;
		}


		public VfDefinedPrimitiveSQLDataType getOutputSqlDataType() {
			return outputSqlDataType;
		}
		
		/**
		 * @return the requiredNumOfColumn
		 */
		public Integer getRequiredNumOfColumn() {
			return requiredNumOfColumn;
		}

	}
}
