package fileformat;

import java.util.HashMap;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import metadata.DataType;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;


/**
 * 
 * @author tanxu
 *
 */
public class FileFormatID implements PrimaryKeyID<FileFormat>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5142875221820260921L;
	
	
	////////////////////
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
	
	
	////////////////////////
	private final SimpleName name;
	private final DataType type;
	
	/**
	 * constructor
	 * @param name
	 * @param type
	 */
	public FileFormatID(SimpleName name, DataType type){
		this.name = name;
		this.type = type;
	}
	
	

	public SimpleName getName() {
		return name;
	}


	public DataType getType() {
		return type;
	}
	
	

	@Override
	public Map<SimpleName, String> getPrimaryKeyAttributeNameStringValueMap() {
		Map<SimpleName, String> ret = new HashMap<>();
		ret.put(NAME_COLUMN.getName(),this.getName().getStringValue());
		ret.put(TYPE_COLUMN.getName(), this.getType().toString());
		return ret;
	}
	
	@Override
	public Map<SimpleName, Boolean> getPrimaryKeyAttributeNameToIgnoreCaseMap() {
		Map<SimpleName, Boolean> ret = new HashMap<>();
		ret.put(NAME_COLUMN.getName(), true);
		ret.put(TYPE_COLUMN.getName(), true);
		return ret;
	}
	///////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		FileFormatID other = (FileFormatID) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "FileFormatID [name=" + name + ", type=" + type + "]";
	}

	

}
