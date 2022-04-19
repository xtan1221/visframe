package generic.graph.reader.filebased;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fileformat.graph.gexf.GEXFUserDefinedAttribute;
import utils.Pair;

public class GEXFFileParseUtils {
	private static Matcher MATCHER;
	//
	static Pattern GEXF_LINE_PATTERN_1 = Pattern.compile("^<[\\w]+\\s+(.+)>$");
	static Pattern GEXF_LINE_PATTERN_2 = Pattern.compile("^<[\\w]+\\s+(.+)/>$");
	static Pattern ATTRIBUTE_PATTERN = Pattern.compile("([^=]+)=\"(.+)\"");
	
	/**
	 * parse the attributes names and string value in the given line from a GEXF file (XML format);
	 * 
	 * <graph defaultedgetype="undirected" idtype="string" mode="static">
	 * <attvalue for="0" value="http://gephi.org"/>
	 * @param line
	 * @return
	 */
	public static Map<String,String> parseLineAttributeNameStringValueMap(String line){
		
		Map<String,String> ret = new LinkedHashMap<>();
		
		Matcher lineMatcher = GEXF_LINE_PATTERN_2.matcher(line);
		Matcher attributeMatcher;
		
		if(GEXF_LINE_PATTERN_2.matcher(line).matches()) {
			lineMatcher = GEXF_LINE_PATTERN_2.matcher(line);
		}else if(GEXF_LINE_PATTERN_1.matcher(line).matches()) {
			lineMatcher = GEXF_LINE_PATTERN_1.matcher(line);
		}else{
			throw new IllegalArgumentException("unrecognized GEXF line:"+line);
		}
		
		lineMatcher.matches();
		
		String content = lineMatcher.group(1);
		
		String[] attributes = content.split("\\s+");
		
		for(String attribute:attributes) {
			attributeMatcher = ATTRIBUTE_PATTERN.matcher(attribute);
			if(attributeMatcher.matches()) {
				ret.put(attributeMatcher.group(1), attributeMatcher.group(2));
			}else {
				throw new IllegalArgumentException("unrecognized GEXF attribute:"+attribute);
			}
		}
		
		
		return ret;
		
	
	}
	
	
	//////////////////////////////
	public static Pattern ATTRIBUTE_DECLARATION_LINE = Pattern.compile("^<attribute\\s+.*$");
	/**
	 * check if the given line is a starting line for an attribute declaration;
	 * 
	 * @param line
	 * @return
	 */
	public static boolean newAttributeDeclarationLine(String line) {
		return ATTRIBUTE_DECLARATION_LINE.matcher(line).matches();
	}
	
	
	////////////////////////////
	public static Pattern USER_DEFINED_ATTRIBUTE_PATTERN_1 = Pattern.compile("^<attribute\\s+id=\"(.+)\"\\s+title=\"(.+)\"\\s+type=\"(.+)\"/>$");//define the id, title and type
	public static Pattern USER_DEFINED_ATTRIBUTE_PATTERN_2 = Pattern.compile("^<attribute\\s+id=\"(.+)\"\\s+title=\"(.+)\"\\s+type=\"(.+)\"><default>(.*)</default></attribute>$");
	/**
	 * parse the GEXFUserDefinedAttribute from the given full attribute declaration string;
	 * two types regarding whether default value is defined;
	 * 
	 * <attribute id="1" title="indegree" type="float"/>
	 * <attribute id="2" title="frog" type="boolean"><default>true</default></attribute>
	 * @param attributeString
	 * @return
	 */
	public static GEXFUserDefinedAttribute parseAttribute(String attributeString) {
		
		attributeString = attributeString.trim();
		
		Matcher matcher;
		if((matcher=USER_DEFINED_ATTRIBUTE_PATTERN_1.matcher(attributeString)).matches()) {// no default value
			GEXFUserDefinedAttribute ret = new GEXFUserDefinedAttribute(
					matcher.group(2), 
					matcher.group(3), 
					null, 
					Integer.parseInt(matcher.group(1)));
			return ret;
		}else if((matcher=USER_DEFINED_ATTRIBUTE_PATTERN_2.matcher(attributeString)).matches()) {// no default value
			GEXFUserDefinedAttribute ret = new GEXFUserDefinedAttribute(
					matcher.group(2), 
					matcher.group(3), 
					matcher.group(4), 
					Integer.parseInt(matcher.group(1)));
			return ret;
		}else {
			throw new IllegalArgumentException("attributeString is not valid!");
		}
	}
	
	///////////////////////////////
	//
	//
	public static Pattern NO_ATTRIBUTE_NODE_STRING_PATTERN = Pattern.compile("^<node\\s+id=\"(.+)\"\\s+label=\"(.+)\"/>$");
	public static Pattern WITH_ATTRIBUTE_NODE_STRING_PATTERN = Pattern.compile("^<node\\s+id=\"(.+)\"\\s+label=\"(.+)\"><attvalues>(.+)</attvalues></node>$");
	
