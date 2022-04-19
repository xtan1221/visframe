package metadata;

/**
 * types of Metadata implemented in current visframe version;
 * @author tanxu
 *
 */
public enum DataType{
	/**
	 * record
	 */
	RECORD,
	
	/**
	 * non-NEWICKTREE graph
	 */
	GRAPH,
	
	/**
	 * tree with a set of visframe defined features/constraints specifically designed for phylogenetic tree operation and visualization
	 */
	vfTREE;
	
	/**
	 * return this DataType is a generic graph or not;
	 * @return
	 */
	public boolean isGenericGraph() {
		return this.equals(GRAPH)|| this.equals(vfTREE);
	}
	
	public DataType getType(String type) {
		return valueOf(type);
	}
}
