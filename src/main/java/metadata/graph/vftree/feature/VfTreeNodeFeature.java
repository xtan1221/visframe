package metadata.graph.vftree.feature;

import java.util.LinkedHashSet;

import metadata.graph.feature.GraphVertexFeature;
import metadata.graph.vftree.VfTreeMandatoryNodeDataTableSchemaUtils;
import rdb.table.data.DataTableColumnName;

/**
 * 
 * @author tanxu
 *
 */
public class VfTreeNodeFeature extends GraphVertexFeature{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1182609545534966095L;
	
	
	////////////////////////
	private transient LinkedHashSet<DataTableColumnName> nonMandatoryAdditionalColumnNameSet;
	
	/**
	 * constructor
	 * @param nonMandatoryAdditionalFeatureColumnNameSet
	 */
	public VfTreeNodeFeature(
//			LinkedHashSet<DataTableColumnName> IDColumnNameSet,
			LinkedHashSet<DataTableColumnName> nonMandatoryAdditionalFeatureColumnNameSet) {
		super(VfTreeMandatoryNodeDataTableSchemaUtils.getIDColumnNameList(), VfTreeMandatoryNodeDataTableSchemaUtils.makeAdditionalFeatureColumnNameSet(nonMandatoryAdditionalFeatureColumnNameSet));
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * return the name set of non-mandatory additional feature columns of VfTree node record data
	 * @return
	 */
	public LinkedHashSet<DataTableColumnName> getNonMandatoryAdditionalColumnNameSet(){
		if(this.nonMandatoryAdditionalColumnNameSet == null) {
			this.nonMandatoryAdditionalColumnNameSet = new LinkedHashSet<>();
			for(DataTableColumnName col:this.getAdditionalFeatureColumnNameSet()) {
				if(!this.getMandatoryAdditionalColumnNameSet().contains(col)) {
					this.nonMandatoryAdditionalColumnNameSet.add(col);
				}
			}
		}
		
		return this.nonMandatoryAdditionalColumnNameSet;
	}
	
	/**
	 * return the name set of mandatory additional features columns
	 * @return
	 */
	LinkedHashSet<DataTableColumnName> getMandatoryAdditionalColumnNameSet(){
		return VfTreeMandatoryNodeDataTableSchemaUtils.getMandatoryAdditionalFeatureColumnNameList();
	}
	
	
	/**
	 * for hashCode and equals methods, see {@link GraphVertexFeature#equals(Object)}
	 */
	//
}
