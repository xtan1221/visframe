package dependency.dos;

import metadata.MetadataID;
import operation.OperationID;

/**
 * DOSEdge implementation for a regular DOSGraph induced by a set of target Metadata from a VisframeContext
 * @author tanxu
 *
 */
public class DOSEdgeImpl implements DOSEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1732388730557832509L;
	
	
	////////////////////////////////
	private final MetadataID source;
	private final MetadataID sink;
	private final DOSEdgeType type;
	private final OperationID operationID;
	
	
	/**
	 * 
	 * @param source
	 * @param sink
	 * @param type
	 * @param operationID can be null if
	 */
	DOSEdgeImpl(MetadataID source, MetadataID sink, DOSEdgeType type, OperationID operationID){
		//
		if(type.equals(DOSEdgeType.OPERATION)) {
			if(operationID==null)
				throw new IllegalArgumentException("given operationID cannot be null when type is OPERATION!");
		}else {
			if(operationID!=null)
				throw new IllegalArgumentException("given operationID must be null when type is not OPERATION!");
		}
		
		
		this.source = source;
		this.sink = sink;
		this.type = type;
		this.operationID = operationID;
	}
	
		
	@Override
	public MetadataID getSource() {
		return source;
	}

	@Override
	public MetadataID getSink() {
		return sink;
	}
	
	@Override
	public DOSEdgeType getType() {
		return type;
	}

	@Override
	public OperationID getOperationID() {
		return operationID;
	}



	////////////////////
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operationID == null) ? 0 : operationID.hashCode());
		result = prime * result + ((sink == null) ? 0 : sink.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DOSEdgeImpl))
			return false;
		DOSEdgeImpl other = (DOSEdgeImpl) obj;
		if (operationID == null) {
			if (other.operationID != null)
				return false;
		} else if (!operationID.equals(other.operationID))
			return false;
		if (sink == null) {
			if (other.sink != null)
				return false;
		} else if (!sink.equals(other.sink))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
