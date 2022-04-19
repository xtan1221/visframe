package fileformat.vftree;

import basic.SimpleName;
import importer.AbstractFileFormat;
import metadata.DataType;

public class VfTreeDataFileFormat extends AbstractFileFormat{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2359314608931872510L;
	
	////////////////////
	private final VfTreeDataFileFormatType fileType;
	
	/**
	 * constructor
	 * @param fileType
	 */
	public VfTreeDataFileFormat(VfTreeDataFileFormatType fileType) {
		super(fileType.getName(), fileType.getNotes());
		
		this.fileType = fileType;
	}
	
	
	@Override
	public DataType getDataType() {
		return DataType.vfTREE;
	}
	
	
	@Override
	public VfTreeDataFileFormat rename(SimpleName newName) {
		throw new UnsupportedOperationException();
	}

	
	public VfTreeDataFileFormatType getFileType() {
		return fileType;
	}

	///////////////////////////////
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
		if (!(obj instanceof VfTreeDataFileFormat))
			return false;
		VfTreeDataFileFormat other = (VfTreeDataFileFormat) obj;
		if (fileType != other.fileType)
			return false;
		return true;
	}
}
