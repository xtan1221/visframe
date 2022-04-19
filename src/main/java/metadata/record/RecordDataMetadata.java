package metadata.record;

import basic.VfNotes;
import metadata.AbstractMetadata;
import metadata.DataType;
import metadata.MetadataID;
import metadata.MetadataName;
import metadata.SourceType;
import operation.OperationID;
import rdb.table.data.DataTableSchema;

/**
 * Metadata class for record data object:
 * 
 * 1. contains the RelationalTableSchemaID of a unique data table and schema in the RDB of owner VisProjectDBContext;
 * 2. all data content of the record data object are stored as records in the data table;
 * 
 * @author tanxu
 *
 */
public class RecordDataMetadata extends AbstractMetadata {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8921230998142284235L;

	////////////////////////////////
	/**
	 * DataTableSchema
	 */
	private final DataTableSchema dataTableSchema;
	
	/**
	 * whether this record data is the node record data of the source composite GraphDataMetadata or not if SourceType is GRAPH or VfTREE;
	 */
	private final Boolean ofGenericGraphNode;
	
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 * @param sourceType
	 * @param sourceCompositeDataMetadataID
	 * @param sourceOperationID
	 * @param dataTableSchema
	 * @param ofGenericGraphNode not null if SourceType is STRUCTURAL_COMPONENT, null otherwise
	 */
	public RecordDataMetadata(
			MetadataName name, VfNotes notes, 
			SourceType sourceType,
			MetadataID sourceCompositeDataMetadataID, OperationID sourceOperationID,
			
			DataTableSchema dataTableSchema,
			Boolean ofGenericGraphNode
			) {
		super(name, notes, sourceType, sourceCompositeDataMetadataID, sourceOperationID);
		
		if(dataTableSchema==null)
			throw new IllegalArgumentException("given dataTableSchema cannot be null!");
		if(ofGenericGraphNode==null&&sourceType.equals(SourceType.STRUCTURAL_COMPONENT))
			throw new IllegalArgumentException("given ofGenericGraphNode cannot be null when sourceType is STRUCTURAL_COMPONENT!");
		
		////////////////
		this.dataTableSchema = dataTableSchema;
		this.ofGenericGraphNode = ofGenericGraphNode;
	}
	
	/**
	 * returns whether this RecordDataMetadata is a node type of a GraphDataMetadata if {@link RecordDataMetadata#getSourceType()} method returns {@link SourceType#STRUCTURAL_COMPONENT} 
	 * 
	 * returns null otherwise;
	 */
	public Boolean isOfGenericGraphNode() {
		return this.ofGenericGraphNode;
	}
	
	/**
	 * returns DataType
	 */
	public DataType getDataType() {
		return DataType.RECORD;
	}
	
	
	public DataTableSchema getDataTableSchema() {
		return dataTableSchema;
	}


	/////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dataTableSchema == null) ? 0 : dataTableSchema.hashCode());
		result = prime * result + ((ofGenericGraphNode == null) ? 0 : ofGenericGraphNode.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RecordDataMetadata))
			return false;
		RecordDataMetadata other = (RecordDataMetadata) obj;
		if (dataTableSchema == null) {
			if (other.dataTableSchema != null)
				return false;
		} else if (!dataTableSchema.equals(other.dataTableSchema))
			return false;
		if (ofGenericGraphNode == null) {
			if (other.ofGenericGraphNode != null)
				return false;
		} else if (!ofGenericGraphNode.equals(other.ofGenericGraphNode))
			return false;
		return true;
	}

	
}
