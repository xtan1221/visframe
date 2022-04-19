package metadata.graph.feature;

import java.util.LinkedHashSet;

import metadata.graph.vftree.feature.VfTreeNodeFeature;
import rdb.table.data.DataTableColumnName;


/**
 * role of columns of data table of the graph node record data in the owner GraphDataMetadata object
 * 
 * @author tanxu
 * 
 */
public class GraphVertexFeature extends GraphComponentFeatureBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2786213582505344252L;
	
	/**
	 * constructor
	 * @param IDColumnNameSet
	 * @param additionalFeatureColumnNameSet
	 */
	public GraphVertexFeature(
			LinkedHashSet<DataTableColumnName> IDColumnNameSet,
			LinkedHashSet<DataTableColumnName> additionalFeatureColumnNameSet
			) {
		super(IDColumnNameSet, additionalFeatureColumnNameSet);
		// TODO Auto-generated constructor stub
	}

	
	//////////////////////////////
	///covers both GraphVertexFeature and VfTreeNodeFeature
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	
	/**
	 * this method takes whether compared items are of VfTreeNodeFeature type or not into consideration 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GraphVertexFeature))
			return false;
		
		//check if only one of this and obj is of VfTreeNodeFeature type while the other is not
		//this will cover the types for VfTreeNodeFeature, thus there is no need to override equals method in VfTreeNodeFeature class
		if((obj instanceof VfTreeNodeFeature && !(this instanceof VfTreeNodeFeature))
				|| (this instanceof VfTreeNodeFeature && !(obj instanceof VfTreeNodeFeature)))
			return false;
		
		return true;
	}
	
	
}
