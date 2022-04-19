package graphics.shape.shape2D.type;

import static graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory.*;

import java.util.LinkedHashMap;
import java.util.Map;

import basic.SimpleName;
import basic.VfNotes;
import function.target.LeafGraphicsPropertyCFGTarget;
import graphics.property.node.PropertyTreeNodePathNameBuilder;
import graphics.property.shape2D.factory.VfArcClosureTypeGraphicsPropertyNodeFactory;
import graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory;
import graphics.property.shape2D.factory.VfColorGraphicsPropertyNodeFactory;
import graphics.property.shape2D.factory.VfStrokeGraphicsPropertyNodeFactory;
import graphics.property.tree.GraphicsPropertyTree;
import graphics.shape.VfShapeTypeBase;

/**
 * class for a Arc shape defined in visframe;
 * 
 * mandatory properties include
 * 1. center coordinate
 * 		x and y
 * 2. radius
 * 		x and y
 * 
 * @author tanxu
 *
 */
public class VfArc extends VfShapeTypeBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -708257906816565793L;
	
	//////////////////////////
	/**
	 * static factory method that returns the singleton instance of {@link VfArc}
	 */
	public static final VfArc SINGLETON;
	
	///////////////////////
	private final static SimpleName TYPE_NAME = new SimpleName("Arc");
	//*************mandatory (implicitly defined==> cannot be null and default value is null)
	public final static GraphicsPropertyTree CENTER_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("CENTER"), VfBasicGraphicsPropertyNodeFactory.LAYOUT_CART_2D, 
    				new VfNotes("Defines the coordinate of the center point of the arc."));
	public final static GraphicsPropertyTree RADIUS_X_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("RADIUSX"), VfBasicGraphicsPropertyNodeFactory.LENDIST, 
    				new VfNotes("Defines the overall width (horizontal radius) of the full ellipse of which this arc is a partial section."));
	public final static GraphicsPropertyTree RADIUS_Y_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("RADIUSY"), VfBasicGraphicsPropertyNodeFactory.LENDIST, 
    				new VfNotes("Defines the overall height (vertical radius) of the full ellipse of which this arc is a partial section."));
	
	//*************non-mandatory;
	public final static GraphicsPropertyTree START_ANGLE_PROPERTY_TREE = 
			new GraphicsPropertyTree(
    				new SimpleName("StartAngle"), VfBasicGraphicsPropertyNodeFactory.THETA_IN_DEGREE, 
    				new VfNotes("Defines the starting angle of the arc in degrees"));
	//note that Arc's angular extent can be 0.0 (default)
	public final static GraphicsPropertyTree LENGTH_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("Length"), VfBasicGraphicsPropertyNodeFactory.THETA_IN_DEGREE, 
    				new VfNotes("Defines the angular extent of the arc in degrees"));
	public final static GraphicsPropertyTree ARC_CLOSURE_TYPE_PROPERTY_TREE = 
    		new GraphicsPropertyTree(new SimpleName("ArcClosureType"), VfArcClosureTypeGraphicsPropertyNodeFactory.ARC_CLOSURE_TYPE, 
    				new VfNotes("Defines the closure type for the arc"));
	
	//fill color; can be transparent
	public final static GraphicsPropertyTree FILLCOLOR_PROPERTY_TREE = 
			new GraphicsPropertyTree(
					new SimpleName("FILLCOLOR"), VfColorGraphicsPropertyNodeFactory.FILL_COLOR, 
					new VfNotes("Defines parameters to fill the interior of a shape with either transparent or a valid color."));
	
	//border color;
    public final static GraphicsPropertyTree BORDERCOLOR_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("BORDERCOLOR"),VfColorGraphicsPropertyNodeFactory.RGB_OPACITY_COLOR, 
    				new VfNotes("Defines the color of border line of a shape."));
    //stroke
    public final static GraphicsPropertyTree STROKE_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("STROKE"), VfStrokeGraphicsPropertyNodeFactory.ARC_RECTANGLE_STROKE, 
    				new VfNotes("Defines parameters of a stroke that is drawn around the outline of a shape."));
    
    
    /**
     * initialize the SINGLETON
     */
    static {
        Map<SimpleName, GraphicsPropertyTree> typeSpecificPropertyTreeNameMap = new LinkedHashMap<>();
        
        typeSpecificPropertyTreeNameMap.put(CENTER_PROPERTY_TREE.getName(), CENTER_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(RADIUS_X_PROPERTY_TREE.getName(), RADIUS_X_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(RADIUS_Y_PROPERTY_TREE.getName(), RADIUS_Y_PROPERTY_TREE);
        
        typeSpecificPropertyTreeNameMap.put(START_ANGLE_PROPERTY_TREE.getName(), START_ANGLE_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(LENGTH_PROPERTY_TREE.getName(), LENGTH_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(ARC_CLOSURE_TYPE_PROPERTY_TREE.getName(), ARC_CLOSURE_TYPE_PROPERTY_TREE);
        
        typeSpecificPropertyTreeNameMap.put(FILLCOLOR_PROPERTY_TREE.getName(), FILLCOLOR_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(BORDERCOLOR_PROPERTY_TREE.getName(), BORDERCOLOR_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(STROKE_PROPERTY_TREE.getName(), STROKE_PROPERTY_TREE);
        
        //
        SINGLETON = new VfArc(TYPE_NAME, new VfNotes(), typeSpecificPropertyTreeNameMap);
    }
	
	
	
	/**
	 * private constructor
	 * @param name
	 * @param notes
	 * @param shapeSpecificGraphicsPropertyTreeNameMap
	 */
	private VfArc(SimpleName typeName, VfNotes notes, Map<SimpleName, GraphicsPropertyTree> typeSpecificPropertyTreeNameMap) {
		super(TYPE_NAME, notes, typeSpecificPropertyTreeNameMap);
		// TODO Auto-generated constructor stub
	}


	/**
	 * center x of the arc
	 */
	@Override
	public LeafGraphicsPropertyCFGTarget<?> getXLayoutLeafTarget() {
		return this.getLeafGraphicsPropertyCFGTargetNameMap().get(PropertyTreeNodePathNameBuilder.start(CENTER_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build());
	}


	/**
	 * center y of the arc
	 */
	@Override
	public LeafGraphicsPropertyCFGTarget<?> getYLayoutLeafTarget() {
		return this.getLeafGraphicsPropertyCFGTargetNameMap().get(PropertyTreeNodePathNameBuilder.start(CENTER_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build());
	}
}
