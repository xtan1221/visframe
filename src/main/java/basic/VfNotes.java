package basic;

import basic.reproduce.SimpleReproducible;

public class VfNotes implements SimpleReproducible{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7044791555877988742L;
	
	////////////////////////////////
	private final String notes;
	
	/**
	 * 
	 */
	public VfNotes() {
		this.notes = "visframe default";
	}
	
	
	public VfNotes(String notes){
		this.notes = notes;
	}
	
	public String getNotesString() {
		return notes;
	}
	
	@Override
	public VfNotes reproduce() {
		return new VfNotes(this.getNotesString());
	}
	

	/**
	 * return a default VfNotes for Visframe defined entities
	 * @return
	 */
	public static VfNotes makeVisframeDefinedVfNotes() {
		return new VfNotes("visframe default");
	}
	
	/////////////////////
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VfNotes))
			return false;
		VfNotes other = (VfNotes) obj;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return notes;
	}
}
