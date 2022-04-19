package context.scheme.appliedarchive.mapping;

import java.util.function.BiPredicate;

import rdb.sqltype.VfDefinedPrimitiveSQLDataType;
import rdb.table.data.DataTableColumn;

public class ColumnMappingFilterFunctionFactory {
	
	/**
	 * return the BiFunction that check if the first VfDefinedPrimitiveSQLDataType of a source data's column can be mapped 
	 * to the second VfDefinedPrimitiveSQLDataType of the target data's column;
	 * 
	 * data type must be compatible:
	 * 		(*target type: compatible source types);
	 * 		double: double, int, big int, small int;
	 * 		big int: small int, int, big int;
	 * 		int: small int, int;
	 * 		small int: small int;
	 * 		varchar(n): varchar(m) where m<=n;
	 * 		boolean: boolean;
	 * 
	 * @return
	 */
	public static BiPredicate<VfDefinedPrimitiveSQLDataType, VfDefinedPrimitiveSQLDataType> colDataTypeCompatibilityCheckingBiPredicate(){
		return (sourceColDataType, targetColDataType)->{
			return targetColDataType.isMappableFrom(sourceColDataType);
			
//			if(targetColDataType.isDouble()) {
//				return sourceColDataType.isNumeric();
//			}else if(targetColDataType.isGenericInt()) {
//				SQLIntegerType integerType = (SQLIntegerType)targetColDataType;
//				if(integerType.getIntegerType().equals(IntegerType.LONG)) {
//					return sourceColDataType.isGenericInt();
//				}else if(integerType.getIntegerType().equals(IntegerType.INT)){
//					if(sourceColDataType.isGenericInt()) {
//						SQLIntegerType sourceIntegerType = (SQLIntegerType)sourceColDataType;
//						return sourceIntegerType.getIntegerType().equals(IntegerType.SHORT) || sourceIntegerType.getIntegerType().equals(IntegerType.INT);
//					}else {
//						return false;
//					}
//				}else if(integerType.getIntegerType().equals(IntegerType.SHORT)){
//					if(sourceColDataType.isGenericInt()) {
//						SQLIntegerType sourceIntegerType = (SQLIntegerType)sourceColDataType;
//						return sourceIntegerType.getIntegerType().equals(IntegerType.SHORT);
//					}else {
//						return false;
//					}
//				}
//			}else if(targetColDataType.isOfStringType()) {
//				if(sourceColDataType.isOfStringType()) {
//					SQLStringType targetStringType = (SQLStringType)targetColDataType;
//					SQLStringType sourceStringType = (SQLStringType)sourceColDataType;
//					
//					return targetStringType.getMaxLength()>=sourceStringType.getMaxLength();
//					
//				}else {
//					return false;
//				}
//				
//			}else if(targetColDataType.isBoolean()) {
//				return sourceColDataType.isBoolean();
//			}
//			throw new VisframeException("unrecognized data type encountered!");
		};
	}
	
	/**
	 * check if the first DataTableColumn of the source data is compatible to be mapped to the second DataTableColumn of the target data;
	 * 
	 * only check data type;
	 * @return
	 */
	public static BiPredicate<DataTableColumn, DataTableColumn> colCompatibilityCheckingBiPredicate(){
		return (sourceCol, targetCol)->{
			return colDataTypeCompatibilityCheckingBiPredicate().test(sourceCol.getSqlDataType(), targetCol.getSqlDataType());
		};
	}
	
	
	
}
