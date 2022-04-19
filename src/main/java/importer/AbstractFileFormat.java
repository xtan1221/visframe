package importer;

import basic.SimpleName;
import basic.VfNotes;
import fileformat.FileFormat;

/**
 * 
 * @author tanxu
 *
 */
public abstract class AbstractFileFormat implements FileFormat{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7575618775784885657L;
	
	///////////////
	private final VfNotes notes;
	private final SimpleName name;
	
	/**
	 * constructor
	 * @param name cannot be null;
	 * @param notes cannot be null;
	 */
	public AbstractFileFormat(SimpleName name, VfNotes notes){
		if(name==null) {
			throw new IllegalArgumentException("given name is null");
		}
		
		this.name = name;
		this.notes = notes;
	}
	
	@Override
	public SimpleName getName() {
		return this.name;
	}
	
	@Override
	public VfNotes getNotes() {
		return this.notes;
	}
	
	//////////////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractFileFormat))
			return false;
		AbstractFileFormat other = (AbstractFileFormat) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		return true;
	}
	
	
}
