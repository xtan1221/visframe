package basic.lookup.project.type.udt;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.project.type.VisframeUDTTypeManagerBase;
import context.project.VisProjectDBContext;
import importer.DataImporter;
import importer.DataImporterID;
import rdb.table.lookup.ManagementTableColumn;


/**
 * primary key = MetadataID of the imported metadata of the DataImporter
 * @author tanxu
 *
 */
public class VisProjectDataImporterManager extends VisframeUDTTypeManagerBase<DataImporter, DataImporterID>{
	
	
	////////////////////
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectDataImporterManager(VisProjectDBContext visProjectDBContext) {
		super(visProjectDBContext, DataImporter.class, DataImporterID.class);
	}

	
	
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap == null) {
			primaryKeyAttributeNameMap = new HashMap<>();
		
			primaryKeyAttributeNameMap.put(DataImporterID.NAME_COLUMN.getName(), DataImporterID.NAME_COLUMN);
			primaryKeyAttributeNameMap.put(DataImporterID.TYPE_COLUMN.getName(), DataImporterID.TYPE_COLUMN);
		}
		return primaryKeyAttributeNameMap;
	}
	
	/**
	 * no type specific column
	 */
	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		return new ArrayList<>();
	}

	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, DataImporter entity) throws SQLException {
		//do nothing
	}

	@Override
	protected void typeSpecificInsertionRelatedActivity(DataImporter t) throws SQLException {
//		this.getVisProjectDBContext().getDOSGraph().addImportedMetadata(t.getMainImportedMetadataID());
	}
}
