/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generic.tree.reader.filebased.newick;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fileformat.vftree.VfTreeDataFileFormatType;
import utils.Pair;
import utils.StringUtils;
import utils.Triple;

/**
 * key word: 
 * Branch label: including edge length or bootstrap value or none or both
 * Node label: normally the name of the node, can be empty
 * 
 * @author tanxu
 */
public class SimpleNewickParserUtils {
    /**
     * delimit first level nodes in the same branch
     */
    private final static String COMMA = ",";
    /**
     * tag for start of an internal node/branch
     */
    private final static String LEFT_PARENTHESIS = "(";
    /**
     * tag for end of an internal node/branch
     */
    private final static String RIGHT_PARENTHESIS = ")";
    /**
     * end of newick tree string
     */
    private final static String SEMICOLON=";";
    /**
     * branch length tag
     */
    private final static String COLON=":";
    
    /**
     * 
     */
    private final static String NON_RESERVED_CHARACTERS = "[^\\,\\(\\)\\;\\:]";
    /**
     * tag for start of bootstrap value
     */
    private final static String LEFT_SQUARE_BRACKET="[";
    /**
     * tag for end of bootstrap value
     */
    private final static String RIGHT_SQUARE_BRACKET="]";
    
    private final static String WHITE_SPACES="\\s+";
    
    private final static String EMPTY="";
    
    private final static String UNDERSCORE="_";
    
    
    
    
    
    /**
     * non-empty node label string and branch label string;
     * 
     * leaf_node_label:leaf_branch_label
     * 
     * group 1 = leaf_node_label; group 2 = leaf_branch_label
     */
    private final static Pattern FULL_LEAF = Pattern.compile(
                "([^\\,\\(\\)\\;\\:]+)" //group 1
                +"\\:"
                +"([^\\,\\(\\)\\;\\:]+)" //group 2
    );
    
    /**
     * non-empty node label string and empty branch label string
     * leaf_node_label
     * 
     * group 0 = leaf_node_label
     */
    private final static Pattern LEAF_NODE_LABEL = Pattern.compile("[^\\,\\(\\)\\;\\:]+");
    /**
     * empty node label string and non-empty branch label string
     * :leaf_branch_label
     * group 1 = leaf_branch_label
     */
    private final static Pattern LEAF_BRANCH_LABEL = Pattern.compile("\\:([^\\,\\(\\)\\;\\:]+)");
    
    
    /**
     * (raccoon:19.19959,bear:6.80041)mammal:0.84600
     * (cn1,cn2,...)node_label:branch_label
     * 
     */
    private final static Pattern SIMPLE_NEWICK_1_FULL_INTERNAL_NODE = Pattern.compile(
                "\\((.+)\\)"   //children newick strings with outer parenthsis removed = group 1
                + "([^\\,\\(\\)\\;\\:]+)"  //node_label = group 2
                + "\\:"  //colon
                + "([^\\,\\(\\)\\;\\:]+)" //branch_label = group 3
    );
    
    
    /**
     * (cn1,cn2,...)node_label
     */
    private final static Pattern SIMPLE_NEWICK_1_INTERNAL_NODE_LABEL_ONLY = Pattern.compile(
                "\\((.+)\\)"   //children newick strings with outer parenthsis removed = group 1
                + "([^\\,\\(\\)\\;\\:]+)"  //node_label = group 2
    );
    
    /**
     * (cn1,cn2,...):branch_label
     */
    private final static Pattern SIMPLE_NEWICK_1_INTERNAL_BRANCH_LABEL_ONLY = Pattern.compile(
                "\\((.+)\\)"   //children newick strings with outer parenthsis removed = group 1
                + "\\:"  //colon
                + "([^\\,\\(\\)\\;\\:]+)" //branch_label = group 2
    );
    