	public static Pattern ATTRIBUTE_VALUE_STRING = Pattern.compile("for=\"(\\d+)\"\\s+value=\"(.+)\""); //for="0" value="http://gephi.org"
	/**
	 * 
	 * parse the given node string;
	 * returned Pair's first map contains the GEXF default attributes "id" and "label" in the node header line;
	 * Pair's second map contains the user defined attributes id and string value for the node;
	 * 
	 * node string is either have no attributes(thus no "attvalues" section) or has attributes
	 * 
	 * <node id="0" label="Hello"/>   //only have header line with id and label attribute values;
	 * <node id="0" label="Gephi"><attvalues><attvalue for="0" value="http://gephi.org"/><attvalue for="1" value="1"/></attvalues></node>
	 */
	public static Pair<Map<String,String>,Map<Integer,String>> parseNode(String nodeString) {
		
		Map<String,String> defaultAttributeNameStringValueMap = new HashMap<>();
		Map<Integer,String> userDefinedAttributeIDStringValueMap = new HashMap<>();
		
		if((MATCHER = NO_ATTRIBUTE_NODE_STRING_PATTERN.matcher(nodeString)).matches()) {
			String idString = MATCHER.group(1);
			String labelString = MATCHER.group(2);
			
			defaultAttributeNameStringValueMap.put("id", idString);
			defaultAttributeNameStringValueMap.put("label", labelString);
			
			return new Pair<>(defaultAttributeNameStringValueMap, userDefinedAttributeIDStringValueMap);
			
		}else if((MATCHER = WITH_ATTRIBUTE_NODE_STRING_PATTERN.matcher(nodeString)).matches()) {
			String idString = MATCHER.group(1);
			String labelString = MATCHER.group(2);
			
			String attributesString = MATCHER.group(3);
			
			defaultAttributeNameStringValueMap.put("id", idString);
			defaultAttributeNameStringValueMap.put("label", labelString);
			
			
			/////////parse attributes
			String[] attributeSplit = attributesString.split("/><attvalue"); //<attvalue for="0" value="http://gephi.org"/><attvalue for="1" value="1"/>
			
			
			for(String split:attributeSplit) {
				MATCHER = ATTRIBUTE_VALUE_STRING.matcher(split);
				if(MATCHER.find()) {
					int attributeID = Integer.parseInt(MATCHER.group(1));
					String valueString = MATCHER.group(2);
					
					userDefinedAttributeIDStringValueMap.put(attributeID, valueString);
				}else {
					throw new IllegalArgumentException("unrecognized attributes string: "+split);
				}
			}
//			Matcher m = ATTRIBUTE_VALUE_STRING.matcher(attributesString);
//			
//			m.results().forEach(e->{
//				
//				int attributeID = Integer.parseInt(e.group(1));
//				String valueString = e.group(2);
//				
//				userDefinedAttributeIDStringValueMap.put(attributeID, valueString);
//			});
			
			return new Pair<>(defaultAttributeNameStringValueMap, userDefinedAttributeIDStringValueMap);
			
		}else {
			throw new IllegalArgumentException("given node string is invalid!");
		}
		
		
		
	}
	
	
	private final static Pattern SIMPLE_EDGE_PATTERN = Pattern.compile("<edge\\s+(.+)/>"); //group1 = all attribute name value string
	private final static Pattern ID_PATTERN = Pattern.compile("id=\"([^\"]+)\"");
	private final static Pattern SOURCE_PATTERN = Pattern.compile("source=\"([^\"]+)\"");
	private final static Pattern TARGET_PATTERN = Pattern.compile("target=\"([^\"]+)\"");
	private final static Pattern WEIGHT_PATTERN = Pattern.compile("weight=\"([^\"]+)\"");
	private final static Pattern TYPE_PATTERN = Pattern.compile("type=\"([^\"]+)\"");
	
	
	/**
	 * the input edge string can only contain GEXF default attributes;
	 * 
	 * "id", "source", "target" attributes are mandatory and must be present;
	 * "weight", "type" attributes are optional;
	 * @param edgeString
	 */
	public static Map<String,String> parseSimpleEdge(String edgeString) {
		edgeString = edgeString.trim();
		
		Map<String,String> ret = new HashMap<>();
		
		
		MATCHER = ID_PATTERN.matcher(edgeString);
		String idString;
		if(MATCHER.find()) {
			idString = MATCHER.group(1);
			ret.put("id", idString);
		}else {
			throw new IllegalArgumentException("no id attribute found!");
		}
		
		
		MATCHER = SOURCE_PATTERN.matcher(edgeString);		
		String sourceIDString;
		if(MATCHER.find()) {
			sourceIDString = MATCHER.group(1);
			ret.put("source", sourceIDString);
		}else {
			throw new IllegalArgumentException("no sourceIDString attribute found!");
		}
		
		MATCHER = TARGET_PATTERN.matcher(edgeString);
		
		String targetIDString;
		if(MATCHER.find()) {
			targetIDString = MATCHER.group(1);
			ret.put("target", targetIDString);
		}else {
			throw new IllegalArgumentException("no targetIDString attribute found!");
		}
		
		///////////////////////////
		MATCHER = TYPE_PATTERN.matcher(edgeString);		
		String typeString;
		if(MATCHER.find()) {
			typeString = MATCHER.group(1);
			ret.put("type", typeString);
		}
		
		MATCHER = WEIGHT_PATTERN.matcher(edgeString);		
		String weightString;
		if(MATCHER.find()) {
			weightString = MATCHER.group(1);
			ret.put("weight", weightString);
		}
		
		return ret;
	}
	
}
