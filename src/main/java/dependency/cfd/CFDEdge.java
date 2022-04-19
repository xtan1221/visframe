package dependency.cfd;

import java.io.Serializable;

import function.composition.CompositionFunctionID;


/**
 * interface for CFDEdge on CFDGraph;
 * each CFDEdge can be based on {@link IndependentFreeInputVariableType} and/or {@link CFGTarget} dependency;
 * 
 * CFD edges are from depending CF(source) to depended CF(sink)
 * 
 * @author tanxu
 * 
 */
public interface CFDEdge extends Serializable{
	/**
	 * return the CFID of the CF that depends on the sink CF
	 * @return
	 */
	CompositionFunctionID getSource();
	
	/**
	 * return the CFID of the CF that is depended by the source CF;
	 * @return
	 */
	CompositionFunctionID getSink();
	
	/**
	 * return whether the edge is based on the FreeInputVariable(depending CF has one or more FreeInputVariable owned by the depended CF); 
	 * @return
	 */
	boolean isBasedOnIndependentFreeInputVariableType();
	
	/**
	 * return whether the edge is based on the assigned CFGTarget to the depended CF;
	 * @return
	 */
	boolean isBasedOnAssignedTarget();
}