    /**
     * not containing any node label nor branch label;
     * (cn1,cn2,...)
     */
    private final static Pattern EMPTY_INTERNAL_NODE = Pattern.compile(
                "\\((.+)\\)"   //children newick strings with outer parenthsis removed = group 1
    );
    
    
    /**
     * simple validation of a newickstring before parsing;
     * 1. equal number of left and right parenthesis
     * 2. for a complete newick string, it must start and end with a paired parenthesis and/or a node label and/or branch label for the root node
     *      a. (); //a tree with a root node with no node label string ------- explicit root;
     *      b. ()node_label; //a tree with a root node with a node label string -------- explicit root
     *      c. ()node_label:branch_label; //a tree with a node with node and branch label string which is the only child of the root node with no node or branch label string-----implicit root node
     *      d. ():branch_label;//a tree with a node with branch label string which is the only child of the root node with no node or branch label string-----implicit root node
     * 
     * 3. there should not be any comma after the last right parenthesis!
     * 		...,leaf_node_label;
     * 
     * @param fullNewickString
     * @return 
     */
    public static void validateFullNewickString(String fullNewickString){
        long leftParenthesisNum = fullNewickString.chars().filter(ch->ch == LEFT_PARENTHESIS.charAt(0)).count();
        long rightParenthesisNum = fullNewickString.chars().filter(ch->ch == RIGHT_PARENTHESIS.charAt(0)).count();
        
        if(leftParenthesisNum==0) {
        	throw new IllegalArgumentException("no parenthesis is found!");
        }
        if(leftParenthesisNum!=rightParenthesisNum){
            throw new IllegalArgumentException("left and right parenthesis number is not the same");
        }
        
        String[] splits = fullNewickString.split("");
        List<String> parenthesisTypeList = new ArrayList<>();
        List<Integer> parenthesisPosList = new ArrayList<>();
        
        int pos = 0;
        for(String s:splits){
            if(s.equals(LEFT_PARENTHESIS)){
                parenthesisTypeList.add(LEFT_PARENTHESIS);
                parenthesisPosList.add(pos);
            }else if(s.equals(RIGHT_PARENTHESIS)){
                parenthesisTypeList.add(RIGHT_PARENTHESIS);
                parenthesisPosList.add(pos);
            }
            
            pos++;
        }
        
        
        List<Integer> parenthesisLayer = new ArrayList<>();
        int layer = 0;
        pos = 0;
        for(String p:parenthesisTypeList){
            if(p.equals(LEFT_PARENTHESIS)){
                layer++;
            }else{// if(p.equals(RIGHT_PARENTHESIS)){
                layer--;
            }
            parenthesisLayer.add(layer);
            pos++;
        }
        
        
        
        //check if the first and last parenthesis are a pair
        if(parenthesisTypeList.get(0).equals(LEFT_PARENTHESIS)&&
                    parenthesisTypeList.get(parenthesisTypeList.size()-1).equals(RIGHT_PARENTHESIS)&&
                    parenthesisLayer.get(0)==1&&
                    parenthesisLayer.get(parenthesisLayer.size()-1)==0){
            
        }else{
            throw new IllegalArgumentException("the first and last parenthesis are not a pair");
        }
        
        //check none of the parenthesis in between the first and last one has layer 0
        for(int i=1;i<parenthesisLayer.size()-1;i++){
            if(parenthesisLayer.get(i)==0){
            	throw new IllegalArgumentException("at least one of the parenthesis before the last one has layer 0!");
            }
        }
        
        //
        List<String> splitStringList = StringUtils.splitS2ByS1AsExpected("\\)", fullNewickString);
        String endingString  = splitStringList.get(splitStringList.size()-1);
        if(endingString.contains(",")) {
        	throw new IllegalArgumentException("comma is found after the last right parenthesis!");
        }
        
        System.out.println("no error is found in newick string");
    }
    
