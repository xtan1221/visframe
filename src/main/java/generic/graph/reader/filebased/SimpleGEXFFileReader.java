package generic.graph.reader.filebased;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.VisframeException;
import fileformat.graph.gexf.GEXFAttributeDataTypeUtils;
import fileformat.graph.gexf.GEXFDefaultAttributeFactory;
import fileformat.graph.gexf.GEXFUserDefinedAttribute;
import generic.graph.VfGraphEdge;
import generic.graph.VfGraphVertex;
import metadata.graph.feature.EdgeDirectednessFeature;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
import utils.Pair;


/**
 * read a graph data from a Simple GEXF data file;
 * 		simple GEXF: static and not hierarchical or phylogeny data
 * 
 * note that the node and edge related attributes should be parsed from the data file;
 * 
 * also in GEXF, the edges element must be declared after the nodes element.
 * 
 * node record data ID attributes: "id"
 * edge record data ID attributes: 
 * 		1. "id" 
 * 		2. if there are any edge of type "mutual" found in the graph, the source/target node id should also be added to the edge id attribute set;
 * 
 * it seems that GEXF does not allow edge entities to have user-defined attributes?
 * all the user-defined attributes in example dataset have class="node", thus defined for node?;
 * 
 * thus, current implementation only consider edge entities with the default attributes;
 * 
 * if an edge has no weight attributes, assume its value is null;
 * 
 * all non-default user-defined attributes should be explicitly defined in the attributes section!;
 * ==============================================================================
 * for graph with directed and/or undirected type edges only, the edge id attribute set only contains the "id" and the edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets is true;
 * 
 * for graph with at least one mutual type edge, the edge id attribute set contains the "id", "source" and "target" and the edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets is false;
 * 
 * @author tanxu
 * 
 */
public class SimpleGEXFFileReader extends GraphFileReader {
	public static int MAX_BATCH_SIZE = 1000;
	
	///////////
	private BufferedReader bufferedReader;
	
	
	/////////////////////preprocessing related fields
	private boolean preprocessingDone = false;;

	private Map<Integer, GEXFUserDefinedAttribute> userDefinedNodeAttributeIDMap;
	
	//information defined in graph section header line ; more maybe added in future
	/**
	 * default edge type, "undirected", "directed", "mutual";
	 * If no direction is specied when an edge is declared, the default direction defaultedgetype is applied to the edge.
	 */
	private String defaultEdgeType;
	
	/**
	 * data type of the node and edge id;
	 * either "string" or "integer";
	 * 
	 */
	private String idType;
	/**
	 * mode of the graph, "static" or "dynamic";
	 * 
	 * note that for simple GEXF, mode must be "static", thus if the mode is detected to be "dynammic", abort this reader;
	 * 
	 */
	private String mode;
	
	/**
	 * whether or not any edge of type == "mutual" is found in the graph; 
	 * if true, two directed VfGraphEdge will be created with opposite source and sink nodes; 
	 * 		also the edge ID attribute set will be composed of (edge id, source node id and target node id)
	 * if false, the edge ID attribute set will be composed of edge id attribute only;
	 */
	private Boolean containingMutualEdge;
	
	
	/////////////////////////////////////////graph node and edge parsing related fields
	private boolean nodeSectionStarted = false;
	private String currentNodeString;
	private Queue<VfGraphVertex> currentBatchOfVfGraphVertex;
	private boolean nodeSectionFullyParsed;
	
//	private String currentEdgeString;
	private Queue<VfGraphEdge> currentBatchOfVfGraphEdge;
	private boolean edgeSectionFullyParsed;
	private boolean edgeWeightAttributeAdded = false;
	
	
	/**
	 * constructor
	 * @param dataFilePath
	 * @throws IOException 
	 */
	public SimpleGEXFFileReader(Path dataFilePath) throws IOException {
		super(dataFilePath);
		
		//
		this.initialize();
	}
	
	
	/**
	 * initialize the file reader object and pre-processing the data file
	 * 
	 * 1. identify user-defined attributes;
	 * 
	 * then read the file until the first vertex is reached; 
	 * @throws IOException 
	 */
	@Override
	public void initialize() throws IOException {
		//pre-processing
		if(!this.preprocessingDone) {
			this.preprocessing();
		}
		
		////////////////////////////
		//set up for graph node and edge parsing;
		this.vertexDone = false;
		this.edgeDone = false;
		
		this.nodeSectionStarted = false;
		this.currentNodeString = "";
		this.currentBatchOfVfGraphVertex = new LinkedList<>();
		this.nodeSectionFullyParsed = false;
		
//		this.currentEdgeString = "";
		this.currentBatchOfVfGraphEdge = new LinkedList<>();
		this.edgeSectionFullyParsed = false;
		
		//reset the bufferedReader
		this.bufferedReader = new BufferedReader(new FileReader(this.getDataFilePath().toFile()));
		
		//
		this.parseNextVertexBatch();
	}
	
