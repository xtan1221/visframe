package graphics.shape;

import java.util.LinkedHashMap;
import java.util.Map;

import basic.SimpleName;
import graphics.shape.shape2D.type.VfArc;
import graphics.shape.shape2D.type.VfCircle;
import graphics.shape.shape2D.type.VfCubicCurve;
import graphics.shape.shape2D.type.VfEllipse;
import graphics.shape.shape2D.type.VfLine;
import graphics.shape.shape2D.type.VfQuadCurve;
import graphics.shape.shape2D.type.VfRectangle;
import graphics.shape.shape2D.type.VfText;

public class VfShapeTypeLookup {
	/**
	 * 
	 */
	private final static Map<SimpleName, VfShapeType> ALL_VF_SHAPE_TYPE_NAME_MAP;
	static {
		ALL_VF_SHAPE_TYPE_NAME_MAP = new LinkedHashMap<>();
		
		ALL_VF_SHAPE_TYPE_NAME_MAP.put(VfArc.SINGLETON.getName(), VfArc.SINGLETON);
		ALL_VF_SHAPE_TYPE_NAME_MAP.put(VfCircle.SINGLETON.getName(), VfCircle.SINGLETON);
		ALL_VF_SHAPE_TYPE_NAME_MAP.put(VfCubicCurve.SINGLETON.getName(), VfCubicCurve.SINGLETON);
		ALL_VF_SHAPE_TYPE_NAME_MAP.put(VfEllipse.SINGLETON.getName(), VfEllipse.SINGLETON);
		ALL_VF_SHAPE_TYPE_NAME_MAP.put(VfLine.SINGLETON.getName(), VfLine.SINGLETON);
		ALL_VF_SHAPE_TYPE_NAME_MAP.put(VfQuadCurve.SINGLETON.getName(), VfQuadCurve.SINGLETON);
		ALL_VF_SHAPE_TYPE_NAME_MAP.put(VfRectangle.SINGLETON.getName(), VfRectangle.SINGLETON);
		ALL_VF_SHAPE_TYPE_NAME_MAP.put(VfText.SINGLETON.getName(), VfText.SINGLETON);
	}
	
	/**
	 * return the full set of visframe defined {@link VfShapeType}s
	 * @return
	 */
	public static Map<SimpleName, VfShapeType> getAllVfShapeTypeNameMap(){
		return ALL_VF_SHAPE_TYPE_NAME_MAP;
	}
}
