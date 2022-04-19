package function.component;

import java.sql.SQLException;
import java.util.Map;

import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import function.composition.CompositionFunctionID;

/**
 * 
 * @author tanxu
 *
 */
public abstract class AbstractComponentFunction implements ComponentFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 859603856957521221L;
	
	
	////////////////////////////////
	private final CompositionFunctionID hostCompositionFunctionID;
	private final int indexID; //unique id among all ComponentFunctions on the same tree
	private final VfNotes notes;
	
	/////////////////////////////
	/**
	 * map from upstream PiecewiseFunction Index ID to the Output Index;
	 * note that all {@link PiecewiseFunction} on the tree between the root {@link ComponentFunction}(inclusive) to this one(exclusive) should be included!
	 */
	protected transient Map<Integer,Integer> upstreamPiecewiseFunctionIndexIDOutputIndexMap;
	
	
	/**
	 * constructor;
	 * @param hostCompositionFunctionID
	 * @param notes
	 * @param next
	 * @param ID
	 */
	AbstractComponentFunction(
			CompositionFunctionID hostCompositionFunctionID,
			int indexID,
			VfNotes notes
			){
		
		this.hostCompositionFunctionID = hostCompositionFunctionID;
		this.notes = notes;
		this.indexID = indexID;
	}
	
	@Override
	public CompositionFunctionID getHostCompositionFunctionID() {
		return this.hostCompositionFunctionID;
	}
	
	@Override
	public int getIndexID() {
		return indexID;
	}
	
	@Override
	public VfNotes getNotes() {
		return notes;
	}
	
	/**
	 * return the map from upstream PiecewiseFunction Index ID to the Output Index;
	 * note that all {@link PiecewiseFunction} on the tree between the root {@link ComponentFunction}(inclusive) to this one(exclusive) should be included!
	 * 
	 * this map should be built by invoking {@link #buildUpstreamPiecewiseFunctionIndexIDOutputIndexMap(Map, Integer, Integer)} method
	 */
	@Override
	public Map<Integer,Integer> getUpstreamPiecewiseFunctionIndexIDOutputIndexMap(){
		return this.upstreamPiecewiseFunctionIndexIDOutputIndexMap;
	}
	
	////////////////////////////////
	/**
	 * 
	 * @param hostVisProjctDBContext the host VisProjectDBContext to which the reproduced cf will be inserted;
	 * @param VSAArchiveReproducerAndInserter the VSAArchiveReproducerAndInserter that triggers the reproduce process; note that the VisSchemeAppliedArchive is contained in this object
	 * @param copyIndex copy index of the VCDNode/VSComponent to which this owner cf is assigned
	 * @return
	 * @throws SQLException
	 */
	@Override
	public abstract AbstractComponentFunction reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex) throws SQLException;

	
	
	
	//////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hostCompositionFunctionID == null) ? 0 : hostCompositionFunctionID.hashCode());
		result = prime * result + indexID;
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractComponentFunction))
			return false;
		AbstractComponentFunction other = (AbstractComponentFunction) obj;
		if (hostCompositionFunctionID == null) {
			if (other.hostCompositionFunctionID != null)
				return false;
		} else if (!hostCompositionFunctionID.equals(other.hostCompositionFunctionID))
			return false;
		if (indexID != other.indexID)
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		return true;
	}
	
	
	
}
