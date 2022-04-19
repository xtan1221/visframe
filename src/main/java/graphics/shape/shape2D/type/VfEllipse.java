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
public class VfEllipse extends VfShapeTypeBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 320226129829376464L;

	//////////////////////////
	/**
	 * static factory method that returns the singleton instance of {@link VfEllipse}
	 */
	public static final VfEllipse SINGLETON;
	
	///////////////////////
	private final static SimpleName TYPE_NAME = new SimpleName("Ellipse");
	
	//*************mandatory (implicitly defined==> cannot be null and default value is null)
	public final static GraphicsPropertyTree CENTER_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("CENTER"), VfBasicGraphicsPropertyNodeFactory.LAYOUT_CART_2D, 
    				new VfNotes("Defines the position of the center of the ellipse in pixels."));
	public final static GraphicsPropertyTree RADIUS_X_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("RADIUSX"),VfBasicGraphicsPropertyNodeFactory.LENDIST, 
    				new VfNotes("Defines the width of the ellipse in pixels."));
	public final static GraphicsPropertyTree RADIUS_Y_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("RADIUSY"),VfBasicGraphicsPropertyNodeFactory.LENDIST, 
    				new VfNotes("Defines the height of the ellipse in pixels."));
	
	//*************non-mandatory;
	public final static GraphicsPropertyTree FILLCOLOR_PROPERTY_TREE = 
			new GraphicsPropertyTree(
					new SimpleName("FILLCOLOR"),VfColorGraphicsPropertyNodeFactory.FILL_COLOR, 
					new VfNotes("Defines parameters to fill the interior of a shape with either transparent or a valid color."));
    public final static GraphicsPropertyTree BORDERCOLOR_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("BORDERCOLOR"),VfColorGraphicsPropertyNodeFactory.RGB_OPACITY_COLOR, 
    				new VfNotes("Defines the color of border line of a shape."));
    public final static GraphicsPropertyTree STROKE_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("STROKE"), VfStrokeGraphicsPropertyNodeFactory.CIRCLE_ELLIPSE_STROKE, 
    				new VfNotes("Defines parameters of a stroke that is drawn around the outline of a shape."));
    
    
    /**
     * initialize the SINGLETON
     */
    static {
        Map<SimpleName, GraphicsPropertyTree> typeSpecificPropertyTreeNameMap = new LinkedHashMap<>();
        
        typeSpecificPropertyTreeNameMap.put(CENTER_PROPERTY_TREE.getName(), CENTER_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(RADIUS_X_PROPERTY_TREE.getName(), RADIUS_X_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(RADIUS_Y_PROPERTY_TREE.getName(), RADIUS_Y_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(FILLCOLOR_PROPERTY_TREE.getName(), FILLCOLOR_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(BORDERCOLOR_PROPERTY_TREE.getName(), BORDERCOLOR_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(STROKE_PROPERTY_TREE.getName(), STROKE_PROPERTY_TREE);
        //
        SINGLETON = new VfEllipse(TYPE_NAME, new VfNotes(), typeSpecificPropertyTreeNameMap);
    }
	
	
	
	/**
	 * private constructor
	 * @param name
	 * @param notes
	 * @param shapeSpecificGraphicsPropertyTreeNameMap
	 */
	private VfEllipse(SimpleName typeName, VfNotes notes, Map<SimpleName, GraphicsPropertyTree> typeSpecificPropertyTreeNameMap) {
		super(TYPE_NAME, notes, typeSpecificPropertyTreeNameMap);
		// TODO Auto-generated constructor stub
	}

	@Override
	public LeafGraphicsPropertyCFGTarget<?> getXLayoutLeafTarget() {
		return this.getLeafGraphicsPropertyCFGTargetNameMap().get(PropertyTreeNodePathNameBuilder.start(CENTER_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build());
	}

	@Override
	public LeafGraphicsPropertyCFGTarget<?> getYLayoutLeafTarget() {
		return this.getLeafGraphicsPropertyCFGTargetNameMap().get(PropertyTreeNodePathNameBuilder.start(CENTER_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build());
	}
}
