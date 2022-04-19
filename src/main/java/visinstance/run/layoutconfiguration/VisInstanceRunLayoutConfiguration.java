package visinstance.run.layoutconfiguration;

import java.util.List;

import basic.HasNotes;
import basic.VfNotes;
import basic.lookup.VisframeUDT;
import basic.process.NonReproduceableProcessType;
import function.group.CompositionFunctionGroupID;
import visinstance.run.VisInstanceRunID;
/**
 * 
 * 
 * @author tanxu
 *
 */
public interface VisInstanceRunLayoutConfiguration extends VisframeUDT, HasNotes, NonReproduceableProcessType{
	
	@Override
	VfNotes getNotes();
	
	int getUID();
	
	@Override
	default VisInstanceRunLayoutConfigurationID getID() {
		return new VisInstanceRunLayoutConfigurationID(this.getUID());
	}
	
	/**
	 * return the VisInstanceRunID of this VisInstanceRunLayout
	 * @return
	 */
	VisInstanceRunID getVisInstanceRunID();
	
	
	/**
	 * return the list of core ShapeCFG's CompositionFunctionGroupIDs in the order of layout;
	 * the one will smaller index will be laid out before those ones with larger index;
	 * note that those laid out earlier may be covered by those laid out later;
	 * 
	 * it is not mandatory that this list must include all core ShapeCFGs of the VisInstanceRun;
	 * 		any subset is allowed;
	 * 
	 * @return
	 */
	List<CompositionFunctionGroupID> getCoreShapeCFGIDListInLayoutOrder();
	
	/**
	 * return the evaluated smaller X coordinate value for the full region of the VisInstanceRun;
	 * 
	 * note that this region should be the one with all core ShapeCFGs of the target VisInstanceRun included;
	 * 
	 * all VisInstanceRunLayouts of the same VisInstanceRun should have the same value;
	 * 
	 * must be strictly smaller than {@link #getFullRegionX2()}
	 * @return
	 */
	double getFullRegionX1();
	
	/**
	 * return the evaluated smaller Y coordinate value for the full region of the VisInstanceRun;
	 * 
	 * note that this region should be the one with all core ShapeCFGs of the target VisInstanceRun included;
	 * 
	 * all VisInstanceRunLayouts of the same VisInstanceRun should have the same value;
	 * @return
	 */
	double getFullRegionY1();
	
	/**
	 * return the evaluated larger X coordinate value for the full region of the VisInstanceRun;
	 * 
	 * note that this region should be the one with all core ShapeCFGs of the target VisInstanceRun included;
	 * 
	 * all VisInstanceRunLayouts of the same VisInstanceRun should have the same value;
	 * 
	 * must be strictly larger than {@link #getFullRegionX1()}
	 * @return
	 */
	double getFullRegionX2();
	
	/**
	 * return the evaluated larger Y coordinate value for the full region of the VisInstanceRun;
	 * 
	 * note that this region should be the one with all core ShapeCFGs of the target VisInstanceRun included;
	 * 
	 * all VisInstanceRunLayouts of the same VisInstanceRun should have the same value;
	 * @return
	 */
	double getFullRegionY2();
	
	boolean isFullRegionSelected();
	
	/**
	 * return the smaller x coordinate value for the border of the layout;
	 * note that this value can be of any value as long as the region of the layout has some overlapping region with the full layout region defined by 
	 * @return
	 */
	double getX1();
	
	/**
	 * return the smaller Y coordinate value for the border of the layout
	 * @return
	 */
	double getY1();
	
	/**
	 * return the larger x coordinate value for the border of the layout
	 * @return
	 */
	double getX2();
	
	/**
	 * return the larger Y coordinate value for the border of the layout
	 * @return
	 */
	double getY2();
}
