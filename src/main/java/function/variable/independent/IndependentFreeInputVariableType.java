package function.variable.independent;

import java.sql.SQLException;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.lookup.VisframeUDT;
import basic.process.NonProcessType;
import basic.reproduce.Reproducible;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;


/**
 * interface for an independent free input variable owned by a CompositionFunction;
 * 
 * all such free input variables owned by the same CompositionFunction should have their unique ID name returned by {@link #getName()};
 * 
 * such free input variables can be used to make {@link FreeInputVariable} for an Evaluator that can be either hosted by the owner CompositionFunction of the free input variable or others;
 * 
 * note that the ID name returned by {@link #getName()} should not be used as alias name of any {@link FreeInputVariable} backed by this IndependentFreeInputVariableType;
 * this is because it is allowed that IndependentFreeInputVariableType owned by different {@link CompositionFunction}s can have same ID name; 
 * thus if they are present in the same {@link Evaluator}, and the ID names are used as alias name of the {@link FreeInputVariable}, error will be resulted;
 * 
 * @author tanxu
 *
 */
public interface IndependentFreeInputVariableType extends HasName, HasNotes, VisframeUDT, NonProcessType, Reproducible{
	/**
	 * return the unique name of this IndependentFreeInputVariableType among all the IndependentFreeInputVariableTypes of the same owner CompositionFunction
	 */
	@Override
	SimpleName getName();
	
	
	/**
	 * return the owner CompositionFunctionID
	 * @return
	 */
	CompositionFunctionID getOwnerCompositionFunctionID();
	
	
	
	@Override
	default IndependentFreeInputVariableTypeID getID() {
		return new IndependentFreeInputVariableTypeID(this.getOwnerCompositionFunctionID(), this.getName());
	}
	
	
	/**
	 * return the sql data type
	 * @return
	 */
	VfDefinedPrimitiveSQLDataType getSQLDataType();
	
	/**
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	IndependentFreeInputVariableType reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;
}
