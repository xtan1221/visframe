package basic.lookup.project.type.udt;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.project.type.VisframeUDTManagementProcessRelatedTableColumnFactory;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import context.project.VisProjectDBContext;
import context.project.process.logtable.ProcessLogTableAndProcessPerformerManager;
import context.project.process.logtable.VfIDCollection;
import fileformat.FileFormat;
import fileformat.FileFormatID;
import fileformat.graph.GraphDataFileFormatType;
import fileformat.vftree.VfTreeDataFileFormatType;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.lookup.ManagementTableColumn;
import visinstance.run.calculation.function.composition.CFTargetValueTableRun;

public class VisProjectFileFormatManager extends VisframeUDTTypeManagerBase<FileFormat, FileFormatID>{
	
	/////////////////file format specific columns, may be updated if more features are needed(not put in {@link VisframeUDTManagementProcessRelatedTableColumnFactory})
	/**
	 * whether the file format is a default one defined by visframe or not;
	 * if true, cannot be deleted from the management table;
	 */
	public static final ManagementTableColumn isVfDefaultColumn = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("IS_VF_DEFAULT"), SQLDataTypeFactory.booleanType(), false, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null 
			);
	
	
	////////////////////
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	////////////////////////
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectFileFormatManager(
			VisProjectDBContext visProjectDBContext
			) {
		super(visProjectDBContext, FileFormat.class, FileFormatID.class);
	}
	
	/**
	 * insert the visframe defined FileFormats into the VisProjectFileFormat management table;
	 * 
	 * should be invoked during the initialization step of a VisProjectDBContext when first time connected;
	 * 
	 * note that this method should not involve the {@link ProcessLogTableAndProcessPerformerManager}
	 * 
	 * @throws SQLException 
	 */
	public void insertVisframeDefinedFileFormats() throws SQLException {
		
		//record type
		
		
		//graph type
		for(GraphDataFileFormatType type:GraphDataFileFormatType.values()) {
			this.insertVisframeDefinedFileFormat(type.getFileFormat());
		}
		
		
		//vftree type
		for(VfTreeDataFileFormatType type:VfTreeDataFileFormatType.values()) {
			this.insertVisframeDefinedFileFormat(type.getFileFormat());
		}
		
	}
	
	/**
	 * insert the visframe defined FileFormat;
	 * note that the insertion should not be depending on a running {@link ProcessLogTableAndProcessPerformerManager};
	 * all the process related columns should be assign default values explicitly;
	 * 
	 * 
	 * check {@link #insert(FileFormat)} for differences with this method
	 * 
	 * @param fileFormat
	 * @throws SQLException 
	 */
	private void insertVisframeDefinedFileFormat(FileFormat fileFormat) throws SQLException {
		
		PreparedStatement ps = this.getEmptyPreparedStatementForInsertion();
		
		this.setBasicColumnValues(ps, fileFormat);
		
		///////////////////////////process related columns 
		//first set the timestamp
		ps.setTimestamp(this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionTimeColumn.getName()), 
				new Timestamp(new Date().getTime()));
		
		//set columns for all VisframeUDT types except for CfTargetValueTableRun
		if(!this.getManagedType().equals(CFTargetValueTableRun.class)) {
			ps.setBoolean(this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.isTempColumn.getName()), 
					false); //not temp since no post-process
			ps.setObject(this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.insertionProcessIDColumn.getName()), 
					null);//null
		}
		
		//
		ps.setObject(this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.dependentProcessIDSetColumn.getName()), 
				new VfIDCollection()); 
		ps.setObject(this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.baseProcessIDSetColumn.getName()), 
				new VfIDCollection());
		ps.setObject(this.getManagementTableSchema().getColumnIndex(VisframeUDTManagementProcessRelatedTableColumnFactory.insertedNonProcessIDSetColumn.getName()), 
				new VfIDCollection());
		
		
		//set the isVfDefaultColumn to true;
		ps.setBoolean(
				this.getManagementTableSchema().getColumnIndex(isVfDefaultColumn.getName()), 
				true
				);
		
		ps.execute();
		
	}
	
	
	
	/////////////////////////////////////////////////////////////////
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap == null) {
			primaryKeyAttributeNameMap = new HashMap<>();
		
			primaryKeyAttributeNameMap.put(FileFormatID.NAME_COLUMN.getName(), FileFormatID.NAME_COLUMN);
			primaryKeyAttributeNameMap.put(FileFormatID.TYPE_COLUMN.getName(), FileFormatID.TYPE_COLUMN);
		}
		return primaryKeyAttributeNameMap;
	}
	
	
	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		List<ManagementTableColumn> ret = new ArrayList<>();
		ret.add(isVfDefaultColumn);
		return ret;
	}
	
	
	//////////////////////////////////////////////////////
	
	/**
	 * this method is only applicable for user-defined FileFormat
	 */
	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, FileFormat entity) throws SQLException {
		ps.setBoolean(
				this.getManagementTableSchema().getColumnIndex(isVfDefaultColumn.getName()), 
				false
				);
	}

	@Override
	protected void typeSpecificInsertionRelatedActivity(FileFormat t) {
		//do nothing
	}
	
}
