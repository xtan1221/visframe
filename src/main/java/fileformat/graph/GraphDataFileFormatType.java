package fileformat.graph;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.VfNotes;


/**
 * visframe supported graph file format that can be imported as GraphDataMetadata
 * 
 * @author tanxu
 *
 */
public enum GraphDataFileFormatType implements HasName, HasNotes{
	//node and edge attributes are either the default ones or user-defined ones in the GEXF data file;
	SIMPLE_GEXF(new SimpleName("SIMPLE_GEXF"), VfNotes.makeVisframeDefinedVfNotes());//basic type of GEXF, different from the HIERARCHICAL OR PHYLEGENY STRUCTURED GRAPH OR DYNAMIC GRAPH
	
	
	/////////////////
	private final SimpleName name;
	private final VfNotes notes;
	
	
	/**
	 * constructor
	 * @param name
	 * @param notes
	 */
	GraphDataFileFormatType(SimpleName name, VfNotes notes){
		this.name = name;
		this.notes = notes;
	}
	
	/**
	 * build and return a GraphDataFileFormat with this GraphDataFileFormatType
	 * @return
	 */
	public GraphDataFileFormat getFileFormat() {
		return new GraphDataFileFormat(this);
	}

	@Override
	public VfNotes getNotes() {
		return notes;
	}

	@Override
	public SimpleName getName() {
		return name;
	}
}
