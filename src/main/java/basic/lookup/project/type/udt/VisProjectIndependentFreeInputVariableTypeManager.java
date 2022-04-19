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
import function.variable.independent.IndependentFreeInputVariableType;
import function.variable.independent.IndependentFreeInputVariableTypeID;
import rdb.table.lookup.ManagementTableColumn;

/**
 * a column in the management table that corresponding to the number CompositionFunctions that use the IndependentFreeInputVariableType in at least one of their FreeInputVriables;
 * 
 * @author tanxu
 * 
 */
public class VisProjectIndependentFreeInputVariableTypeManager extends VisframeUDTTypeManagerBase<IndependentFreeInputVariableType, IndependentFreeInputVariableTypeID>{
	
	private Map<SimpleName, ManagementTableColumn> primaryKeyAttributeNameMap;
	
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 */
	public VisProjectIndependentFreeInputVariableTypeManager(
			VisProjectDBContext visProjectDBContext
			) {
		super(visProjectDBContext, IndependentFreeInputVariableType.class, IndependentFreeInputVariableTypeID.class);
	}
	
	@Override
	public Map<SimpleName, ManagementTableColumn> getPrimaryKeyAttributeNameMap() {
		if(this.primaryKeyAttributeNameMap == null) {
			primaryKeyAttributeNameMap = new HashMap<>();
		
			primaryKeyAttributeNameMap.putAll(this.getHostVisProjectDBContext().getHasIDTypeManagerController().getCompositionFunctionManager().getPrimaryKeyAttributeNameMap());
			primaryKeyAttributeNameMap.put(IndependentFreeInputVariableTypeID.NAME_COLUMN.getName(), IndependentFreeInputVariableTypeID.NAME_COLUMN);
		}
		
		return primaryKeyAttributeNameMap;
	}
	
	
	@Override
	public List<ManagementTableColumn> getTypeSpecificManagementTableColumnList() {
		return new ArrayList<>();
	}

	@Override
	protected void setTypeSpecificColumnValues(PreparedStatement ps, IndependentFreeInputVariableType entity)
			throws SQLException {
		//do nothing
	}
	
	@Override
	protected void typeSpecificInsertionRelatedActivity(IndependentFreeInputVariableType t) {
		//do nothing
	}

}
