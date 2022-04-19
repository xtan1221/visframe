package operation.graph.layout.utils;

import java.util.ArrayList;
import java.util.List;

import basic.VfNotes;
import operation.graph.layout.GraphNode2DLayoutOperationBase;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;

/**
 * build the data table schema for a {@link GraphNode2DLayoutOperationBase} operation;
 * 
 * note that all coordinate columns can have null value because algorithm may produce NaN number;
 * @author tanxu
 *
 */
public class GraphNode2DLayoutCoordinateColumnUtils {
//	DataTableColumnName name, SQLDataType sqlDataType, boolean inPrimaryKey,
//	Boolean unique, Boolean notNull, String defaultStringValue, String additionalConstraints,
//	
//	VfNotes notes
	/////cartesian coordinate
	static final DataTableColumn X_COORD_COLUMN = new DataTableColumn(
			new DataTableColumnName("X"), SQLDataTypeFactory.doubleType(), false, 
			false, false, null, null, //Boolean unique, Boolean notNull, String defaultStringValue, String additionalConstraints,
			VfNotes.makeVisframeDefinedVfNotes());
	
	static final DataTableColumn Y_COORD_COLUMN = new DataTableColumn(
			new DataTableColumnName("Y"), SQLDataTypeFactory.doubleType(), false, 
			false, false, null, null, ////Boolean unique, Boolean notNull, String defaultStringValue, String additionalConstraints,
			VfNotes.makeVisframeDefinedVfNotes());
	/////polar coordinate
	static final DataTableColumn RADIUS_COORD_COLUMN = new DataTableColumn(
			new DataTableColumnName("RADIUS"), SQLDataTypeFactory.doubleType(), false, 
			false, false, null, "CHECK (RADIUS >= 0)", //value must be non-negative//Boolean unique, Boolean notNull, String defaultStringValue, String additionalConstraints,
			VfNotes.makeVisframeDefinedVfNotes());
	static final DataTableColumn THETA_COORD_COLUMN = new DataTableColumn(
			new DataTableColumnName("THETA"), SQLDataTypeFactory.doubleType(), false, 
			false, false, null, null, ////Boolean unique, Boolean notNull, String defaultStringValue, String additionalConstraints,
			VfNotes.makeVisframeDefinedVfNotes());
	
	/**
	 * return the pair of 2D coordinate columns based on whether the needed coordinate system is Cartesian or not (polar)
	 * @param cartesian
	 * @return
	 */
	public static List<DataTableColumn> get2DCoordColumnList(boolean cartesian){
		List<DataTableColumn> ret =  new ArrayList<>();
		
		if(cartesian) {
			ret.add(X_COORD_COLUMN);
			ret.add(Y_COORD_COLUMN);
		
		}else {
			ret.add(RADIUS_COORD_COLUMN);
			ret.add(THETA_COORD_COLUMN);
		}
		return ret;
	}
	
}
