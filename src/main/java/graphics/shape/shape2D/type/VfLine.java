package graphics.shape.shape2D.type;

import static graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory.*;

import java.util.LinkedHashMap;
import java.util.Map;

import basic.SimpleName;
import basic.VfNotes;
import function.target.LeafGraphicsPropertyCFGTarget;
import graphics.property.node.PropertyTreeNodePathNameBuilder;
import graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory;
import graphics.property.shape2D.factory.VfColorGraphicsPropertyNodeFactory;
import graphics.property.shape2D.factory.VfStrokeGraphicsPropertyNodeFactory;
import graphics.property.tree.GraphicsPropertyTree;
import graphics.shape.VfShapeTypeBase;

/**
 * class for a Line shape defined in visframe;
 * 
 * @author tanxu
 *
 */
public class VfLine extends VfShapeTypeBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5373218444751130166L;
	
	
	//////////////////////////
	/**
	 * static factory method that returns the singleton instance of {@link VfLine}
	 */
	public static final VfLine SINGLETON;
	
	///////////////////////
	private final static SimpleName TYPE_NAME = new SimpleName("LINE");
	//*************mandatory (implicitly defined==> cannot be null and default value is null)
	public final static GraphicsPropertyTree START_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("START"), VfBasicGraphicsPropertyNodeFactory.LAYOUT_CART_2D, 
    				new VfNotes("The coordinate of the start point of the line segment."));
	public final static GraphicsPropertyTree END_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("END"),VfBasicGraphicsPropertyNodeFactory.LAYOUT_CART_2D, 
    				new VfNotes("The coordinate of the end point of the line segment."));
	
	//*************non-mandatory;
	public final static GraphicsPropertyTree COLOR_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("COLOR"),VfColorGraphicsPropertyNodeFactory.RGB_OPACITY_COLOR, 
    				new VfNotes());
	public final static GraphicsPropertyTree STROKE_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("STROKE"), VfStrokeGraphicsPropertyNodeFactory.CURVE_LINE_STROKE, 
    				new VfNotes());
    
    /**
     * initialize the SINGLETON
     */
    static {
        Map<SimpleName, GraphicsPropertyTree> typeSpecificPropertyTreeNameMap = new LinkedHashMap<>();
        
        typeSpecificPropertyTreeNameMap.put(START_PROPERTY_TREE.getName(), START_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(END_PROPERTY_TREE.getName(), END_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(COLOR_PROPERTY_TREE.getName(), COLOR_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(STROKE_PROPERTY_TREE.getName(), STROKE_PROPERTY_TREE);
        
        //
        SINGLETON = new VfLine(TYPE_NAME, new VfNotes(), typeSpecificPropertyTreeNameMap);
    }
	
	
	/**
	 * private constructor
	 * @param name
	 * @param notes
	 * @param shapeSpecificGraphicsPropertyTreeNameMap
	 */
	private VfLine(SimpleName typeName, VfNotes notes, Map<SimpleName, GraphicsPropertyTree> typeSpecificPropertyTreeNameMap) {
		super(TYPE_NAME, notes, typeSpecificPropertyTreeNameMap);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * return the start x property of the line;
	 */
	@Override
	public LeafGraphicsPropertyCFGTarget<?> getXLayoutLeafTarget() {
		SimpleName startXAttributeName = PropertyTreeNodePathNameBuilder.start(START_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
		return this.getLeafGraphicsPropertyCFGTargetNameMap().get(startXAttributeName);
	}
	
	
	/**
	 * return the start Y property of the line;
	 */
	@Override
	public LeafGraphicsPropertyCFGTarget<?> getYLayoutLeafTarget() {
		SimpleName startYAttributeName = 
				PropertyTreeNodePathNameBuilder.start(START_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();
        
		return this.getLeafGraphicsPropertyCFGTargetNameMap().get(startYAttributeName);
	}
	
	
}
