package visinstance.run.extractor;

import javafx.geometry.Point2D;

public class LayoutUtils {
	
	/**
	 * check and return whether the given target coordinate is inside the rectangle constrained by the given two corner coordinates;
	 * @param corner1
	 * @param corner2
	 * @param target
	 * @return
	 */
	public static boolean isInRegion(Point2D upperLeft, Point2D bottomRight, Point2D target) {
		return target.getX()>=upperLeft.getX() && target.getX()<=bottomRight.getX() && target.getY()>=upperLeft.getY()&&target.getY()<=bottomRight.getY();
	}
	
	
}
