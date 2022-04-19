package visinstance.run.calculation.function.composition;

import java.sql.SQLException;

import context.project.VisProjectDBContext;
import context.project.process.simple.VisInstanceRunInserterAndCalculator;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.variable.input.recordwise.RecordwiseInputVariable;
import function.variable.input.recordwise.type.CFGTargetInputVariable;
import function.variable.input.recordwise.type.RecordAttributeInputVariable;
import function.variable.input.recordwise.type.UpstreamValueTableColumnOutputVariableInputVariable;
import function.variable.output.type.CFGTargetOutputVariable;
import function.variable.output.type.TemporaryOutputVariable;
import function.variable.output.type.ValueTableColumnOutputVariable;
import metadata.record.RecordDataMetadata;
import rdb.table.HasIDTypeRelationalTableSchema;
import rdb.table.HasIDTypeRelationalTableSchemaID;
import rdb.table.value.type.CFTargetValueTableInitializer;
import rdb.table.value.type.CFTargetValueTableSchemaID;
import rdb.table.value.type.PiecewiseFunctionIndexIDOutputIndexValueTableInitializer;
import rdb.table.value.type.TemporaryOutputVariableValueTableInitializer;
import sql.SQLStringUtils;
import sql.derby.TableSchemaUtils;
import utils.Pair;
import visinstance.run.calculation.IndependentFIVTypeIDStringValueMap;


/**
 * 
 * initialize and populate the target value table of a CompositionFunction with a specific set of IndependentFreeInputVariableType values in the host VisProjectDBContext;
 * 
 * invoked from {@link VisInstanceRunInserterAndCalculator};
 * 
 * note that CFTargetValueTableRun is not inserted by this class, rather, it is inserted by the invoker {@link VisInstanceRunInserterAndCalculator} after the value table is successfully created in the host VisProjectDBContext;
 * @author tanxu
 * 
 */
public final class CFTargetValueTableRunCalculator {
	//////
	private final VisInstanceRunInserterAndCalculator visInstanceRunInserterAndCalculator;
	private final CompositionFunction targetCompositionFunction;
	private final IndependentFIVTypeIDStringValueMap independetFIVTypeStringValueMap;
	
	///////////////////////////////////
	/**
	 * run UID for the target CFTargetValueTableRun calculated by this {@link CFTargetValueTableRunCalculator}
	 */
	private int runUID; //used to build the unique value table names
	
	/**
	 * initializer for the target value table;
	 */
	private CFTargetValueTableInitializer CFTargetValueTableInitializer;
	
	private PiecewiseFunctionIndexIDOutputIndexValueTableInitializer piecewiseFunctionIndexIDOutputIndexValueTableInitializer;
	private TemporaryOutputVariableValueTableInitializer temporaryOutputVariableValueTableInitializer;
	
	/**
	 * 
	 */
	private RecordDataMetadata ownerRecordDataMetadata;
	
	/**
	 * 
	 * @param visInstanceRunCalculator
	 * @param targetCompositionFunction
	 * @param independetFIVTypeStringValueMap
	 * @throws SQLException 
	 */
	public CFTargetValueTableRunCalculator(
			VisInstanceRunInserterAndCalculator visInstanceRunCalculator,
			CompositionFunction targetCompositionFunction,
			IndependentFIVTypeIDStringValueMap independetFIVTypeStringValueMap
			) throws SQLException{
		if(visInstanceRunCalculator==null)
			throw new IllegalArgumentException("given visInstanceRunCalculator cannot be null!");
		if(targetCompositionFunction==null)
			throw new IllegalArgumentException("given targetCompositionFunction cannot be null!");
		if(independetFIVTypeStringValueMap==null)
			throw new IllegalArgumentException("given independetFIVTypeStringValueMap cannot be null!");
		
		
		
		this.visInstanceRunInserterAndCalculator = visInstanceRunCalculator;
		this.targetCompositionFunction = targetCompositionFunction;
		this.independetFIVTypeStringValueMap = independetFIVTypeStringValueMap;
		
		//Initialize
		this.makeRunUID();
		
		this.buildValueTables();
	}
	
	/**
	 * @return the independetFIVTypeStringValueMap
	 */
	public IndependentFIVTypeIDStringValueMap getIndependetFIVTypeStringValueMap() {
		return independetFIVTypeStringValueMap;
	}

	
	public int getRunUID() {
		return this.runUID;
	}

	/**
	 * @return the cFTargetValueTableInitializer
	 */
	public CFTargetValueTableInitializer getCFTargetValueTableInitializer() {
		return CFTargetValueTableInitializer;
	}

	/**
	 * @return the piecewiseFunctionIndexIDOutputIndexValueTableInitializer
	 */
	public PiecewiseFunctionIndexIDOutputIndexValueTableInitializer getPiecewiseFunctionIndexIDOutputIndexValueTableInitializer() {
		return piecewiseFunctionIndexIDOutputIndexValueTableInitializer;
	}

