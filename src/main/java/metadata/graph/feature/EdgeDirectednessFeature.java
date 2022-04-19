package metadata.graph.feature;

import java.io.Serializable;
import java.util.Map;

import generic.graph.DirectedType;
import rdb.table.data.DataTableColumnName;

/**
 * contains the full set of information of the directed-ness of all edges defined by a {@link GraphEdgeFeature} of a {@link GraphDataMetadata}
 * 
 * @author tanxu
 *
 */
public class EdgeDirectednessFeature implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8729842075157653576L;
	
	//////////////////////////
	/**
	 * whether or not there is a column whose value indicate the directed type of edge
	 * 
	 */
	private final boolean hasDirectednessIndicatorColumn;
	
	/**
	 * the DataTableColumnName of the column of the data table schema of the corresponding edge record data that indicate the directed type;
	 * must be null if {@link #hasDirectednessIndicatorColumn} is false; must be non-null otherwise;
	 * if not null, this value should not be same with any of the DataTableColumnNames in the edge ID column set or source/sink node ID column set in the owner {@link GraphEdgeFeature}
	 */
	private final DataTableColumnName directednessIndicatorColumnName;
	
	/**
	 * default DirectedType;
	 * cannot be null
	 */
	private final DirectedType defaultDirectedType;
	
	/**
	 * map from string value of the {@link #directednessIndicatorColumnName} to the DirectedType it indicates
	 * must be non-null and non-empty if {@link #hasDirectednessIndicatorColumn} is true; must be null otherwise;
	 * 
	 * note that the string value is case-sensitive;
	 */
	private final Map<String, DirectedType> columnValueStringDirectedTypeMap;
	
	/**
	 * constructor
	 * @param hasDirectednessIndicatorColumn 
	 * @param directednessIndicatorColumnName non-null if hasDirectednessIndicatorColumn is true
	 * @param defaultDirectedType must be non-null
	 * @param columnValueStringDirectedTypeMap non-null and non-empty if hasDirectednessIndicatorColumn is true
	 */
	public EdgeDirectednessFeature(
			boolean hasDirectednessIndicatorColumn,
			DataTableColumnName directednessIndicatorColumnName,
			DirectedType defaultDirectedType,
			Map<String, DirectedType> columnValueStringDirectedTypeMap
			){
		if(defaultDirectedType==null)
			throw new IllegalArgumentException("given defaultDirectedType cannot be null!");
		if(hasDirectednessIndicatorColumn) {
			if(directednessIndicatorColumnName==null)
				throw new IllegalArgumentException("given directednessIndicatorColumnName cannot be null when hasDirectednessIndicatorColumn is true!");
			if(columnValueStringDirectedTypeMap==null||columnValueStringDirectedTypeMap.isEmpty())
				throw new IllegalArgumentException("given columnValueStringDirectedTypeMap cannot be null or empty when hasDirectednessIndicatorColumn is true!");
		}else {
			if(directednessIndicatorColumnName!=null)
				throw new IllegalArgumentException("given directednessIndicatorColumnName must be null when hasDirectednessIndicatorColumn is false!");
			if(columnValueStringDirectedTypeMap!=null)
				throw new IllegalArgumentException("given columnValueStringDirectedTypeMap must be null or empty when hasDirectednessIndicatorColumn is false!");
		}
		
		
		this.hasDirectednessIndicatorColumn = hasDirectednessIndicatorColumn;
		this.directednessIndicatorColumnName = directednessIndicatorColumnName;
		this.defaultDirectedType = defaultDirectedType;
		this.columnValueStringDirectedTypeMap = columnValueStringDirectedTypeMap;
	}

	/**
	 * @return the hasDirectednessIndicatorColumn
	 */
	public boolean hasDirectednessIndicatorColumn() {
		return hasDirectednessIndicatorColumn;
	}

	/**
	 * @return the directednessIndicatorColumnName
	 */
	public DataTableColumnName getDirectednessIndicatorColumnName() {
		return directednessIndicatorColumnName;
	}

	/**
	 * @return the defaultDirectedType
	 */
	public DirectedType getDefaultDirectedType() {
		return defaultDirectedType;
	}

	/**
	 * @return the columnValueStringDirectedTypeMap
	 */
	public Map<String, DirectedType> getColumnValueStringDirectedTypeMap() {
		return columnValueStringDirectedTypeMap;
	}

	
	////////////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnValueStringDirectedTypeMap == null) ? 0 : columnValueStringDirectedTypeMap.hashCode());
		result = prime * result + ((defaultDirectedType == null) ? 0 : defaultDirectedType.hashCode());
		result = prime * result
				+ ((directednessIndicatorColumnName == null) ? 0 : directednessIndicatorColumnName.hashCode());
		result = prime * result + (hasDirectednessIndicatorColumn ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof EdgeDirectednessFeature))
			return false;
		EdgeDirectednessFeature other = (EdgeDirectednessFeature) obj;
		if (columnValueStringDirectedTypeMap == null) {
			if (other.columnValueStringDirectedTypeMap != null)
				return false;
		} else if (!columnValueStringDirectedTypeMap.equals(other.columnValueStringDirectedTypeMap))
			return false;
		if (defaultDirectedType != other.defaultDirectedType)
			return false;
		if (directednessIndicatorColumnName == null) {
			if (other.directednessIndicatorColumnName != null)
				return false;
		} else if (!directednessIndicatorColumnName.equals(other.directednessIndicatorColumnName))
			return false;
		if (hasDirectednessIndicatorColumn != other.hasDirectednessIndicatorColumn)
			return false;
		return true;
	}
	
	
}
