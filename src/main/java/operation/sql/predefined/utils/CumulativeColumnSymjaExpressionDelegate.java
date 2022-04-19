package operation.sql.predefined.utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import basic.reproduce.DataReproducible;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import rdb.table.data.DataTableColumnName;
import symja.VfSymjaVariableName;
import symja.VfSymjaSinglePrimitiveOutputExpression;

/**
 * contains a VfSymjaExpression for Cumulative numeric Column calculation for {@link AddNumericCumulativeColumnOperation}
 * 
 * some notable features
 * 1. there must be one variable in the expression assigned to the previouseRecordCumulativeColumnSymjaVariableName
 * 
 * 2. current version only support that the variables assigned to columns must be of numeric type, thus the assigned columns should have numeric sql data type
 * 		see {@link VfSymjaSinglePrimitiveOutputExpression#variableNameSQLDataTypeMap}
 * 3. the data type of the previouseRecordCumulativeColumnSymjaVariableName (which is equivalent to the expression type of the {@link VfSymjaSinglePrimitiveOutputExpression}) is double
 * 
 * @author tanxu
 *
 */
public class CumulativeColumnSymjaExpressionDelegate implements DataReproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2346418167946358517L;
	
	/////////////////
	/**
	 * the underlying {@link VfSymjaSinglePrimitiveOutputExpression}
	 */
	private final VfSymjaSinglePrimitiveOutputExpression symjaExpression;
	
	/**
	 * <p>map between the input table column and the alias name in the expression string;</p>
	 * different column MUST have distinct alias name;<br>
	 * can be empty if only the cumulative value of previous record is needed to calculate the cumulative value of current record;
	 * 
	 * note that values of those columns must be non-null;
	 */
	private final BiMap<DataTableColumnName,VfSymjaVariableName> columnNameSymjaVariableNameMap;
	
	/**
	 * variable name string in the expression that represent the value of the cumulative column of previous record;
	 * cannot be null or empty
	 */
	private final VfSymjaVariableName previouseRecordCumulativeColumnSymjaVariableName; 
	
	
	/**
	 * constructor
	 * @param symjaExpression
	 * @param columnSymjaVariableNameMap cannot be null; but can be empty; map from column to variable name string in symja expression; variable name in symja is case sensitive!!!!
	 * @param previouseRecordCumulativeColumnSymjaVariableName cannot be null or empty string;
	 */
	public CumulativeColumnSymjaExpressionDelegate(
			VfSymjaSinglePrimitiveOutputExpression symjaExpression,
			
			BiMap<DataTableColumnName,VfSymjaVariableName> columnSymjaVariableNameMap,
			
			VfSymjaVariableName previouseRecordCumulativeColumnSymjaVariableName
			
			) {
		
		//validation
		//given sqlDataType must be of numeric type
		if(!symjaExpression.getSqlDataType().isNumeric()) {
			throw new IllegalArgumentException("VfSymjaExpression is not of numeric type");
		}
		
		if(columnSymjaVariableNameMap==null) {
			throw new IllegalArgumentException("given columnSymjaVariableNameMap cannot be null!");
		}
		
		if(previouseRecordCumulativeColumnSymjaVariableName==null) {
			throw new IllegalArgumentException("given previouseRecordCumulativeColumnSymjaVariableName cannot be null!");
		}
		
		
		//columnNameSymjaVariableNameMap should contain distinct map values, all of which must be different from previouseRecordCumulativeColumnSymjaVariableName
		Set<VfSymjaVariableName> symjaVariableNameSet = new HashSet<>();
		columnSymjaVariableNameMap.forEach((k,v)->{
			if(symjaVariableNameSet.contains(v)) {
				throw new IllegalArgumentException("duplicate variable name are found for multiple columns in given columnSymjaVariableNameMap!");
			}
			symjaVariableNameSet.add(v);
		});
		if(symjaVariableNameSet.contains(previouseRecordCumulativeColumnSymjaVariableName)) {
			throw new IllegalArgumentException("given previouseRecordCumulativeColumnSymjaVariableName is duplicate with one in columnSymjaVariableNameMap!");
		}
		
		
		//the columnNameSymjaVariableNameMap and previouseRecordCumulativeColumnSymjaVariableName should be in the symjaExpression
		columnSymjaVariableNameMap.forEach((k,v)->{
			if(!symjaExpression.getVariableNameSQLDataTypeMap().keySet().contains(v)) {
				throw new IllegalArgumentException("variable name in given columnSymjaVariableNameMap is not found in the given symjaExpression!");
			}
		});
		
		if(!symjaExpression.getVariableNameSQLDataTypeMap().keySet().contains(previouseRecordCumulativeColumnSymjaVariableName)) {
			throw new IllegalArgumentException("given previouseRecordCumulativeColumnSymjaVariableName is not found in the given symjaExpression!");
		}
		
		
		
		
		//
		this.symjaExpression = symjaExpression;
		
		this.columnNameSymjaVariableNameMap = columnSymjaVariableNameMap;
		this.previouseRecordCumulativeColumnSymjaVariableName = previouseRecordCumulativeColumnSymjaVariableName;
	}


	public VfSymjaSinglePrimitiveOutputExpression getSymjaExpression() {
		return symjaExpression;
	}
	
	public BiMap<DataTableColumnName, VfSymjaVariableName> getColumnSymjaVariableNameMap() {
		return columnNameSymjaVariableNameMap;
	}
	
	public VfSymjaVariableName getPreviouseRecordCumulativeColumnSymjaVariableName() {
		return previouseRecordCumulativeColumnSymjaVariableName;
	}
	

	////////////////////////////////////////////////////
	/**
	 * calculate the cumulative column value with the given input column string values;
	 * @param currentRecordColumnNameStringValueMap cannot be null; must be consistent with the columnNameSymjaVariableNameMap
	 * @param previouseRecordVal cannot be null or empty string
	 * @return
	 */
	public String evaluate(Map<DataTableColumnName,String> currentRecordColumnNameStringValueMap, String previouseRecordVal) {
		Map<VfSymjaVariableName, String> symjaExpressionVariableNameStringValueMap = new HashMap<>();
		for(DataTableColumnName colName:columnNameSymjaVariableNameMap.keySet()) {
			symjaExpressionVariableNameStringValueMap.put(
					columnNameSymjaVariableNameMap.get(colName), 
					currentRecordColumnNameStringValueMap.get(colName));
		}
		
		symjaExpressionVariableNameStringValueMap.put(this.previouseRecordCumulativeColumnSymjaVariableName, previouseRecordVal);
		
//		String v = this.symjaExpression.evaluate(symjaExpressionVariableNameStringValueMap);
		return this.symjaExpression.evaluate(symjaExpressionVariableNameStringValueMap);
	}

	
	///////////////////////////////////////////
	/**
	 * reproduce and return a new CumulativeColumnSymjaExpressionDelegate of this one;
	 * 
	 * @param visSchemeAppliedArchiveReproducedAndInsertedInstanceUID
	 * @param ownerRecordMetadataID the MetadataID of the owner record data
	 * @param ownerMetadataCopyIndex
	 * @return
	 * @throws SQLException
	 */
	@Override
	public CumulativeColumnSymjaExpressionDelegate reproduce(
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, 
			MetadataID ownerRecordMetadataID, 
			int ownerMetadataCopyIndex) throws SQLException {
		
		BiMap<DataTableColumnName,VfSymjaVariableName> columnSymjaVariableNameMap = HashBiMap.create();
		
		for(DataTableColumnName column:this.getColumnSymjaVariableNameMap().keySet()) {
			columnSymjaVariableNameMap.put(
					column.reproduce(VSAArchiveReproducerAndInserter, ownerRecordMetadataID, ownerMetadataCopyIndex), 
					this.getColumnSymjaVariableNameMap().get(column)).reproduce();
		}
		
		return new CumulativeColumnSymjaExpressionDelegate(
				this.getSymjaExpression().reproduce(),
				columnSymjaVariableNameMap,
				this.getPreviouseRecordCumulativeColumnSymjaVariableName().reproduce()
				);
	}


	////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnNameSymjaVariableNameMap == null) ? 0 : columnNameSymjaVariableNameMap.hashCode());
		result = prime * result + ((previouseRecordCumulativeColumnSymjaVariableName == null) ? 0
				: previouseRecordCumulativeColumnSymjaVariableName.hashCode());
		result = prime * result + ((symjaExpression == null) ? 0 : symjaExpression.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CumulativeColumnSymjaExpressionDelegate))
			return false;
		CumulativeColumnSymjaExpressionDelegate other = (CumulativeColumnSymjaExpressionDelegate) obj;
		if (columnNameSymjaVariableNameMap == null) {
			if (other.columnNameSymjaVariableNameMap != null)
				return false;
		} else if (!columnNameSymjaVariableNameMap.equals(other.columnNameSymjaVariableNameMap))
			return false;
		if (previouseRecordCumulativeColumnSymjaVariableName == null) {
			if (other.previouseRecordCumulativeColumnSymjaVariableName != null)
				return false;
		} else if (!previouseRecordCumulativeColumnSymjaVariableName
				.equals(other.previouseRecordCumulativeColumnSymjaVariableName))
			return false;
		if (symjaExpression == null) {
			if (other.symjaExpression != null)
				return false;
		} else if (!symjaExpression.equals(other.symjaExpression))
			return false;
		return true;
	}

	
	
}