    /**
     * check whether the newick string has an implicit root or not;
     * if yes, before parsing the newick string in this api, need to add a pair of parenthesis to cover the full newick string to make the implicit root explicit;
     * 
     * for a complete newick string, it must start and end with a paired parenthesis and/or a node label and/or branch label for the root node
     *      a. (...); //a tree with a root node with no node label string ------- explicit root;
     *      b. (...)node_label; //a tree with a root node with a node label string -------- explicit root
     *      c. (...)node_label:branch_label; //a tree with a node with node and branch label string which is the only child of the root node with no node or branch label string-----implicit root node
     *      d. (...):branch_label;//a tree with a node with branch label string which is the only child of the root node with no node or branch label string-----implicit root node
     * 
     * 		/////
     * 		e. ...,leaf_node_label;//!!!!!!!!NOTE that this is the invalid format, should not be considered since the given fullNewickString should always be valid here
     * 
     * @param fullNewickString a validated newick string with ending semicolon removed
     * @return 
     */
    public static boolean hasImplicitRoot(String fullNewickString){
        //todo
        List<String> splits = StringUtils.splitS2ByS1AsExpected("\\)", fullNewickString);
        String endingString  = splits.get(splits.size()-1);
        System.out.println("ending string:"+endingString);
        
        
        if(!endingString.contains(":")){
            return false;
        }
        
        List<String> nodeAndBranchLabelStringSplits = StringUtils.splitS2ByS1AsExpected("\\:",endingString);
        
        if(nodeAndBranchLabelStringSplits.size()!=2){
            throw new IllegalArgumentException("node and branch label string contains more than one ':'");
        }
        
        String nodeLabelString = nodeAndBranchLabelStringSplits.get(0);
        String branchLabelString = nodeAndBranchLabelStringSplits.get(1);
        System.out.println("nodeLabelString="+nodeLabelString);
        System.out.println("branchLabelString="+branchLabelString);
        
        if(branchLabelString.isEmpty()){//no branch information for the node to parent node
            throw new IllegalArgumentException("branch lable string is abnormally empty");
        }else{
            return true;
        }
        
    }
    
    
    
    
    /**
     * pre-process the newick string read from a newick format file;
     * 1. remove ending semicolon;
     * 2. if implicit root, add a pair of parenthesis to cover the full newick string
     * 
     * @param rawString
     * @return 
     */
    public static String preprocessNewickStringFromFile(String rawString){
        
        if(rawString.endsWith(SEMICOLON)){
            rawString = rawString.substring(0,rawString.length()-1);
        }
        
        //
        if(hasImplicitRoot(rawString)){
            rawString = LEFT_PARENTHESIS.concat(rawString).concat(RIGHT_PARENTHESIS);
        }
        //add any processing if necessary
        
        
        return rawString;
        
    }
    
    /**
     * 
     * @param newickString
     * @param formatType
     * @return
     */
    public static Triple<String, String, String> extractChildrenNodeStringNodeLabelStringAndBranchLabelString(String newickString, VfTreeDataFileFormatType formatType){
    	if(formatType.equals(VfTreeDataFileFormatType.SIMPLE_NEWICK_1)) {
			return extract_SIMPLE_NEWICK_1_ChildrenNodeStringNodeLabelStringAndBranchLabelString(newickString);
		}else if(formatType.equals(VfTreeDataFileFormatType.SIMPLE_NEWICK_2)) {
			return extract_SIMPLE_NEWICK_2_ChildrenNodeStringNodeLabelStringAndBranchLabelString(newickString);
		}else {
			throw new UnsupportedOperationException("given VfTreeDataFileFormatType is not supported yet!");
		}
    }
    
    /**
     * {@link VfTreeDataFileFormatType#SIMPLE_NEWICK_1} format specific
     * 
     * 
     * 
     * extract a newick string of a node N into children node string, node label string of N, branch label string between N and its parent node;
     * the input string must be a VALID newick string except that the node string is an empty leaf without node and branch label strings (trivial case); 
     * 
     * if no children nodes exist(leaf), return a null value;
     * if no node or branch label string exist, return an empty string;
     * 
     * @param newickString
     * @return 
     */
    protected static Triple<String, String, String> extract_SIMPLE_NEWICK_1_ChildrenNodeStringNodeLabelStringAndBranchLabelString(String newickString){
//        System.out.println(newickString);
        String childrenNodesString = null;
        String nodeLabelString = "";
        String branchLabelString = "";
        
        Matcher m;
        if(newickString.startsWith(LEFT_PARENTHESIS)){//internal node
            if((m = SIMPLE_NEWICK_1_FULL_INTERNAL_NODE.matcher(newickString)).matches()){
                childrenNodesString = m.group(1);
                nodeLabelString = m.group(2);
                branchLabelString = m.group(3);
            }else if((m = SIMPLE_NEWICK_1_INTERNAL_NODE_LABEL_ONLY.matcher(newickString)).matches()){
                childrenNodesString = m.group(1);
                nodeLabelString = m.group(2);
            }else if((m = SIMPLE_NEWICK_1_INTERNAL_BRANCH_LABEL_ONLY.matcher(newickString)).matches()){
                childrenNodesString = m.group(1);
                branchLabelString = m.group(2);
            }else if((m = EMPTY_INTERNAL_NODE.matcher(newickString)).matches()){// the internal node has no node label string nor branch label string
                childrenNodesString = m.group(1);
            }else{
                throw new IllegalArgumentException("unrecognized internal node string:"+newickString);
            }
            
        }else{//leaf
            if((m = FULL_LEAF.matcher(newickString)).matches()){
                nodeLabelString = m.group(1);
                branchLabelString = m.group(2);
            }else if((m = LEAF_NODE_LABEL.matcher(newickString)).matches()){
                nodeLabelString = m.group(0);
            }else if((m = LEAF_BRANCH_LABEL.matcher(newickString)).matches()){
                branchLabelString = m.group(1);
            }else if(newickString.isEmpty()){//empty leaf node == input string is an empty string
                //do nothing
            }else{
                throw new IllegalArgumentException("unrecognized internal leaf string:"+newickString);
            }
        }
        
        
        return new Triple<>(childrenNodesString, nodeLabelString, branchLabelString);
    }
    
