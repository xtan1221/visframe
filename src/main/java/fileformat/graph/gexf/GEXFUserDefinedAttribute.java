package fileformat.graph.gexf;


/**
 * a user-defined attribute in GEXF;
 * 	see the Attributes Specification section in the GEXF 1.2draft Primer;
 * 
 * note that options (it defines the available values, separated by a pipe.) of an Attribute is ignored;
 * 
 * @author tanxu
 *
 */
public class GEXFUserDefinedAttribute extends GEXFAttribute{

	private final int attributeID; //unique id distinguish this attribute from other user-defined attributes in the same GEXF file
	
	/**
	 * constructor
	 * @param title
	 * @param type
	 * @param defaultValueString
	 * @param id
	 */
	public GEXFUserDefinedAttribute(
			String title, String type, String defaultValueString,
			int id) {
		super(title, type, defaultValueString);
		// TODO Auto-generated constructor stub
		this.attributeID = id;
	}
	
	
	public int getAttributeID() {
		return attributeID;
	}
	
}
