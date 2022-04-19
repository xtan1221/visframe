package fileformat.record.attribute;

import java.io.Serializable;

import basic.HasName;
import basic.HasNotes;
import basic.SimpleName;
import basic.VfNotes;


/**
 * base class for attribute of a record of data file in a record file format;
 * note that this is totally different from the RelationalTableColumn;
 * 
 * by default, any non-tag attribute can have string value same with the null valued string (if it is set);
 * 
 * @author tanxu
 * 
 */
public abstract class AbstractRecordAttributeFormat implements HasName, HasNotes, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1873454198978583181L;
	
	///////////////////////
	private final SimpleName name;
	private final VfNotes notes;
	
	
	/**
	 * constructor
	 * @param name cannot be null
	 * @param notes cannot be null
	 */
	public AbstractRecordAttributeFormat(SimpleName name, VfNotes notes) {
		if(name==null) {
			throw new IllegalArgumentException("name cannot be null!");
		}
		
		if(notes == null) {
			throw new IllegalArgumentException("notes cannot be null!");
		}
		
		this.name = name;
		this.notes = notes;
	}
	
	public VfNotes getNotes() {
		return this.notes;
	}
	
	public SimpleName getName() {
		return this.name;
	}

	
	/////////////////////////////
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
		if (!(obj instanceof AbstractRecordAttributeFormat))
			return false;
		AbstractRecordAttributeFormat other = (AbstractRecordAttributeFormat) obj;
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
