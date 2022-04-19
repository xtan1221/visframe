package visinstance.run;

import basic.HasNotes;
import basic.lookup.VisframeUDT;
import basic.lookup.project.type.VisframeUDTManagementProcessRelatedTableColumnFactory;
import basic.process.NonReproduceableProcessType;
import visinstance.VisInstanceID;
import visinstance.run.calculation.IndependentFIVTypeIDStringValueMap;


/**
 * interface for a VisInstanceRun;
 * 
 * note that the set of CFTargetValueTableRun that participate in this VisInstanceRun is not stored in the class;
 * rather they are stored as a column {@link VisframeUDTManagementProcessRelatedTableColumnFactory#involvedCfTargetValueTableRunIDSetColumn} in the management table of VisInstanceRun
 * 
 * @author tanxu
 *
 */
public interface VisInstanceRun extends VisframeUDT, HasNotes, NonReproduceableProcessType{
	/**
	 * return the unique integer UID of this VisInstanceRun among all VisInstanceRuns in the host VisProjectDBContext's rdb
	 * @return
	 */
	int getRunUID();
	
	
	@Override
	default VisInstanceRunID getID() {
		return new VisInstanceRunID(this.getRunUID());
	}
	
	/**
	 * return the ID of the owner {@link VisInstance} of this VisInstanceRun in the host {@link VisProjectDBContext}'s rdb
	 * @return
	 */
	VisInstanceID getVisInstanceID();
	
	/**
	 * return the CFDGraphIndependetFIVStringValueMap containing assigned string values for all IndependetFIV present on the CFD graph induced by the owner VisInstance of this VisInstanceRun;
	 * @return
	 */
	IndependentFIVTypeIDStringValueMap getCFDGraphIndependetFIVStringValueMap();
}
