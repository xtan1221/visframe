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
import graphics.property.shape2D.factory.VfRectangleShapeTypeSpecificGraphicsPropertyNodeFactory;
import graphics.property.shape2D.factory.VfStrokeGraphicsPropertyNodeFactory;
import graphics.property.tree.GraphicsPropertyTree;
import graphics.shape.VfShapeTypeBase;

public class VfRectangle extends VfShapeTypeBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2027732755507610946L;

	///////////////////////////////////
	/**
     * a singleton object 
     */
    public static final VfRectangle SINGLETON;
    
    ///
    public final static SimpleName TYPE_NAME = new SimpleName("RECTANGLE");
    //*************mandatory (implicitly defined==> cannot be null and default value is null)
    public final static GraphicsPropertyTree UPPERLEFT_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("UPPERLEFT"), VfBasicGraphicsPropertyNodeFactory.LAYOUT_CART_2D, 
    				new VfNotes("Defines the coordinate of the upper-left corner of the rectangle."));
    public final static GraphicsPropertyTree HEIGHT_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("HEIGHT"), VfBasicGraphicsPropertyNodeFactory.LENDIST, 
    				new VfNotes("Defines the height of the rectangle."));
    public final static GraphicsPropertyTree WIDTH_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("WIDTH"), VfBasicGraphicsPropertyNodeFactory.LENDIST, 
    				new VfNotes("Defines the width of the rectangle."));
    
    
    //*************non-mandatory;
    /**
     * "The vertical diameter of the arc at the four corners of the rectangle. The rectangle will have rounded corners if and only if both of the arc width and arc height properties are greater than 0.0." 
     */
    public final static GraphicsPropertyTree ARCHEIGHT_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("ARCHEIGHT"), VfRectangleShapeTypeSpecificGraphicsPropertyNodeFactory.ARC_DIAMETER, 
    				new VfNotes("Defines the vertical diameter of the arc at the four corners of the rectangle."));
    /**
     * "The horizontal diameter of the arc at the four corners of the rectangle. The rectangle will have rounded corners if and only if both of the arc width and arc height properties are greater than 0.0."
     */
    public final static GraphicsPropertyTree ARCHWIDTH_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("ARCHWIDTH"), VfRectangleShapeTypeSpecificGraphicsPropertyNodeFactory.ARC_DIAMETER, 
    				new VfNotes("Defines the horizontal diameter of the arc at the four corners of the rectangle."));
    
    public final static GraphicsPropertyTree FILLCOLOR_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("FILLCOLOR"), VfColorGraphicsPropertyNodeFactory.FILL_COLOR, 
    				new VfNotes("Defines parameters to fill the interior of a shape with either transparent or a valid color."));
    public final static GraphicsPropertyTree BORDERCOLOR_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("BORDERCOLOR"), VfColorGraphicsPropertyNodeFactory.RGB_OPACITY_COLOR, 
    				new VfNotes("Defines the color of border line of a shape."));
    
    public final static GraphicsPropertyTree STROKE_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("STROKE"), VfStrokeGraphicsPropertyNodeFactory.ARC_RECTANGLE_STROKE, 
    				new VfNotes("Defines parameters of a stroke that is drawn around the outline of a shape."));
    
    /**
     * initialize
     */
    static{
        Map<SimpleName, GraphicsPropertyTree> rectangleSpecificPropertyTreeNameMap = new LinkedHashMap<>();
        rectangleSpecificPropertyTreeNameMap.put(UPPERLEFT_PROPERTY_TREE.getName(), UPPERLEFT_PROPERTY_TREE);
        rectangleSpecificPropertyTreeNameMap.put(HEIGHT_PROPERTY_TREE.getName(), HEIGHT_PROPERTY_TREE);
        rectangleSpecificPropertyTreeNameMap.put(WIDTH_PROPERTY_TREE.getName(), WIDTH_PROPERTY_TREE);
        rectangleSpecificPropertyTreeNameMap.put(ARCHEIGHT_PROPERTY_TREE.getName(), ARCHEIGHT_PROPERTY_TREE);
        rectangleSpecificPropertyTreeNameMap.put(ARCHWIDTH_PROPERTY_TREE.getName(), ARCHWIDTH_PROPERTY_TREE);
        rectangleSpecificPropertyTreeNameMap.put(FILLCOLOR_PROPERTY_TREE.getName(), FILLCOLOR_PROPERTY_TREE);
        rectangleSpecificPropertyTreeNameMap.put(BORDERCOLOR_PROPERTY_TREE.getName(), BORDERCOLOR_PROPERTY_TREE);
        rectangleSpecificPropertyTreeNameMap.put(STROKE_PROPERTY_TREE.getName(), STROKE_PROPERTY_TREE);

        //
        SINGLETON = new VfRectangle(TYPE_NAME, new VfNotes(), rectangleSpecificPropertyTreeNameMap);
    }
    
    
    ////////////////////////
	private VfRectangle(SimpleName name, VfNotes notes,
			Map<SimpleName, GraphicsPropertyTree> shapeSpecificGraphicsPropertyTreeNameMap) {
		super(name, notes, shapeSpecificGraphicsPropertyTreeNameMap);
		// TODO Auto-generated constructor stub
	}

	/**
	 * return the upper left x property
	 */
	@Override
	public LeafGraphicsPropertyCFGTarget<?> getXLayoutLeafTarget() {
		return this.getLeafGraphicsPropertyCFGTargetNameMap()
				.get(PropertyTreeNodePathNameBuilder.start(UPPERLEFT_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build());
	}

	/**
	 * return the upper left y property
	 */
	@Override
	public LeafGraphicsPropertyCFGTarget<?> getYLayoutLeafTarget() {
		return this.getLeafGraphicsPropertyCFGTargetNameMap()
				.get(PropertyTreeNodePathNameBuilder.start(UPPERLEFT_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build());
	}

}
