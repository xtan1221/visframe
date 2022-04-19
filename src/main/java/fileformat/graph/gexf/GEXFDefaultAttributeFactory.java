package fileformat.graph.gexf;

import rdb.table.data.DataTableColumnName;

public class GEXFDefaultAttributeFactory {
	public static DataTableColumnName ID_COLUMN_NAME = new DataTableColumnName("id");
	public static DataTableColumnName LABEL_COLUMN_NAME = new DataTableColumnName("label");
	
	public static DataTableColumnName EDGE_TYPE_COLUMN_NAME = new DataTableColumnName("type");
	public static DataTableColumnName SOURCE_COLUMN_NAME = new DataTableColumnName("source");
	public static DataTableColumnName SINK_COLUMN_NAME = new DataTableColumnName("target");
	public static DataTableColumnName WEIGHT_COLUMN_NAME = new DataTableColumnName("weight");
	
	//node attributes
	//id  ==> integer or string? primary key
	
	private static GEXFAttribute nodeIDAttribute;
	public static GEXFAttribute nodeIDAttribute(String dataTypeString) {
		//String title, String type, String defaultValueString
		if(nodeIDAttribute == null) {
			nodeIDAttribute = new GEXFAttribute("id", dataTypeString, null);
		}
		
		return nodeIDAttribute;
	}
	
	
	//label ==> descriptive information
	private static GEXFAttribute nodeLabelAttribute;
	public static GEXFAttribute nodeLabelAttribute() {
		//String title, String type, String defaultValueString
		if(nodeLabelAttribute == null) {
			nodeLabelAttribute = new GEXFAttribute("label", "string", null);
		}
		
		return nodeLabelAttribute;
	}
	
	
	//edge attributes
	//id ==> primary key
	public static GEXFAttribute edgeIDAttribute(String dataTypeString) {
		return nodeIDAttribute(dataTypeString);
	}
	
	
	//type ==> edge type, string, default = "undirected", possible values "directed", "undirected", "mutual"
	private static GEXFAttribute edgeTypeAttribute;
	public static GEXFAttribute edgeTypeAttribute() {
		//String title, String type, String defaultValueString
		if(edgeTypeAttribute == null) {
			edgeTypeAttribute = new GEXFAttribute("type", "string", "undirected");
		}
		return edgeTypeAttribute;
	}
	
	
	//label ==> descriptive information
	public static GEXFAttribute edgeLabelAttribute() {
		return nodeLabelAttribute();
	}
	
	//source ==> source node id, same data type with node.id
	private static GEXFAttribute edgeSourceNodeIDAttribute;
	public static GEXFAttribute edgeSourceNodeIDAttribute(String dataTypeString) {
		//String title, String type, String defaultValueString
		if(edgeSourceNodeIDAttribute == null) {
			edgeSourceNodeIDAttribute = new GEXFAttribute("source", dataTypeString, null);
		}
		return edgeSourceNodeIDAttribute;
	}
	//target ==> sink node id, same data type with node.id
	private static GEXFAttribute edgeSinkNodeIDAttribute;
	public static GEXFAttribute edgeSinkNodeIDAttribute(String dataTypeString) {
		//String title, String type, String defaultValueString
		if(edgeSinkNodeIDAttribute == null) {
			edgeSinkNodeIDAttribute = new GEXFAttribute("target", dataTypeString, null);
		}
		return edgeSinkNodeIDAttribute;
	}
	
	
	//weight ==> edge weight, double, default value = 1.0;
	private static GEXFAttribute edgeWeightAttribute;
	public static GEXFAttribute edgeWeightAttribute() {
		//String title, String type, String defaultValueString
		if(edgeWeightAttribute == null) {
			edgeWeightAttribute = new GEXFAttribute("weight", "double", "1.0");
		}
		return edgeWeightAttribute;
	}
	
	
	
}