	/**
	 * pre-processing;
	 * 
	 * check if any edge of type mutual is found in the graph;
	 * also find out the id type for edge and node;
	 * 
	 * then set up the fields for this reader accordingly before parsing the graph content;
	 * 
	 * @throws IOException 
	 */
	private void preprocessing() throws IOException {
		this.bufferedReader = new BufferedReader(new FileReader(this.getDataFilePath().toFile()));
		
		this.parseGraphSectionHeader();
		
		this.parseAttributesDeclarationSection();
		
		this.parseEdgeAndIDType();
		
//		protected Map<DataTableColumnName, DataTableColumn> vertexAttributeColNameMap = new HashMap<>();
//		protected Map<DataTableColumnName, DataTableColumn> edgeAttributeColNameMap = new HashMap<>();
//		
//		////////////information that must be extracted when the GraphFileReader is done parsing the data file;
//		protected LinkedHashSet<DataTableColumnName> vertexIDColumnNameSet = new LinkedHashSet<>();
//		protected LinkedHashSet<DataTableColumnName> vertexAdditionalFeatureColumnNameSet = new LinkedHashSet<>();
//		
//		protected LinkedHashSet<DataTableColumnName> edgeIDColumnNameSet = new LinkedHashSet<>();
//		protected LinkedHashSet<DataTableColumnName> edgeAdditionalFeatureColumnNameSet = new LinkedHashSet<>();
//		
//		protected boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets;
//		protected LinkedHashMap<DataTableColumnName, DataTableColumnName> sourceNodeIDColumnNameEdgeColumnNameMap = new LinkedHashMap<>();
//		protected LinkedHashMap<DataTableColumnName, DataTableColumnName> sinkNodeIDColumnNameEdgeColumnNameMap = new LinkedHashMap<>();
		
		//add id related columns
		DataTableColumn nodeIDCol;
		DataTableColumn edgeIDCol;
		DataTableColumn edgeSourceNodeIDCol;
		DataTableColumn edgeSinkNodeIDCol;
		if(this.idType == "integer") { //
			nodeIDCol = GEXFDefaultAttributeFactory.nodeIDAttribute(GEXFAttributeDataTypeUtils.integerType()).toDataTableColumn(true);
			this.vertexAttributeColNameMap.put(nodeIDCol.getName(), nodeIDCol);
			
			edgeIDCol = GEXFDefaultAttributeFactory.edgeIDAttribute(GEXFAttributeDataTypeUtils.integerType()).toDataTableColumn(true);
			this.edgeAttributeColNameMap.put(edgeIDCol.getName(), edgeIDCol);
			
			edgeSourceNodeIDCol = GEXFDefaultAttributeFactory.edgeSourceNodeIDAttribute(GEXFAttributeDataTypeUtils.integerType()).toDataTableColumn(this.containingMutualEdge);
			this.edgeAttributeColNameMap.put(edgeSourceNodeIDCol.getName(), edgeSourceNodeIDCol);
			
			edgeSinkNodeIDCol = GEXFDefaultAttributeFactory.edgeSinkNodeIDAttribute(GEXFAttributeDataTypeUtils.integerType()).toDataTableColumn(this.containingMutualEdge);
			this.edgeAttributeColNameMap.put(edgeSinkNodeIDCol.getName(), edgeSinkNodeIDCol);
			
		}else {//"string"
			nodeIDCol = GEXFDefaultAttributeFactory.nodeIDAttribute(GEXFAttributeDataTypeUtils.stringType()).toDataTableColumn(true);
			this.vertexAttributeColNameMap.put(nodeIDCol.getName(), nodeIDCol);
			
			edgeIDCol = GEXFDefaultAttributeFactory.edgeIDAttribute(GEXFAttributeDataTypeUtils.stringType()).toDataTableColumn(true);
			this.edgeAttributeColNameMap.put(edgeIDCol.getName(), edgeIDCol);
			
			edgeSourceNodeIDCol = GEXFDefaultAttributeFactory.edgeSourceNodeIDAttribute(GEXFAttributeDataTypeUtils.stringType()).toDataTableColumn(this.containingMutualEdge);
			this.edgeAttributeColNameMap.put(edgeSourceNodeIDCol.getName(), edgeSourceNodeIDCol);
			
			edgeSinkNodeIDCol = GEXFDefaultAttributeFactory.edgeSinkNodeIDAttribute(GEXFAttributeDataTypeUtils.stringType()).toDataTableColumn(this.containingMutualEdge);
			this.edgeAttributeColNameMap.put(edgeSinkNodeIDCol.getName(), edgeSinkNodeIDCol);
		}
		
		////
		this.vertexIDColumnNameSet.add(nodeIDCol.getName());
		this.edgeIDColumnNameSet.add(edgeIDCol.getName());
		
		if(this.containingMutualEdge) {
			this.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = false;
			this.edgeIDColumnNameSet.add(edgeSourceNodeIDCol.getName());
			this.edgeIDColumnNameSet.add(edgeSinkNodeIDCol.getName());
		}else {
			this.edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = true;
		}
		
		//////////
		//add label attribute as additional attribute
		this.vertexAttributeColNameMap.put(GEXFDefaultAttributeFactory.nodeLabelAttribute().getDataTableColumnName(), GEXFDefaultAttributeFactory.nodeLabelAttribute().toDataTableColumn(false));
		this.vertexAdditionalFeatureColumnNameSet.add(GEXFDefaultAttributeFactory.nodeLabelAttribute().getDataTableColumnName());
		this.edgeAttributeColNameMap.put(GEXFDefaultAttributeFactory.edgeLabelAttribute().getDataTableColumnName(), GEXFDefaultAttributeFactory.edgeLabelAttribute().toDataTableColumn(false));
		this.edgeAdditionalFeatureColumnNameSet.add(GEXFDefaultAttributeFactory.edgeLabelAttribute().getDataTableColumnName());
		
		//
		this.vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap.put(nodeIDCol.getName(),edgeSourceNodeIDCol.getName());
		this.vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap.put(nodeIDCol.getName(),edgeSinkNodeIDCol.getName());
		
		
		this.preprocessingDone = true;
	}
	