    protected static Triple<String, String, String> extract_SIMPLE_NEWICK_2_ChildrenNodeStringNodeLabelStringAndBranchLabelString(String newickString){
    	//TODO
    	return null;
    }
    
    
    /**
     * parse the given internal node string containing a list of children nodes into a list of children node string;
     * the input node's labels and the outer parenthesis must be removed before feed to this method!
     * (c1,c2,c3,....)node_label:branch_label   =====>  c1,c2,c3,... (this is the correct input string);
     * for example: (raccoon:19.19959,bear:6.80041):0.84600[50] is not acceptable as input string, 
     * need to be raccoon:19.19959,bear:6.80041 with the node/branch labels and outer parenthesis of the node parsed and removed;
     * @param internalNodeString
     * @return 
     */
    public static List<String> splitNakedInternalNodeStringIntoChildrenNodeStrings(String internalNodeString){
        List<String> ret = new ArrayList<>();
        String[] splits = internalNodeString.split(EMPTY); //split newick string into single characters
        
        int layer=0;
        int previousUnParsedPos = 0; //index of the first unparsed char
        
        for(int i=0;i<splits.length;i++){
            
            if(splits[i].equals(LEFT_PARENTHESIS)){
                layer++;
            }
            
            if(splits[i].equals(RIGHT_PARENTHESIS)){
                layer--;
            }
//            System.out.print(layer);
            
            
            if(splits[i].equals(COMMA)){
                if(layer == 0){ //first layer comma indicate end of a sub-branch
                    
                    String branchString = internalNodeString.substring(previousUnParsedPos, i);
                    ret.add(branchString);
                    
                    previousUnParsedPos = i+1;
                }
            }   
            
            if(i == splits.length-1){//end of string
                if(layer != 0){
                    throw new IllegalArgumentException("Given newick string is invalid with unequal number of left and right parenthesis:"+internalNodeString);
                }


                String branchString = internalNodeString.substring(previousUnParsedPos);

                ret.add(branchString);


            }   
            
        }
        
        
        return ret;
    }
    

    /**
     * 
     * @param edgeLabelString
     * @param formatType
     * @return
     */
    public static Pair<Double, Integer> parseEdgeLabelStringForLengthAndBootstrap(String edgeLabelString,
			VfTreeDataFileFormatType formatType) {
		if(formatType.equals(VfTreeDataFileFormatType.SIMPLE_NEWICK_1)) {
			return parse_NEWICK_1_EdgeLabelStringForLengthAndBootstrap(edgeLabelString);
		}else if(formatType.equals(VfTreeDataFileFormatType.SIMPLE_NEWICK_2)) {
			return parse_NEWICK_2_EdgeLabelStringForLengthAndBootstrap(edgeLabelString);
		}else {
			throw new UnsupportedOperationException("given VfTreeDataFileFormatType is not supported yet!");
		}
	}
	
