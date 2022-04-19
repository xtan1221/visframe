package visinstance.run.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import context.project.VisProjectDBContext;
import function.composition.CompositionFunction;
import function.composition.CompositionFunctionID;
import function.group.ShapeCFG;
import metadata.record.RecordDataMetadata;
import rdb.table.data.DataTableSchema;
import visinstance.run.calculation.function.composition.CFTargetValueTableRun;


/**
 * 
 * @author tanxu
 *
 * @param <T> 
 */
public abstract class CoreShapeCFGExtractorBase<T> {
	private final VisProjectDBContext hostVisProjectDBContext;
	/**
	 * 
	 */
	private final ShapeCFG coreShapeCFG;
	
	/**
	 * the set of CompositionFunction and their CFTargetValueTableRun included in this extractor; 
	 * not necessarily the full set of CompositionFunction of the core ShapeCFG!
	 */
	private final Map<CompositionFunction, CFTargetValueTableRun> compostionFunctionCFTargetValueTableRunMap;
	
	/////////////////////////////////////
	private Map<CompositionFunctionID, CompositionFunction> compositionFunctionIDMap;
	private DataTableSchema ownerRecordDataTableSchema;
	private String sqlQueryString;
	protected ResultSet resultSet;
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param coreShapeCFG
	 * @param compostionFunctionCFTargetValueTableRunMap
	 * @throws SQLException 
	 */
	CoreShapeCFGExtractorBase(
			VisProjectDBContext hostVisProjectDBContext, 
			ShapeCFG coreShapeCFG,
			Map<CompositionFunction, CFTargetValueTableRun> compostionFunctionCFTargetValueTableRunMap
			) throws SQLException{
		//
		
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		this.coreShapeCFG = coreShapeCFG;
		this.compostionFunctionCFTargetValueTableRunMap = compostionFunctionCFTargetValueTableRunMap;
		
		
		//////////////
		this.preprocess();
	}


	/**
	 * @return the hostVisProjectDBContext
	 */
	protected VisProjectDBContext getHostVisProjectDBContext() {
		return hostVisProjectDBContext;
	}

	/**
	 * @return the coreShapeCFG
	 */
	public ShapeCFG getCoreShapeCFG() {
		return coreShapeCFG;
	}

	/**
	 * @return the compostionFunctionCFTargetValueTableRunMap
	 */
	protected Map<CompositionFunction, CFTargetValueTableRun> getCompostionFunctionCFTargetValueTableRunMap() {
		return compostionFunctionCFTargetValueTableRunMap;
	}
	
	
	protected CompositionFunction getCompositionFunction(CompositionFunctionID cfID) {
		if(this.compositionFunctionIDMap == null) {
			this.compositionFunctionIDMap = new HashMap<>();
			this.compostionFunctionCFTargetValueTableRunMap.keySet().forEach(k->{
				this.compositionFunctionIDMap.put(k.getID(), k);
			});
		}
		
		return this.compositionFunctionIDMap.get(cfID);
	}
	
	/**
	 * @return the ownerRecordData
	 * @throws SQLException 
	 */
	protected DataTableSchema getOwnerRecordDataTableSchema() throws SQLException {
		if(this.ownerRecordDataTableSchema==null) {
			RecordDataMetadata recordData =  (RecordDataMetadata)this.getHostVisProjectDBContext().getMetadataLookup().lookup(this.coreShapeCFG.getOwnerRecordDataMetadataID());
			this.ownerRecordDataTableSchema =recordData.getDataTableSchema();
		}
		return this.ownerRecordDataTableSchema;
	}

	
	
	//////////////////////////////////
	protected abstract void preprocess() throws SQLException;
	
	/**
	 * build the full sql query string;
	 * @throws SQLException 
	 */
	protected void buildSqlQueryString() throws SQLException {
		this.sqlQueryString = 
				this.buildSelectSqlString().concat(" ")
				.concat(this.buildFromSqlString()).concat(" ")
				.concat(this.buildWhereSqlString());
	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException 
	 */
	protected abstract String buildSelectSqlString() throws SQLException;
	
	/**
	 * 
	 * @return
	 * @throws SQLException 
	 */
	protected abstract String buildFromSqlString() throws SQLException;
	
	/**
	 * 
	 * @return
	 * @throws SQLException 
	 */
	protected abstract String buildWhereSqlString() throws SQLException;
	
	
	/**
	 * run the built {@link #sqlQueryString} to initialize the {@link #resultSet};
	 * @throws SQLException
	 */
	protected void runSqlQuery() throws SQLException {
		Statement statement = this.getHostVisProjectDBContext().getDBConnection().createStatement();
		
		this.resultSet = statement.executeQuery(this.sqlQueryString);
	}
	
	/**
	 * return the object to be extracted for next record in the {@link #resultSet} resulted from the {@link #sqlQueryString};
	 * 
	 * if there is no more record left, return null;
	 * @return
	 * @throws SQLException 
	 */
	public abstract T nextRecord() throws SQLException;

}