	/**
	 * Graphs in GEXF are mixed, in other words, they can contain directed and
	undirected edges at the same time. If no direction is specified when an edge is
	declared, the default direction defaultedgetype is applied to the edge. If you know
	what kind of edges are stored, you may interpret the mixed graph as a directed
	or an undirected graph at your own risks.
	The default direction is declared as the optional XML-attribute defaulted-
	getype of the graph element. The three possible values for this XML-attribute
	are directed, undirected and mutual. Note that the default direction is optional
	and would be assumed as undirected.
	The optional XML-attribute mode set the kind of network: static or dynamic.
	Last one provides time support (see the section 5 on Dynamics). Static mode
	is assumed by default.
	 * @throws IOException 
	 */
	private void parseGraphSectionHeader() throws IOException {
		String line;
		while((line = this.bufferedReader.readLine())!=null) {
			line = line.trim();
			if(line.startsWith("<graph")) {
				Map<String,String> attributeNameStringValueMap = GEXFFileParseUtils.parseLineAttributeNameStringValueMap(line);
				
				for(String key:attributeNameStringValueMap.keySet()) {
					if(key.equals("defaultedgetype")) {
						this.defaultEdgeType = attributeNameStringValueMap.get(key);
					}else if(key.equals("idtype")) {
						this.idType = attributeNameStringValueMap.get(key);
					}else if(key.equals("mode")) {
						this.mode = attributeNameStringValueMap.get(key);
					}else {
						throw new UnsupportedOperationException("unrecognized attribute name in graph header line "+key);
					}
				}
				
				if(this.mode==null||this.mode.contentEquals("dynamic")) {
					throw new VisframeException("dynamic graph found for SimpleGEXFFileReader!");
				}
				
				return;
			}
			
			if(line.startsWith("<attributes")) {
				throw new VisframeException("Graph header line is missing in the GEXF data file!");
			}
		}
		
		//default edge type is "undirected" based on GEXF primer
		if(this.defaultEdgeType==null) {
			this.defaultEdgeType="undirected";
		}
		
	}
	
