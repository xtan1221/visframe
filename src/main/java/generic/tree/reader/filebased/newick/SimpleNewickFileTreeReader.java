package generic.tree.reader.filebased.newick;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import basic.VfNotes;
import fileformat.vftree.VfTreeDataFileFormatType;
import generic.tree.VfTreeNode;
import generic.tree.reader.filebased.VfDataFileTreeReader;
import rdb.sqltype.SQLStringType;
import rdb.table.data.DataTableColumn;
import rdb.table.data.DataTableColumnName;
/**
 * vf tree built from a {@link VfTreeDataFileFormatType#SIMPLE_NEWICK_1} or {@link VfTreeDataFileFormatType#SIMPLE_NEWICK_2} format data string
 * @author tanxu
 * 
 */
public class SimpleNewickFileTreeReader extends VfDataFileTreeReader{
	/**
	 * simple newick tree specific non-mandatory additional feature
	 * @return
	 */
	public static DataTableColumn newickTreeNodeLabelStringColumn() {
		return new DataTableColumn(
				new DataTableColumnName("NODE_LABEL"), 
				new SQLStringType(100,false),
				false, false, false, null,null,//boolean inPrimaryKey, Boolean unique, Boolean notNull, String defaultStringValue,
				VfNotes.makeVisframeDefinedVfNotes());
	}
	
	///////////////////////
	private String fullNewickTreeString;
	private SimpleNewickFileTreeNode rootNode;
	
	/**
	 * constructor
	 * @param fullNewickTreeString
	 */
	public SimpleNewickFileTreeReader(Path treeFilePath, VfTreeDataFileFormatType formatType) {
		super(treeFilePath, formatType);
//		//validations
//		SimpleNewickParserUtils.validateFullNewickString(fullNewickTreeString);
//		
//		fullNewickTreeString = SimpleNewickParserUtils.preprocessNewickStringFromFile(fullNewickTreeString);
//		
		if(formatType!=VfTreeDataFileFormatType.SIMPLE_NEWICK_1&&formatType!=VfTreeDataFileFormatType.SIMPLE_NEWICK_2) {
			throw new IllegalArgumentException("VfTreeDataFileFormatType must be BASE_NEWICK_1 or BASE_NEWICK_2!");
		}
		
		if(formatType==VfTreeDataFileFormatType.SIMPLE_NEWICK_2) {
			throw new UnsupportedOperationException("SIMPLE_NEWICK_2 format is not implemented yet!");
		}
	}
	
	
	
	/**
	 * 1. read in the full newick tree string from the simple newick tree data file;
	 * 
	 * 2. 
	 * @throws IOException 
	 */
	@Override
	public void perform() throws IOException {
		this.readDataFile();
		
		//this will initialize the construction of the tree from root to each leaf
		this.rootNode = new SimpleNewickFileTreeNode(fullNewickTreeString, null, 0, this.getFormatType());
	}
	
	/**
	 * read the full newick tree string from the data file;
	 * @throws IOException
	 */
	private void readDataFile() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(this.getTreeFilePath().toFile()));
		
		this.fullNewickTreeString = "";
		String line;
		while((line = bufferedReader.readLine())!=null) {
			line = line.trim();
			fullNewickTreeString = fullNewickTreeString.concat(line);
		}
		
		fullNewickTreeString = SimpleNewickParserUtils.preprocessNewickStringFromFile(fullNewickTreeString);
		
		bufferedReader.close();
	}
	
	@Override
	public VfTreeNode getRootNode() {
		return this.rootNode;
	}
	
	/**
	 * for simple newick tree, there is one single non mandatory additional attributes which is the node label;
	 */
	@Override
	public Map<DataTableColumnName, DataTableColumn> getNonMandatoryAdditionalNodeFeatureColumnNameMap() {
		Map<DataTableColumnName, DataTableColumn> ret = new LinkedHashMap<>();
		
		ret.put(newickTreeNodeLabelStringColumn().getName(), newickTreeNodeLabelStringColumn());
		
		return ret;
	}
	
	/**
	 * for simple newick tree, there is no non-mandatory attributes for edges;
	 */
	@Override
	public Map<DataTableColumnName, DataTableColumn> getNonMandatoryAdditionalEdgeFeatureColumnNameMap() {
		return new LinkedHashMap<>();
	}
}
