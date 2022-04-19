package context.project.rdb.initialize;

import java.sql.SQLException;

import basic.lookup.PrimaryKeyID;
import basic.lookup.VisframeUDT;
import context.project.VisProjectDBFeatures;

/**
 * create all management tables including Process_LOG table and management tables for VisframeUDTs
 * 
 * @author tanxu
 *
 */
public class VisProjectManagementTableBuilder extends AbstractVisProjectDBInitializer{
	
	/**
	 * constructor
	 * @param projectDBFeatures
	 */
	public VisProjectManagementTableBuilder(VisProjectDBFeatures projectDBFeatures) {
		super(projectDBFeatures);
	}
	
	
	/**
	 * create all management table schema;
	 * 
	 * also invoke any management table specific initialization;
	 */
	@Override
	public void initialize() throws SQLException {
		for(Class<? extends PrimaryKeyID<? extends VisframeUDT>> visframeUDT:this.getVisProjectDBFeatures().getHasIDTypeManagerController().getUDTTypeManagerMap().keySet()) {
			this.getVisProjectDBFeatures().getHasIDTypeManagerController().getUDTTypeManagerMap().get(visframeUDT)
			.createManagementTableSchemaInVisProjectRDB();
		}
		
		//insert visframe predefined file formats
		this.getVisProjectDBFeatures().getHasIDTypeManagerController().getFileFormatManager().insertVisframeDefinedFileFormats();
	}
	
	
	/**
	 * check if management table schema exists or not;
	 */
	@Override
	public boolean allExist() throws SQLException {
		System.out.println("================");
		for(Class<? extends PrimaryKeyID<? extends VisframeUDT>> visframeUDT:this.getVisProjectDBFeatures().getHasIDTypeManagerController().getUDTTypeManagerMap().keySet()) {
			System.out.println(visframeUDT.getSimpleName());
			if(!this.getVisProjectDBFeatures().getHasIDTypeManagerController().getUDTTypeManagerMap().get(visframeUDT).doesTableExist()) {
				return false;
			}
		}
		
		System.out.println("project management table schema are all found");
		return true;
	}
	
}