	/**
	 * parse the user-defined attributes in the attributes section in the GEXF data file;
	 * 
	 * note that it is possible that there is no attribute declaration section in a GEXF data file;
	 * 
	 * until the node section is reached <nodes ...
	 * 
	 * 
	 * it seems that GEXF only allow user-defined attributes for nodes, not for edges;
	 * 		<attributes class="node" mode="static"> ...
	 * 
	 * @throws IOException 
	 */
	private void parseAttributesDeclarationSection() throws IOException {
		this.userDefinedNodeAttributeIDMap = new HashMap<>();
		
		String currentAttributeString = "";//the full attributes string between the <attributes ... /attributes>
		String line;
		while((line = this.bufferedReader.readLine())!=null) {
			line = line.trim();
			
			if(line.startsWith("<attributes")) {//skip the attributes section starting line
				continue;
			}
			
			if(GEXFFileParseUtils.newAttributeDeclarationLine(line)) {//start a new attribute declaration
				if(currentAttributeString.isEmpty()) {
					currentAttributeString = line;
				}else {
					GEXFUserDefinedAttribute attribute = GEXFFileParseUtils.parseAttribute(currentAttributeString);
					this.userDefinedNodeAttributeIDMap.put(attribute.getAttributeID(),attribute);
					currentAttributeString = line;
				}
			}else if(line.startsWith("</attributes>")) {//end of attributes declaration section, parse the last declared attribute
				if(!currentAttributeString.isEmpty()) {
					GEXFUserDefinedAttribute attribute = GEXFFileParseUtils.parseAttribute(currentAttributeString);
					this.userDefinedNodeAttributeIDMap.put(attribute.getAttributeID(),attribute);
				}
				
				return;
				
			}else{//inside the attributes declaration section
				currentAttributeString = currentAttributeString.concat(line);
			}
			
		}
	}
	
	
	/**
	 * parse the "id" attribute data type and whether there is at least one edge of mutual edge type;
	 * 
	 * @throws IOException
	 */
	private void parseEdgeAndIDType() throws IOException {
		String line;
		while((line = this.bufferedReader.readLine())!=null) {
			line = line.trim();
//			System.out.println(line);
			if(this.idType==null) {//id type is not set in the graph header line, need to find it out in the node section
				if(line.startsWith("<node")) {
					Pattern p = Pattern.compile("id=\"([^\\s]+)\"");
					Matcher m = p.matcher(line);
					m.find();
					String idStringValue = m.group(1);
					try{
						//integer type
						Integer.parseInt(idStringValue);
						this.idType = "integer";					
					}catch(NumberFormatException e) { //string type
						this.idType = "string";
					}
					
				}
			}
			
			if(line.startsWith("<edge")) {
				if(line.contains("type=\"mutual\"")) {
					this.containingMutualEdge = true;
					return;
				}
			}
		}
		
		this.containingMutualEdge = false;
	}
	
	
	////////////////////////////////////////////
	/**
	 * continue parsing the file for vertex in the nodes section until
	 * 1. the batch size is reaching the limit or
	 * 2. the edge section is reached
	 * @throws IOException 
	 */
	private void parseNextVertexBatch() throws IOException {
		
		if(this.nodeSectionFullyParsed) {
			throw new VisframeException("node Section has been Fully Parsed!");
		}
		
		String line;
		
		while((line = this.bufferedReader.readLine())!=null) {
			line = line.trim();
			
			if(line.startsWith("<nodes")) {//header line of nodes section
				nodeSectionStarted = true;
				continue;
			}
			
			if(nodeSectionStarted) {//
				if(line.startsWith("</node>")) {//end of a node
					this.currentNodeString = this.currentNodeString.concat(line);
					
					Pair<Map<String,String>,Map<Integer,String>> parsedVertexContent = GEXFFileParseUtils.parseNode(currentNodeString);
					
					Map<DataTableColumnName, String> IDAttributeNameStringValueMap = new HashMap<>();
					Map<DataTableColumnName, String> additionalAttributeNameStringValueMap = new HashMap<>();
					
					IDAttributeNameStringValueMap.put(GEXFDefaultAttributeFactory.ID_COLUMN_NAME, parsedVertexContent.getFirst().get("id"));
					additionalAttributeNameStringValueMap.put(GEXFDefaultAttributeFactory.LABEL_COLUMN_NAME, parsedVertexContent.getFirst().get("label"));
					
					for(Integer attributeID:parsedVertexContent.getSecond().keySet()) {
						//add the user defined attribute to the vertex attribute set;
						DataTableColumn userDefinedCol = this.userDefinedNodeAttributeIDMap.get(attributeID).toDataTableColumn(false);
						this.vertexAttributeColNameMap.put(userDefinedCol.getName(),userDefinedCol);
						this.vertexAdditionalFeatureColumnNameSet.add(userDefinedCol.getName());
						
						additionalAttributeNameStringValueMap.put(
								this.userDefinedNodeAttributeIDMap.get(attributeID).getDataTableColumnName(), 
								parsedVertexContent.getSecond().get(attributeID));
						
					}
					
					VfGraphVertex vertex = new VfGraphVertex(IDAttributeNameStringValueMap, additionalAttributeNameStringValueMap);
					
					this.currentBatchOfVfGraphVertex.add(vertex);
					
					////////////////
					//reset currentNodeString
					this.currentNodeString = "";
					
					if(this.currentBatchOfVfGraphEdge.size()>MAX_BATCH_SIZE) {
						return;
					}
					
				}else if(line.startsWith("<edges")) {//reaches the start of edges section; the full nodes section is finished
					this.currentNodeString = "";
					this.nodeSectionFullyParsed = true;
					return;
				}else {//inside a node
					this.currentNodeString = this.currentNodeString.concat(line);
				}
			}
		}
		
	}
	
