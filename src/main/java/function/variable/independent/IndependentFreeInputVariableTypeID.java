package function.variable.independent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import rdb.sqltype.SQLStringType;
import rdb.table.lookup.ManagementTableColumn;

/**
 * ID for FreeInputVariable for lookup in a VisProjectDBContext to enforce the constraints on FreeInputVariables when creating CompositionFunctions;
 * @author tanxu
 *
 */
public class IndependentFreeInputVariableTypeID implements PrimaryKeyID<IndependentFreeInputVariableType>, Reproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6025392506071582440L;
	
	////////////////////////////
	public static final ManagementTableColumn NAME_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("NAME"), new SQLStringType(10,false), true, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	/////////////////////////////////
	private final CompositionFunctionID ownerCompositionFunctionID;
	private final SimpleName aliasName;
	
	/**
	 * constructor
	 * @param ownerCompositionFunctionID
	 * @param aliasName
	 */
	public IndependentFreeInputVariableTypeID(CompositionFunctionID ownerCompositionFunctionID, SimpleName aliasName){
		this.ownerCompositionFunctionID = ownerCompositionFunctionID;
		this.aliasName = aliasName;
	}
	
	
	public CompositionFunctionID getOwnerCompositionFunctionID() {
		return ownerCompositionFunctionID;
	}


	public SimpleName getAliasName() {
		return aliasName;
	}
	
	
	@Override
	public Map<SimpleName, String> getPrimaryKeyAttributeNameStringValueMap() {
		Map<SimpleName, String> ret = new HashMap<>();
		//
		ret.putAll(this.getOwnerCompositionFunctionID().getPrimaryKeyAttributeNameStringValueMap());
		
		ret.put(NAME_COLUMN.getName(),this.getAliasName().getStringValue());
		
		return ret;
	}
	
	@Override
	public Map<SimpleName, Boolean> getPrimaryKeyAttributeNameToIgnoreCaseMap() {
		Map<SimpleName, Boolean> ret = new HashMap<>();
		//
		ret.putAll(this.getOwnerCompositionFunctionID().getPrimaryKeyAttributeNameToIgnoreCaseMap());
		
		ret.put(NAME_COLUMN.getName(), true);
		
		return ret;
	}
	
	/**
	 * reproduce this IndependentFreeInputVariableTypeID and return the reproduced one;
	 * 
	 * 
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced IndependentFreeInputVariableTypeID will be inserted;
	 * @param VSAArchiveReproducerAndInserter
	 * @param copyIndex copy index of VCCLNode of VCDNode to which the owner CompositionFunction of this IndependentFreeInputVariableTypeID is assigned
	 * @throws SQLException
	 */
	@Override
	public IndependentFreeInputVariableTypeID reproduce(VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, int copyIndex)
			throws SQLException {
		
		return new IndependentFreeInputVariableTypeID(
				this.getOwnerCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
				this.aliasName.reproduce()
				);
	}

	

	//////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aliasName == null) ? 0 : aliasName.hashCode());
		result = prime * result + ((ownerCompositionFunctionID == null) ? 0 : ownerCompositionFunctionID.hashCode());
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
		IndependentFreeInputVariableTypeID other = (IndependentFreeInputVariableTypeID) obj;
		if (aliasName == null) {
			if (other.aliasName != null)
				return false;
		} else if (!aliasName.equals(other.aliasName))
			return false;
		if (ownerCompositionFunctionID == null) {
			if (other.ownerCompositionFunctionID != null)
				return false;
		} else if (!ownerCompositionFunctionID.equals(other.ownerCompositionFunctionID))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "IndependentFreeInputVariableTypeID [ownerCompositionFunctionID=" + ownerCompositionFunctionID
				+ ", aliasName=" + aliasName + "]";
	}

	
}
