package function.group;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import basic.SimpleName;
import basic.VfNameString;
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
public class CompositionFunctionGroupID implements PrimaryKeyID<CompositionFunctionGroup>, Reproducible{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4010215157432533543L;
	
	//////////////////////////////
	public static final ManagementTableColumn NAME_COLUMN = new ManagementTableColumn(
			//SimpleName name, SQLDataType sqlDataType, boolean inPrimaryKey, Boolean unique,
			new SimpleName("GROUP_NAME"), new SQLStringType(50,false), true, false,
			//Boolean notNull, String defaultStringValue, String additionalConstraints
			true, null, null
			);
	
	
	/////////////////////////
	private final CompositionFunctionGroupName name;
	
	/**
	 * constructor
	 * @param name
	 * @param typeName
	 */
	public CompositionFunctionGroupID(CompositionFunctionGroupName name){
		this.name = name;
	}
	
	////////////////////////////////
	public CompositionFunctionGroupName getName() {
		return name;
	}

	
	@Override
	public Map<SimpleName, String> getPrimaryKeyAttributeNameStringValueMap() {
		Map<SimpleName, String> ret = new HashMap<>();
		ret.put(NAME_COLUMN.getName(),this.getName().getStringValue());
		return ret;
	}

	@Override
	public Map<SimpleName, Boolean> getPrimaryKeyAttributeNameToIgnoreCaseMap() {
		Map<SimpleName, Boolean> ret = new HashMap<>();
		ret.put(NAME_COLUMN.getName(), name instanceof VfNameString);
		return ret;
	}
	/**
	 * reproduce this CompositionFunctionGroupID (if not already) and return the reproduced one;
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced CompositionFunctionGroup will be inserted;
	 * @param VSAArchiveReproducerAndInserter
	 * @param copyIndex copy index of VCCLNode of VCDNode to which this CompositionFunctionGroupID is assigned
	 * @throws SQLException 
	 */
	@Override
	public CompositionFunctionGroupID reproduce(VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter, int copyIndex)
			throws SQLException {
		//check if the CompositionFunctionGroupID of the given copy index has already been reproduced or not
		//for example, after the CFG was reproduced and inserted, its CF will be reproduced, in which process, the reproduced CFGID will be used but should not be reproduced again;
		if(VSAArchiveReproducerAndInserter.getCFGReproducingAndInsertionTracker().getOriginalCFGIDCopyIndexReproducedCFGIDMapMap().containsKey(this)
				&&VSAArchiveReproducerAndInserter.getCFGReproducingAndInsertionTracker().getOriginalCFGIDCopyIndexReproducedCFGIDMapMap().get(this).containsKey(copyIndex)) {//already reproduced
			//
			return VSAArchiveReproducerAndInserter.getCFGReproducingAndInsertionTracker().getOriginalCFGIDCopyIndexReproducedCFGIDMapMap().get(this).get(copyIndex);
		
		}else {
			//not reproduced yet
			CompositionFunctionGroupID reproducedID = 
					hostVisProjctDBContext.getHasIDTypeManagerController().getCompositionFunctionGroupManager().buildReproducedID(this, VSAArchiveReproducerAndInserter.getCFGReproducingAndInsertionTracker());
			
			//add to the reproduced MetadataID map in VSAArchiveReproducerAndInserter
			//this is done inside the {@link CFGReproducingAndInsertionTracker#build()} method
//			VSAArchiveReproducerAndInserter.getCFGReproducingAndInsertionTracker().addToOriginalCFGIDCopyIndexReproducedCFGIDMapMap(
//					this, copyIndex, reproducedID);
			
			return reproducedID;
		}
	}

	
	
	///////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		CompositionFunctionGroupID other = (CompositionFunctionGroupID) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CompositionFunctionGroupID [name=" + name + "]";
	}
}