	/**
	 * parse the next vertex into a {@link VfGraphVertex} instance and return it;
	 * if no more vertex remained in the data file, return null and set {@link #vertexDone} to true;
	 * 
	 * also add discovered attributes of nodes to the {@link #vertexAttributeColNameMap} with {@link #addVertexAttribute(DataTableColumn)};
	 * @throws IOException 
	 */
	@Override
	public VfGraphVertex nextVertex() throws IOException {
		if(this.vertexDone) {
			throw new UnsupportedOperationException("cannot get next vertex when vertexDone is true!");
		}
		
		
		if(this.currentBatchOfVfGraphVertex.isEmpty()) {
			if(this.nodeSectionFullyParsed) {
				this.vertexDone = true;
				return null;
			}else {
				this.parseNextVertexBatch();
			}
		}
		
		return this.currentBatchOfVfGraphVertex.poll();
	}
	
	
	/**
	 * continue parsing the file for edge in the edges section until
	 * 1. the batch size is reaching the limit or
	 * 2. the edge section is reached
	 * 
	 * note that the default attributes for edge includes "id", "source", "target", "weight", "type";
	 * in every edge, "id", "source", "target" must be explicitly set while "weight", "type" can be absent, in which case the default value will be used;
	 * 
	 * two major types of edge data string are possible:
	 * 1. no user-defined attributes:
	 * <edge id="0" source="0" target="1" type="undirected"/>
	 * 2. has user-defined attributes:????? not validated
	 * @throws IOException 
	 * 
	 * 
	 */
	private void parseNextEdgeBatch() throws IOException {
		if(this.edgeSectionFullyParsed) {
			throw new VisframeException("edge Section has been Fully Parsed!");
		}
		
		String line;
		while((line = this.bufferedReader.readLine())!=null) {
			line = line.trim();
			
			
			if(line.startsWith("<edges")) {//header line of edges section
				continue;
			}
			
			if(line.startsWith("<edge")) {//an edge line
				Map<String,String> result = GEXFFileParseUtils.parseSimpleEdge(line);
				
				Map<DataTableColumnName, String> IDAttributeNameStringValueMap = new HashMap<>();
				boolean edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets = !this.containingMutualEdge;
				Map<DataTableColumnName,String> sourceVertexIDAttributeNameStringValueMap = new LinkedHashMap<>();
				Map<DataTableColumnName,String> sinkVertexIDAttributeNameStringValueMap = new LinkedHashMap<>();
				Map<DataTableColumnName, String> additionalAttributeNameStringValueMap = new LinkedHashMap<>();
				
				IDAttributeNameStringValueMap.put(GEXFDefaultAttributeFactory.ID_COLUMN_NAME, result.get("id"));
				if(this.containingMutualEdge) {
					IDAttributeNameStringValueMap.put(GEXFDefaultAttributeFactory.SOURCE_COLUMN_NAME, result.get("source"));
					IDAttributeNameStringValueMap.put(GEXFDefaultAttributeFactory.SINK_COLUMN_NAME, result.get("target"));
				}
				sourceVertexIDAttributeNameStringValueMap.put(GEXFDefaultAttributeFactory.SOURCE_COLUMN_NAME, result.get("source"));
				sinkVertexIDAttributeNameStringValueMap.put(GEXFDefaultAttributeFactory.SINK_COLUMN_NAME, result.get("target"));
				
				///weight
				if(result.containsKey("weight")) {//has weight attribute
					if(!edgeWeightAttributeAdded) {
						DataTableColumn edgeWeightCol = GEXFDefaultAttributeFactory.edgeWeightAttribute().toDataTableColumn(false);
						this.edgeAttributeColNameMap.put(edgeWeightCol.getName(), edgeWeightCol);
						this.edgeAdditionalFeatureColumnNameSet.add(edgeWeightCol.getName());
					}
					additionalAttributeNameStringValueMap.put(GEXFDefaultAttributeFactory.WEIGHT_COLUMN_NAME, result.get("weight"));
				}else {//if not set, do not add weight attribute
					//
				}
				
				//////edge directed type
				String edgeType;
				if(result.containsKey("type")) {
					edgeType = result.get("type");
				}else {//use default value
					if(this.defaultEdgeType==null) {
						edgeType = this.defaultEdgeType;
					}else {
						throw new VisframeException("no edge type is defined in edge string nor default edge type is defined in graph header line;");
					}
				}
				
				if(edgeType.contentEquals("undirected")) {//add a single undirected edge
					VfGraphEdge edge = new VfGraphEdge(
							IDAttributeNameStringValueMap, 
							edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets, 
							this.vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
							sourceVertexIDAttributeNameStringValueMap, 
							this.vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap,
							sinkVertexIDAttributeNameStringValueMap,
							additionalAttributeNameStringValueMap, 
							false);
//					
					this.currentBatchOfVfGraphEdge.add(edge);
					
				}else if(edgeType.contentEquals("directed")) {//add a single directed edges
					VfGraphEdge edge = new VfGraphEdge(
							IDAttributeNameStringValueMap, 
							edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets, 
							this.vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
							sourceVertexIDAttributeNameStringValueMap, 
							this.vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap,
							sinkVertexIDAttributeNameStringValueMap,
							additionalAttributeNameStringValueMap, 
							true);
//					
					this.currentBatchOfVfGraphEdge.add(edge);
					
				}else if(edgeType.contentEquals("mutual")) {// add two directed edges
					
					VfGraphEdge edge1 = new VfGraphEdge(
							IDAttributeNameStringValueMap, 
							edgeIDColumnSetDisjointWithSourceAndSinkNodeIDColumnSets,
							this.vertexIDAttributeNameEdgeSourceVertexIDAttributeNameMap,
							sourceVertexIDAttributeNameStringValueMap, 
							this.vertexIDAttributeNameEdgeSinkVertexIDAttributeNameMap,
							sinkVertexIDAttributeNameStringValueMap,
							additionalAttributeNameStringValueMap, 
							true);
					
					VfGraphEdge edge2 = edge1.oppositeEdge();
					
					this.currentBatchOfVfGraphEdge.add(edge1);
					this.currentBatchOfVfGraphEdge.add(edge2);
					
				}else {
					throw new IllegalArgumentException("unrecognized edge type string:"+result.get("type"));
				}
				
				if(this.currentBatchOfVfGraphEdge.size()>MAX_BATCH_SIZE) {
					return;
				}
				
			}else if(line.startsWith("</edges>")) {//end of edges section reached
				this.edgeSectionFullyParsed = true;
				return;
			}
			
		}
	}
	
	
	/**
	 * if {@link #vertexDone} is false, throw {@link UnsupportedOperationException};
	 * else, parse the next edge into a {@link VfGraphEdge} instance and return it; 
	 * 
	 * if no more edge remained in the data file, return null and set {@link #edgeDone} to true and close the {@link #reader};
	 * also add discovered attributes of nodes to the {@link #edgeAttributeColNameMap} with {@link #addEdgeAttribute(DataTableColumn)};
	 * @throws IOException 
	 */
	@Override
	public VfGraphEdge nextEdge() throws IOException {
		if(!this.vertexDone) {
			throw new UnsupportedOperationException("cannot get next edge when vertexDone is false!");
		}
		
		
		if(this.edgeDone) {
			throw new UnsupportedOperationException("cannot get next edge when edgeDone is true!");
		}
		
		if(this.currentBatchOfVfGraphEdge.isEmpty()) {
			if(this.edgeSectionFullyParsed) {
				this.edgeDone = true;
				//close the reader
				this.bufferedReader.close();
				return null;
			}else {
				this.parseNextEdgeBatch();
			}
		}
		
		return this.currentBatchOfVfGraphEdge.poll();
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void restart() throws IOException {
		this.initialize();
	}

	
	@Override
	public EdgeDirectednessFeature getEdgeDirectednessFeature() {
		// TODO Auto-generated method stub
		return null;
	}

}
