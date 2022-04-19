package sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;

public class ResultSetUtils {

	/**
	 * retrieve the string values for all the columns of the currently pointed record of the given {@link ResultSet} rs;
	 * 
	 * if the value for a column is null, the value will be NULL as well;
	 * 
	 * this method will assume the given ResultSet is non-empty and there is a valid record currently pointed by it;
	 * 
	 * also this method is only applicable for columns of {@link VfDefinedPrimitiveSQLDataType};
	 * 
	 * @param rs
	 * @param colNameSet
	 * @return
	 * @throws SQLException 
	 */
	public static Map<DataTableColumnName, String> getCurrentRecordColumnNameStringValueMap(ResultSet rs, Map<DataTableColumnName, DataTableColumn> colNameMap) throws SQLException{
		Map<DataTableColumnName, String> ret = new HashMap<>();
		
		for(DataTableColumnName columnName:colNameMap.keySet()) {
			ret.put(columnName, colNameMap.get(columnName).getSqlDataType().getStringValue(rs, columnName.getStringValue()));
		}
		
		return ret;
	}
}
