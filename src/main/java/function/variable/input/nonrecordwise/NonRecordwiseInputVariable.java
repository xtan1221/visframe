package function.variable.input.nonrecordwise;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.input.InputVariable;

/**
 * InputVariable whose value is the same for all records of the data table;
 * 
 * thus the value should be either assigned or calculated before the record by record calculation starts of the host evaluator;
 * 
 * @author tanxu
 *
 */
public abstract class NonRecordwiseInputVariable extends InputVariable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6355198472714733955L;

	/**
	 * constructor
	 * @param hostCompositionFunctionID
	 * @param aliasName
	 * @param notes
	 * @param SQLDataType
	 * @param hostEvaluatorIndexID
	 */
	protected NonRecordwiseInputVariable(
//			MetadataID ownerRecordDataMetadataID,
			CompositionFunctionID hostCompositionFunctionID, 
			int hostComponentFunctionIndexID,
			int hostEvaluatorIndexID,
			SimpleName aliasName, VfNotes notes
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, hostEvaluatorIndexID, aliasName, notes);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract NonRecordwiseInputVariable reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;

}
