package importer;

import java.util.HashMap;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import metadata.DataType;
import metadata.MetadataName;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;

public class DataImporterID implements PrimaryKeyID<DataImporter>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9065903923060426413L;
	
	public static final ManagementTableColumn TYPE_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("TYPE"), new SQLStringType(10,false), true, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	public static final ManagementTableColumn NAME_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("NAME"), new SQLStringType(50,false), true, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	////////////////////////////////
	/**
	 * name of the main imported metadata
	 */
	private final MetadataName metadataName;
	/**
	 * type of the main imported metadata
	 */
	private final DataType metadataType;
	
	/**
	 * constructor
	 * @param metadataName
	 * @param metadataType
	 */
	DataImporterID(MetadataName metadataName, DataType metadataType){
		this.metadataName = metadataName;
		this.metadataType = metadataType;
	}

	public MetadataName getMetadataName() {
		return metadataName;
	}


	public DataType getMetadataType() {
		return metadataType;
	}
	
	
	@Override
	public Map<SimpleName, String> getPrimaryKeyAttributeNameStringValueMap() {
		Map<SimpleName, String> ret = new HashMap<>();
		ret.put(NAME_COLUMN.getName(),this.getMetadataName().getStringValue());
		ret.put(TYPE_COLUMN.getName(), this.getMetadataType().toString());
		return ret;
	}

	@Override
	public Map<SimpleName, Boolean> getPrimaryKeyAttributeNameToIgnoreCaseMap() {
		Map<SimpleName, Boolean> ret = new HashMap<>();
		
		ret.put(NAME_COLUMN.getName(), true);
		ret.put(TYPE_COLUMN.getName(), true);
		return ret;
	}
	
	//////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metadataName == null) ? 0 : metadataName.hashCode());
		result = prime * result + ((metadataType == null) ? 0 : metadataType.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataImporterID other = (DataImporterID) obj;
		if (metadataName == null) {
			if (other.metadataName != null)
				return false;
		} else if (!metadataName.equals(other.metadataName))
			return false;
		if (metadataType != other.metadataType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataImporterID [metadataName=" + metadataName + ", metadataType=" + metadataType + "]";
	}


}
