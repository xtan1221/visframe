package dependency.dos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.SimpleDirectedGraph;

import dependency.dos.DOSEdge.DOSEdgeType;
import metadata.MetadataID;
import operation.Operation;
import operation.OperationID;
import rdb.table.data.DataTableColumnName;

/**
 * 
 * @author tanxu
 *
 * @param <N>
 * @param <E>
 */
public class SimpleDOSGraph extends DOSGraphBase<DOSNodeImpl, DOSEdgeImpl>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8061028657873557598L;
	
	/////////////////////////////
	
	/**
	 * set of MetadataID from which the DOS graph is induced; 
	 * 
	 * note that inducing MetadataIDs are not necessarily all leaf nodes on the DOS graph
	 */
	private final Set<MetadataID> inducingMetadataIDSet;
	
	/**
	 * map from record data MetadataID to the set of column names used as input parameter of one or more Operations on the DOS graph;
	 * 
	 * extracted with {@link Operation#getInputRecordMetadataIDInputColumnNameSetMap()} method of all {@link Operation}s on the DOS graph;
	 * 
	 */
	private final Map<MetadataID, Set<DataTableColumnName>> operationInputRecordMetadataIDInputColumnNameSetMap;
	
	/**
	 * full set of {@link MetadataID} of all data types present on DOS graph as node;
	 * 
	 * including the inducing MetadataID set as well as all induced MetadataIDs on the graph;
	 */
	private Set<MetadataID> metadataIDSet;
	
	/**
	 * full set of {@link OperationID} that are present on the DOS graph as edge of type {@link DOSEdgeType#OPERATION}
	 */
	private Set<OperationID> operationIDSet;
	
	
	private Map<MetadataID, DOSNodeImpl> metadataIDDOSNodeMap;
	
	/**
	 * constructor
	 * @param underlyingGraph
	 * @param inducingMetadataIDSet
	 * @param operationInputRecordMetadataIDInputColumnNameSetMap not null; may be empty if there is no operation involved;
	 */
	public SimpleDOSGraph(
			SimpleDirectedGraph<DOSNodeImpl, DOSEdgeImpl> underlyingGraph, 
			
			Set<MetadataID> inducingMetadataIDSet,
			Map<MetadataID, Set<DataTableColumnName>> operationInputRecordMetadataIDInputColumnNameSetMap) {
		super(underlyingGraph, DOSEdgeImpl.class);
		// TODO Auto-generated constructor stub
		
		this.inducingMetadataIDSet = inducingMetadataIDSet;
		this.operationInputRecordMetadataIDInputColumnNameSetMap = operationInputRecordMetadataIDInputColumnNameSetMap;
	}


	public Set<MetadataID> getInducingMetadataIDSet() {
		return inducingMetadataIDSet;
	}
	
	
	public Set<MetadataID> getMetadataIDSet() {
		if(this.metadataIDSet == null) {
			this.metadataIDSet = new HashSet<>();
			this.getUnderlyingGraph().vertexSet().forEach(e->{
				this.metadataIDSet.add(e.getMetadataID());
			});
		}
		
		return metadataIDSet;
	}

	
	public Set<OperationID> getOperationIDSet() {
		if(this.operationIDSet==null) {
			this.operationIDSet = new HashSet<>();
			
			this.getUnderlyingGraph().edgeSet().forEach(e->{
				if(e.getType().equals(DOSEdgeType.OPERATION))
					this.operationIDSet.add(e.getOperationID());
			});
		}
		
		return operationIDSet;
	}


	public Map<MetadataID, Set<DataTableColumnName>> getOperationInputRecordMetadataIDInputColumnNameSetMap() {
		return operationInputRecordMetadataIDInputColumnNameSetMap;
	}


	/**
	 * @return the metadataIDDOSNodeMap
	 */
	public Map<MetadataID, DOSNodeImpl> getMetadataIDDOSNodeMap() {
		if(this.metadataIDDOSNodeMap == null) {
			this.metadataIDDOSNodeMap = new HashMap<>();
			
			this.getUnderlyingGraph().vertexSet().forEach(v->{
				this.metadataIDDOSNodeMap.put(v.getMetadataID(), v);
			});
		}
		return metadataIDDOSNodeMap;
	}

}
