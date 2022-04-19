package basic.process;

import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducedAndInsertedInstance;

/**
 * marker interface for {@link ProcessType} that can NOT be reproduced and inserted by a {@link VisSchemeAppliedArchiveReproducedAndInsertedInstance};
 * 
 * note that for a ProcessType, it must be a ReproduceableProcessType or NonReproduceableProcessType, but not both!
 * 
 * note that a new rollback can be triggered starting from a process entity of NonReproduceableProcessType;
 * 
 * @author tanxu
 *
 */
public interface NonReproduceableProcessType extends ProcessType{
	
}
