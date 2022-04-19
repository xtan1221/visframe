package operation.sql.predefined.type;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import metadata.record.RecordDataMetadata;
import operation.AbstractOperation;
import operation.Operation;
import operation.parameter.DataReproducibleParameter;
import operation.parameter.Parameter;
import operation.parameter.SimpleReproducibleParameter;
import operation.parameter.primitive.DoubleParameter;
import operation.sql.predefined.SingleInputRecordDataPredefinedSQLOperation;
import operation.sql.predefined.utils.CumulativeColumnSymjaExpressionDelegate;
import operation.sql.predefined.utils.SqlSortOrderType;
import operation.utils.DataTableColumnNameLinkedHashSet;
import operation.utils.SQLColumnSortOrderArrayList;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableName;
import rdb.table.data.DataTableSchema;
import rdb.table.data.DataTableSchemaFactory;
import sql.derby.TableContentSQLStringFactory;


/**
 * 
 * first group the data table of the input record data by a set of columns C1, then sort each group with a disjoint set of columns C2;
 * then for each record in each group, calculate a cumulative value based on a SYMJA expression string;
 * 
 * the output record data table columns is composed of C1, C2, the column for the cumulative value and any other columns from input data table to be kept in output record data table;
 * 
 * 
 * Null values of columns in the GROUP_BY_COLUMN_SET
 * Null values of a column are grouped as a separate group. //check it out
 * But in visframe, any record with one or more columns in the GROUP_BY_COLUMN_SET with null values will be ignored;
 * GROUP_BY_COLUMN_SET are all in the primary key of the output data table
 * 
 * Null values in ORDER_BY_COLUMN_SET
 * In sql, null valued records will be grouped either at the beginning or ending of the group, depending on the sorting type;
 * In visframe, any record with the null valued column in ORDER_BY_COLUMN_SET will be ignored;
 * ===================================
 * 
 * this operation can simplify to add an index column to each record by setting the CumulativeColumnSymjaExpressionDelegate as
 * 		a + 1
 * where a is the value of previous record's cumulative column and its initial value is 0 or 1;
 * 
 * ===================================
 * there is no constraints on relationship between 
 * 		input record data table's primary key column set (iPK) and 
 * 		{@link #GROUP_BY_COLUMN_NAME_SET}, {@link #ORDER_BY_COLUMN_NAME_SET}, {@link #OTHER_KEPT_NON_PRIMARY_KEK_COLUMN_NAME_SET};
 * 
 * it is allowed for none, one or more columns in iPK to be present in the three input column set as long as the constraints regarding the three input column sets are obeyed;
 * 
 * also, in the output record data table schema, all iPK should be present to ensure the data consistency;
 * thus, if for iPK columns not included in the three input column sets, add them to the data table schema of output record data table;
 * 
 * ===================================
 * scenarios regarding the {@link #GROUP_BY_COLUMN_NAME_SET and {@link #ORDER_BY_COLUMN_NAME_SET} being empty or not
 * 1. both {@link #GROUP_BY_COLUMN_NAME_SET and {@link #ORDER_BY_COLUMN_NAME_SET} are empty;
 * 		if all empty, the output record data table schema contains the iPK columns and the cumulative numeric column with all record in the same group and original order is used;
 * 2. if {@link #GROUP_BY_COLUMN_NAME_SET} is empty, but {@link #ORDER_BY_COLUMN_NAME_SET} is non-empty
 * 		simply sort full data set as a single group then calculate cumulative numeric column;
 * 		no concerns for case;
 * 3. if {@link #GROUP_BY_COLUMN_NAME_SET} is non-empty, but {@link #ORDER_BY_COLUMN_NAME_SET} is empty
 * 		simply group the data set and calculate cumulative numeric column with the order as it is in each group;
 * 		should be cautious because the order within each group maybe unpredictable;
 * 4. if both {@link #GROUP_BY_COLUMN_NAME_SET and {@link #ORDER_BY_COLUMN_NAME_SET} are non-empty
 * 		trivial
 * 
 * =================================
 * any records in the input record data table with any null valued column in the 
 * 		1. {@link #GROUP_BY_COLUMN_NAME_SET} or 
 * 		2. {@link #ORDER_BY_COLUMN_NAME_SET} or 
 * 		3. {@link CumulativeColumnSymjaExpressionDelegate#getColumnSymjaVariableNameMap()} of {@link #getCumulativeNumericColumnSymjaExpression()};
 * will be filtered out and not inserted into the output record data table;
 * 
 * ======================================
 * 
 * 
 * 
 * 
 * @author tanxu
 *
 */
