package graphics.property.tree;

import static graphics.property.shape2D.factory.VfTransformGraphicsPropertyNodeFactory.ROTATE;
import static graphics.property.shape2D.factory.VfTransformGraphicsPropertyNodeFactory.SCALE;
import static graphics.property.shape2D.factory.VfTransformGraphicsPropertyNodeFactory.SHEAR;
import static graphics.property.shape2D.factory.VfTransformGraphicsPropertyNodeFactory.TRANSLATE;

import basic.SimpleName;
import basic.VfNotes;

/**
 * contains the singleton instance of all transform related graphics property related {@link GraphicsPropertyTree}s defined in visframe;
 * 
 * @author tanxu
 *
 */
public class VfTransformPropertyTreeFactory {
	
	public static final GraphicsPropertyTree TRANSLATE_TREE = 
			new GraphicsPropertyTree(new SimpleName("TRANSLATE"),TRANSLATE,new VfNotes("Translation transform."));
	public static final GraphicsPropertyTree SCALE_TREE = 
			new GraphicsPropertyTree(new SimpleName("SCALE"),SCALE,new VfNotes("Scaling transform."));
	public static final GraphicsPropertyTree SHEAR_TREE = 
			new GraphicsPropertyTree(new SimpleName("SHEAR"),SHEAR,new VfNotes("Shearing transform."));
	public static final GraphicsPropertyTree ROTATE_TREE = 
			new GraphicsPropertyTree(new SimpleName("ROTATE"),ROTATE,new VfNotes("Rotation transform."));
	
	
}
