package function.group;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.VfNotes;
import basic.lookup.VisframeUDT;
import basic.process.ReproduceableProcessType;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.target.CFGTarget;
import metadata.MetadataID;
import visinstance.VisInstance;

/**
 * interface for a related group of {@link CompositionFunction} that together calculates a set of target variables of type {@link CFGTarget} for all records in the 
 * data table of a {@link RecordDataMetadata};
 * 
 * @author tanxu
 *  
 */
public interface CompositionFunctionGroup extends HasName, HasNotes, VisframeUDT, ReproduceableProcessType, Reproducible{
	
	SimpleName getTypeName();//final subclass
	
	VfNotes getTypeNotes();
	
	/**
	 * return the {@link CompositionFunctionGroupName} of this {@link CompositionFunctionGroup}
	 * 
	 */
	@Override
	CompositionFunctionGroupName getName();
	
	/**
	 * return the {@link CompositionFunctionGroupID} of this {@link CompositionFunctionGroup};
	 * all {@link CompositionFunctionGroup}s in the same {@link VisframeContext} should have their own unique {@link CompositionFunctionGroupID}
	 */
	default CompositionFunctionGroupID getID() {
		return new CompositionFunctionGroupID(this.getName());
	}
	
	/**
	 * return the {@link MetadataID} of the {@link RecordDataMetadata} for which this {@link CompositionFunctionGroup} is created;
	 * @return
	 */
	MetadataID getOwnerRecordDataMetadataID();
	
	/**
	 * return the map from the unique name of each {@link CFGTarget} of this {@link CompositionFunctionGroup} to the {@link CFGTarget}
	 * @return
	 */
	Map<SimpleName,? extends CFGTarget<?>> getTargetNameMap();
	
	/**
	 * return the set of target names that must be explicitly assigned to a {@link CompositionFunction} so that a valid value can be calculated;
	 * 
	 * specifically, those targets are those without non-null default value AND can not be null;
	 * 
	 * ===========================!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * for such targets, they must be assigned to {@link CompositionFunction} to be used as {@link CFGTargetInputVariable};
	 * 
	 * for {@link ShapeCFG}, all of such targets must be assigned to {@link CompositionFunction} before it can be used as core ShapeCFG of any {@link VisInstance};
	 * 
	 * ===========================
	 * more, when creating {@link VisInstance}, all mandatory targets of composition functions involved in the underlying dependency graph 
	 * should be assigned to {@link CompositionFunction}; otherwise, the {@link VisInstance} cannot be created;
	 * 
	 * @return
	 */
	default Set<SimpleName> getMandatoryTargetNameSet(){
		Set<SimpleName> ret = new LinkedHashSet<>();
		this.getTargetNameMap().forEach((k,v)->{
			if(v.getDefaultValue()==null&&!v.canBeNull()) {
				ret.add(k);
			}
		});
		
		return ret;
	}
	
	
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced CFG will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this CFG is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	CompositionFunctionGroup reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
}
