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
import graphics.property.shape2D.factory.VfTextShapeTypeSpecificGraphicsPropertyNodeFactory;
import graphics.property.tree.GraphicsPropertyTree;
import graphics.shape.VfShapeTypeBase;

/**
 * class for a Line shape defined in visframe;
 * 
 * @author tanxu
 *
 */
public class VfText extends VfShapeTypeBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6263197793876385081L;

	//////////////////////////
	/**
	 * static factory method that returns the singleton instance of {@link VfText}
	 */
	public static final VfText SINGLETON;
	
	///////////////////////
	private final static SimpleName TYPE_NAME = new SimpleName("Text");
	//*************mandatory (implicitly defined==> cannot be null and default value is null)
	//text origin coordinate
	public final static GraphicsPropertyTree UPPERLEFT_COORDINATE_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("UpperLeftCoordinate"), VfBasicGraphicsPropertyNodeFactory.LAYOUT_CART_2D, 
    				new VfNotes("coordinate of text origin"));
	
	//text content
	public final static GraphicsPropertyTree CONTENT_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("CONTENT"), VfTextShapeTypeSpecificGraphicsPropertyNodeFactory.TEXT_CONTENT_10000, 
    				new VfNotes("Defines text string that is to be displayed."));
	
	
	//*************non-mandatory;
	//text color
	public final static GraphicsPropertyTree COLOR_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("COLOR"), VfColorGraphicsPropertyNodeFactory.RGB_OPACITY_COLOR, 
    				new VfNotes("text color."));
	
	//font
	public final static GraphicsPropertyTree FONT_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("FONT"), VfTextShapeTypeSpecificGraphicsPropertyNodeFactory.FONT, 
    				new VfNotes("Defines the font of text."));
	
	//text origin; Defines the origin of text coordinate system in local coordinates.
	public final static GraphicsPropertyTree ORIGIN_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("ORIGIN"), VfTextShapeTypeSpecificGraphicsPropertyNodeFactory.TEXT_ORIGIN, 
    				new VfNotes("Defines the origin of text coordinate system in local coordinates."));
	
	//text bounds type
	public final static GraphicsPropertyTree BOUNDS_TYPE_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("BoundsType"), VfTextShapeTypeSpecificGraphicsPropertyNodeFactory.TEXT_BOUNDS_TYPE, 
    				new VfNotes("Determines how the bounds of the text node are calculated."));
	
	//text wrapping width
	public final static GraphicsPropertyTree WRAPPING_WIDTH_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("WrappingWidth"), VfTextShapeTypeSpecificGraphicsPropertyNodeFactory.TEXT_WRAPPING_WIDTH, 
    				new VfNotes("Defines a width constraint for the text in user space coordinates, e.g. pixels, not glyph or character count. If the value is > 0 text will be line wrapped as needed to satisfy this constraint."));
	
	//text is underlined
	public final static GraphicsPropertyTree IS_UNDERLINED_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("IsUnderlined"), VfTextShapeTypeSpecificGraphicsPropertyNodeFactory.TEXT_IS_UNDERLINED, 
    				new VfNotes("Defines if each line of text should have a line below it."));
	
	//text is strike through
	public final static GraphicsPropertyTree IS_STRIKE_THROUGH_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("IsStrikeThrough"), VfTextShapeTypeSpecificGraphicsPropertyNodeFactory.TEXT_IS_STRIKE_THROUGH, 
    				new VfNotes("Defines if each line of text should have a line through it."));
	
	//text alignment type
	public final static GraphicsPropertyTree ALIGNMENT_TYPE_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("AlignmentType"), VfTextShapeTypeSpecificGraphicsPropertyNodeFactory.TEXT_ALIGNMENT_TYPE, 
    				new VfNotes("Defines horizontal text alignment in the bounding box."));
	
	//text line spacing
	public final static GraphicsPropertyTree LINE_SPACING_PROPERTY_TREE = 
    		new GraphicsPropertyTree(
    				new SimpleName("LineSpacing"), VfTextShapeTypeSpecificGraphicsPropertyNodeFactory.TEXT_LINE_SPACING,
    				new VfNotes("Defines the vertical space in pixel between lines."));
	
    
    /**
     * initialize the SINGLETON
     */
    static {
        Map<SimpleName, GraphicsPropertyTree> typeSpecificPropertyTreeNameMap = new LinkedHashMap<>();
        
        typeSpecificPropertyTreeNameMap.put(UPPERLEFT_COORDINATE_PROPERTY_TREE.getName(), UPPERLEFT_COORDINATE_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(CONTENT_PROPERTY_TREE.getName(), CONTENT_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(COLOR_PROPERTY_TREE.getName(), COLOR_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(FONT_PROPERTY_TREE.getName(), FONT_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(ORIGIN_PROPERTY_TREE.getName(), ORIGIN_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(BOUNDS_TYPE_PROPERTY_TREE.getName(), BOUNDS_TYPE_PROPERTY_TREE);
        
        typeSpecificPropertyTreeNameMap.put(WRAPPING_WIDTH_PROPERTY_TREE.getName(), WRAPPING_WIDTH_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(IS_UNDERLINED_PROPERTY_TREE.getName(), IS_UNDERLINED_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(IS_STRIKE_THROUGH_PROPERTY_TREE.getName(), IS_STRIKE_THROUGH_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(ALIGNMENT_TYPE_PROPERTY_TREE.getName(), ALIGNMENT_TYPE_PROPERTY_TREE);
        typeSpecificPropertyTreeNameMap.put(LINE_SPACING_PROPERTY_TREE.getName(), LINE_SPACING_PROPERTY_TREE);
        
        //
        SINGLETON = new VfText(TYPE_NAME, new VfNotes(), typeSpecificPropertyTreeNameMap);
    }
	
	
	
	/**
	 * private constructor
	 * @param name
	 * @param notes
	 * @param shapeSpecificGraphicsPropertyTreeNameMap
	 */
	private VfText(SimpleName typeName, VfNotes notes, Map<SimpleName, GraphicsPropertyTree> typeSpecificPropertyTreeNameMap) {
		super(TYPE_NAME, notes, typeSpecificPropertyTreeNameMap);
		// TODO Auto-generated constructor stub
	}


	/**
	 * return x
	 */
	@Override
	public LeafGraphicsPropertyCFGTarget<?> getXLayoutLeafTarget() {
		return this.getLeafGraphicsPropertyCFGTargetNameMap().get(PropertyTreeNodePathNameBuilder.start(UPPERLEFT_COORDINATE_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build());
	}


	/**
	 * return y
	 */
	@Override
	public LeafGraphicsPropertyCFGTarget<?> getYLayoutLeafTarget() {
		return this.getLeafGraphicsPropertyCFGTargetNameMap().get(PropertyTreeNodePathNameBuilder.start(UPPERLEFT_COORDINATE_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build());
	}
}
