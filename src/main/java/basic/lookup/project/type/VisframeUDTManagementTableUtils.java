package basic.lookup.project.type;

import basic.SimpleName;
import basic.lookup.VisframeUDT;

public class VisframeUDTManagementTableUtils {
	public static final SimpleName VISFRAME_UDT_COLUMN_NAME = new SimpleName("VISFRAME_UDT");
	
	
	public static SimpleName makeVisframeUDTManagementTableName(Class<? extends VisframeUDT> type) {
		return new SimpleName(type.getSimpleName());
	}
	
}
