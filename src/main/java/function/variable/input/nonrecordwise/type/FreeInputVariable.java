package function.variable.input.nonrecordwise.type;

import java.sql.SQLException;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;
import function.variable.independent.IndependentFreeInputVariableType;
import function.variable.input.nonrecordwise.NonRecordwiseInputVariable;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;


/**
 * MODIFIED:
 * 
 * FreeInputVariable is of NonRecordwiseInputVariable type;
 * 
 * ====================following should be updated TODO
 * see 
 * 
 * FreeInputVariable of a CompositionFunction(ownerCompositionFunctionID) can be used in another CompositionFunction(host CompositionFunction) and the relationship should be reflected on the CFD graph;
 * thus, it cannot result in CYCLE in CFD graph!?
 * 
 * since FreeInputVariable dependency is included in CFD graph, thus even if a CF(cf1) is depended by another CF(cf2) merely by a single FreeInputVariable, 
 * when to calculate value table of cf2, the value table for cf1 will be calculated as well, which is very resource-consuming! 
 * 
 * thus, try to avoid cross-CompositionFunction sharing of FreeInputVariable unless it is a MUST-DO;
 * 
 * 
 * =========================================================
 * PREVIOUS:
 * FreeInputVariable are shared by all evaluators in the same host VisframeContext and are distinguished by the alias name;
 * 
 * it is not allowed to have FreeInputVariables with the same alias name but different SQLDataTypes;
 * 
 * @author tanxu
 *
 */
public class FreeInputVariable extends NonRecordwiseInputVariable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2395102764484992051L;
	
	
	///////////////
	private final IndependentFreeInputVariableType independentFreeInputVariableType;
	
	/**
	 * constructor
	 * @param hostCompositionFunctionID where this FIV is used
	 * @param aliasName alias name of this FreeInputVariable used in the owner evaluator
	 * @param notes
	 * @param ownerCompositionFunctionID for which CompositionFunction this FIV is originally created
	 * @param SQLDataType
	 */
	/**
	 * 
	 * @param ownerRecordDataMetadataID
	 * @param hostCompositionFunctionID
	 * @param hostComponentFunctionIndexID
	 * @param hostEvaluatorIndexID
	 * @param aliasName
	 * @param notes
	 * @param independentFreeInputVariableType
	 */
	public FreeInputVariable(
			CompositionFunctionID hostCompositionFunctionID,
			int hostComponentFunctionIndexID,
			int hostEvaluatorIndexID,
			SimpleName aliasName, 
			VfNotes notes,
			IndependentFreeInputVariableType independentFreeInputVariableType
			) {
		super(hostCompositionFunctionID, hostComponentFunctionIndexID, hostEvaluatorIndexID, aliasName, notes);
		// TODO Auto-generated constructor stub
		
		this.independentFreeInputVariableType = independentFreeInputVariableType;
	}
	
	
	public IndependentFreeInputVariableType getIndependentFreeInputVariableType() {
		return independentFreeInputVariableType;
	}

	//////////////////////////////
	@Override
	public VfDefinedPrimitiveSQLDataType getSQLDataType() {
		return independentFreeInputVariableType.getSQLDataType();
	}
	
	
	/**
	 * reproduce and return a new FreeInputVariable of this one;
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException 
	 */
	@Override
	public FreeInputVariable reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException {
		
		//find out the copy index of the VCDNode to which the owner record Metadata is assigned
//		int copyIndexOfOwnerRecordMetadata = 
//				VSAArchiveReproducerAndInserter.getApplierArchive().lookupCopyIndexOfOwnerRecordMetadata(
//						this.getHostCompositionFunctionID().getHostCompositionFunctionGroupID(), copyIndex);
		
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
		
		//
		//first find out the copy index of the owner CF of the IndependentFreeInputVariableType
		int copyIndexOfOwnerCF = 
				VSAArchiveReproducerAndInserter.getAppliedArchive().lookupDependedCFCopyIndex(
						this.getHostCompositionFunctionID(), copyIndex, this.getIndependentFreeInputVariableType().getOwnerCompositionFunctionID());
		
		IndependentFreeInputVariableType reproducedIndependentFreeInputVariableType = 
				this.getIndependentFreeInputVariableType().reproduce(hostVisProjctDBContext, VSAArchiveReproducerAndInserter, copyIndexOfOwnerCF);
		
		
		return new FreeInputVariable(
//				reproducedOwnerRecordDataMetadataID,
				reproducedHostCompositionFunctionID,
				reproducedHostComponentFunctionIndexID,
				reproducedHostEvaluatorIndexID,
				reproducedAliasName,
				reproducedNotes,
				reproducedIndependentFreeInputVariableType
				);
		
	}


	//////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((independentFreeInputVariableType == null) ? 0 : independentFreeInputVariableType.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof FreeInputVariable))
			return false;
		FreeInputVariable other = (FreeInputVariable) obj;
		if (independentFreeInputVariableType == null) {
			if (other.independentFreeInputVariableType != null)
				return false;
		} else if (!independentFreeInputVariableType.equals(other.independentFreeInputVariableType))
			return false;
		return true;
	}

}
