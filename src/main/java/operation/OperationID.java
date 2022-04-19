package operation;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;

/**
 * 
 * @author tanxu
 *
 */
public class OperationID implements PrimaryKeyID<Operation>, Reproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = -308775312773741669L;
	
	///////////////////
	public static final ManagementTableColumn NAME_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("INSTANCE_NAME"), new SQLStringType(50, false), true, true,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	
	////////////////////////////
	private final OperationName instanceName;
		
	/**
	 * constructor
	 * @param instanceName cannot be null
	 * @param dataType cannot be null
	 */
	public OperationID(OperationName instanceName) {
		this.instanceName = instanceName;
	}
	
	///////////////////////////////
	public OperationName getInstanceName() {
		return instanceName;
	}
	
	
	@Override
	public Map<SimpleName, String> getPrimaryKeyAttributeNameStringValueMap() {
		Map<SimpleName, String> ret = new LinkedHashMap<>();
		ret.put(NAME_COLUMN.getName(),this.getInstanceName().getStringValue());
		return ret;
	}
	
	@Override
	public Map<SimpleName, Boolean> getPrimaryKeyAttributeNameToIgnoreCaseMap() {
		Map<SimpleName, Boolean> ret = new LinkedHashMap<>();
		ret.put(NAME_COLUMN.getName(), true);
		return ret;
	}
	
	/**
	 * reproduce this OperationID (if not already) and return the reproduced one;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced Metadata will be inserted;
	 * @param VSAArchiveReproducerAndInserter
	 * @param copyIndex copy index of VCCLNode of VCDNode to which this OperationID is assigned
	 * @throws SQLException 
	 */
	@Override
	public OperationID reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, 
			int copyIndex)throws SQLException {
		//check if the OperationID of the given copy index has already been reproduced or not
		//????this scenario should never be encountered?? anyway, not removing this will NOT result in any error;
		if(VSAArchiveReproducerAndInserter.getOperationReproducingAndInsertionTracker().getOriginalOperationIDCopyIndexReproducedOperationIDMapMap().containsKey(this) 
				&&VSAArchiveReproducerAndInserter.getOperationReproducingAndInsertionTracker().getOriginalOperationIDCopyIndexReproducedOperationIDMapMap().get(this).containsKey(copyIndex)) {//already reproduced
			//already reproduced;
			return VSAArchiveReproducerAndInserter.getOperationReproducingAndInsertionTracker().getOriginalOperationIDCopyIndexReproducedOperationIDMapMap().get(this).get(copyIndex);
		}else {
			//not reproduced yet
			OperationID reproducedOperationID = 
					hostVisProjctDBContext.getHasIDTypeManagerController().getOperationManager().buildReproducedID(this);
			//add to the reproduced MetadataID map in VSAArchiveReproducerAndInserter
			VSAArchiveReproducerAndInserter.getOperationReproducingAndInsertionTracker()
			.addToOriginalOperationIDCopyIndexReproducedOperationIDMapMap(this, copyIndex, reproducedOperationID);
			
			//
			return reproducedOperationID;
		}
	}
	
	/////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instanceName == null) ? 0 : instanceName.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof OperationID))
			return false;
		OperationID other = (OperationID) obj;
		if (instanceName == null) {
			if (other.instanceName != null)
				return false;
		} else if (!instanceName.equals(other.instanceName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OperationID [name=" + instanceName + "]";
	}


}
