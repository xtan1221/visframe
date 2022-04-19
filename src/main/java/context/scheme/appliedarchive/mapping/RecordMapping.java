package context.scheme.appliedarchive.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import metadata.DataType;
import metadata.MetadataID;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import rdb.table.data.DataTableSchemaID;

/**
 * mapping from a record metadata R1(source record metadata) in the host VisProjectDBContext to 
 * a node selected in solution set on the trimmed integrated DOS graph containing a record metadata R2 (target record metadata);
 * let the set of primary key columns of R2 be PK2, the set of non-primary key INPUT columns of R2 be nPK2;
 * 		1. all the primary key columns of R2 must be included in mapping
 * 		2. only the non-primary key columns of R2 that are
 * 					
 * 	
 * let the set of primary key columns of R1 be PK1, the set of non-primary key columns of R1 be nPK1;
 * 
 * 
 * 
 * rules on mapping:
 * 1. Primary key columns PK2 must be one-to-one fully mapped by PK1
 * 		Thus the number of primary key columns must be the same;
 * 2. non-primary key input columns input-nPK2 must be one-to-one fully mapped from a subset of nPK1;
 * 		the number of columns in nPK1 must be equal to or larger than nPK2;
 * 3. same column from PK1 and nPK1 cannot be mapped to multiple columns in PK2 and input-nPK2;
 * 		Note that this may be changed in future version!!!!
 * 
 * as to mapping of each column, data type must be compatible:
 * 		(*target type: compatible source types);
 * 		double: double, int, big int, small int;
 * 		big int: small int, int, big int;
 * 		int: small int, int;
 * 		small int: small int;
 * 		varchar(n): varchar(m) where m<=n;
 * 		boolean: boolean;
 * 
 * 
 * @author tanxu
 *
 */
public class RecordMapping extends MetadataMapping{
	/**
	 * 
	 */
	private static final long serialVersionUID = -916172761286241420L;
	
	////////////////////////////
	/**
	 * DataTableSchemaID of the data table of the source record Metadata;
	 */
	private final DataTableSchemaID sourceRecordDataTableSchemaID;
	/**
	 * map from the target primary key DataTableColumn to the one of the source record Metadata;
	 * must be non-empty;
	 */
	private final Map<DataTableColumn, DataTableColumn> targetSourcePKColumMap;
	/**
	 * map from the target non-primary key DataTableColumn to the one of the source record Metadata;
	 * must be non-null; can be empty;
	 */
	private final Map<DataTableColumn, DataTableColumn> targetSourceNonPKColumMap;
	
	//////////////////////////////
	private transient Map<DataTableColumn, DataTableColumn> targetSourceColumnMap;
	private transient Map<DataTableColumnName, DataTableColumnName> targetSourceColumnNameMap;
	/**
	 * constructor
	 * @param targetMetadataID
	 * @param sourceMetadataID
	 * @param targetSourcePKColumMap
	 * @param targetSourceNonPKColumMap
	 */
	public RecordMapping(
			DataTableSchemaID sourceRecordDataTableSchemaID,
			MetadataID targetMetadataID, MetadataID sourceMetadataID,
			Map<DataTableColumn, DataTableColumn> targetSourcePKColumMap,
			Map<DataTableColumn, DataTableColumn> targetSourceNonPKColumMap
			) {
		super(targetMetadataID, sourceMetadataID);
		//validations
		if(targetSourcePKColumMap==null||targetSourcePKColumMap.isEmpty())
			throw new IllegalArgumentException("given targetSourcePKColumMap cannot be null or empty!");
		if(targetSourceNonPKColumMap==null)
			throw new IllegalArgumentException("given targetSourcePKColumMap cannot be null!");
		
		targetSourcePKColumMap.forEach((t,s)->{
			if(!t.getSqlDataType().isMappableFrom(s.getSqlDataType())) {
				throw new IllegalArgumentException("data type of at least one PK column from source record data is not mappable to the target PK column!");
			}
		});
		
		targetSourceNonPKColumMap.forEach((t,s)->{
			if(!t.getSqlDataType().isMappableFrom(s.getSqlDataType())) {
				throw new IllegalArgumentException("data type of at least one non-PK column from source record data is not mappable to the target non-PK column!");
			}
		});
		
		this.sourceRecordDataTableSchemaID = sourceRecordDataTableSchemaID;
		this.targetSourcePKColumMap = targetSourcePKColumMap;
		this.targetSourceNonPKColumMap = targetSourceNonPKColumMap;
	}

	/**
	 * @return the targetSourcePKColumMap
	 */
	public Map<DataTableColumn, DataTableColumn> getTargetSourcePKColumMap() {
		return targetSourcePKColumMap;
	}
	
	/**
	 * @return the targetSourceNonPKColumMap
	 */
	public Map<DataTableColumn, DataTableColumn> getTargetSourceNonPKColumMap() {
		return targetSourceNonPKColumMap;
	}
	
	@Override
	protected Predicate<DataType> getTargetMetadataTypePredicate() {
		return e->{return e.equals(DataType.RECORD);};
	}

	@Override
	protected Predicate<DataType> getSourceMetadataTypePredicate() {
		return e->{return e.equals(DataType.RECORD);};
	}
	
	////////////////////////////////////
	/**
	 * 
	 * @return
	 */
	public Map<DataTableColumn, DataTableColumn> getTargetSourceColumnMap() {
		if(this.targetSourceColumnMap == null) {
			this.targetSourceColumnMap = new HashMap<>();
			
			this.targetSourceColumnMap.putAll(this.targetSourcePKColumMap);
			this.targetSourceColumnMap.putAll(this.targetSourceNonPKColumMap);
		}
		return this.targetSourceColumnMap;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<DataTableColumnName, DataTableColumnName> getTargetSourceColumnNameMap() {
		if(this.targetSourceColumnNameMap == null) {
			this.targetSourceColumnNameMap = new HashMap<>();
			
			this.getTargetSourceColumnMap().forEach((k,v)->{
				this.targetSourceColumnNameMap.put(k.getName(), v.getName());
			});
		}
		return this.targetSourceColumnNameMap;
	}

	/**
	 * @return the sourceRecordDataTableSchemaID
	 */
	public DataTableSchemaID getSourceRecordDataTableSchemaID() {
		return sourceRecordDataTableSchemaID;
	}

	
	/////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((sourceRecordDataTableSchemaID == null) ? 0 : sourceRecordDataTableSchemaID.hashCode());
		result = prime * result + ((targetSourceNonPKColumMap == null) ? 0 : targetSourceNonPKColumMap.hashCode());
		result = prime * result + ((targetSourcePKColumMap == null) ? 0 : targetSourcePKColumMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RecordMapping))
			return false;
		RecordMapping other = (RecordMapping) obj;
		if (sourceRecordDataTableSchemaID == null) {
			if (other.sourceRecordDataTableSchemaID != null)
				return false;
		} else if (!sourceRecordDataTableSchemaID.equals(other.sourceRecordDataTableSchemaID))
			return false;
		if (targetSourceNonPKColumMap == null) {
			if (other.targetSourceNonPKColumMap != null)
				return false;
		} else if (!targetSourceNonPKColumMap.equals(other.targetSourceNonPKColumMap))
			return false;
		if (targetSourcePKColumMap == null) {
			if (other.targetSourcePKColumMap != null)
				return false;
		} else if (!targetSourcePKColumMap.equals(other.targetSourcePKColumMap))
			return false;
		return true;
	}
	

}