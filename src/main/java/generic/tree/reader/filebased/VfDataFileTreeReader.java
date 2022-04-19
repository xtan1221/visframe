package generic.tree.reader.filebased;

import java.io.IOException;
import java.nio.file.Path;

import fileformat.vftree.VfTreeDataFileFormatType;
import generic.tree.reader.VfTreeReader;

/**
 * base class that read a tree from a data file of a specific tree file format;
 * @author tanxu
 *
 */
public abstract class VfDataFileTreeReader extends VfTreeReader{
	
	private final Path treeFilePath;
	private final VfTreeDataFileFormatType formatType;
	
	/**
	 * constructor
	 * @param treeFilePath
	 * @param formatType
	 */
	protected VfDataFileTreeReader(Path treeFilePath, VfTreeDataFileFormatType formatType){
		this.treeFilePath = treeFilePath;
		this.formatType = formatType;
	}
	
	/**
	 * read the data file and parse the data into a VfTree;
	 * @throws IOException 
	 * 
	 */
	public abstract void perform() throws IOException;
	
	
	public Path getTreeFilePath() {
		return treeFilePath;
	}

	public VfTreeDataFileFormatType getFormatType() {
		return formatType;
	}
	
}
