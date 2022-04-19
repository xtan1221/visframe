package dependency.cfd;

import java.io.Serializable;

import function.composition.CompositionFunctionID;

public interface CFDNode extends Serializable{
	/**
	 * return the contained CompositionFunctionID
	 * @return
	 */
	CompositionFunctionID getCFID();
}
