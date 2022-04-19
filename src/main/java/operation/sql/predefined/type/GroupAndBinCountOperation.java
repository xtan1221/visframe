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
import operation.sql.predefined.utils.SqlSortOrderType;
import operation.utils.DataTableColumnNameLinkedHashSet;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableName;
import rdb.table.data.DataTableSchema;
import rdb.table.data.DataTableSchemaFactory;
import sql.derby.TableContentSQLStringFactory;


/**
 * group an input record data table by a set of columns C1, then sort each group by a numeric column c not in C1; <br>
 * divide the value range of c in each sorted group with a bin value and count the number of records in each bin; <br>
 * output a record data with data table columns containing columns in C1 and the columns containing each bin information; <br>
 * <p></p>
 * in details:
 * columns of input record data table schema can be divided into three groups:
 * (columns in Group By), (A numeric column C to sort and bin), (other columns)
 * where 
 * (columns in Group By) == A set of columns to group; can be empty;
 * (A numeric column C to sort and bin) == A numeric column C to bin, which should not be in the GROUP BY set; cannot be null
 * (other columns) == other columns that are not in any of the previous two groups and will be absent in the output record data table;
 * 
 * 
 * output record data table schema:
 * (columns in Group By), bin_start, bin_end, bin_count
 * where
 * (columns in Group By) == same as the input data tables' (columns in Group By)
 * bin_start, bin_end, bin_count == information for each bin in each group
 * 
 * ===========================
 * Null values of columns in the GROUP_BY_COLUMN_SET
 * Null values of a column are grouped as a separate group. //check it out
 * But in visframe, any record with one or more columns in the GROUP_BY_COLUMN_SET with null values will be ignored;
 * GROUP_BY_COLUMN_SET are all in the primary key of the output data table
 * 
 * ===========================
 * note that this class employs the 
 * 		ORDER BY (group by columns + numeric binned column)
 * 		to realize grouping by the groupby columns then sort by the binned column
 * rather than
 * 		GROUP BY (group by columns) ORDER BY (numeric binned column)
 * 		since this is invalid in sql syntax!
 * 
 * 
 * @author tanxu
 *
 */
