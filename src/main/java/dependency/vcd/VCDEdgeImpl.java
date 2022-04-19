package dependency.vcd;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import context.scheme.VSComponent;
import function.composition.CompositionFunctionID;
import function.group.CompositionFunctionGroupID;
import metadata.MetadataID;
import operation.OperationID;
import utils.Pair;

public class VCDEdgeImpl implements VCDEdge {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2365859275406578399L;
	
	//////////////////
	private final VSComponent source;
	private final VSComponent sink;
	
	
	/////////////////
	//the fields built during the process of building the vcd graph in VCDGraphBuilder;
	//TODO included in the equals and hashcode methods?
	private Set<Pair<OperationID, MetadataID>> operationIDInputMetadataIDDependencyPairSet;
	private Set<Pair<CompositionFunctionGroupID, MetadataID>> cfgIDOwnerRecordMetadataIDDependencyPairSet;
	private Set<Pair<CompositionFunctionID, CompositionFunctionID>> cfIDDependedCfIDDependencyPairSet;
	private Set<Pair<CompositionFunctionID, MetadataID>> cfIDDependedRecordMetadataIDDependencyPairSet;
	
	
	/**
	 * constructor 
	 * @param source not null
	 * @param sink not null
	 */
	VCDEdgeImpl(
			VSComponent source, VSComponent sink
			){
		if(source==null||sink==null)
			throw new IllegalArgumentException("given souce and sink cannot be null!");
		
		if(source.equals(sink))
			throw new IllegalArgumentException("given source and sink cannot be equal!");
		
		this.source = source;
		this.sink = sink;
		
		this.operationIDInputMetadataIDDependencyPairSet = new LinkedHashSet<>();
		this.cfgIDOwnerRecordMetadataIDDependencyPairSet = new LinkedHashSet<>();
		this.cfIDDependedCfIDDependencyPairSet = new LinkedHashSet<>();
		this.cfIDDependedRecordMetadataIDDependencyPairSet = new LinkedHashSet<>();
		
	}
	
	
	///////////////////
	@Override
	public VSComponent getSource() {
		return source;
	}
	
	@Override
	public VSComponent getSink() {
		return sink;
	}
	
	@Override
	public Set<Pair<OperationID, MetadataID>> getOperationIDInputMetadataIDDependencyPairSet() {
		return Collections.unmodifiableSet(this.operationIDInputMetadataIDDependencyPairSet);
	}


	@Override
	public void addOperationIDInputMetadataIDDependency(OperationID dependingID, MetadataID dependedID) {
		this.operationIDInputMetadataIDDependencyPairSet.add(new Pair<>(dependingID, dependedID));
	}
	

	@Override
	public Set<Pair<CompositionFunctionGroupID, MetadataID>> getCfgIDOwnerRecordMetadataIDDependencyPairSet() {
		return Collections.unmodifiableSet(this.cfgIDOwnerRecordMetadataIDDependencyPairSet);
	}


	@Override
	public void addCfgIDOwnerRecordMetadataIDDependency(CompositionFunctionGroupID dependingID, MetadataID dependedID) {
		this.cfgIDOwnerRecordMetadataIDDependencyPairSet.add(new Pair<>(dependingID, dependedID));
	}


	@Override
	public Set<Pair<CompositionFunctionID, CompositionFunctionID>> getCfIDDependedCfIDDependencyPairSet() {
		return Collections.unmodifiableSet(this.cfIDDependedCfIDDependencyPairSet);
	}


	@Override
	public void addCfIDDependedCfIDDependency(CompositionFunctionID dependingID, CompositionFunctionID dependedID) {
		this.cfIDDependedCfIDDependencyPairSet.add(new Pair<>(dependingID, dependedID));
	}


	@Override
	public Set<Pair<CompositionFunctionID, MetadataID>> getCfIDDependedRecordMetadataIDDependencyPairSet() {
		return Collections.unmodifiableSet(this.cfIDDependedRecordMetadataIDDependencyPairSet);
	}


	@Override
	public void addCfIDDependedRecordMetadataIDDependency(CompositionFunctionID dependingID, MetadataID dependedID) {
		this.cfIDDependedRecordMetadataIDDependencyPairSet.add(new Pair<>(dependingID, dependedID));
	}



	///////////////////////////////
	//TODO
	//equals and hashcode methods only include the final fields??? 
	//non final fields (even though not transient) may change in the process of building the vcd graph during which the equals method may be used???
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sink == null) ? 0 : sink.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VCDEdgeImpl))
			return false;
		VCDEdgeImpl other = (VCDEdgeImpl) obj;
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
		return true;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((cfIDDependedCfIDDependencyPairSet == null) ? 0 : cfIDDependedCfIDDependencyPairSet.hashCode());
//		result = prime * result + ((cfIDDependedRecordMetadataIDDependencyPairSet == null) ? 0
//				: cfIDDependedRecordMetadataIDDependencyPairSet.hashCode());
//		result = prime * result + ((cfgIDOwnerRecordMetadataIDDependencyPairSet == null) ? 0
//				: cfgIDOwnerRecordMetadataIDDependencyPairSet.hashCode());
//		result = prime * result + ((operationIDInputMetadataIDDependencyPairSet == null) ? 0
//				: operationIDInputMetadataIDDependencyPairSet.hashCode());
//		result = prime * result + ((sink == null) ? 0 : sink.hashCode());
//		result = prime * result + ((source == null) ? 0 : source.hashCode());
//		return result;
//	}
//
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!(obj instanceof VCDEdgeImpl))
//			return false;
//		VCDEdgeImpl other = (VCDEdgeImpl) obj;
//		if (cfIDDependedCfIDDependencyPairSet == null) {
//			if (other.cfIDDependedCfIDDependencyPairSet != null)
//				return false;
//		} else if (!cfIDDependedCfIDDependencyPairSet.equals(other.cfIDDependedCfIDDependencyPairSet))
//			return false;
//		if (cfIDDependedRecordMetadataIDDependencyPairSet == null) {
//			if (other.cfIDDependedRecordMetadataIDDependencyPairSet != null)
//				return false;
//		} else if (!cfIDDependedRecordMetadataIDDependencyPairSet
//				.equals(other.cfIDDependedRecordMetadataIDDependencyPairSet))
//			return false;
//		if (cfgIDOwnerRecordMetadataIDDependencyPairSet == null) {
//			if (other.cfgIDOwnerRecordMetadataIDDependencyPairSet != null)
//				return false;
//		} else if (!cfgIDOwnerRecordMetadataIDDependencyPairSet
//				.equals(other.cfgIDOwnerRecordMetadataIDDependencyPairSet))
//			return false;
//		if (operationIDInputMetadataIDDependencyPairSet == null) {
//			if (other.operationIDInputMetadataIDDependencyPairSet != null)
//				return false;
//		} else if (!operationIDInputMetadataIDDependencyPairSet
//				.equals(other.operationIDInputMetadataIDDependencyPairSet))
//			return false;
//		if (sink == null) {
//			if (other.sink != null)
//				return false;
//		} else if (!sink.equals(other.sink))
//			return false;
//		if (source == null) {
//			if (other.source != null)
//				return false;
//		} else if (!source.equals(other.source))
//			return false;
//		return true;
//	}

	
}
