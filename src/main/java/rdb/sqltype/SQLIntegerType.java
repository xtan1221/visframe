package rdb.sqltype;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * sql integer type either by INT or BIGINT or SMALLINT
 * @author tanxu
 *
 */
public class SQLIntegerType extends VfDefinedPrimitiveSQLDataType {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5553862638016028499L;
	
	////////////////////////////////////////////
	private final IntegerType type;
	
	/**
	 * constructor
	 * @param type
	 */
	public SQLIntegerType(IntegerType type) {
		if(type==null) {
			throw new IllegalArgumentException("given type cannot be null!");
		}
		
		this.type = type;
	}

	/**
	 * return whether the given sourceType can be used as this SQLIntegerType;
	 */
	@Override
	public boolean isMappableFrom(VfDefinedPrimitiveSQLDataType sourceType) {
		if(sourceType instanceof SQLIntegerType) {
			SQLIntegerType sourceIntType = (SQLIntegerType)sourceType;
			//
			if(sourceIntType.getIntegerType().equals(IntegerType.SHORT)) {//always ok to use short as int or big int;
				return true;
			}else if(sourceIntType.getIntegerType().equals(IntegerType.INT)) {
				return this.getIntegerType().equals(IntegerType.INT) ||this.getIntegerType().equals(IntegerType.LONG);
			}else {//BIGINT
				return this.getIntegerType().equals(IntegerType.LONG);
			}
			
		}else {
			return false;
		}
	}


	@Override
	public boolean isValidStringValue(String value) {
		if(value == null)
			return false;
		
		return this.type.isValidStringValue(value);
	}


	//sql INTEGER [-2147483648,2147483647] == java Integer 
	//sql BIGINT  [-9223372036854775808,9223372036854770000] == java Long
	//sql SMALLINT  [-32768,32767] == java Short
	
	@Override
	public String getSQLString() {
		return this.type.sqlTypeString;
	}
	
	@Override
	public boolean isNumeric() {
		return true;
	}
	
	public IntegerType getIntegerType() {
		return this.type;
	}

	@Override
	public Integer getDefaultValueObject(String defaultStringValue) {
		return Integer.parseInt(defaultStringValue);
	}


	@Override
	public boolean isGenericInt() {
		return true;
	}
	



	@Override
	public boolean isBoolean() {
		return false;
	}


	@Override
	public boolean isDouble() {
		return false;
	}


	/**
	 * 
	 */
	@Override
	public SQLIntegerType reproduce() {
		return this;
	}
	

	@Override
	public boolean isOfStringType() {
		return false;
	}

	@Override
	public void setPreparedStatement(PreparedStatement ps, int colIndex, String stringValue) throws SQLException {
		if(stringValue==null) {
			ps.setNull(colIndex, this.getIntegerType().getJavaSqlType());
		}else {
			ps.setInt(colIndex, Integer.parseInt(stringValue));
		}
		
		
	}

	
	//////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SQLIntegerType))
			return false;
		SQLIntegerType other = (SQLIntegerType) obj;
		if (type != other.type)
			return false;
		return true;
	}


	////////////////////////
	/**
	 * types of sql integer type allowed in visframe
	 * @author tanxu
	 *
	 */
	public static enum IntegerType{
		INT("INT","[-2147483648,2147483647]", java.sql.Types.INTEGER),
		LONG("BIGINT","[-9223372036854775808, 9223372036854770000]",java.sql.Types.BIGINT),
		SHORT("SMALLINT","[-32768,32767]",java.sql.Types.SMALLINT);
		
		private final String sqlTypeString;
		private final String description;
		private final int javaSqlType;
		
		/**
		 * constructor
		 * @param sqlTypeString
		 * @param description
		 */
		IntegerType(String sqlTypeString,String description,int javaSqlType){
			this.sqlTypeString = sqlTypeString;
			this.description = description;
			this.javaSqlType = javaSqlType;
		}
		
		public String getDescription() {
			return description;
		}
		
		@Override
		public String toString() {
			return this.sqlTypeString;
		}

		public int getJavaSqlType() {
			return javaSqlType;
		}
		
		/**
		 * check if the given string value is a valid number of the IntegerType
		 * @param value
		 * @return
		 */
		public boolean isValidStringValue(String value) {
			if(this.equals(INT)) {
				try {
					Integer.parseInt(value);
					return true;
				}catch(NumberFormatException  e) {
					return false;
				}
			}else if(this.equals(LONG)) {
				try {
					Long.parseLong(value);
					return true;
				}catch(NumberFormatException  e) {
					return false;
				}
			}else {
				try {
					Short.parseShort(value);
					return true;
				}catch(NumberFormatException  e) {
					return false;
				}
			}
			
		}
	}

}
