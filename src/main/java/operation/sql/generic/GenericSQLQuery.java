package operation.sql.generic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.misc.ParseCancellationException;

import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import metadata.record.RecordDataMetadata;
import operation.AbstractOperation;
import operation.OperationID;
import operation.sql.SQLOperationBase;
import operation.sql.generic.utils.GenericSQLQueryProcessor;
import operation.sql.generic.utils.GenericSQLQueryProcessor.DottedFullColumnName;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import sql.SQLStringUtils;
import utils.SQLStringParserUtils;

/**
 * delegate to a SQL query string for a {@link GenericSQLOperation};
 * 
 * this class is designed to work with the syntax rules defined in ANTLR4 MySqlLexer.g4 and MySqlParser.g4 for parsing;
 * 
 * see {@link SQLStringParserUtils} for the set of methods based on ANTLR4 to parse sql string;
 * 
 * distinguish with with {@link VfSQLExpression}
 * 
 * 
 * @author tanxu
 * 
 */
public class GenericSQLQuery implements Reproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3249896922823598578L;
	
	//////////////////////
	/**
	 * SQL query string that results in a table view with a set of columns
	 * 
	 * all the column names and table names should be in alias name form obeying the basic naming convention of visframe AND in uppercase;
	 * 
	 * all keywords and alias names should be in upper case;
	 * only single quoted string value can be in lower case;
	 * 
	 * 1. each column must be of the format TABLE_NAME_ALIAS.COLUMN_NAME_ALIAS;
	 * 2. Aggregate function is allowed;
	 * 3. AS key word is NOT allowed;
	 * 4. star '*' is not allowed in select clause
	 * 
	 * the minimal content of the string should contain SELECT and FROM clauses;
	 * 
	 * example:
	 * 		SELECT A.COL2, B.COL3, C.COL4 FROM A, B, C WHERE A.COL=2 AND B.COL='SDFdsfsdfs'
	 * 
	 * 
	 * cannot be null or empty;
	 */
	private final String aliasedSQLQueryString;
	
	/**
	 * map from the input table alias name present in the {@link #aliasedSQLQueryString} to the {@link DataTableAndColumnsMapping} containing the full mapping information of all the involved columns of the data table of the corresponding record Metadata;
	 * 
	 * cannot be null or empty;
	 */
	private final Map<String, DataTableAndColumnsMapping> tableAliasNameMappingMap;
	
	/**
	 * the list of column for output data table of the {@link #aliasedSQLQueryString};
	 * 
	 * column number should be consistent with the {@link #aliasedSQLQueryString};
	 * 
	 * the column with the same index in this schema should refer to the term in the SELECT clause in the {@link #aliasedSQLQueryString} with the same index?;
	 * 
	 * note that RUID column not included!!!
	 * 
	 * not null;
	 */
	private final List<DataTableColumn> orderedListOfColumnInOutputDataTable;


	/////////////////////////////////////////the following two fields are fully derived from the tableAliasNameMappingMap
	/**
	 * map from table alias name in the {@link #aliasedSQLQueryString} to the {@link MetadataID} of the {@link RecordDataMetadata} of the corresponding data table;
	 * 
	 */
	private final Map<String, MetadataID> tableAliasNameRecordDataMetadataIDMap;
	
	/**
	 * map from table alias name in the {@link #aliasedSQLQueryString} to the map from column;
	 */
	private final Map<String, Map<String, DataTableColumnName>> tableAliasNameColumnAliasNameDataTableColumnNameMapMap;
	
	
	///////////////////////////////////////////////
	/**
	 * OperationID of the owner {@link GenericSQLOperation};
	 * 
	 * needed to perform the {@link #reproduce(VisProjectDBContext, VisSchemeAppliedArchiveReproducerAndInserter, int)} method
	 * 
	 * set in the constructor of {@link GenericSQLOperation} since the OperationID is at the {@link AbstractOperation} level;
	 * 
	 */
	private OperationID ownerOperationID; //owner operationID
	
	/**
	 * output record MetadataID;
	 * 
	 * needed to reproduce the {@link #outputDataTableSchema};
	 * 
	 * set in the constructor of {@link GenericSQLOperation} since the output record MetadataID is at the {@link SQLOperationBase} level;
	 */
	private MetadataID outputRecordMetadataID;
	
	
	
	/**
	 * constructor
	 * @param ownerOperationID not null; of a {@link GenericSQLOperation}
	 * @param aliasedSQLQueryString not empty and consistent with the MySqlParser rules; not contain 'AS' keyword and '*' in select clause;
	 * @param tableAliasNameMappingMap not null or empty; contains all the involved tables and columns and the mapping to alias names;
	 * @param outputDataTableSchema
	 */
	/**
	 * 
	 * @param aliasedSQLQueryString not empty and consistent with the MySqlParser rules; not contain 'AS' keyword and '*' in select clause;
	 * @param tableAliasNameMappingMap not null or empty; contains all the involved tables and columns and the mapping to alias names;
	 * @param orderedListOfColumnInOutputDataTable list of DataTableColumn in output data table schema; order must be consistent with the result of the given aliasedSQLQueryString (not checked in constructor though, will be checked implicitly when inserting the owner Operation into host VisProjectDBContext); 
	 */
	public GenericSQLQuery(
			String aliasedSQLQueryString,
			Map<String, DataTableAndColumnsMapping> tableAliasNameMappingMap,
			List<DataTableColumn> orderedListOfColumnInOutputDataTable
			){
		//constraints;
		if(aliasedSQLQueryString==null||aliasedSQLQueryString.isEmpty())
			throw new IllegalArgumentException("given sqlStringWithAliasedIDs cannot be null or empty!");
			
		if(tableAliasNameMappingMap==null||tableAliasNameMappingMap.isEmpty())
			throw new IllegalArgumentException("given tableAliasNameMappingMap cannot be null or empty!");
		
		
		//build tableAliasNameRecordDataMetadataIDMap and tableAliasNameColumnAliasNameDataTableColumnNameMapMap based on tableAliasNameMappingMap
		tableAliasNameRecordDataMetadataIDMap = new LinkedHashMap<>();
		tableAliasNameColumnAliasNameDataTableColumnNameMapMap = new LinkedHashMap<>();
		tableAliasNameMappingMap.keySet().forEach(e->{
			tableAliasNameColumnAliasNameDataTableColumnNameMapMap.put(e, new LinkedHashMap<>());
		});
		tableAliasNameMappingMap.forEach((k,v)->{
			tableAliasNameRecordDataMetadataIDMap.put(k, v.getRecordMetadataID());
			tableAliasNameColumnAliasNameDataTableColumnNameMapMap.get(k).putAll(v.getColumnAliasNameDataTableColumnNameMap());
		});
		
		
		///check the consistency between the given aliasedSQLQueryString and the tableAliasNameRecordDataMetadataIDMap, tableAliasNameColumnAliasNameDataTableColumnNameMapMap
		try {
			GenericSQLQueryProcessor processor = new GenericSQLQueryProcessor(aliasedSQLQueryString);
			
			if(!processor.getTableNameSet().equals(tableAliasNameRecordDataMetadataIDMap.keySet()))
				throw new IllegalArgumentException("given table alias names in tableAliasNameRecordDataMetadataIDMap is not consistent with the sqlStringWithAliasedIDs!");
			
			processor.getTableNameDottedFullColumnNameSetMap().forEach((k,v)->{
				v.forEach(e->{
					if(!tableAliasNameColumnAliasNameDataTableColumnNameMapMap.get(k).containsKey(e.getColName())){
						throw new IllegalArgumentException("at least one table column in given sqlStringWithAliasedIDs is not found in given tableAliasNameColumnAliasNameDataTableColumnNameMapMap!");
					}
				});
			});
			
			
			//
			if(orderedListOfColumnInOutputDataTable.size()!=processor.getSelectElementNum())
				throw new IllegalArgumentException("given outputDataTableSchema contains unequal number of columns with the given sqlStringWithAliasedIDs!");
		
		}catch(ParseCancellationException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		
		
		/////////////////////////////
		this.aliasedSQLQueryString = aliasedSQLQueryString;
		this.tableAliasNameMappingMap = tableAliasNameMappingMap;
		this.orderedListOfColumnInOutputDataTable = orderedListOfColumnInOutputDataTable;
	}
	
	/**
	 * @return the sqlStringWithAliasedIDs
	 */
	public String getSqlStringWithAliasedIDs() {
		return aliasedSQLQueryString;
	}
	
	/**
	 * @return the tableAliasNameMappingMap
	 */
	public Map<String, DataTableAndColumnsMapping> getTableAliasNameMappingMap() {
		return tableAliasNameMappingMap;
	}
	
	/**
	 * @return the outputDataTableSchema
	 */
	public List<DataTableColumn> getOrderedListOfColumnInOutputDataTable() {
		return this.orderedListOfColumnInOutputDataTable;
	}
	
	
	/**
	 * @return the tableAliasNameRecordDataMetadataIDMap
	 */
	public Map<String, MetadataID> getTableAliasNameRecordDataMetadataIDMap() {
		return tableAliasNameRecordDataMetadataIDMap;
	}
	
	/**
	 * @return the tableAliasNameColumnAliasNameDataTableColumnNameMapMap
	 */
	public Map<String, Map<String, DataTableColumnName>> getTableAliasNameColumnAliasNameDataTableColumnNameMapMap() {
		return tableAliasNameColumnAliasNameDataTableColumnNameMapMap;
	}
	
	/////////////////////////////////////////
	/**
	 * return a SQL query string based on the {@link #aliasedSQLQueryString} with all table and column alias replaced with real names in the given host VisProjectDBContext;
	 * @param hostVisProjectDBContext
	 * @return
	 * @throws SQLException 
	 */
	public String buildCustomizedSQLStringWithRealTableAndColumnNames(VisProjectDBContext hostVisProjectDBContext) throws SQLException {
		GenericSQLQueryProcessor processor = new GenericSQLQueryProcessor(aliasedSQLQueryString);
		
		Map<String, String> tableNameReplacementMap = new HashMap<>();
		Map<DottedFullColumnName, String> dottedFullColumnNameReplacementMap = new HashMap<>();
		
		//
		for(String tableAliasName:this.tableAliasNameMappingMap.keySet()){
			DataTableAndColumnsMapping mapping = this.tableAliasNameMappingMap.get(tableAliasName);
			
			//
			RecordDataMetadata rdmd = (RecordDataMetadata) hostVisProjectDBContext.getHasIDTypeManagerController().getMetadataManager().lookup(mapping.getRecordMetadataID());
			String tableFullPathName = SQLStringUtils.buildTableFullPathString(rdmd.getDataTableSchema().getID());
			tableNameReplacementMap.put(tableAliasName, tableFullPathName);
			
			//
			mapping.getColumnAliasNameDataTableColumnNameMap().forEach((colAlias, colName)->{
				DottedFullColumnName dottedName = new DottedFullColumnName(tableAliasName, colAlias);
				
				String colFullPathName = SQLStringUtils.buildTableColumnFullPathString(rdmd.getDataTableSchema().getID(), colName); //schema.table.col
				
				dottedFullColumnNameReplacementMap.put(dottedName, colFullPathName);
			});
		}
		
		
		return processor.replace(tableNameReplacementMap, dottedFullColumnNameReplacementMap);
	}
	
	
	///////////////////////////////////////
	/**
	 * @return the ownerOperationID
	 */
	public OperationID getOwnerOperationID() {
		return ownerOperationID;
	}
	
	/**
	 * @param ownerOperationID the ownerOperationID to set
	 */
	public void setOwnerOperationID(OperationID ownerOperationID) {
		this.ownerOperationID = ownerOperationID;
	}
	
	/**
	 * @return the outputRecordMetadataID
	 */
	public MetadataID getOutputRecordMetadataID() {
		return outputRecordMetadataID;
	}

	/**
	 * @param outputRecordMetadataID the outputRecordMetadataID to set
	 */
	public void setOutputRecordMetadataID(MetadataID outputRecordMetadataID) {
		this.outputRecordMetadataID = outputRecordMetadataID;
	}

	/////////////////////////////////////////
	/**
	 * reproduce and return this {@link GenericSQLQuery};
	 * note that the {@link #ownerOperationID} and {@link #outputRecordMetadataID} will be set in the constructor of the owner reproduced {@link GenericQLOperation};
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Operation will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this Operation is assigned
	 * @throws SQLException 
	 */
	@Override
	public GenericSQLQuery reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int ownerOperationCopyIndex) throws SQLException{
		//1
		String reproducedSqlStringWithAliasedIDs = this.aliasedSQLQueryString;
		//2
		Map<String, DataTableAndColumnsMapping> reproducedTableAliasNameMappingMap = new HashMap<>();
		//note that record Metadata in each DataTableAndColumnsMapping is an input Metadata of the operation;
		for(String tableAliasName:this.tableAliasNameMappingMap.keySet()){
			DataTableAndColumnsMapping mapping = this.tableAliasNameMappingMap.get(tableAliasName);
			//find out the copy index of the input record Metadata in the DataTableAndColumnsMapping
			int inputRecordMetadataCopyIndex = 
					VSAArchiveReproducerAndInserter.getAppliedArchive().lookupCopyIndexOfOperationInputMetadata(
							this.ownerOperationID, ownerOperationCopyIndex, mapping.getRecordMetadataID());
			
			reproducedTableAliasNameMappingMap.put(
					tableAliasName, 
					mapping.reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, inputRecordMetadataCopyIndex));
		}
		
		//3
