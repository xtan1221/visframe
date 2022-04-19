package function.variable.output.type;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.output.OutputVariable;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * the single {@link OutputVariable} of an {@link Evaluator} in a {@link ConditionalEvaluatorDelegator} of a {@link PiecewiseFunction};
 * 
 * must be of boolean data type;
 * 
 * 
 * the calculated value of this EvaluatorSpecificOutputVariable will not be directly put into any value table related with the host CompositionFunction;
 * 
 * rather, the calculated value is used to facilitate evaluating the value of the column corresponding to the host PiecewiseFunction 
 * in the (Upstream piecewise function index ID output index value table) of the host CompositionFunction during the calculation of the CFtarget value table;
 * 
 * cannot be used by downstream {@link UpstreamValueTableColumnOutputVariableInputVariable};
 * 
 * @author tanxu
 *
 */
public class PFConditionEvaluatorBooleanOutputVariable extends OutputVariable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7675732562720603103L;
	
	///////////////////////////////////////////
	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param aliasName
	 * @param notes
	 * @param hostEvaluatorIndexID
	 */
	public PFConditionEvaluatorBooleanOutputVariable(
			CompositionFunctionID hostCompositionFunctionID, 
			int hostComponentFunctionIndexID,
			int hostEvaluatorIndexID,
			SimpleName aliasName, VfNotes notes
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, hostEvaluatorIndexID, aliasName, notes);
		// TODO Auto-generated constructor stub
	}
	
	/////////////////////////
	@Override
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return SQLDataTypeFactory.booleanType();
	}
	
	
	/**
	 * reproduce and return a new PFConditionEvaluatorBooleanOutputVariable of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public PFConditionEvaluatorBooleanOutputVariable reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
//		//find out the copy index of the VCDNode to which the owner record Metadata is assigned
//		int copyIndexOfOwnerRecordMetadata = 
//				VSAArchiveReproducerAndInserter.getApplierArchive().lookupCopyIndexOfOwnerRecordMetadata(
//						this.getHostCompositionFunctionID().getHostCompositionFunctionGroupID(), copyIndex);
//		
//		MetadataID reproducedOwnerRecordDataMetadataID = 
//				this.getOwnerRecordDataMetadataID().reproduce(
//						hostVisProjctDBContext, 
//						VSAArchiveReproducerAndInserter,
//						copyIndexOfOwnerRecordMetadata);//find out the copy index of owner record data
		
		//
		CompositionFunctionID reproducedHostCompositionFunctionID =
				this.getHostCompositionFunctionID().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndex);
		
		
		int reproducedHostComponentFunctionIndexID = this.getHostComponentFunctionIndexID();
		int reproducedHostEvaluatorIndexID = this.getHostEvaluatorIndexID();
		SimpleName reproducedAliasName = this.getAliasName().reproduce();
		VfNotes reproducedNotes = this.getNotes().reproduce();
		
		
		
		return new PFConditionEvaluatorBooleanOutputVariable(
//				reproducedOwnerRecordDataMetadataID,
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedHostEvaluatorIndexID,
				reproducedAliasName,
				reproducedNotes
				);
	}
	
	
	
}
