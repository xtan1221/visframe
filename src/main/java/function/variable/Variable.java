package function.variable;

import java.sql.SQLException;

import basic.HasNotes;
import basic.SimpleName;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * interface for Variable in CompositionFunction API;
 * 
 * NOTE that variable domain is not implemented in current Variable API;
 * 
 * however, constraints checking on the domain of the variable can be built into Evaluator such as SymjaExpressionEvaluator or in a PiecewiseFunction;
 * 
 * @author tanxu
 * 
 */
public interface Variable extends HasNotes, Reproducible{
	/**
	 * return the CompositionFunctionID of the CompositionFunction where this Variable is used;
	 * @return
	 */
	CompositionFunctionID getHostCompositionFunctionID();
	
	/**
	 * 
	 * @return
	 */
	default CompositionFunctionGroupID getHostCompositionFunctionGroupID() {
		return this.getHostCompositionFunctionID().getHostCompositionFunctionGroupID();
	}
	
	
	int getHostComponentFunctionIndexID();
	
	int getHostEvaluatorIndexID();
	
	/**
	 * return the alias name of this Variable
	 * @return
	 */
	SimpleName getAliasName();
	
	/**
	 * return the sql data type of this Variable;
	 * @return
	 */
	VfDefinedPrimitiveSQLDataType getSQLDataType();
	
	/**
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	Variable reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
}