//		DataTableSchema reproducedOutputDataTableSchema = this.outputDataTableSchema.reproduce(VSAArchiveReproducerAndInserter, this.outputRecordMetadataID, ownerOperationCopyIndex);
		List<DataTableColumn> reproducedOrderedListOfColumnInOutputDataTable = new ArrayList<>();
		for(DataTableColumn col:this.getOrderedListOfColumnInOutputDataTable()){
			reproducedOrderedListOfColumnInOutputDataTable.add(col.reproduce(VSAArchiveReproducerAndInserter, this.outputRecordMetadataID, ownerOperationCopyIndex));
		}
		
		return new GenericSQLQuery(
				reproducedSqlStringWithAliasedIDs,
				reproducedTableAliasNameMappingMap,
				reproducedOrderedListOfColumnInOutputDataTable
				);
	}
	
	
	


	/////////////////////////////////////////
	///contains the non-final fields ownerOperationID and outputRecordMetadataID ?? TODO
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aliasedSQLQueryString == null) ? 0 : aliasedSQLQueryString.hashCode());
		result = prime * result + ((orderedListOfColumnInOutputDataTable == null) ? 0
				: orderedListOfColumnInOutputDataTable.hashCode());
		result = prime * result + ((outputRecordMetadataID == null) ? 0 : outputRecordMetadataID.hashCode());
		result = prime * result + ((ownerOperationID == null) ? 0 : ownerOperationID.hashCode());
		result = prime * result + ((tableAliasNameColumnAliasNameDataTableColumnNameMapMap == null) ? 0
				: tableAliasNameColumnAliasNameDataTableColumnNameMapMap.hashCode());
		result = prime * result + ((tableAliasNameMappingMap == null) ? 0 : tableAliasNameMappingMap.hashCode());
		result = prime * result + ((tableAliasNameRecordDataMetadataIDMap == null) ? 0
				: tableAliasNameRecordDataMetadataIDMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GenericSQLQuery))
			return false;
		GenericSQLQuery other = (GenericSQLQuery) obj;
		if (aliasedSQLQueryString == null) {
			if (other.aliasedSQLQueryString != null)
				return false;
		} else if (!aliasedSQLQueryString.equals(other.aliasedSQLQueryString))
			return false;
		if (orderedListOfColumnInOutputDataTable == null) {
			if (other.orderedListOfColumnInOutputDataTable != null)
				return false;
		} else if (!orderedListOfColumnInOutputDataTable.equals(other.orderedListOfColumnInOutputDataTable))
			return false;
		if (outputRecordMetadataID == null) {
			if (other.outputRecordMetadataID != null)
				return false;
		} else if (!outputRecordMetadataID.equals(other.outputRecordMetadataID))
			return false;
		if (ownerOperationID == null) {
			if (other.ownerOperationID != null)
				return false;
		} else if (!ownerOperationID.equals(other.ownerOperationID))
			return false;
		if (tableAliasNameColumnAliasNameDataTableColumnNameMapMap == null) {
			if (other.tableAliasNameColumnAliasNameDataTableColumnNameMapMap != null)
				return false;
		} else if (!tableAliasNameColumnAliasNameDataTableColumnNameMapMap
				.equals(other.tableAliasNameColumnAliasNameDataTableColumnNameMapMap))
			return false;
		if (tableAliasNameMappingMap == null) {
			if (other.tableAliasNameMappingMap != null)
				return false;
		} else if (!tableAliasNameMappingMap.equals(other.tableAliasNameMappingMap))
			return false;
		if (tableAliasNameRecordDataMetadataIDMap == null) {
			if (other.tableAliasNameRecordDataMetadataIDMap != null)
				return false;
		} else if (!tableAliasNameRecordDataMetadataIDMap.equals(other.tableAliasNameRecordDataMetadataIDMap))
			return false;
		return true;
	}


}	
