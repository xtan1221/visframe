package dependency.cfd;

import function.composition.CompositionFunctionID;

/**
 * an implementation of CFDEdge
 * 
 * @author tanxu
 *
 */
public class CFDEdgeImpl implements CFDEdge {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2841935314616085974L;
	
	/////////////////////
	private final CompositionFunctionID source;
	
	private final CompositionFunctionID sink;
	
	private final boolean basedOnIndependentFreeInputVariableType;
	
	private final boolean basedOnAssignedTarget;
	
	
	/**
	 * constructor
	 * @param source not null
	 * @param sink not null
	 * @param basedOnAssignedTarget 
	 * @param basedOnIndieFreeInputVariableType
	 */
	CFDEdgeImpl(CompositionFunctionID source, CompositionFunctionID sink, Boolean basedOnAssignedTarget, Boolean basedOnIndieFreeInputVariableType){
		
		if(source==null)
			throw new IllegalArgumentException("given source cannot be null!");
		
		if(sink==null)
			throw new IllegalArgumentException("given sink cannot be null!");
		
		//basedOnAssignedTarget and basedOnIndieFreeInputVariableType cannot both be false;
		if(!basedOnAssignedTarget && !basedOnIndieFreeInputVariableType)
			throw new IllegalArgumentException("basedOnAssignedTarget and basedOnIndieFreeInputVariableType cannot both be false");
		
		
		this.source = source;
		this.sink = sink;
		
		this.basedOnIndependentFreeInputVariableType = basedOnIndieFreeInputVariableType;
		this.basedOnAssignedTarget = basedOnAssignedTarget;
	}
	
	
	@Override
	public CompositionFunctionID getSource() {
		return source;
	}

	@Override
	public CompositionFunctionID getSink() {
		return sink;
	}

	@Override
	public boolean isBasedOnIndependentFreeInputVariableType() {
		return basedOnIndependentFreeInputVariableType;
	}
	
	@Override
	public boolean isBasedOnAssignedTarget() {
		return basedOnAssignedTarget;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (basedOnAssignedTarget ? 1231 : 1237);
		result = prime * result + (basedOnIndependentFreeInputVariableType ? 1231 : 1237);
		result = prime * result + ((sink == null) ? 0 : sink.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CFDEdgeImpl))
			return false;
		CFDEdgeImpl other = (CFDEdgeImpl) obj;
		if (basedOnAssignedTarget != other.basedOnAssignedTarget)
			return false;
		if (basedOnIndependentFreeInputVariableType != other.basedOnIndependentFreeInputVariableType)
			return false;
		if (sink == null) {
			if (other.sink != null)
				return false;
		} else if (!sink.equals(other.sink))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}


	
	
}