public final class GroupAndBinCountOperation extends SingleInputRecordDataPredefinedSQLOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7212283242225130244L;
	////////////////////////////
	///////operation type related
	public static final SimpleName TYPE_NAME = new SimpleName("GroupAndBinCountOperation");
	public static final VfNotes TYPE_NOTES = new VfNotes();
	
	
	/////////////////////////////////////
	////bin related table columns for the output data table
	public static final DataTableColumn BIN_START_COLUMN = 
			new DataTableColumn(new DataTableColumnName("BIN_START"), SQLDataTypeFactory.doubleType(), 
					true, false, true, null, null, VfNotes.makeVisframeDefinedVfNotes());//inPrimaryKey, unique, notNull, defaultStringValue, additionalConstraints
	public static final DataTableColumn BIN_END_COLUMN = 
			new DataTableColumn(new DataTableColumnName("BIN_END"), SQLDataTypeFactory.doubleType(), 
					true, false, true, null, null, VfNotes.makeVisframeDefinedVfNotes());//inPrimaryKey, unique, notNull, defaultStringValue, additionalConstraints
	public static final DataTableColumn BIN_COUNT_COLUMN = 
			new DataTableColumn(new DataTableColumnName("BIN_COUNT"), SQLDataTypeFactory.integerType(), 
					false, false, true, null, null, VfNotes.makeVisframeDefinedVfNotes());//inPrimaryKey, unique, notNull, defaultStringValue, additionalConstraints
	
	///////////////////////////////////////
	/**
	 * static method to build the level specific parameter name value object map with explicit parameter values
	 * @param name
	 * @param notes
	 * @return
	 */
	public static Map<SimpleName, Object> buildGroupAndBinCountOperationLevelSpecificParameterNameValueObjectMap(
			LinkedHashSet<DataTableColumnName> groupByColumnNameSet,
			DataTableColumnName numericColumnNameToSortAndBin,
			SqlSortOrderType numericColumnSortType,
			double binSize,
			Double binMin, //can be null;
			Double binMax //can be null;
			){
		Map<SimpleName, Object> ret = new LinkedHashMap<>();
		ret.put(GROUP_BY_COLUMN_NAME_SET.getName(), new DataTableColumnNameLinkedHashSet(groupByColumnNameSet));
		ret.put(NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN.getName(), numericColumnNameToSortAndBin);
		ret.put(NUMERIC_COLUMN_SORT_TYPE.getName(), numericColumnSortType);
		ret.put(BIN_SIZE.getName(), binSize);
		ret.put(BIN_MIN.getName(), binMin);
		ret.put(BIN_MAX.getName(), binMax);
		
		return ret;
	}
	//////////////////////////////////////
	///////GroupAndBinCountOperation specific parameters
	/**
	 * parameter for column set to group by; can be empty set, but cannot be null;
	 */
	public static final DataReproducibleParameter<DataTableColumnNameLinkedHashSet> GROUP_BY_COLUMN_NAME_SET =
			new DataReproducibleParameter<>(DataTableColumnNameLinkedHashSet.class, new SimpleName("groupByColumnNameSet"), new VfNotes(), "Column name set to group", 
					true, null, null);//mandatory, defaultValue, nonNullValueAdditionalConstraints 
	public DataTableColumnNameLinkedHashSet getGroupByColumnNameSet() {
		return (DataTableColumnNameLinkedHashSet) this.levelSpecificParameterObjectValueMap.get(GROUP_BY_COLUMN_NAME_SET.getName());
	}
	
	/**
	 * parameter for numeric column to sort and bin; cannot be null;
	 * must be numeric type( checked at the constructor??!!!)
	 */
	public static final DataReproducibleParameter<DataTableColumnName> NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN =
			new DataReproducibleParameter<>(DataTableColumnName.class, new SimpleName("numericColumnNameToSortAndBin"), new VfNotes(), "Numeric column name to sort and bin", 
					true, null, null);// 
	public DataTableColumnName getNumericColumnNameToSortAndBin() {
		return (DataTableColumnName)this.levelSpecificParameterObjectValueMap.get(NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN.getName());
	}
	/**
	 * parameter for sort type of numeric column to be sorted and binned; can be null; default value is ASC; mandatory
	 */
	public static final SimpleReproducibleParameter<SqlSortOrderType> NUMERIC_COLUMN_SORT_TYPE =
			new SimpleReproducibleParameter<>(SqlSortOrderType.class, new SimpleName("numericColumnSortType"), new VfNotes(), "Numeric column sort type", 
					true, SqlSortOrderType.ASCEND, null);// 
	public SqlSortOrderType getNumericColumnSortType() {
		return (SqlSortOrderType)this.levelSpecificParameterObjectValueMap.get(NUMERIC_COLUMN_SORT_TYPE.getName());
	}
	/**
	 * parameter for bin size; must be positive number; mandatory, no default value
	 */
	public static final DoubleParameter BIN_SIZE = 
			new DoubleParameter(new SimpleName("binSize"), new VfNotes(), "BIN size", 
					true, null, 
					bs->{return bs>0;} //additional constraints
					,false);// 
	public double getBinSize() {
		return (Double)this.levelSpecificParameterObjectValueMap.get(BIN_SIZE.getName());
	}
	
	/**
	 * parameter for min value of bin for each group; can be null; not mandatory, no default value
	 */
	public static final DoubleParameter BIN_MIN = 
			new DoubleParameter(new SimpleName("binMin"), new VfNotes(), "Minimal bin value to be include", 
					false, null, null, false);//  mandatory, defaultValue, nonNullValueAdditionalConstraints, inputDataTableContentDependent
	public Double getBinMin() {
		Object binMinObject = this.levelSpecificParameterObjectValueMap.get(BIN_MIN.getName());
		return binMinObject==null?null:(Double)binMinObject;
	}
	
	/**
	 * parameter for max value of bin for each group; can be null; not mandatory, no default value
	 */
	public static final DoubleParameter BIN_MAX = 
			new DoubleParameter(new SimpleName("binMax"), new VfNotes(), "Maximal bin value to be include", 
					false, null, null, false);//  mandatory, defaultValue, nonNullValueAdditionalConstraints, inputDataTableContentDependent
	public Double getBinMax() {
		Object binMaxObject = this.levelSpecificParameterObjectValueMap.get(BIN_MAX.getName());
		return binMaxObject==null?null:(Double)binMaxObject;
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
			levelSpecificParameterNameMap.put(NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN.getName(), NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN);
			levelSpecificParameterNameMap.put(NUMERIC_COLUMN_SORT_TYPE.getName(), NUMERIC_COLUMN_SORT_TYPE);
			levelSpecificParameterNameMap.put(BIN_SIZE.getName(), BIN_SIZE);
			levelSpecificParameterNameMap.put(BIN_MIN.getName(), BIN_MIN);
			levelSpecificParameterNameMap.put(BIN_MAX.getName(), BIN_MAX);
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
	 * @param groupAndBinCountOperationLevelParameterObjectValueMap
	 */
	public GroupAndBinCountOperation(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			Map<SimpleName, Object> SQLOperationBaseLevelParameterObjectValueMap,
			Map<SimpleName, Object> predefinedSQLBasedOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> singleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap,
			Map<SimpleName, Object> groupAndBinCountOperationLevelParameterObjectValueMap,
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent
			) {
		super(operationLevelParameterObjectValueMap, 
				SQLOperationBaseLevelParameterObjectValueMap,
				predefinedSQLBasedOperationLevelParameterObjectValueMap,
				singleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap);
		
		//always first validate each value objects with the constraints implemented in the Parameter<?> object of each parameter
		for(SimpleName parameterName:levelSpecificParameterNameMap().keySet()) {
			if(!groupAndBinCountOperationLevelParameterObjectValueMap.containsKey(parameterName)) {//parameter not found in the input value map
				throw new IllegalArgumentException("given groupAndBinCountOperationLevelParameterObjectValueMap does not contain the value for parameter:"+parameterName.getStringValue());
			}
		}
		//
		
		this.levelSpecificParameterObjectValueMap = groupAndBinCountOperationLevelParameterObjectValueMap;
		
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
		//TODO
		
		
		
//		//then validate constraints directly depending on the value object of input parameters but not implemented in the Parameter<?> object
//		//if the NUMERIC_COLUMN_TO_SORT_AND_BIN's value RelationalTableColumn is of numeric type;
//		RecordDataMetadata inputRecord = (RecordDataMetadata)this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.getInputRecordDataMetadataID());
//		
//		//check if the given column names are present in the data table schema of the input RecordDataMetadata
//		this.getGroupByColumnNameSet().getSet().forEach(colName->{
//			if(!inputRecord.getDataTableSchema().getOrderListOfColumnName().contains(colName)) {
//				throw new IllegalArgumentException("given group by col is not found in the data table schema of input RecordDataMetadata:"+colName.getStringValue());
//			}
//		});
//		if(!inputRecord.getDataTableSchema().getOrderListOfColumnName().contains(this.getNumericColumnNameToSortAndBin())) {
//			throw new IllegalArgumentException("given numeric binned col is not found in the data table schema of input RecordDataMetadata:"+this.getNumericColumnNameToSortAndBin().getStringValue());
//		}
//		
//		//
//		DataTableColumn sortedColumn = inputRecord.getDataTableSchema().getColumn(this.getNumericColumnNameToSortAndBin());
		
//		if(!sortedColumn.getSqlDataType().isNumeric()) {
//			throw new IllegalArgumentException("given column to sort and bin is not of numeric");
//		}
		
		//if BIN_MIN and BIN_MAX are both set to non-null value, BIN_MIN < BIN_MAX
		Double binMin = this.getBinMin();
		Double binMax = this.getBinMax();
		
		if(binMin!=null&&binMax!=null) {
			if(binMin>=binMax) {
				throw new IllegalArgumentException("given non-null bin min value is not smaller than non-null bin max value");
			}
			
			
			if(binMin+this.getBinSize()>=binMax) {
				throw new IllegalArgumentException("given bin min + bin size value is larger than given bin max value!");
			}
		}
		
		
//		Map<SimpleName, Object> groupAndBinCountOperationLevelParameterObjectValueMap = new HashMap<>();
//		groupAndBinCountOperationLevelParameterObjectValueMap.put(NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN.getName(), numericColumnToSortAndBin.getName());
//		groupAndBinCountOperationLevelParameterObjectValueMap.putAll(otherGroupAndBinCountOperationLevelParameterObjectValueMap);
		
	}
	
	
	///////////////////////////////////////////
	@Override
	public SimpleName getOperationTypeName() {
		return TYPE_NAME;
	}
	
	@Override
	public VfNotes getOperationTypeNotes() {
		return TYPE_NOTES;
	}
	
	
	/////////////////////////////////////////////////////////
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
	
	//////////////////////////////////////////////////////////////////////

	/**
	 * <p>build and return the IndependentInputColumnSet of the single input record data;</p>
	 * 
	 * note that for {@link GroupAndBinCountOperation} type, all columns given in value object of {@link GROUP_BY_COLUMN_SET} and {@link NUMERIC_COLUMN_TO_SORT_AND_BIN} are independent input columns
	 */
	@Override
	public Map<MetadataID, Set<DataTableColumnName>> getInputRecordMetadataIDInputColumnNameSetMap() {
		Map<MetadataID, Set<DataTableColumnName>> ret = new HashMap<>();
		
		Set<DataTableColumnName> independentInputColumnNameSet = new HashSet<>();
		
		independentInputColumnNameSet.addAll(this.getGroupByColumnNameSet().getSet());
		independentInputColumnNameSet.add(this.getNumericColumnNameToSortAndBin());
		
		ret.put(this.getInputRecordDataMetadataID(), independentInputColumnNameSet);
		
		return ret;
	}

	/////////////////////////////////////////////////////////////
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException 
	 */
	private Map<SimpleName, Object> reproduceGroupAndBinCountOperationLevelParameterObjectValueMap(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException{
		int inputRecordMetadataCopyIndex = 
				VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOperationInputMetadata(
						this.getID(), copyIndex, this.getInputRecordDataMetadataID());
		
		DataTableColumnNameLinkedHashSet groupByColumnSet =
				this.getGroupByColumnNameSet().reproduce(VSAArchiveReproducerAndInserter, this.getInputRecordDataMetadataID(), inputRecordMetadataCopyIndex);
		
		DataTableColumnName numericColumnNameToSortAndBin = 
				this.getNumericColumnNameToSortAndBin().reproduce(VSAArchiveReproducerAndInserter, this.getInputRecordDataMetadataID(), inputRecordMetadataCopyIndex);
		
		SqlSortOrderType numericColumnSortType = this.getNumericColumnSortType().reproduce();
		
		Double binSize = this.getBinSize();
		Double binMin = this.getBinMin();
		Double binMax = this.getBinMax();
		
		
		Map<SimpleName, Object> ret = new HashMap<>();
		ret.put(GROUP_BY_COLUMN_NAME_SET.getName(), groupByColumnSet);
		ret.put(NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN.getName(),numericColumnNameToSortAndBin);
		ret.put(NUMERIC_COLUMN_SORT_TYPE.getName(),numericColumnSortType);
		ret.put(BIN_SIZE.getName(),binSize);
		ret.put(BIN_MIN.getName(),binMin);
		ret.put(BIN_MAX.getName(),binMax);
		
		
		return ret;
	}
	
	
	
	/**
	 * reproduce and return a new GroupAndBinCountOperation
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @return
	 * @throws SQLException 
	 */
	@Override
	public GroupAndBinCountOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		return new GroupAndBinCountOperation(
//				true, ////
				this.reproduceAbstractOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceSQLOperationBaseLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproducePredefinedSQLBasedOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceSingleInputRecordDataPredefinedSQLOperationLevelParamterNameObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.reproduceGroupAndBinCountOperationLevelParameterObjectValueMap(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				false //toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent should be false if the operation is reproduced;
				);
	}
	
	////////////////////////////////////////////////////
	//call() method related facilitating methods

	/**
	 * build and insert the {@link DataTableSchema} of the output record data;
	 * 
	 * 1. generate the data table name;
	 * 2. build the column list
	 * 		add RUID column
	 * 		build and add columns based on {@link #GROUP_BY_COLUMN_NAME_SET} ; note that {@link #NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN} is NOT included
	 * 		add bin related columns 
	 * 			{@link #BIN_START_COLUMN}, {@link #BIN_END_COLUMN}, {@link #BIN_COUNT_COLUMN}
	 * note that primary key column set includes:
	 * 		{@link #GROUP_BY_COLUMN_NAME_SET}, {@link #BIN_START_COLUMN}, {@link #BIN_END_COLUMN}
	 * @throws SQLException 
	 */
	@Override
	protected void buildAndInsertOutputDataTableSchema() throws SQLException {
		this.inputRecordDataMetadata = (RecordDataMetadata)this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.getInputRecordDataMetadataID());
		
		DataTableName tableName = this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().findNextAvailableName(new DataTableName(this.getOutputRecordDataID().getName().getStringValue()));
		
		List<DataTableColumn> orderedListOfColumn = new ArrayList<>();
//		Set<DataTableColumnName> colNameSet = new HashSet<>();
		//RUID
		orderedListOfColumn.add(DataTableSchemaFactory.makeRUIDColumn());
		
		//GROUP_BY_COLUMN_NAME_SET
		for(DataTableColumnName colName:this.getGroupByColumnNameSet().getSet()) {
			DataTableColumn originalCol = this.inputRecordDataMetadata.getDataTableSchema().getColumn(colName);
			DataTableColumn newCol = new DataTableColumn(
					originalCol.getName(),//DataTableColumnName name, 
					originalCol.getSqlDataType(),//SQLDataType sqlDataType, 
					true, //boolean inPrimaryKey, always true;
					false, //Boolean unique, 
					true,//Boolean notNull, 
					null,//String defaultStringValue, 
					null,//String additionalConstraints,
					VfNotes.makeVisframeDefinedVfNotes());
			orderedListOfColumn.add(newCol);
			
//			colNameSet.add(newCol.getName());
		}
		
		//need to check if any column in the GROUP_BY_COLUMN_NAME_SET already take the column namse of the bin related columns; 
		//if yes, add integer index at the end TODO
		orderedListOfColumn.add(BIN_START_COLUMN);
		orderedListOfColumn.add(BIN_END_COLUMN);
		orderedListOfColumn.add(BIN_COUNT_COLUMN);
		
		this.outputDataTableSchema = new DataTableSchema(tableName, orderedListOfColumn);
		
		this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().insert(this.outputDataTableSchema);
	}
	
	/**
	 * build the sql string that group the input record data table with the {@link #GROUP_BY_COLUMN_NAME_SET} and sort by {@link #NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN} with sort order by {@link #getNumericColumnSortType()}
	 * note that this operation will automatically filter out records in the input record data table with one or more columns above having null values;
	 * 
	 * select clause:
	 * 		columns in {@link #GROUP_BY_COLUMN_NAME_SET} and {@link #NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN}
	 * from clause:
	 * 		data table of {@link #getInputRecordDataMetadataID()}
	 * where clause:
	 * 		all columns in {@link #GROUP_BY_COLUMN_NAME_SET} and {@link #NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN} must be non-null;
	 * group by clause:
	 * 		all columns in {@link #GROUP_BY_COLUMN_NAME_SET}, which can be empty;
	 * order by clause:
	 * 		{@link #NUMERIC_COLUMN_NAME_TO_SORT_AND_BIN}
	 */
	@Override
	protected void buildSQLQueryString() {
		StringBuilder sb = new StringBuilder();
		
		//
		Set<String> selectedColNameSet = new LinkedHashSet<>();
		Set<String> colNameUpperCaseStringSet = new LinkedHashSet<>();
		
		this.getGroupByColumnNameSet().getSet().forEach(e->{
			colNameUpperCaseStringSet.add(e.getStringValue().toUpperCase());
			selectedColNameSet.add(e.getStringValue().toUpperCase());
		});
		
		selectedColNameSet.add(this.getNumericColumnNameToSortAndBin().getStringValue().toUpperCase());
		colNameUpperCaseStringSet.add(this.getNumericColumnNameToSortAndBin().getStringValue().toUpperCase());
		
		//
		String whereCondition = TableContentSQLStringFactory.buildAllColumnValueNotNullConditionConditionString(colNameUpperCaseStringSet);
		
		
		//select...from...where
		sb.append(TableContentSQLStringFactory.buildSelectSQLString(
				this.inputRecordDataMetadata.getDataTableSchema().getSchemaName().getStringValue(), 
				this.inputRecordDataMetadata.getDataTableSchema().getName().getStringValue(), 
				selectedColNameSet, 
				whereCondition));
		
//		//group by
//		if(!this.getGroupByColumnNameSet().getSet().isEmpty()) {
//			List<String> groupByColNameStringList = new ArrayList<>();
//			this.getGroupByColumnNameSet().getSet().forEach(e->{
//				groupByColNameStringList.add(e.getStringValue().toUpperCase());
//			});
//			sb.append(" GROUP BY ").append(TableContentSQLStringFactory.buildGroupByClauseContentSqlString(groupByColNameStringList));
//		}
		
		//order by clause
		List<String> orderByColNameStringList = new ArrayList<>();
		List<Boolean> orderByASCList = new ArrayList<>();
		//first add the columns in the group by columns
		this.getGroupByColumnNameSet().getSet().forEach(e->{
			orderByColNameStringList.add(e.getStringValue().toUpperCase());
			orderByASCList.add(true);
		});
		//add the numeric binned column
		orderByColNameStringList.add(this.getNumericColumnNameToSortAndBin().getStringValue());
		orderByASCList.add(this.getNumericColumnSortType().equals(SqlSortOrderType.ASCEND));
		
		sb.append(" ORDER BY ").append(TableContentSQLStringFactory.buildOrderByClauseContentSqlString(orderByColNameStringList, orderByASCList));
		
		
		//
		this.SQLString = sb.toString();
	}
	
	/**
	 * process the ResultSet and populate the output record data table 
	 * 		
	 * @throws SQLException 
	 */
	@Override
	protected void populateOutputDataTable() throws SQLException {
		Map<DataTableColumnName, DataTableColumn> groupByColNameMap = new LinkedHashMap<>();
		
		this.getGroupByColumnNameSet().getSet().forEach(colName->{
			groupByColNameMap.put(colName, this.outputDataTableSchema.getColumn(colName));
		});
		//
		GroupAndBinCountOperationSqlResultSetProcessor processer = new GroupAndBinCountOperationSqlResultSetProcessor(
				this.getHostVisProjectDBContext(),
				this.outputDataTableSchema,
				
				this.getBinSize(),
				this.getBinMin(),
				this.getBinMax(),
				this.getNumericColumnSortType().equals(SqlSortOrderType.ASCEND),
				
				this.resultSet,
				groupByColNameMap,
				this.inputRecordDataMetadata.getDataTableSchema().getColumn(this.getNumericColumnNameToSortAndBin())
				);
		//
		processer.perform();
	}

	
	////////////////////////////////
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
		if (!(obj instanceof GroupAndBinCountOperation))
			return false;
		GroupAndBinCountOperation other = (GroupAndBinCountOperation) obj;
		if (levelSpecificParameterObjectValueMap == null) {
			if (other.levelSpecificParameterObjectValueMap != null)
				return false;
		} else if (!levelSpecificParameterObjectValueMap.equals(other.levelSpecificParameterObjectValueMap))
			return false;
		return true;
	}
	
	
	
}
