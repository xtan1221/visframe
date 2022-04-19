package importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import basic.VfNotes;
import context.project.VisProjectDBContext;
import fileformat.FileFormatID;
import metadata.MetadataID;
import metadata.MetadataName;

public abstract class AbstractDataImporter implements DataImporter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1485044203178944294L;
	
	
	///////////////
	private final VfNotes notes;
	private final String dataSourcePathString;
	private final FileFormatID fileFormatID;
	private final MetadataName mainImportedMetadataName;
	
	//////
	private transient VisProjectDBContext hostVisProject;
	private transient Path dataSourcePath;
	
	/**
	 * constructor
	 * @param notes cannot be null
	 * @param dataSourcePath cannot be null or empty string
	 * @param metadataName cannot be null
	 */
	public AbstractDataImporter(VfNotes notes, Path dataSourcePath, FileFormatID fileFormatID, MetadataName mainImportedMetadataName) {
		if(notes==null)
			throw new IllegalArgumentException("given notes cannot be null!");
		if(dataSourcePath==null)
			throw new IllegalArgumentException("given dataSourcePath cannot be null!");
		if(fileFormatID==null)
			throw new IllegalArgumentException("given fileFormatID cannot be null!");
		if(mainImportedMetadataName==null)
			throw new IllegalArgumentException("given mainImportedMetadataName cannot be null!");
		
		//////////////
		this.notes = notes;
		this.dataSourcePath = dataSourcePath;
		this.dataSourcePathString = this.dataSourcePath.toString();
		this.fileFormatID = fileFormatID;
		this.mainImportedMetadataName = mainImportedMetadataName;
	}
	
	/**
	 * @return the dataSourcePathString
	 */
	public String getDataSourcePathString() {
		return dataSourcePathString;
	}
	
	@Override
	public VfNotes getNotes() {
		return this.notes;
	}
	
	@Override
	public Path getDataSourcePath() {
		if(this.dataSourcePath==null) {
			this.dataSourcePath = Paths.get(this.dataSourcePathString);
		}
		return this.dataSourcePath;
	}
	
	@Override
	public MetadataID getMainImportedMetadataID() {
		return new MetadataID(this.getMainImportedMetadataName(),this.getDataType());
	}
	
	/**
	 * @return the mainImportedMetadataName
	 */
	@Override
	public MetadataName getMainImportedMetadataName() {
		return mainImportedMetadataName;
	}

	@Override
	public FileFormatID getFileFormatID() {
		return this.fileFormatID;
	}
	
	@Override
	public void setHostVisProjectDBContext(VisProjectDBContext hostVisProject) {
		this.hostVisProject = hostVisProject;
	}
	
	@Override
	public VisProjectDBContext getHostVisProjectDBContext() {
		return this.hostVisProject;
	}
	//////////////////////////////////////
	//facilitating methods to perform the importer after {@link #setHostVisProjectDBContext(VisProjectDBContext)} is invoked
	/**
	 * create a FileParser subclass instance to read and parse the data file into data tables in the rdb of host VisProjectDBContext;
	 * @throws SQLException 
	 * @throws IOException 
	 */
	protected abstract void readAndParseIntoDataTables() throws SQLException, IOException;
	/**
	 * create and insert the imported Metadata of this DataImporter in the Metadata management table in the rdb of host VisProjectDBContext;
	 * @throws SQLException 
	 */
	protected abstract void createAndStoreImportedMetadata() throws SQLException;
	/**
	 * insert this DataImporter into the DataImporter management table in the rdb of host VisProjectDBContext;
	 * @throws SQLException 
	 */
	protected abstract void storeDataImporter() throws SQLException;

	///////////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataSourcePathString == null) ? 0 : dataSourcePathString.hashCode());
		result = prime * result + ((fileFormatID == null) ? 0 : fileFormatID.hashCode());
		result = prime * result + ((mainImportedMetadataName == null) ? 0 : mainImportedMetadataName.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractDataImporter))
			return false;
		AbstractDataImporter other = (AbstractDataImporter) obj;
		if (dataSourcePathString == null) {
			if (other.dataSourcePathString != null)
				return false;
		} else if (!dataSourcePathString.equals(other.dataSourcePathString))
			return false;
		if (fileFormatID == null) {
			if (other.fileFormatID != null)
				return false;
		} else if (!fileFormatID.equals(other.fileFormatID))
			return false;
		if (mainImportedMetadataName == null) {
			if (other.mainImportedMetadataName != null)
				return false;
		} else if (!mainImportedMetadataName.equals(other.mainImportedMetadataName))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		return true;
	}
	
}
