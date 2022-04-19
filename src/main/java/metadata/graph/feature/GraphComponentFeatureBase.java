package metadata.graph.feature;

import java.io.Serializable;
import java.util.LinkedHashSet;

import rdb.table.data.DataTableColumnName;


public abstract class GraphComponentFeatureBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -55833855676254941L;
	
	/**
	 * id columns for the graph component, must be the same with the primary key attributes of the corresponding record data's table schema
	 */
	protected final LinkedHashSet<DataTableColumnName> IDColumnNameSet;
	
	/**
	 * non-id columns for this graph component; the same with the non-primary key (and non-RUID) columns of the corresponding record data table schema;
	 */
	private final LinkedHashSet<DataTableColumnName> additionalFeatureColumnNameSet;
	
	/**
	 * constructor
	 * @param IDColumnSet
	 * @param additionalFeatureColumnSet
	 * @param columnOrderListInTableSchema
	 */
	public GraphComponentFeatureBase(
			LinkedHashSet<DataTableColumnName> IDColumnNameSet, //order of the columns in the LinkedHashSet will be kept in the data table schema
			LinkedHashSet<DataTableColumnName> additionalFeatureColumnNameSet
//			List<SimpleName> columnOrderListInTableSchema
			) {
		//TODO validations
		//IDColumnSet cannot be empty
		//additionalFeatureColumnSet can be empty but not null;
		//additionalFeatureColumnSet cannot be overlapping with IDColumnSet;
		//columnOrderListInTableSchema must be non empty; note that columnOrderListInTableSchema may be a superset of union of IDColumnSet and additionalFeatureColumnSet
		//because for edge data, the id columns set may be non-overlapping with the source/sink node columns
		
		this.IDColumnNameSet = IDColumnNameSet;
		this.additionalFeatureColumnNameSet = additionalFeatureColumnNameSet;
//		this.columnOrderListInTableSchema = columnOrderListInTableSchema;
	}
	
	
	/**
	 * @return the iDColumnNameSet
	 */
	public LinkedHashSet<DataTableColumnName> getIDColumnNameSet() {
		return IDColumnNameSet;
	}
	
	/**
	 * @return the additionalFeatureColumnNameSet
	 */
	public LinkedHashSet<DataTableColumnName> getAdditionalFeatureColumnNameSet() {
		return additionalFeatureColumnNameSet;
	}
	
	
	/**
	 * return the full set of column names for features of this {@link GraphComponentFeatureBase} including those in the ID and those in the additional features;
	 * @return
	 */
	public LinkedHashSet<DataTableColumnName> getAllFeaturesColumnNameSet(){
		LinkedHashSet<DataTableColumnName> ret = new LinkedHashSet<>();
		ret.addAll(this.getIDColumnNameSet());
		ret.addAll(this.getAdditionalFeatureColumnNameSet());
		return ret;
	}


	////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((IDColumnNameSet == null) ? 0 : IDColumnNameSet.hashCode());
		result = prime * result
				+ ((additionalFeatureColumnNameSet == null) ? 0 : additionalFeatureColumnNameSet.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GraphComponentFeatureBase))
			return false;
		GraphComponentFeatureBase other = (GraphComponentFeatureBase) obj;
		if (IDColumnNameSet == null) {
			if (other.IDColumnNameSet != null)
				return false;
		} else if (!IDColumnNameSet.equals(other.IDColumnNameSet))
			return false;
		if (additionalFeatureColumnNameSet == null) {
			if (other.additionalFeatureColumnNameSet != null)
				return false;
		} else if (!additionalFeatureColumnNameSet.equals(other.additionalFeatureColumnNameSet))
			return false;
		return true;
	}
	
	
}
