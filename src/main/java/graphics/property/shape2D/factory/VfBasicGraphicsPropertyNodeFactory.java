package graphics.property.shape2D.factory;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import basic.SimpleName;
import basic.VfNotes;
import basic.attribute.PrimitiveTypeVfAttributeFactory;
import graphics.property.node.GraphicsPropertyNonLeafNode;
import graphics.property.node.GraphicsPropertyLeafNode;
import graphics.property.node.GraphicsPropertyNode;

/**
 * singleton instance for visframe defined VfPrimitiveGraphicsProperty and VfGraphicsPropertyNonLeafNode
 * @author tanxu
 * 
 */
public final class VfBasicGraphicsPropertyNodeFactory {
	private static Set<GraphicsPropertyNode> ROOT_GRAPHICS_PROPERTY_NODE_SET;
	/**
	 * return the set of root GraphicsPropertyNode defined in this factory class
	 * @return
	 */
	public static Set<GraphicsPropertyNode> getRootGraphicsPropertyNodeSet(){
		if(ROOT_GRAPHICS_PROPERTY_NODE_SET==null) {
			ROOT_GRAPHICS_PROPERTY_NODE_SET = new LinkedHashSet<>();
			ROOT_GRAPHICS_PROPERTY_NODE_SET.add(LAYOUT_CART_2D);
			ROOT_GRAPHICS_PROPERTY_NODE_SET.add(THETA_IN_DEGREE);
			ROOT_GRAPHICS_PROPERTY_NODE_SET.add(LENDIST);
		}
		return ROOT_GRAPHICS_PROPERTY_NODE_SET;
	}
	
	////////////////////////////////////////
	/**
	 * value on a coordinate axis;
	 * exclusively used for layout coordinate's X,Y,...
	 * 		{@link #LAYOUT_X}, {@link #LAYOUT_Y}
	 * 
	 * no default value, thus must be explicitly assigned (mandatory);
	 * 
	 * note that for transform related coordinate, use {@link VfTransformGraphicsPropertyNodeFactory#TRANSFORM_COORD}
	 */
	public static final GraphicsPropertyLeafNode<Double> LAYOUT_COORD = new GraphicsPropertyLeafNode<>(
			PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
					new SimpleName("coord"),//SimpleName name, 
					VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
					e->{return true;}, //Predicate<T> nonNullValueConstraints,
					null,//T defaultValue, 
					false//boolean canBeNull
			));
	
	////////////////
	/**
	 * a coordinate on the x axis;
	 * exclusively used for layout of a Shape instance;
	 * 		{@link #LAYOUT_CART_2D}
	 * 
	 * for transform related coordinate use
	 * 		{@link VfTransformGraphicsPropertyNodeFactory#TRANSFORM_X}
	 */
    public static final GraphicsPropertyNonLeafNode LAYOUT_X
                = new GraphicsPropertyNonLeafNode(
                		new SimpleName("X"), new VfNotes(), 
                		ImmutableMap.of(
                				LAYOUT_COORD.getName(), LAYOUT_COORD
                		),
                		
                		ImmutableMap.of(
                				LAYOUT_COORD.getName(), ""
                		)
                	);
    
    /**
     * a coordinate on the y axis;
	 * exclusively used for layout of a Shape instance;
	 * 		{@link #LAYOUT_CART_2D}
	 * 
	 * for transform related coordinate use
	 * 		{@link VfTransformGraphicsPropertyNodeFactory#TRANSFORM_Y}
     */
    public static final GraphicsPropertyNonLeafNode LAYOUT_Y = 
    		new GraphicsPropertyNonLeafNode(
    				new SimpleName("Y"), new VfNotes(), 
		    		ImmutableMap.of(
		    				LAYOUT_COORD.getName(), LAYOUT_COORD
		    				),
		    		ImmutableMap.of(
		    				LAYOUT_COORD.getName(), ""
		    				)
		    		);
    

	
    /**
     * cartesian 2d coordinate for layout related properties of a shape;
     * 
     * for transform related coordinate, use {@link VfTransformGraphicsPropertyNodeFactory#TRANSFORM_CART_2D}
     */
    public static final GraphicsPropertyNonLeafNode LAYOUT_CART_2D = 
    		new GraphicsPropertyNonLeafNode(
    				new SimpleName("CART2D"), new VfNotes(),
    				ImmutableMap.of(
    						LAYOUT_X.getName(), LAYOUT_X,
    						LAYOUT_Y.getName(), LAYOUT_Y
		    				),
    				ImmutableMap.of(
    						LAYOUT_X.getName(), "",
    						LAYOUT_Y.getName(), ""
		    				)
    				);
    

    
    /////////////////
    /**
     * shape property in degree;
     * 
     * default value is 0.0
     */
    public static final GraphicsPropertyLeafNode<Double> THETA_IN_DEGREE = new GraphicsPropertyLeafNode<>(
    		PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
    				new SimpleName("TID"),//SimpleName name, 
    				VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    				e->{return true;}, //Predicate<T> nonNullValueConstraints, can be of any numeric value;
    				0.0,//T defaultValue, 
    				false//boolean canBeNull
    				));
    
    
    /**
     * 122620-update
     * 
     * length or radius that must be positive;
     * 
     * used for radius or height/width that must be positive;
     * 
     * note that for arc height and arc width of rectangle, they can be 0.0 (default value), thus must be represented by another GraphicsPropertyLeafNode
     * 		see {@link VfRectangleShapeTypeSpecificGraphicsPropertyNodeFactory#ARC_DIAMETER}
     * 
     */
    public static final GraphicsPropertyLeafNode<Double> LENDIST = new GraphicsPropertyLeafNode<>(
    		PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
    				new SimpleName("LENDIST"),//SimpleName name, 
    				VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    				e->{return e>0;}, //Predicate<T> nonNullValueConstraints,
//    				SQLDataTypeFactory.doubleType(),//???SQLDataType SQLDataType,
    				null,//T defaultValue, 
    				false//boolean canBeNull
    				));
    
    //////////////////////////////
    //value on a coordinate axis
  	
  	////////////////////////////
  	
}
