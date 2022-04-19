package visinstance.run;

import basic.VfNotes;
import visinstance.VisInstanceID;
import visinstance.run.calculation.IndependentFIVTypeIDStringValueMap;

public class VisInstanceRunImpl implements VisInstanceRun{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8824665107222901669L;
	
	/////////////////////
	private final int runUID;
	private final VisInstanceID visInstanceID;
	private final IndependentFIVTypeIDStringValueMap CFDGraphIndependetFIVStringValueMap;
	private final VfNotes notes;
	
	/**
	 * constructor
	 * @param runUID unique id of this VisInstanceRunImpl among all VisInstanceRunImpl in the same host VisProjectDBContext
	 * @param visInstanceID not null
	 * @param CFDGraphIndependetFIVStringValueMap values for all IndependentFreeInputVariableTypes of FreeInputVariables of CompositionFunctions on the CFD graph
	 * @param notes
	 */
	public VisInstanceRunImpl(
			int runUID,
			VisInstanceID visInstanceID,
			IndependentFIVTypeIDStringValueMap CFDGraphIndependetFIVStringValueMap,
			VfNotes notes
			){
		if(visInstanceID==null)
			throw new IllegalArgumentException("given visInstanceID cannot be null!");
		if(CFDGraphIndependetFIVStringValueMap==null)
			throw new IllegalArgumentException("given CFDGraphIndependetFIVStringValueMap cannot be null!");
		if(notes==null)
			throw new IllegalArgumentException("given notes cannot be null!");
		
		
		
		this.runUID = runUID;
		this.visInstanceID = visInstanceID;
		this.CFDGraphIndependetFIVStringValueMap = CFDGraphIndependetFIVStringValueMap;
		this.notes = notes;
	}
	
	@Override
	public VfNotes getNotes() {
		return notes;
	}

	@Override
	public IndependentFIVTypeIDStringValueMap getCFDGraphIndependetFIVStringValueMap() {
		return CFDGraphIndependetFIVStringValueMap;
	}
	
	@Override
	public int getRunUID() {
		return runUID;
	}

	@Override
	public VisInstanceID getVisInstanceID() {
		return visInstanceID;
	}
	
	///////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((CFDGraphIndependetFIVStringValueMap == null) ? 0 : CFDGraphIndependetFIVStringValueMap.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + runUID;
		result = prime * result + ((visInstanceID == null) ? 0 : visInstanceID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VisInstanceRunImpl))
			return false;
		VisInstanceRunImpl other = (VisInstanceRunImpl) obj;
		if (CFDGraphIndependetFIVStringValueMap == null) {
			if (other.CFDGraphIndependetFIVStringValueMap != null)
				return false;
		} else if (!CFDGraphIndependetFIVStringValueMap.equals(other.CFDGraphIndependetFIVStringValueMap))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (runUID != other.runUID)
			return false;
		if (visInstanceID == null) {
			if (other.visInstanceID != null)
				return false;
		} else if (!visInstanceID.equals(other.visInstanceID))
			return false;
		return true;
	}
}
