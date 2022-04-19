package rdb.table.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import basic.VfNameString;
import basic.reproduce.DataReproducible;
import context.project.rdb.VisProjectRDBConstants;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import rdb.table.AbstractRelationalTableColumn;
import rdb.table.HasIDTypeRelationalTableSchema;

/**
 * data table schema 
 * 
 * RUID column is included in DataTableSchema; 
 * 
 * @author tanxu
 * 
 */
public class DataTableSchema extends HasIDTypeRelationalTableSchema<DataTableColumn> implements DataReproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2600012544957079762L;
	
	//////////////////////
	private transient Map<DataTableColumnName, DataTableColumn> columnNameMap;
	
	
	/**
	 * constructor
	 * @param tableName
	 * @param orderedListOfColumn list of columns
	 */
	public DataTableSchema(
			DataTableName tableName,
			List<DataTableColumn> orderedListOfColumn
			) {
		super(VisProjectRDBConstants.DATA_SCHEMA_NAME, tableName, orderedListOfColumn);
		
	}
	
	
	public List<DataTableColumn> getOrderedListOfSubSetColumns(
			Set<DataTableColumnName> columnNameSet) {
		List<DataTableColumn> ret = new ArrayList<>();
		
		for(DataTableColumn column:this.getOrderedListOfColumn()) {
			if(columnNameSet.contains(column.getName())) {
				ret.add(column);
			}
		}
		
		return ret;
	}
	
	/**
	 * return the DataTableColumn of the given column name;
	 * @param colName
	 * @return
	 */
	public DataTableColumn getColumn(DataTableColumnName colName) {
		if(this.columnNameMap == null) {
			this.columnNameMap = new HashMap<>();
			for(DataTableColumn column:this.getOrderedListOfColumn()) {
				this.columnNameMap.put(column.getName(), column);
			}
		}
		return this.columnNameMap.get(colName);
	}
	
	///////////////////////////////////////
	@Override
	public DataTableSchemaID getID() {
		return new DataTableSchemaID(this.getName());
	}
	
	@Override
	public DataTableName getName() {
		return (DataTableName)this.tableName;
	}
	
	
	@Override
	public List<DataTableColumn> getOrderedListOfColumn() {
		List<DataTableColumn> ret = new ArrayList<>();
		
		for(AbstractRelationalTableColumn column:this.orderedListOfColumn) {
			ret.add((DataTableColumn)column);
		}
		return ret;
	}
	
	

	
	@Override
	public Set<DataTableColumnName> getPrimaryKeyColumnNameSet() {
		Set<DataTableColumnName> ret = new LinkedHashSet<>();
		
		for(AbstractRelationalTableColumn column:this.orderedListOfColumn) {
			if(column.isInPrimaryKey()) {
				ret.add((DataTableColumnName)column.getName());
			}
		}
		return ret;
	}
	
	public Set<DataTableColumn> getPrimaryKeyColumnSet() {
		Set<DataTableColumn> ret = new LinkedHashSet<>();
		
		this.getPrimaryKeyColumnNameSet().forEach(colName->{
			ret.add(this.getColumn(colName));
		});
		return ret;
	}
	
	/**
	 * return the set of non-primary key column name set except for the RUID column
	 * @return
	 */
	public Set<DataTableColumnName> getNonRUIDNonPrimaryKeyColNameSet(){
		Set<DataTableColumnName> ret = new LinkedHashSet<>();
		this.getOrderedListOfNonRUIDColumn().forEach(col->{
			if(!col.isInPrimaryKey())
				ret.add(col.getName());
		});
		return ret;
	}
	
	/**
	 * return the set of non-primary key column set except for the RUID column
	 * @return
	 */
	public Set<DataTableColumn> getNonRUIDNonPrimaryKeyColSet(){
		Set<DataTableColumn> ret = new LinkedHashSet<>();
		this.getNonRUIDNonPrimaryKeyColNameSet().forEach(colName->{
			ret.add(this.getColumn(colName));
		});
		return ret;
	}
	
	@Override
	public List<DataTableColumnName> getOrderListOfColumnName() {
		List<DataTableColumnName> ret = new ArrayList<>();
		
		for(AbstractRelationalTableColumn column:this.orderedListOfColumn) {
			ret.add((DataTableColumnName)column.getName());
		}
		
		return ret;
	}
	
	
	public List<DataTableColumnName> getOrderListOfNonRUIDColumnName() {
		List<DataTableColumnName> ret = new ArrayList<>();
		
		for(AbstractRelationalTableColumn column:this.orderedListOfColumn) {
			if(column.getName().equals(DataTableSchemaFactory.makeRUIDColumn().getName())) {
				continue;
			}
			ret.add((DataTableColumnName)column.getName());
		}
		
		return ret;
	}
	
	@Override
	public int getColumnIndex(VfNameString columnName) {
		if(columnName instanceof DataTableColumnName) {
			return this.getOrderListOfColumnName().indexOf((DataTableColumnName)columnName)+1;
		}else {
			throw new IllegalArgumentException("given columnName is not of DataTableColumnName type");
		}
	}
	
	///////////////////////////
	/**
	 * reproduce and return a new DataTableSchema of this one;
	 * @throws SQLException 
	 */
	@Override
	public DataTableSchema reproduce(
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, 
			MetadataID ownerMetadataID, 
			int ownerMetadataCopyIndex) throws SQLException {
		
		List<DataTableColumn> orderedListOfColumn = new ArrayList<>();
		for(DataTableColumn column:this.getOrderedListOfColumn()) {
			orderedListOfColumn.add(column.reproduce(VSAArchiveReproducerAndInserter, ownerMetadataID, ownerMetadataCopyIndex));
		}
		
		return new DataTableSchema(
				this.getID().reproduce(VSAArchiveReproducerAndInserter, ownerMetadataID, ownerMetadataCopyIndex).getTableName(),
				orderedListOfColumn
				);
	}

	

}
