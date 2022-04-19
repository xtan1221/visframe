package graphics.shape.shape2D.fx;

import java.util.LinkedHashMap;
import java.util.Map;

import graphics.shape.VfShapeType;
import graphics.shape.shape2D.fx.type.VfArcFXNodeFactoryImpl;
import graphics.shape.shape2D.fx.type.VfCircleFXNodeFactoryImpl;
import graphics.shape.shape2D.fx.type.VfCubicCurveFXNodeFactoryImpl;
import graphics.shape.shape2D.fx.type.VfEllipseFXNodeFactoryImpl;
import graphics.shape.shape2D.fx.type.VfLineFXNodeFactoryImpl;
import graphics.shape.shape2D.fx.type.VfQuadCurveFXNodeFactoryImpl;
import graphics.shape.shape2D.fx.type.VfRectangleFXNodeFactoryImpl;
import graphics.shape.shape2D.fx.type.VfTextFXNodeFactoryImpl;
import graphics.shape.shape2D.type.VfArc;
import graphics.shape.shape2D.type.VfCircle;
import graphics.shape.shape2D.type.VfCubicCurve;
import graphics.shape.shape2D.type.VfEllipse;
import graphics.shape.shape2D.type.VfLine;
import graphics.shape.shape2D.type.VfQuadCurve;
import graphics.shape.shape2D.type.VfRectangle;
import graphics.shape.shape2D.type.VfText;

/**
 * 
 * @author tanxu
 *
 */
public final class VfShapeTypeFXNodeFactoryFactory {
	
	private static final Map<VfShapeType, VfShapeTypeFXNodeFactory<?,?>> shapeTypeFactoryMap;
	
	static {
		shapeTypeFactoryMap = new LinkedHashMap<>();
		
		shapeTypeFactoryMap.put(VfArc.SINGLETON, VfArcFXNodeFactoryImpl.getSingleton());
		shapeTypeFactoryMap.put(VfCircle.SINGLETON, VfCircleFXNodeFactoryImpl.getSingleton());
		shapeTypeFactoryMap.put(VfCubicCurve.SINGLETON, VfCubicCurveFXNodeFactoryImpl.getSingleton());
		shapeTypeFactoryMap.put(VfEllipse.SINGLETON, VfEllipseFXNodeFactoryImpl.getSingleton());
		shapeTypeFactoryMap.put(VfLine.SINGLETON, VfLineFXNodeFactoryImpl.getSingleton());
		shapeTypeFactoryMap.put(VfQuadCurve.SINGLETON, VfQuadCurveFXNodeFactoryImpl.getSingleton());
		shapeTypeFactoryMap.put(VfRectangle.SINGLETON, VfRectangleFXNodeFactoryImpl.getSingleton());
		shapeTypeFactoryMap.put(VfText.SINGLETON, VfTextFXNodeFactoryImpl.getSingleton());
	}
	
	/**
	 * 
	 * @param shapeType
	 * @return
	 */
	public static VfShapeTypeFXNodeFactory<?,?> getFactory(VfShapeType shapeType) {
		//for test
//		boolean b = 
//				shapeType.equals(VfCircle.SINGLETON);
//		boolean b2 = 
//				shapeType.getGraphicsPropertyTreeNameMap().get(new SimpleName("SHEAR")).equals(VfCircle.SINGLETON.getGraphicsPropertyTreeNameMap().get(new SimpleName("SHEAR")));
		
//		shapeType.getGraphicsPropertyTreeNameMap().forEach((name, tree)->{
//			GraphicsPropertyTree tree2 = VfCircle.SINGLETON.getGraphicsPropertyTreeNameMap().get(name);
//			
//			boolean same = tree.equals(tree2);
//			System.out.println();
//		});
		
		
		return shapeTypeFactoryMap.get(shapeType);
	}
	
//	public static void main(String[] args) {
//		
//		VfShapeType type = VfCircle.SINGLETON;
//		
//		VfShapeTypeFXNodeFactory<?,?> factory = shapeTypeFactoryMap.get(type);
//		
//		System.out.println();
//	}
}