	/**
	 * @return the temporaryOutputVariableValueTableInitializer
	 */
	public TemporaryOutputVariableValueTableInitializer getTemporaryOutputVariableValueTableInitializer() {
		return temporaryOutputVariableValueTableInitializer;
	}
	
	
	
	///////////////////////////////
	public VisInstanceRunInserterAndCalculator getVisInstanceRunCalculator() {
		return visInstanceRunInserterAndCalculator;
	}
	
	/**
	 * return the target CompositionFunction;
	 * @return
	 */
	public CompositionFunction getTargetCompositionFunction() {
		return targetCompositionFunction;
	}
	
	public IndependentFIVTypeIDStringValueMap getTargetCFCFDGraphIndependetFIVStringValueMap() {
		return this.independetFIVTypeStringValueMap;
	}
	
	
	public VisProjectDBContext getHostVisProjectDBContext() {
		return this.getVisInstanceRunCalculator().getHostVisProjectDBContext();
	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public RecordDataMetadata getOwnerRecordDataMetadata() throws SQLException {
		if(this.ownerRecordDataMetadata==null) {
			this.ownerRecordDataMetadata = (RecordDataMetadata) this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().lookup(this.getTargetCompositionFunction().getOwnerRecordDataMetadataID());
		}
		return this.ownerRecordDataMetadata;
	}
	
	
	///////////////////////////////////////////////
	/**
	 * generate the UID of this run;
	 * @throws SQLException 
	 */
	private void makeRunUID() throws SQLException{
		this.runUID = this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCFTargetValueTableRunManager().findNextAvailableUID();
	}
	
	/**
	 * simply build the table schema for needed value tables;
	 * do NOT insert them into the VisProjectDBContext's VALUE schema yet;
	 * 		which is done in
	 * @throws SQLException
	 */
	private void buildValueTables() throws SQLException {
		
		this.CFTargetValueTableInitializer = new CFTargetValueTableInitializer(
				this.getHostVisProjectDBContext(), this.getTargetCompositionFunction(), this.runUID);
		
		/////
		if(this.targetCompositionFunction.getSortedListOfPiecewiseFunctionIndexID().isEmpty())
			return;
		
		this.piecewiseFunctionIndexIDOutputIndexValueTableInitializer = new PiecewiseFunctionIndexIDOutputIndexValueTableInitializer(
				this.getHostVisProjectDBContext(), this.getTargetCompositionFunction(), this.runUID);
		
		////
		//if there is no TemporaryOutputVariable, skip
		if(this.targetCompositionFunction.getSortedListOfTemporaryOutputVariable().isEmpty())
			return;
		
		this.temporaryOutputVariableValueTableInitializer = new TemporaryOutputVariableValueTableInitializer(
				this.getHostVisProjectDBContext(), this.getTargetCompositionFunction(), this.runUID);
		
	}

	
	/**
	 * calculate all ComponentFunctions on the ComponentFunction tree;
	 * 
	 * 1. insert needed value table scheam into host VisProjectDBContext
	 * 		then populate the value tables' RUID column
	 * 
	 * 2. invoke the target CompositionFunction’s {@link CompositionFunction#calculate(CFTargetValueTableRunCalculatorBase)} method
	 * 
	 * 3. drop the helper value tables from the rdb of the host VisProjectDBContext;
	 * @throws SQLException 
	 * 
	 */
	public void perform() throws SQLException{
		//1
		this.insertValueTablesAndPopulateRUIDColumn();
		
		//2 calculate CompositionFunction
		this.getTargetCompositionFunction().calculate(this);
		
		
		//3 drop helper value tables;
		this.dropPiecewiseFunctionIndexIDOutputIndexValueTable();
		this.dropTemporaryOutputVariableValueTable();
		
		
		//post-process and validations???
	}
	
	
	////////////////////////////////////////////////////////////////////

	/**
	 * insert needed value tables built by {@link #buildValueTables()}
	 * 
	 * then initialize the value tables by populating the RUID column;
	 * 
	 * must be invoked as the first step of {@link #perform()}
	 * @throws SQLException
	 */
	private void insertValueTablesAndPopulateRUIDColumn() throws SQLException {
		//
		this.CFTargetValueTableInitializer.insertIntoHostVisProjectDBContextAndPopulateRUIDColumn();
		if(this.piecewiseFunctionIndexIDOutputIndexValueTableInitializer!=null)
			this.piecewiseFunctionIndexIDOutputIndexValueTableInitializer.insertIntoHostVisProjectDBContextAndPopulateRUIDColumn();
		
		if(this.temporaryOutputVariableValueTableInitializer!=null)
			this.temporaryOutputVariableValueTableInitializer.insertIntoHostVisProjectDBContextAndPopulateRUIDColumn();
	}
	
	/**
	 * @throws SQLException 
	 * 
	 */
	private void dropPiecewiseFunctionIndexIDOutputIndexValueTable() throws SQLException {
		if(this.piecewiseFunctionIndexIDOutputIndexValueTableInitializer!=null)
			TableSchemaUtils.dropTable(
					this.getHostVisProjectDBContext().getDBConnection(), 
					this.piecewiseFunctionIndexIDOutputIndexValueTableInitializer.getValueTableSchema().getSchemaName(), 
					this.piecewiseFunctionIndexIDOutputIndexValueTableInitializer.getValueTableSchema().getName());
	}
	
	/**
	 * @throws SQLException 
	 * 
	 */
	private void dropTemporaryOutputVariableValueTable() throws SQLException {
		if(this.temporaryOutputVariableValueTableInitializer!=null)
			
			TableSchemaUtils.dropTable(
					this.getHostVisProjectDBContext().getDBConnection(), 
					this.temporaryOutputVariableValueTableInitializer.getValueTableSchema().getSchemaName(), 
					this.temporaryOutputVariableValueTableInitializer.getValueTableSchema().getName());
	}

	
	
	//////////////////
	/**
	 * build and return the involved table schema id and the full path name of the corresponding column of the given RecordwiseInputVariable;
	 * 
	 * @param iv
	 * @return
	 */
	public Pair<HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema>,String> buildRecordwiseInputVariableTableSchemaIDFullPathNameStringPair(RecordwiseInputVariable iv) {
		Pair<HasIDTypeRelationalTableSchemaID<? extends HasIDTypeRelationalTableSchema>,String> ret;
		
		if(iv instanceof CFGTargetInputVariable) {
			CFGTargetInputVariable cfgtiv = (CFGTargetInputVariable)iv;
			
			CompositionFunctionID cfid = this.getHostVisProjectDBContext().getCompositionFuncitionID(cfgtiv.getTargetCompositionFunctionGroupID(), cfgtiv.getTarget().getName());
			
			CFTargetValueTableRun run = this.visInstanceRunInserterAndCalculator.getCalculatedCFIDTargetValueTableRunMap().get(cfid);
			
			CFTargetValueTableSchemaID tableSchemaID = run.getTableSchemaID();
			
			String fullPathName = SQLStringUtils.buildTableColumnFullPathString(tableSchemaID, run.getTargetNameColumnNameMap().get(cfgtiv.getTarget().getName()));
			
			ret = new Pair<>(tableSchemaID, fullPathName);
		}else if(iv instanceof RecordAttributeInputVariable) {
			RecordAttributeInputVariable rdtciv = (RecordAttributeInputVariable)iv;
			
			String fullPathName = SQLStringUtils.buildTableColumnFullPathString(rdtciv.getDataTableSchemaID(), rdtciv.getColumn().getName().getStringValue());
			
			ret = new Pair<>(rdtciv.getDataTableSchemaID(), fullPathName);
			
		}else if(iv instanceof UpstreamValueTableColumnOutputVariableInputVariable) {
			UpstreamValueTableColumnOutputVariableInputVariable uvtcoviv = (UpstreamValueTableColumnOutputVariableInputVariable)iv;
			
			ValueTableColumnOutputVariable vtcov = uvtcoviv.getUpstreamValueTableColumnOutputVariable();
			
			if(vtcov instanceof CFGTargetOutputVariable) {
				CFGTargetOutputVariable tov = (CFGTargetOutputVariable)vtcov;
				
				String fullPathName = SQLStringUtils.buildTableColumnFullPathString(
						this.CFTargetValueTableInitializer.getValueTableSchema().getID(),
						this.CFTargetValueTableInitializer.getTargetNameColNameMap().get(tov.getTarget().getName()));
				
				ret = new Pair<>(this.CFTargetValueTableInitializer.getValueTableSchema().getID(), fullPathName);
			}else if(vtcov instanceof TemporaryOutputVariable) {
				TemporaryOutputVariable tov = (TemporaryOutputVariable)vtcov;
				
				String fullPathName = SQLStringUtils.buildTableColumnFullPathString(
						this.temporaryOutputVariableValueTableInitializer.getValueTableSchema().getID(),
						this.temporaryOutputVariableValueTableInitializer.getTemporaryOutputVariableColumnNameMap().get(tov));
				
				ret = new Pair<>(this.CFTargetValueTableInitializer.getValueTableSchema().getID(), fullPathName);
			}else {
				throw new UnsupportedOperationException();
			}
			
			
		}else {
			throw new UnsupportedOperationException();
		}
		
		return ret;
	}
}
