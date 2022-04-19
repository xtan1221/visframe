package fileformat.graph;

import basic.SimpleName;
import importer.AbstractFileFormat;
import metadata.DataType;

public class GraphDataFileFormat extends AbstractFileFormat{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2359314608931872510L;
	
	////////////////////
	private final GraphDataFileFormatType fileType;
	
	/**
	 * constructor
	 * @param fileType
	 */
	public GraphDataFileFormat(GraphDataFileFormatType fileType) {
		super(fileType.getName(), fileType.getNotes());
		
		this.fileType = fileType;
	}
	
	
	@Override
	public DataType getDataType() {
		return DataType.GRAPH;
	}
	
	
	@Override
	public GraphDataFileFormat rename(SimpleName newName) {
		throw new UnsupportedOperationException();
	}
	
	
	public GraphDataFileFormatType getFileType() {
		return fileType;
	}


	///////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fileType == null) ? 0 : fileType.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof GraphDataFileFormat))
			return false;
		GraphDataFileFormat other = (GraphDataFileFormat) obj;
		if (fileType != other.fileType)
			return false;
		return true;
	}
	
}