public final class AddNumericCumulativeColumnOperation extends SingleInputRecordDataPredefinedSQLOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1837276362193020541L;
	
	///////operation type related
	public static final SimpleName TYPE_NAME = new SimpleName("AddNumericCumulativeColumnOperation");
	public static final VfNotes TYPE_NOTES = new VfNotes();
	
	///////////////////
	public static final DataTableColumnName DEFAULT_CUMULATIVE_COLUMN_NAME = new DataTableColumnName("VF_CUMULATIVE_VALUE");
	///////////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildAddNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap(
			LinkedHashSet<DataTableColumnName> groupByColumnNameSet,
			LinkedHashSet<DataTableColumnName> orderByColumnNameSet,
			ArrayList<SqlSortOrderType> orderByColumnSortTypeList,
			LinkedHashSet<DataTableColumnName> otherKeptNonPKColumnNameSet,
			CumulativeColumnSymjaExpressionDelegate cumulativeColumnSymjaExpression,
			DataTableColumnName cumulativeColumnNameInOutputDataTable, //can be null;
			double initialValue //can be null;
			){
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(GROUP_BY_COLUMN_NAME_SET.getName(), new DataTableColumnNameLinkedHashSet(groupByColumnNameSet));
		ret.put(ORDER_BY_COLUMN_NAME_SET.getName(), new DataTableColumnNameLinkedHashSet(orderByColumnNameSet));
		ret.put(ORDER_BY_COLUMN_SORT_TYPE_LIST.getName(), new SQLColumnSortOrderArrayList(orderByColumnSortTypeList));
		ret.put(OTHER_KEPT_NON_PRIMARY_KEK_COLUMN_NAME_SET.getName(), new DataTableColumnNameLinkedHashSet(otherKeptNonPKColumnNameSet));
		ret.put(CUMULATIVE_NUMERIC_COLUMN_SYMJA_EXPRESSION.getName(), cumulativeColumnSymjaExpression);
		ret.put(CUMULATIVE_COLUMN_NAME_IN_OUTPUT_DATA_TABLE.getName(), cumulativeColumnNameInOutputDataTable);
		ret.put(INITIAL_VALUE.getName(), initialValue);
		
		return ret;
	}
	
	//////////////////////////////////////
	/**
	 * parameter for column set to group by; can be empty set, but cannot be null;
	 * if empty, all data set will be treated as one single group;
	 * note that {@link #GROUP_BY_COLUMN_NAME_SET} and {@link #ORDER_BY_COLUMN_NAME_SET} cannot be both empty;
	 * 
	 * also {@link #GROUP_BY_COLUMN_NAME_SET} and {@link #ORDER_BY_COLUMN_NAME_SET} should be disjoint with each other???!!!!!!!!!!
	 * 
	 */
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> GROUP_BY_COLUMN_NAME_SET =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("groupByColumnNameSet"), new VfNotes(), "Column name set to group", 
					true, null, null);// mandatory, defaultValue, nonNullValueAdditionalConstraints 
	public DataTableColumnNameLinkedHashSet getGroupByColumnNameSet() {
		return (DataTableColumnNameLinkedHashSet)this.levelSpecificParameterObjectValueMap.get(GROUP_BY_COLUMN_NAME_SET.getName());
	}
	
	///
	/**
	 * parameter for column set to order by; can be empty set, but cannot be null;
	 * if empty, original order will be used;
	 * note that {@link #GROUP_BY_COLUMN_NAME_SET} and {@link #ORDER_BY_COLUMN_NAME_SET} cannot be both empty;
	 * 
	 * also {@link #GROUP_BY_COLUMN_NAME_SET} and {@link #ORDER_BY_COLUMN_NAME_SET} should be disjoint with each other???!!!!!!!!!!
	 */
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> ORDER_BY_COLUMN_NAME_SET =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("orderByColumnNameSet"), new VfNotes(), "Column name list to sort", 
					true, null, null);// mandatory, defaultValue, nonNullValueAdditionalConstraints 
	public DataTableColumnNameLinkedHashSet getOrderByColumnNameSet() {
		return (DataTableColumnNameLinkedHashSet) this.levelSpecificParameterObjectValueMap.get(ORDER_BY_COLUMN_NAME_SET.getName());
	}
	
	/**
	 * parameter for sort types for column set to order by; can be empty set, but cannot be null
	 * must be consistent with {@link #ORDER_BY_COLUMN_NAME_SET} in terms of number of elements;
	 */
	public static final SimpleReproducibleParameter<SQLColumnSortOrderArrayList> ORDER_BY_COLUMN_SORT_TYPE_LIST =
			new SimpleReproducibleParameter<>(SQLColumnSortOrderArrayList.class, new SimpleName("orderByColumnSortTypeList"), new VfNotes(), "list of sort type of Columns to sort", 
					true, null, null);// mandatory, defaultValue, nonNullValueAdditionalConstraints 
	public SQLColumnSortOrderArrayList getOrderByColumnSortTypeList() {
		return (SQLColumnSortOrderArrayList) this.levelSpecificParameterObjectValueMap.get(ORDER_BY_COLUMN_SORT_TYPE_LIST.getName());
	}
	
	//TODO
	//replace ORDER_BY_COLUMN_NAME_SET and ORDER_BY_COLUMN_SORT_TYPE_LIST with a LinkedHashMap<DataTableColumn, SqlSortOrderType>
	//

	
	/**
	 * parameter for other column set(different from GROUP_BY_COLUMN_SET and ORDER_BY_COLUMN_SET) to be kept in output data table schema; 
	 * can be empty set, but cannot be null; mandatory, no default value
	 */
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> OTHER_KEPT_NON_PRIMARY_KEK_COLUMN_NAME_SET =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("otherKeptNonPKColumnNameSet"), new VfNotes(), "other Non-PK Column name set to be kept in the output data table", 
					true, null, null);// mandatory, defaultValue, nonNullValueAdditionalConstraints 
	public DataTableColumnNameLinkedHashSet getOtherKeptNonPKColumnNameSet() {
		return (DataTableColumnNameLinkedHashSet)this.levelSpecificParameterObjectValueMap.get(OTHER_KEPT_NON_PRIMARY_KEK_COLUMN_NAME_SET.getName());
	}
	
	/**
	 * parameter for symja expression to calculate the cumulative numeric column value for each record;
	 * must be of numeric data type;
	 * 
	 * can be null; no default value; not mandatory
	 * 
	 * note that if null, the cumulative column will be irrelevant and will not be present in the output data table schema;
	 * and the operation simply assign an order index to each record in its group
	 */
	public static final DataReproducibleParameter<CumulativeColumnSymjaExpressionDelegate> CUMULATIVE_NUMERIC_COLUMN_SYMJA_EXPRESSION =
			new DataReproducibleParameter<>(CumulativeColumnSymjaExpressionDelegate.class, new SimpleName("cumulativeNumericColumnSymjaExpression"), new VfNotes(), "cumulative numeric column symja expression", 
					true, null, null);// mandatory, defaultValue, nonNullValueAdditionalConstraints 
	public CumulativeColumnSymjaExpressionDelegate getCumulativeNumericColumnSymjaExpression() {
		return (CumulativeColumnSymjaExpressionDelegate)this.levelSpecificParameterObjectValueMap.get(CUMULATIVE_NUMERIC_COLUMN_SYMJA_EXPRESSION.getName());
	}
	
	/**
	 * parameter for name of cumulative column in the output data table to hold the calculated cumulative value for each record;
	 * can be null; mandatory, no default value;
	 * 
	 * note that the data type of the cumulative column will be the same with the sql data type of the CUMULATIVE_COLUMN_SYMJA_EXPRESSION!!
	 */
	public static final DataReproducibleParameter<DataTableColumnName> CUMULATIVE_COLUMN_NAME_IN_OUTPUT_DATA_TABLE =
			new DataReproducibleParameter<>(DataTableColumnName.class, new SimpleName("cumulativeColumnNameInOutputDataTable"), new VfNotes(), "cumulative column name in output data table", 
					true, null, null);// mandatory, defaultValue, nonNullValueAdditionalConstraints
	
	public DataTableColumnName getCumulativeNumericColumnNameInOutputDataTable() {
		return this.levelSpecificParameterObjectValueMap.get(CUMULATIVE_COLUMN_NAME_IN_OUTPUT_DATA_TABLE.getName())==null?
				DEFAULT_CUMULATIVE_COLUMN_NAME:
					(DataTableColumnName)this.levelSpecificParameterObjectValueMap.get(CUMULATIVE_COLUMN_NAME_IN_OUTPUT_DATA_TABLE.getName());
	}
	
	/**
	 * parameter for initial cumulative value for the first record in each sorted group; 
	 * cannot be null, mandatory, no default value (thus an non-null value must be given in constructor)
	 */
	public static final DoubleParameter INITIAL_VALUE = 
			new DoubleParameter(new SimpleName("initialValue"), new VfNotes(), "Initial value", 
					true, null, null, false);// mandatory, defaultValue, nonNullValueAdditionalConstraints, inputDataTableContentDependent 
	public double getInitialValue() {
		return (Double)this.levelSpecificParameterObjectValueMap.get(INITIAL_VALUE.getName());
	}
	
	
	////////////////////
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap;
	/**
	 * return all Parameters defined at the GroupAndBinCountOperation level
	 * @return
	 */
	private static Map<SimpleName, Parameter<?>> levelSpecificParameterNameMap(){
		if(levelSpecificParameterNameMap==null) {
			levelSpecificParameterNameMap = new LinkedHashMap<>();
			levelSpecificParameterNameMap.put(GROUP_BY_COLUMN_NAME_SET.getName(), GROUP_BY_COLUMN_NAME_SET);
			levelSpecificParameterNameMap.put(ORDER_BY_COLUMN_NAME_SET.getName(), ORDER_BY_COLUMN_NAME_SET);
			levelSpecificParameterNameMap.put(ORDER_BY_COLUMN_SORT_TYPE_LIST.getName(), ORDER_BY_COLUMN_SORT_TYPE_LIST);
			levelSpecificParameterNameMap.put(OTHER_KEPT_NON_PRIMARY_KEK_COLUMN_NAME_SET.getName(), OTHER_KEPT_NON_PRIMARY_KEK_COLUMN_NAME_SET);
			
			levelSpecificParameterNameMap.put(CUMULATIVE_COLUMN_NAME_IN_OUTPUT_DATA_TABLE.getName(), CUMULATIVE_COLUMN_NAME_IN_OUTPUT_DATA_TABLE);
			levelSpecificParameterNameMap.put(CUMULATIVE_NUMERIC_COLUMN_SYMJA_EXPRESSION.getName(), CUMULATIVE_NUMERIC_COLUMN_SYMJA_EXPRESSION);
			levelSpecificParameterNameMap.put(INITIAL_VALUE.getName(), INITIAL_VALUE);
		}
		
		return levelSpecificParameterNameMap;
	}
	
	////////////////////////////////////////
	//object field
	private final Map<SimpleName, Object> levelSpecificParameterObjectValueMap; 
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param SQLOperationBaseLevelParameterObjectValueMap
	 * @param predefinedSQLBasedOperationLevelParameterObjectValueMap
	 * @param singleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap
	 * @param addNumericCumulativeColumnOperationLevelParameterObjectValueMap
	 */
	public AddNumericCumulativeColumnOperation(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			Map<SimpleName, Object> SQLOperationBaseLevelParameterObjectValueMap,
			Map<SimpleName, Object> predefinedSQLBasedOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> singleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap,
			Map<SimpleName, Object> addNumericCumulativeColumnOperationLevelParameterObjectValueMap,
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent
			) {
		super(operationLevelParameterObjectValueMap, SQLOperationBaseLevelParameterObjectValueMap,
				predefinedSQLBasedOperationLevelParameterObjectValueMap,
				singleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap);
		//always first validate each value objects with the constraints implemented in the Parameter<?> object of each parameter
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!addNumericCumulativeColumnOperationLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given addNumericCumulativeColumnOperationLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		
		this.levelSpecificParameterObjectValueMap = addNumericCumulativeColumnOperationLevelParameterObjectValueMap;
		
		///////////////check value constraints
		this.validateParametersValueConstraints(toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent);
	}
	
	/**
	 * {@inheritDoc}
	 * @throws SQLException 
	 */
	@Override
	protected void validateParametersValueConstraints(boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent) {
		//1. super class's constraints
		super.validateParametersValueConstraints(toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent);
		//2. level specific parameter's basic constraints defined by the Parameter class
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			Parameter<?> parameter = levelSpecificParameterNameMap().get(parameterName);
			
			if(!parameter.validateObjectValue(levelSpecificParameterObjectValueMap.get(parameterName), toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent)){
				throw new IllegalArgumentException("invalid value object found for singleGenericGraphAsInputOperationLevelParameterObjectValueMap:"+parameterName);
			}
		}
		//3. additional inter-parameter constraints involving parameters at this level

		//then validate constraints directly depending on the value object of input parameters but not implemented in the Parameter<?> object

		//1. {@link #GROUP_BY_COLUMN_NAME_SET}, ORDER_BY_COLUMN_SET and OTHER_KEPT_COLUMN_SET
		//1.1 must be disjoint
		for(DataTableColumnName colName:this.getGroupByColumnNameSet().getSet()) {
			if(this.getOrderByColumnNameSet().getSet().contains(colName)) {
				throw new IllegalArgumentException("duplicate column found in both GROUP_BY_COLUMN_NAME_SET and ORDER_BY_COLUMN_NAME_SET!");
			}
			if(this.getOtherKeptNonPKColumnNameSet().getSet().contains(colName)) {
				throw new IllegalArgumentException("duplicate column found in both GROUP_BY_COLUMN_NAME_SET and OTHER_KEPT_NON_PRIMARY_KEY_COLUMN_NAME_SET!");
			}
		}
		for(DataTableColumnName colName:this.getOrderByColumnNameSet().getSet()) {
			if(this.getOtherKeptNonPKColumnNameSet().getSet().contains(colName)) {
				throw new IllegalArgumentException("duplicate column found in both ORDER_BY_COLUMN_NAME_SET and OTHER_KEPT_NON_PRIMARY_KEY_COLUMN_NAME_SET!");
			}
		}
		
		//2. ORDER_BY_COLUMN_NAME_SET and ORDER_BY_COLUMN_SORT_TYPE_LIST must be of same size
		if(this.getOrderByColumnNameSet().getSet().size()!=this.getOrderByColumnSortTypeList().getList().size()) {
			throw new IllegalArgumentException("given ORDER_BY_COLUMN_NAME_SET and ORDER_BY_COLUMN_SORT_TYPE_LIST do not have same size!");
		}
		
		//3. CUMULATIVE_COLUMN_NAME_IN_OUTPUT_DATA_TABLE
		//TODO
				//if CUMULATIVE_COLUMN_SYMJA_EXPRESSION is not null, CUMULATIVE_COLUMN_NAME_IN_OUTPUT_DATA_TABLE must be different from any column names included in GROUP_BY_COLUMN_SET, ORDER_BY_COLUMN_SET and OTHER_KEPT_COLUMN_SET
		//
				//3. 
		
		//4. CUMULATIVE_NUMERIC_COLUMN_SYMJA_EXPRESSION must be of numeric data type;
		if(!this.getCumulativeNumericColumnSymjaExpression().getSymjaExpression().getSqlDataType().isNumeric()) {
			throw new IllegalArgumentException("given CUMULATIVE_NUMERIC_COLUMN_SYMJA_EXPRESSION is not of numeric type!");
		}
		
	}
	

	
	////////////////////////
	@Override
	public SimpleName getOperationTypeName() {
		return TYPE_NAME;
	}
	

	@Override
	public VfNotes getOperationTypeNotes() {
		return TYPE_NOTES;
	}

	
	//////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<SimpleName, Parameter<?>> getAllParameterNameMapOfCurrentAndAboveLevels() {
		Map<SimpleName, Parameter<?>> ret = new LinkedHashMap<>();
		ret.putAll(super.getAllParameterNameMapOfCurrentAndAboveLevels());
		ret.putAll(levelSpecificParameterNameMap());
		return ret;
	}
	
	@Override
	public Map<SimpleName, Object> getAllParameterNameValueObjectMapOfCurrentAndAboveLevels() {
		
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.putAll(super.getAllParameterNameMapOfCurrentAndAboveLevels());
		ret.putAll(levelSpecificParameterNameMap());
		
		return ret;
	}
	
	/**
	 * since {@link AbstractOperation} is the root class of {@link Operation} hierarchy, if the given parameter is not in {@link #getLevelSpecificParameterNameMap()}, throw {@link IllegalArgumentException}
	 */
	@Override
	public void setParameterValueObject(SimpleName parameterName, Object value) {
		if(levelSpecificParameterNameMap().containsKey(parameterName)) {
			this.setLevelSpecificParameterValueObject(parameterName, value);
		}else {
			super.setParameterValueObject(parameterName, value);
		}
	}
	
	@Override
	public void setLevelSpecificParameterValueObject(SimpleName parameterName, Object value) {
//		if(!levelSpecificParameterNameMap().get(parameterName).validateObjectValue(value, this.isReproduced())) {
//			throw new IllegalArgumentException("given parameter value object is invalid:"+value);
//		}
		
		this.levelSpecificParameterObjectValueMap.put(parameterName, value);
	}
	//////////////////////////////////////////////////////////
	/**
	 * <p>build and return the IndependentInputColumnSet of the single input record data;</p>
	 * 
	 * note that for {@link AddNumericCumulativeColumnOperation} type, all columns given in value object of {@link GROUP_BY_COLUMN_SET}, {@link ORDER_BY_COLUMN_SET}, and {@link OTHER_KEPT_COLUMN_SET} are independent input columns
	 */
	@Override
	public Map<MetadataID, Set<DataTableColumnName>> getInputRecordMetadataIDInputColumnNameSetMap() {
		Map<MetadataID, Set<DataTableColumnName>> ret = new HashMap<>();
		
		Set<DataTableColumnName> independentInputColumnNameSet = new HashSet<>();
		
		independentInputColumnNameSet.addAll(this.getGroupByColumnNameSet().getSet());
		independentInputColumnNameSet.addAll(this.getOrderByColumnNameSet().getSet());
		independentInputColumnNameSet.addAll(this.getOtherKeptNonPKColumnNameSet().getSet());
		
		
		//add the columns used in the syma expression to calculate the cumulative column value;
		independentInputColumnNameSet.addAll(this.getCumulativeNumericColumnSymjaExpression().getColumnSymjaVariableNameMap().keySet());
		
		//them remove those columns in primary key column set!!!!!!!!!!!!!
		//TODO
		
		
		
		ret.put(this.getInputRecordDataMetadataID(), independentInputColumnNameSet);
		return ret;
	}

	

	////////////////////////////////////////////////////
	/**
	 * reproduce and return a new AddNumericCumulativeColumnOperation of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException 
	 */
	@Override
	public AddNumericCumulativeColumnOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		return new AddNumericCumulativeColumnOperation(
//				true, /////
				this.reproduceAbstractOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceSQLOperationBaseLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproducePredefinedSQLBasedOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceSingleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceAddNumericCumulativeColumnOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				false //toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent should be false if the operation is reproduced;
				);
	}

	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException 
	 */
	private Map<SimpleName, Object> reproduceAddNumericCumulativeColumnOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException{
		//
		int inputRecordMetadataCopyIndex = 
				VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOperationInputMetadata(
						this.getID(), copyIndex, this.getInputRecordDataMetadataID());
		
		DataTableColumnNameLinkedHashSet groupByColumnSet =
				this.getGroupByColumnNameSet().reproduce(VSAArchiveReproducerAndInserter, this.getInputRecordDataMetadataID(), inputRecordMetadataCopyIndex);
		
		DataTableColumnNameLinkedHashSet orderByColumnSet = 
				this.getOrderByColumnNameSet().reproduce(VSAArchiveReproducerAndInserter, this.getInputRecordDataMetadataID(), inputRecordMetadataCopyIndex);
		
		SQLColumnSortOrderArrayList orderByColumnSortTypeList = 
				this.getOrderByColumnSortTypeList().reproduce();
		
		DataTableColumnNameLinkedHashSet otherKeptNonPKColumnNameSet = 
				this.getOtherKeptNonPKColumnNameSet().reproduce(VSAArchiveReproducerAndInserter, this.getInputRecordDataMetadataID(), inputRecordMetadataCopyIndex);
		
		CumulativeColumnSymjaExpressionDelegate cumulativeColumnSymjaExpression = 
				this.getCumulativeNumericColumnSymjaExpression().reproduce(VSAArchiveReproducerAndInserter, this.getInputRecordDataMetadataID(), inputRecordMetadataCopyIndex);
		
		DataTableColumnName cumulativeColumnNameInOutputDataTable = //output record data's column, use the copyIndex same with this operation
				this.getCumulativeNumericColumnNameInOutputDataTable().reproduce(
						VSAArchiveReproducerAndInserter, this.getOutputRecordDataID(), copyIndex);
		
		Double initialValue = this.getInitialValue();
		
		////////////////////////////
		Map<SimpleName, Object> ret = new HashMap<>();
		ret.put(GROUP_BY_COLUMN_NAME_SET.getName(), groupByColumnSet);
		ret.put(ORDER_BY_COLUMN_NAME_SET.getName(), orderByColumnSet);
		ret.put(ORDER_BY_COLUMN_SORT_TYPE_LIST.getName(), orderByColumnSortTypeList);
		
		ret.put(OTHER_KEPT_NON_PRIMARY_KEK_COLUMN_NAME_SET.getName(), otherKeptNonPKColumnNameSet);
		ret.put(CUMULATIVE_NUMERIC_COLUMN_SYMJA_EXPRESSION.getName(), cumulativeColumnSymjaExpression);
		ret.put(CUMULATIVE_COLUMN_NAME_IN_OUTPUT_DATA_TABLE.getName(), cumulativeColumnNameInOutputDataTable);
		ret.put(INITIAL_VALUE.getName(), initialValue);
		
		
		return ret;
	
	}
	

	/////////////////////////////////////////////////////////
	//call() method related facilitating methods
	/**
	 * build and return the {@link DataTableColumn} for cumulative numeric column
	 * @return
	 */
	private DataTableColumn makeCumulativeNumericColumn() {
		return new DataTableColumn(
				this.getCumulativeNumericColumnNameInOutputDataTable(),//DataTableColumnName name, 
				this.getCumulativeNumericColumnSymjaExpression().getSymjaExpression().getSqlDataType(),//SQLDataType sqlDataType, 
				false, //boolean inPrimaryKey, always true;
				false, //Boolean unique, 
				true,//Boolean notNull, 
				null,//String defaultStringValue, 
				null,//String additionalConstraints,
				VfNotes.makeVisframeDefinedVfNotes());
	}
	
	/**
	 * build and insert the data table schema for output record data of this operation;
	 * 
	 * the output table schema should includes the following columns
	 * 1. all columns in {@link #GROUP_BY_COLUMN_NAME_SET}
	 * 2. all columns in {@link #ORDER_BY_COLUMN_NAME_SET}
	 * 3. cumulative numeric column {@link #getCumulativeNumericColumnNameInOutputDataTable()};
	 * 4. primary key columns in input data table schema not included in above columns;
	 * 5. other non-primary key columns in input data table schema {@link #OTHER_KEPT_NON_PRIMARY_KEK_COLUMN_NAME_SET}
	 * 
	 * primary key column set should be the same with the input data table schema
	 */
	@Override
	protected void buildAndInsertOutputDataTableSchema() throws SQLException {
		this.inputRecordDataMetadata = (RecordDataMetadata)this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.getInputRecordDataMetadataID());
		
		DataTableName tableName = this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().findNextAvailableName(new DataTableName(this.getOutputRecordDataID().getName().getStringValue()));
		
		List<DataTableColumn> orderedListOfColumn = new ArrayList<>();
		
		//RUID
		orderedListOfColumn.add(DataTableSchemaFactory.makeRUIDColumn());
		
		//1. {@link #GROUP_BY_COLUMN_NAME_SET}
		for(DataTableColumnName colName:this.getGroupByColumnNameSet().getSet()) {
			//directly add the column from the input data table
			orderedListOfColumn.add(this.inputRecordDataMetadata.getDataTableSchema().getColumn(colName));
		}
		
		//2. {@link #ORDER_BY_COLUMN_NAME_SET}
		for(DataTableColumnName colName:this.getOrderByColumnNameSet().getSet()) {
			//directly add the column from the input data table
			orderedListOfColumn.add(this.inputRecordDataMetadata.getDataTableSchema().getColumn(colName));
		}
		
		//3. {@link #getCumulativeColumnNameInOutputDataTable()};
		orderedListOfColumn.add(this.makeCumulativeNumericColumn());
		
		//4. primary key columns in input data table schema not included in above columns;
		for(DataTableColumnName colName:this.inputRecordDataMetadata.getDataTableSchema().getPrimaryKeyColumnNameSet()) {
			if(!this.getGroupByColumnNameSet().getSet().contains(colName) && !this.getOrderByColumnNameSet().getSet().contains(colName)) {
				orderedListOfColumn.add(this.inputRecordDataMetadata.getDataTableSchema().getColumn(colName));
			}
		}
		
		//5. other non-primary key columns in input data table schema {@link #OTHER_KEPT_NON_PRIMARY_KEK_COLUMN_NAME_SET}
		for(DataTableColumnName colName:this.getOtherKeptNonPKColumnNameSet().getSet()) {
			//directly add the column from the input data table
			orderedListOfColumn.add(this.inputRecordDataMetadata.getDataTableSchema().getColumn(colName));
		}
		
		//
		this.outputDataTableSchema = new DataTableSchema(tableName, orderedListOfColumn);
		
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().insert(this.outputDataTableSchema);
	}
	
	
	/**
	 * build the sql query that group and sort the input data table as well as extract all the needed columns to populate the output record data table;
	 * 
	 * SELECT clause: 
	 * 		1. all columns in {@link #GROUP_BY_COLUMN_NAME_SET}
	 * 		2. all columns in {@link #ORDER_BY_COLUMN_NAME_SET}
	 * 		3. primary key columns in input data table schema not included in above columns;
	 * 		4. other non-primary key columns in input data table schema {@link #OTHER_KEPT_NON_PRIMARY_KEK_COLUMN_NAME_SET}
	 * 
	 * FROM clause: input record data table
	 * 
	 * WHERE clause: all columns in {@link #GROUP_BY_COLUMN_NAME_SET} and {@link #ORDER_BY_COLUMN_NAME_SET} are not NULL
	 * 
	 * !!!!NO GROUP BY clause!!!!!
	 * 
	 * ORDER BY clause: first columns in {@link #GROUP_BY_COLUMN_NAME_SET} each with sort order equals to ASC, then {@link #ORDER_BY_COLUMN_NAME_SET} each with sort order in {@link #ORDER_BY_COLUMN_SORT_TYPE_LIST}
	 * 
	 */
	@Override
	protected void buildSQLQueryString() {
		StringBuilder sb = new StringBuilder();
		
		//columns to be selected from the input record data table for cumulative column calculation and output record data table populating;
		//including those in the output record data schema and those in the {@link CumulativeColumnSymjaExpressionDelegate#getColumnSymjaVariableNameMap()}
		Set<String> selectedColNameUpperCaseSet = new LinkedHashSet<>();
		//columns from input record data table that must be not null when selecting from it;
		//includes those columns in {@link #GROUP_BY_COLUMN_NAME_SET} and {@link #ORDER_BY_COLUMN_NAME_SET} and {@link CumulativeColumnSymjaExpressionDelegate#getColumnSymjaVariableNameMap()}
		Set<String> notNullColNameUpperCaseStringSet = new LinkedHashSet<>();
		
		//add all columns except the cumulative column in output data table schema to selectedColNameUpperCaseSet
		for(DataTableColumn col:this.outputDataTableSchema.getOrderedListOfNonRUIDColumn()) {
			if(!col.getName().equals(this.getCumulativeNumericColumnNameInOutputDataTable())) {
				selectedColNameUpperCaseSet.add(col.getName().getStringValue().toUpperCase());
			}
		}
		
		//add all columns in {@link #GROUP_BY_COLUMN_NAME_SET} and {@link #ORDER_BY_COLUMN_NAME_SET} to notNullColNameUpperCaseStringSet
		this.getGroupByColumnNameSet().getSet().forEach(e->{
			notNullColNameUpperCaseStringSet.add(e.getStringValue().toUpperCase());
		});
		
		//add all columns in {@link CumulativeColumnSymjaExpressionDelegate} to both selectedColNameUpperCaseSet and notNullColNameUpperCaseStringSet
		for(DataTableColumnName colName:this.getCumulativeNumericColumnSymjaExpression().getColumnSymjaVariableNameMap().keySet()) {
			selectedColNameUpperCaseSet.add(colName.getStringValue().toUpperCase());
			notNullColNameUpperCaseStringSet.add(colName.getStringValue().toUpperCase());
		}
		
		
		//
		String whereCondition = TableContentSQLStringFactory.buildAllColumnValueNotNullConditionConditionString(notNullColNameUpperCaseStringSet);
		
		
		//select...from...where
		sb.append(TableContentSQLStringFactory.buildSelectSQLString(
				this.inputRecordDataMetadata.getDataTableSchema().getSchemaName().getStringValue(), 
				this.inputRecordDataMetadata.getDataTableSchema().getName().getStringValue(), 
				selectedColNameUpperCaseSet, 
				whereCondition));
		
		
		//order by
		List<String> orderByColNameStringList = new ArrayList<>();
		List<Boolean> orderByASCList = new ArrayList<>();
		
		this.getGroupByColumnNameSet().getSet().forEach(e->{
			orderByColNameStringList.add(e.getStringValue().toUpperCase());
			orderByASCList.add(true);
		});
		
		int i=0;
		for(DataTableColumnName colName:this.getOrderByColumnNameSet().getSet()) {
			orderByColNameStringList.add(colName.getStringValue().toUpperCase());
			orderByASCList.add(this.getOrderByColumnSortTypeList().getList().get(i).equals(SqlSortOrderType.ASCEND));
			i++;
		}
		
		if(!orderByColNameStringList.isEmpty())
			sb.append(" ORDER BY ").append(TableContentSQLStringFactory.buildOrderByClauseContentSqlString(orderByColNameStringList, orderByASCList));
		
		//
		this.SQLString = sb.toString();
	}
	
	
	/**
	 * parse the ResultSet from the sql query string and calculate the value for the cumulative numeric column and add to the PreparedStatement
	 */
	@Override
	protected void populateOutputDataTable() throws SQLException {
		
		Map<DataTableColumnName, DataTableColumn> groupByColNameMap = new LinkedHashMap<>();
		this.getGroupByColumnNameSet().getSet().forEach(colName->{
			groupByColNameMap.put(colName, this.inputRecordDataMetadata.getDataTableSchema().getColumn(colName));
		});
		
		Map<DataTableColumnName, DataTableColumn> orderByColNameMap = new LinkedHashMap<>();
		this.getOrderByColumnNameSet().getSet().forEach(colName->{
			orderByColNameMap.put(colName, this.inputRecordDataMetadata.getDataTableSchema().getColumn(colName));
		});
		
		Map<DataTableColumnName, DataTableColumn> otherNonPKColNameMap = new LinkedHashMap<>();
		this.getOtherKeptNonPKColumnNameSet().getSet().forEach(colName->{
			otherNonPKColNameMap.put(colName, this.inputRecordDataMetadata.getDataTableSchema().getColumn(colName));
		});
		
		Map<DataTableColumnName, DataTableColumn> otherPKColNameMap = new LinkedHashMap<>();
		this.inputRecordDataMetadata.getDataTableSchema().getOrderedListOfNonRUIDColumn().forEach(col->{
			if(col.isInPrimaryKey()) {
				if(!this.getGroupByColumnNameSet().getSet().contains(col.getName()) && !this.getOrderByColumnNameSet().getSet().contains(col.getName())) {
					otherPKColNameMap.put(col.getName(), col);
				}
			}
		});
		
		Map<DataTableColumnName, DataTableColumn> symjaExpressionVariableColNameMap = new LinkedHashMap<>();
		this.getCumulativeNumericColumnSymjaExpression().getColumnSymjaVariableNameMap().keySet().forEach(colName->{
			symjaExpressionVariableColNameMap.put(colName, this.inputRecordDataMetadata.getDataTableSchema().getColumn(colName));
		});
		
		///
		AddNumericCumulativeColumnOperationSqlResultSetProcessor processer = new AddNumericCumulativeColumnOperationSqlResultSetProcessor(
				this.getHostVisProjectDBContext(), 
				this.outputDataTableSchema,
				
				this.getInitialValue(),
				this.getCumulativeNumericColumnNameInOutputDataTable(),
				this.getCumulativeNumericColumnSymjaExpression(),
				
				groupByColNameMap,
				orderByColNameMap,
				otherNonPKColNameMap,
				otherPKColNameMap,
				symjaExpressionVariableColNameMap,
				
				this.resultSet
				);
		
		processer.perform();
		
	}

	
	/////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((levelSpecificParameterObjectValueMap == null) ? 0
				: levelSpecificParameterObjectValueMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof AddNumericCumulativeColumnOperation))
			return false;
		AddNumericCumulativeColumnOperation other = (AddNumericCumulativeColumnOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
	
}
