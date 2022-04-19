package metadata;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import basic.reproduce.Reproducible;
import context.VisframeContextConstants;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import dependency.dos.integrated.IntegratedDOSGraphNode;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;

/**
 * MapID for Metadata
 * 
 * see {@link DataReproducible} doc for why MetadataID is not a DataReproducible
 * @author tanxu
 */
public class MetadataID implements PrimaryKeyID<Metadata>, Reproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8748740053030771017L;
	
	//////////////////////
	public static final ManagementTableColumn TYPE_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("TYPE"), new SQLStringType(10,false), true, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	public static final ManagementTableColumn NAME_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("NAME"), new SQLStringType(VisframeContextConstants.MAX_METADATA_NAME_LEN,false), true, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	public static MetadataID record(MetadataName name) {
		return new MetadataID(name, DataType.RECORD);
	}
	public static MetadataID graph(MetadataName name) {
		return new MetadataID(name, DataType.GRAPH);
	}
	public static MetadataID vftree(MetadataName name) {
		return new MetadataID(name, DataType.vfTREE);
	}
	
	///////////////////////
	private final MetadataName name;
	private final DataType dataType;
	
	/**
	 * constructor
	 * @param name cannot be null
	 * @param dataType cannto be null
	 */
	public MetadataID(MetadataName name, DataType dataType) {		
		this.name = name;
		this.dataType = dataType;
	}
	
	////////////////////////////////////////
	public MetadataName getName() {
		return name;
	}
	

	public DataType getDataType() {
		return dataType;
	}
	
	@Override
	public Map<SimpleName, String> getPrimaryKeyAttributeNameStringValueMap() {
		Map<SimpleName, String> ret = new LinkedHashMap<>();
		ret.put(NAME_COLUMN.getName(),this.getName().getStringValue());
		ret.put(TYPE_COLUMN.getName(), this.getDataType().toString());
		return ret;
	}
	

	@Override
	public Map<SimpleName, Boolean> getPrimaryKeyAttributeNameToIgnoreCaseMap() {
		Map<SimpleName, Boolean> ret = new LinkedHashMap<>();
		ret.put(NAME_COLUMN.getName(), true);
		ret.put(TYPE_COLUMN.getName(), true);
		return ret;
	}
	
	
	/**
	 * reproduce and return a MetadataID;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Metadata will be inserted;
	 * @param VSAArchiveReproducerAndInserter
	 * @param copyIndex copy index of VCCLNode of VCDNode to which this MetadataID is assigned
	 * @throws SQLException 
	 */
	@Override
	public MetadataID reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		//
		IntegratedDOSGraphNode node = VSAArchiveReproducerAndInserter.getAppliedArchive().lookupIntegratedDOSGraphNode(this, copyIndex);
		//check if the IntegratedDOSGraphNode corresponding to this MetadataID and the given copy index is in the solution set or a component Metadata of a node in solution set;
		if(VSAArchiveReproducerAndInserter.getAppliedArchive().getIntegratedDOSGraphNodeMappedMetadataIDMap().containsKey(node)) {
			return VSAArchiveReproducerAndInserter.getAppliedArchive().getIntegratedDOSGraphNodeMappedMetadataIDMap().get(node);
		}else {
			//first check if this MetadataID of the given copy index has already been reproduced or not
			//for example, this MetadataID is the output of a reproduced Operation, thus it is reproduced whenever the operation was reproduced;
			//when this MetadataID is later used as input for other reproduced Operation or as depended record data of CFG or CF, it should not be reproduced again!
			
			if(VSAArchiveReproducerAndInserter.getOperationReproducingAndInsertionTracker().getIntegratedDOSGraphNodeReproducedMetadataIDMap().containsKey(node)) {
				//already reproduced, return it directly
				return VSAArchiveReproducerAndInserter.getOperationReproducingAndInsertionTracker().getIntegratedDOSGraphNodeReproducedMetadataIDMap().get(node);
			}else {
				//not reproduced yet
				MetadataID reproducedMetadataID = 
						hostVisProjctDBContext.getHasIDTypeManagerController().getMetadataManager().buildReproducedID(this);
				
				//add to the reproduced MetadataID map in VSAArchiveReproducerAndInserter
				VSAArchiveReproducerAndInserter.getOperationReproducingAndInsertionTracker().addToIntegratedDOSGraphNodeReproducedMetadataIDMap(node, reproducedMetadataID);
				
				return reproducedMetadataID;
			}
		}
	}
	
	//////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		MetadataID other = (MetadataID) obj;
		if (dataType != other.dataType)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "MetadataID [name=" + name + ", dataType=" + dataType + "]";
	}
}
