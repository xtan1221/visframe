package fileformat.vftree;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.VfNotes;


/**
 * visframe supported tree file format that can be imported as VfTreeDataMetadata
 * @author tanxu
 *
 */
public enum VfTreeDataFileFormatType implements HasName, HasNotes{
	//internal node label is allowed
	SIMPLE_NEWICK_1("SIMPLE_NEWICK_1", new VfNotes("bootstrap value (if exist) is in squared bracket after branch length like in \n\t ((raccoon:19.19959,bear:6.80041):0.84600[50],((sea_lion:11.99700, seal:12.00300):7.52973[100],((monkey:100.85930,cat:47.14069):20.59201[80], weasel:18.87953):2.09460[75]):3.87382[50],dog:25.46154);")),  
	//this type of newick seems not allow internal node labels since the edge length takes the position of the internal node label of SIMPLE_NEWICK_1?
	//thus parsing of this type regarding the 
	SIMPLE_NEWICK_2("SIMPLE_NEWICK_2", new VfNotes("bootstrap value (if exist) is before branch length with a colon like in \n\t ((raccoon:19.19959,bear:6.80041)50:0.84600,((sea_lion:11.99700, seal:12.00300)100:7.52973,((monkey:100.85930,cat:47.14069)80:20.59201, weasel:18.87953)75:2.09460)50:3.87382,dog:25.46154);")),  
	EXTENDED_NEWICK("Extended_NEWICK", VfNotes.makeVisframeDefinedVfNotes()),
	RICH_NEWICK("Rich newick", VfNotes.makeVisframeDefinedVfNotes()),
	NHX("New Hampshire X", VfNotes.makeVisframeDefinedVfNotes()),
	NEXUS("NEXUS", VfNotes.makeVisframeDefinedVfNotes()), //contains a newick tree data string with other supportive features;
	PhyloXML("PhyloXML", VfNotes.makeVisframeDefinedVfNotes());//do not contain any newick tree data string
	
	
	///////////////////////////////////////////////
	private final String fullName;
	private final VfNotes notes;
	
	
	/**
	 * constructor
	 * @param fullName
	 * @param notes
	 */
	VfTreeDataFileFormatType(String fullName, VfNotes notes){
		this.fullName = fullName;
		this.notes = notes;
	}
	
	public VfTreeDataFileFormat getFileFormat() {
		return new VfTreeDataFileFormat(this);
	}

	@Override
	public VfNotes getNotes() {
		return notes;
	}
	
	
	public String getFullName() {
		return fullName;
	}

	@Override
	public SimpleName getName() {
		// TODO Auto-generated method stub
		return new SimpleName(this.toString());
	}
}
