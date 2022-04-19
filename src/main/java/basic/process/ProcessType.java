package basic.process;

import basic.lookup.VisframeUDT;

/**
 * marker interface for a {@link VisframeUDT} type whose entities can be of process type;
 * 
 * the difference between {@link ProcessType} and {@link NonProcessType} is that
 * 1. for {@link ProcessType} in visframe, they can be directly created and inserted into visframe project DB by UI end user;
 * 2. for {@link NonProcessType} in visframe, 
 * 			they can NOT be directly created and inserted into visframe project DB by UI end user;
 * 			instead, they can only be created and initialized by either another {@link NonProcessType} entity or a {@link ProcessType};
 * 
 * @author tanxu
 *
 */
public interface ProcessType {
	
}
