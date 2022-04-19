package visinstance.run.layoutconfiguration;

import java.util.List;

import basic.VfNotes;
import function.group.CompositionFunctionGroupID;
import visinstance.run.VisInstanceRunID;

/**
 * 
 * @author tanxu
 *
 */
public class VisInstanceRunLayoutConfigurationImpl implements VisInstanceRunLayoutConfiguration{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6701992189296091703L;
	
	////////////////////////
	private final VisInstanceRunID visInstanceRunID;
	private final int UID;
	private final VfNotes notes;
	
	
	/////////////
	private final double fullRegionX1;
	private final double fullRegionY1;
	private final double fullRegionX2;
	private final double fullRegionY2;
	
	//////////////
	private final List<CompositionFunctionGroupID> coreShapeCFGIDListInLayoutOrder;
	private boolean fullRegionSelected;
	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;
	
	/**
	 * 
	 * @param visInstanceRunID
	 * @param UID
	 * @param notes
	 * @param coreShapeCFGIDListInLayoutOrder
	 * @param fullRegionX1
	 * @param fullRegionY1
	 * @param fullRegionX2
	 * @param fullRegionY2
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public VisInstanceRunLayoutConfigurationImpl(
			VisInstanceRunID visInstanceRunID,
			int UID,
			VfNotes notes,
			
			double fullRegionX1,
			double fullRegionY1,
			double fullRegionX2,
			double fullRegionY2,
			
			///
			List<CompositionFunctionGroupID> coreShapeCFGIDListInLayoutOrder,
			boolean fullRegionSelected,
			double x1,
			double y1,
			double x2,
			double y2
			){
		if(visInstanceRunID==null)
			throw new IllegalArgumentException("given visInstanceRunID cannot be null!");
		if(notes == null)
			throw new IllegalArgumentException("given notes cannot be null!");
		
		
		this.visInstanceRunID = visInstanceRunID;
		this.UID = UID;
		this.notes = notes;
		/////////
		this.fullRegionX1 = fullRegionX1;
		this.fullRegionY1 = fullRegionY1;
		this.fullRegionX2 = fullRegionX2;
		this.fullRegionY2 = fullRegionY2;
		///////////
		this.coreShapeCFGIDListInLayoutOrder = coreShapeCFGIDListInLayoutOrder;
		this.fullRegionSelected = fullRegionSelected;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public VfNotes getNotes() {
		return this.notes;
	}

	@Override
	public int getUID() {
		return this.UID;
	}
	

	@Override
	public VisInstanceRunID getVisInstanceRunID() {
		return this.visInstanceRunID;
	}

	@Override
	public List<CompositionFunctionGroupID> getCoreShapeCFGIDListInLayoutOrder() {
		return this.coreShapeCFGIDListInLayoutOrder;
	}

	@Override
	public boolean isFullRegionSelected() {
		return fullRegionSelected;
	}

	@Override
	public double getFullRegionX1() {
		return this.fullRegionX1;
	}

	@Override
	public double getFullRegionY1() {
		return this.fullRegionY1;
	}

	@Override
	public double getFullRegionX2() {
		return this.fullRegionX2;
	}

	@Override
	public double getFullRegionY2() {
		return this.fullRegionY2;
	}

	@Override
	public double getX1() {
		return this.x1;
	}

	@Override
	public double getY1() {
		return this.y1;
	}

	@Override
	public double getX2() {
		return this.x2;
	}

	@Override
	public double getY2() {
		return this.y2;
	}

	

	/////////////////////////////////////////
	//equals and hashcode methods
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + UID;
		result = prime * result
				+ ((coreShapeCFGIDListInLayoutOrder == null) ? 0 : coreShapeCFGIDListInLayoutOrder.hashCode());
		result = prime * result + (fullRegionSelected ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(fullRegionX1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(fullRegionX2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(fullRegionY1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(fullRegionY2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((visInstanceRunID == null) ? 0 : visInstanceRunID.hashCode());
		temp = Double.doubleToLongBits(x1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(x2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VisInstanceRunLayoutConfigurationImpl))
			return false;
		VisInstanceRunLayoutConfigurationImpl other = (VisInstanceRunLayoutConfigurationImpl) obj;
		if (UID != other.UID)
			return false;
		if (coreShapeCFGIDListInLayoutOrder == null) {
			if (other.coreShapeCFGIDListInLayoutOrder != null)
				return false;
		} else if (!coreShapeCFGIDListInLayoutOrder.equals(other.coreShapeCFGIDListInLayoutOrder))
			return false;
		if (fullRegionSelected != other.fullRegionSelected)
			return false;
		if (Double.doubleToLongBits(fullRegionX1) != Double.doubleToLongBits(other.fullRegionX1))
			return false;
		if (Double.doubleToLongBits(fullRegionX2) != Double.doubleToLongBits(other.fullRegionX2))
			return false;
		if (Double.doubleToLongBits(fullRegionY1) != Double.doubleToLongBits(other.fullRegionY1))
			return false;
		if (Double.doubleToLongBits(fullRegionY2) != Double.doubleToLongBits(other.fullRegionY2))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (visInstanceRunID == null) {
			if (other.visInstanceRunID != null)
				return false;
		} else if (!visInstanceRunID.equals(other.visInstanceRunID))
			return false;
		if (Double.doubleToLongBits(x1) != Double.doubleToLongBits(other.x1))
			return false;
		if (Double.doubleToLongBits(x2) != Double.doubleToLongBits(other.x2))
			return false;
		if (Double.doubleToLongBits(y1) != Double.doubleToLongBits(other.y1))
			return false;
		if (Double.doubleToLongBits(y2) != Double.doubleToLongBits(other.y2))
			return false;
		return true;
	}
	
}
