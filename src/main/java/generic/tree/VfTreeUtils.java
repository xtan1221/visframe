package generic.tree;

import java.util.LinkedHashMap;
import java.util.Map;

import rdb.table.data.DataTableColumnName;

public class VfTreeUtils {
	
	public static Map<DataTableColumnName, String> cloneMap(Map<DataTableColumnName, String> inputMap){
		if(inputMap==null) {
			return null;
		}
		
		Map<DataTableColumnName, String> ret = new LinkedHashMap<>();
		
		for(DataTableColumnName name:inputMap.keySet()) {
			ret.put(new DataTableColumnName(name.getStringValue()), inputMap.get(name));
		}
		
		return ret;
	}
}
