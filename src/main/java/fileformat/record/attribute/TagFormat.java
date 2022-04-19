package fileformat.record.attribute;

import java.io.Serializable;
import java.util.Map;

import fileformat.record.utils.StringMarker;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * contains the full set of information regarding the structure of a tag attribute which contains a primitive type attribute's name and value which are mandatory, 
 * as well the data type indicator string which is optional;
 * 
 * note that tag format always has a single type of component delimiter string and tag string cannot start with a delimiter;
 * 
 * note that tag attribute does not have null valued string; if an tag attribute is null, simply leave it out from the record
 * @author tanxu
 * 
 */
public class TagFormat implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8238294323450842440L;
	
	
	//////////////////////////////
	private final boolean hasDataTypeIndicatorComponent;
//	private final boolean dataTypeIndicatorComponentStringCaseSensitive; //always sensitive
	private final Map<String,VfDefinedPrimitiveSQLDataType> dataTypeIndicatorComponentStringSQLDataTypeMap; 
	private final VfDefinedPrimitiveSQLDataType defaultSQLDataType; //if hasDataTypeIndicatorComponent is false or dataTypeIndicatorComponentString is not found in dataTypeIndicatorComponentStringSqlTypeStringMap
	private final StringMarker componentDelimiter;//
	
	private final int nameComponentStringIndex;
	private final int valueComponentStringIndex;
	private final Integer dataTypeIndicatorComponentStringIndex; //cannot be null if hasDataTypeIndicatorComponent is true
	
	/**
	 * constructor
	 * @param hasDataTypeIndicatorComponent
	 * @param dataTypeIndicatorComponentStringCaseSensitive whether the data type indicator string is case sensitive or not 
	 * @param dataTypeIndicatorComponentStringSqlTypeStringMap cannot be null or empty if hasDataTypeIndicatorComponent is true;
	 * @param defaultSqlTypeString cannot be null or empty string if hasDataTypeIndicatorComponent is false; if hasDataTypeIndicatorComponent is true, this will be used if the data type indicator is not recognized;
	 * @param componentDelimiter cannot be null
	 * @param nameComponentStringIndex must be non-negative integer and must be different from valueComponentStringIndex
	 * @param valueComponentStringIndex must be non-negative integer and must be different from nameComponentStringIndex
	 * @param dataTypeIndicatorComponentStringIndex must be non-negative integer and must be different from nameComponentStringIndex and valueComponentStringIndex if hasDataTypeIndicatorComponent is true;
	 */
	public TagFormat(
			boolean hasDataTypeIndicatorComponent, 
//			boolean dataTypeIndicatorComponentStringCaseSensitive,
			Map<String,VfDefinedPrimitiveSQLDataType> dataTypeIndicatorComponentStringSQLDataTypeMap,
			VfDefinedPrimitiveSQLDataType defaultSQLDataType,
			StringMarker componentDelimiter,
			int nameComponentStringIndex,
			int valueComponentStringIndex,
			Integer dataTypeIndicatorComponentStringIndex
			){
		//
		if(hasDataTypeIndicatorComponent) {
			if(dataTypeIndicatorComponentStringSQLDataTypeMap==null||dataTypeIndicatorComponentStringSQLDataTypeMap.isEmpty()) {
				throw new IllegalArgumentException("dataTypeIndicatorComponentStringSQLDataTypeMap cannot be null or empty when hasDataTypeIndicatorComponent is true!");
			}
		}else {
			if(dataTypeIndicatorComponentStringSQLDataTypeMap!=null) {
				throw new IllegalArgumentException("dataTypeIndicatorComponentStringSQLDataTypeMap should be null or empty when hasDataTypeIndicatorComponent is false!");
			}
			if(defaultSQLDataType == null) {
				throw new IllegalArgumentException("defaultSQLDataType cannot be null when hasDataTypeIndicatorComponent is false!");
			}
		}
		
		if(componentDelimiter==null) {
			throw new IllegalArgumentException("componentDelimiter cannot be null!");
		}
		
		if(nameComponentStringIndex<0) {
			throw new IllegalArgumentException("nameComponentStringIndex cannot be negative integer!");
		}
		
		if(valueComponentStringIndex<0) {
			throw new IllegalArgumentException("valueComponentStringIndex cannot be negative integer!");
		}
		
		if(nameComponentStringIndex==valueComponentStringIndex) {
			throw new IllegalArgumentException("nameComponentStringIndex and valueComponentStringIndex cannot be equal!");
		}
		
		if(hasDataTypeIndicatorComponent) {
			if(dataTypeIndicatorComponentStringIndex==null) {
				throw new IllegalArgumentException("dataTypeIndicatorComponentStringIndex cannot be null when hasDataTypeIndicatorComponent is true!");
			}else {
				if(dataTypeIndicatorComponentStringIndex==nameComponentStringIndex||dataTypeIndicatorComponentStringIndex==valueComponentStringIndex) {
					throw new IllegalArgumentException("dataTypeIndicatorComponentStringIndex cannot be equal to nameComponentStringIndex or valueComponentStringIndex!");
				}
			}
		}
		
		
		
		
		
		this.hasDataTypeIndicatorComponent = hasDataTypeIndicatorComponent;
//		this.dataTypeIndicatorComponentStringCaseSensitive = dataTypeIndicatorComponentStringCaseSensitive;
		this.dataTypeIndicatorComponentStringSQLDataTypeMap = dataTypeIndicatorComponentStringSQLDataTypeMap;
		this.defaultSQLDataType = defaultSQLDataType;
		this.componentDelimiter = componentDelimiter;
		this.nameComponentStringIndex = nameComponentStringIndex;
		this.valueComponentStringIndex = valueComponentStringIndex;
		this.dataTypeIndicatorComponentStringIndex = dataTypeIndicatorComponentStringIndex;
	}
	
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean hasDataTypeIndicatorComponent() {
		return hasDataTypeIndicatorComponent;
	}

	public Map<String, VfDefinedPrimitiveSQLDataType> getDataTypeIndicatorComponentStringSQLDataTypeMap() {
		return dataTypeIndicatorComponentStringSQLDataTypeMap;
	}
	
	public VfDefinedPrimitiveSQLDataType getDefaultSQLDataType() {
		return defaultSQLDataType;
	}

	public StringMarker getComponentDelimiter() {
		return componentDelimiter;
	}

	public int getNameComponentStringIndex() {
		return nameComponentStringIndex;
	}

	public int getValueComponentStringIndex() {
		return valueComponentStringIndex;
	}

	public Integer getDataTypeIndicatorComponentStringIndex() {
		return dataTypeIndicatorComponentStringIndex;
	}


	@Override
	public String toString() {
		return "TagFormat [hasDataTypeIndicatorComponent=" + hasDataTypeIndicatorComponent
				+ ", dataTypeIndicatorComponentStringSQLDataTypeMap=" + dataTypeIndicatorComponentStringSQLDataTypeMap
				+ ", defaultSQLDataType=" + defaultSQLDataType + ", componentDelimiter=" + componentDelimiter
				+ ", nameComponentStringIndex=" + nameComponentStringIndex + ", valueComponentStringIndex="
				+ valueComponentStringIndex + ", dataTypeIndicatorComponentStringIndex="
				+ dataTypeIndicatorComponentStringIndex + "]";
	}


	///////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((componentDelimiter == null) ? 0 : componentDelimiter.hashCode());
		result = prime * result + ((dataTypeIndicatorComponentStringIndex == null) ? 0
				: dataTypeIndicatorComponentStringIndex.hashCode());
		result = prime * result + ((dataTypeIndicatorComponentStringSQLDataTypeMap == null) ? 0
				: dataTypeIndicatorComponentStringSQLDataTypeMap.hashCode());
		result = prime * result + ((defaultSQLDataType == null) ? 0 : defaultSQLDataType.hashCode());
		result = prime * result + (hasDataTypeIndicatorComponent ? 1231 : 1237);
		result = prime * result + nameComponentStringIndex;
		result = prime * result + valueComponentStringIndex;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TagFormat))
			return false;
		TagFormat other = (TagFormat) obj;
		if (componentDelimiter == null) {
			if (other.componentDelimiter != null)
				return false;
		} else if (!componentDelimiter.equals(other.componentDelimiter))
			return false;
		if (dataTypeIndicatorComponentStringIndex == null) {
			if (other.dataTypeIndicatorComponentStringIndex != null)
				return false;
		} else if (!dataTypeIndicatorComponentStringIndex.equals(other.dataTypeIndicatorComponentStringIndex))
			return false;
		if (dataTypeIndicatorComponentStringSQLDataTypeMap == null) {
			if (other.dataTypeIndicatorComponentStringSQLDataTypeMap != null)
				return false;
		} else if (!dataTypeIndicatorComponentStringSQLDataTypeMap
				.equals(other.dataTypeIndicatorComponentStringSQLDataTypeMap))
			return false;
		if (defaultSQLDataType == null) {
			if (other.defaultSQLDataType != null)
				return false;
		} else if (!defaultSQLDataType.equals(other.defaultSQLDataType))
			return false;
		if (hasDataTypeIndicatorComponent != other.hasDataTypeIndicatorComponent)
			return false;
		if (nameComponentStringIndex != other.nameComponentStringIndex)
			return false;
		if (valueComponentStringIndex != other.valueComponentStringIndex)
			return false;
		return true;
	}
}
