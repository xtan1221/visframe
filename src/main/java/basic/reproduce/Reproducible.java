package basic.reproduce;

import java.io.Serializable;
import java.sql.SQLException;

import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;


/**
 * for CFG and Operation and CF;
 * 
 * a generic reproducible type is of type Reproducible if it contains at least one {@link Reproducible} attribute needed to be reproduced;
 * 
 * @author tanxu
 * 
 */
public interface Reproducible extends Serializable{
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced entity will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex 
	 * @return
	 * @throws SQLException
	 */
	Reproducible reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
}
