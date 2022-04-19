package function.composition;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import basic.SimpleName;
import basic.lookup.PrimaryKeyID;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.group.CompositionFunctionGroupID;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.table.lookup.ManagementTableColumn;

public class CompositionFunctionID implements PrimaryKeyID<CompositionFunction>, Reproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8572967329079830817L;
	
	////////////////////
	public static final ManagementTableColumn INDEX_ID_COLUMN = new ManagementTableColumn(
		//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
		new SimpleName("INDEX_ID"), SQLDataTypeFactory.integerType(), true, false,
		//Boolean notNull, String defaultStringValue, String additionalConstraints
		true, null, null
	);
	
	
	//////////////////
	private final CompositionFunctionGroupID cfgID;
	
	private final int indexID;
	
	/**
	 * constructor
	 * @param hostCFGID
	 * @param indexID
	 */
	public CompositionFunctionID(CompositionFunctionGroupID hostCFGID, int indexID){
		this.cfgID = hostCFGID;
		this.indexID = indexID;
	}
	
	//////////////////////////////////
	public int getIndexID() {
		return indexID;
	}
	
	public CompositionFunctionGroupID getHostCompositionFunctionGroupID() {
		return cfgID;
	}

	
	@Override
	public Map<SimpleName, String> getPrimaryKeyAttributeNameStringValueMap() {
		Map<SimpleName, String> ret = new HashMap<>();
		//
		ret.putAll(this.getHostCompositionFunctionGroupID().getPrimaryKeyAttributeNameStringValueMap());
		
		ret.put(INDEX_ID_COLUMN.getName(),Integer.toString(this.getIndexID()));
		
		return ret;
	}
	
	@Override
	public Map<SimpleName, Boolean> getPrimaryKeyAttributeNameToIgnoreCaseMap() {
		Map<SimpleName, Boolean> ret = new HashMap<>();
		//
		ret.putAll(this.getHostCompositionFunctionGroupID().getPrimaryKeyAttributeNameToIgnoreCaseMap());
		
		ret.put(INDEX_ID_COLUMN.getName(), null);
		
		return ret;
	}
	/**
	 * reproduce this CompositionFunctionID (if not already) and return the reproduced one;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced CompositionFunctionID will be inserted;
	 * @param VSAArchiveReproducerAndInserter
	 * @param copyIndex copy index of VCCLNode of VCDNode to which this CompositionFunctionID is assigned
	 * @throws SQLException 
	 */
	@Override
	public CompositionFunctionID reproduce(VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, int copyIndex)
			throws SQLException {
		//check if the CompositionFunctionGroupID of the given copy index has already been reproduced or not
		//for example, after a CompositionFuntion is reproduced and inserted, its depending CompositionFunction may be reproduced, in which process, the depended CompositionFunctionID will be used but should not be reproduced again;
		if(VSAArchiveReproducerAndInserter.getCFReproducingAndInsertionTracker().getOriginalCFIDCopyIndexReproducedCFIDMapMap().containsKey(this)
				&&VSAArchiveReproducerAndInserter.getCFReproducingAndInsertionTracker().getOriginalCFIDCopyIndexReproducedCFIDMapMap().get(this).containsKey(copyIndex)) {//already reproduced
			//already reproduced
			return VSAArchiveReproducerAndInserter.getCFReproducingAndInsertionTracker().getOriginalCFIDCopyIndexReproducedCFIDMapMap().get(this).get(copyIndex);
		}else {
			//not reproduced yet
			//note that CF is assigned to the same VCDNode/VSComponent of the host CFG, thus the copy index of the host CFG should be the same
			CompositionFunctionID reproducedID = new CompositionFunctionID(
					this.getHostCompositionFunctionGroupID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex),
					this.indexID
					);
			//add to the reproduced MetadataID map in VSAArchiveReproducerAndInserter
			VSAArchiveReproducerAndInserter.getCFReproducingAndInsertionTracker().addToOriginalCFIDCopyIndexReproducedCFIDMapMap(
					this, copyIndex, reproducedID);
			
			
			return reproducedID;
		}
	}

	//// implements equals() and hashCode() methods
	//critical for CFD graph
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cfgID == null) ? 0 : cfgID.hashCode());
		result = prime * result + indexID;
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
		CompositionFunctionID other = (CompositionFunctionID) obj;
		if (cfgID == null) {
			if (other.cfgID != null)
				return false;
		} else if (!cfgID.equals(other.cfgID))
			return false;
		if (indexID != other.indexID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CompositionFunctionID [groupName=" + cfgID.getName() + ", indexID=" + indexID + "]";
	}
}
