package basic.process;

import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;

/**
 * marker interface for {@link ProcessType} that can be reproduced and inserted by a {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance}
 * 
 * note that for a ProcessType, it must be a ReproduceableProcessType or NonReproduceableProcessType, but not both!
 * @author tanxu
 *
 */
public interface ReproduceableProcessType extends ProcessType{
	
}