    /**
     * parse the edge label string for the possible length and bootstrap with respect to {@link VfTreeDataFileFormatType#SIMPLE_NEWICK_1} format:
     * length[bootstrap]  ===== 0.05818[100]
     * length ==== 0.15420
     * [bootstrap] ===== [92]
     * empty string ==== no length nor bootstrap
     * 
     * 
     * 
     * @param branchLabelString
     * @return 
     */
    protected static Pair<Double,Integer> parse_NEWICK_1_EdgeLabelStringForLengthAndBootstrap(String branchLabelString){
        
        Double length = null;
        Integer bootstrap = null;
        
        if(branchLabelString.trim().isEmpty()){
        	//both length and bootstrap are empty
        }else{
            if(branchLabelString.contains("[")&&branchLabelString.contains("]")){//has bootstrap;
                Pattern p = Pattern.compile("(.*)\\[(.+)\\]");
                Matcher m = p.matcher(branchLabelString);
                if(m.matches()){
                    if(m.group(1).trim().isEmpty()){
                        
                    }else{
                        length = Double.parseDouble(m.group(1));
                    }
                    
                    bootstrap = Integer.parseInt(m.group(2).trim());
                }else{
                    throw new IllegalArgumentException("input branchLabelString is invalid");
                }
                
            }else{//no bootstrap;
                length = Double.parseDouble(branchLabelString.trim());
            }
            
            
        }
        
        //if length is not null and negative, set it to 0;
        length = length==null?null:length<0?0:length;
        
        Pair<Double,Integer> ret = new Pair<>(length, bootstrap);
        
        
        return ret;
    }

	
    /**
     * parse the edge label string for the possible length and bootstrap with respect to {@link VfTreeDataFileFormatType#SIMPLE_NEWICK_2} format:
     * bootstrap:length  ===== 50:0.84600
     * :length ==== :25.46154
     * bootstrap ===== 92
     * empty string ==== no length nor bootstrap
     * @param branchLabelString
     * @return 
     */
    protected static Pair<Double,Integer> parse_NEWICK_2_EdgeLabelStringForLengthAndBootstrap(String branchLabelString){
        
        Double length = null;
        Integer bootstrap = null;
        
        if(branchLabelString.trim().isEmpty()){
        	//both length and bootstrap are empty
        }else{
            if(branchLabelString.contains(":")){//has length;
                Pattern p = Pattern.compile("(.*)\\:(.+)");
                Matcher m = p.matcher(branchLabelString);
                if(m.matches()){
                    if(m.group(1).trim().isEmpty()){//bootstrap is empty
                        
                    }else{
                    	bootstrap = Integer.parseInt(m.group(1));
                    }
                    
                    length = Double.parseDouble(m.group(2).trim());
                }else{
                    throw new IllegalArgumentException("input branchLabelString is invalid");
                }
                
            }else{//no length;
            	bootstrap = Integer.parseInt(branchLabelString.trim());
            }
            
        }
        
        //if length is not null and negative, set it to 0;
        length = length==null?null:length<0?0:length;
        
        Pair<Double,Integer> ret = new Pair<>(length, bootstrap);
        
        
        return ret;
    }
    
    
    public static void main(String[] args){
//        String s = "a:1sldkjf";
//        String ss = "(raccoon:19.19959,bear:6.80041)mammal:0.84600";
//        Matcher m = FULL_INTERNAL_NODE.matcher(ss);
//        if(m.matches()){
//            for(int i = 0;i<m.groupCount()+1;i++){
//                System.out.println(i+ ":" + m.group(i));
//            }
//        }
//        
        
        
//        String nakedInternalNodeString1 = "(raccoon, bear),((sea_lion,seal),((monkey,cat), weasel)),dog";
//        String nakedInternalNodeString2 = "(raccoon:19.19959,bear:6.80041):0.84600[50],((sea_lion:11.99700, seal:12.00300):7.52973[100],((monkey:100.85930,cat:47.14069):20.59201[80], weasel:18.87953):2.09460[75]):3.87382[50],dog:25.46154";
//        String nakedInternalNodeString3 = "(sea_lion:11.99700, seal:12.00300):7.52973[100],((monkey:100.85930,cat:47.14069):20.59201[80], weasel:18.87953):2.09460[75]";
//        List<String> clist = splitNakedInternalNodeStringIntoChildrenNodeStrings(nakedInternalNodeString3);
//        for(String c:clist){
//           System.out.println(c);
//        }
        
        
        
//        String tree1 = "((raccoon, bear),((sea_lion,seal),((monkey,cat), weasel)),dog);";
//        
//        Triple<String,String,String> splits = extractChildrenNodeStringNodeLabelStringAndBranchLabelString(tree1);
//        System.out.println("left:"+splits.getLeft());
//        System.out.println("middle:"+splits.getMiddle());
//        System.out.println("right:"+splits.getRight());
        


        String branchLabelString = " [232]";
        Pair<Double,Integer> ret = parse_NEWICK_1_EdgeLabelStringForLengthAndBootstrap(branchLabelString);
        
        System.out.println("length=="+ret.getFirst());
        System.out.println("bootstrap=="+ret.getSecond());
        
    }
    
    
    
    
}
