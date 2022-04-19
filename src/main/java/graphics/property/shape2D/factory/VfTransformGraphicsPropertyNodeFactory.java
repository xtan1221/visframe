package graphics.property.shape2D.factory;

import static graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory.*;

import com.google.common.collect.ImmutableMap;

import basic.SimpleName;
import basic.VfNotes;
import basic.attribute.PrimitiveTypeVfAttributeFactory;
import graphics.property.node.GraphicsPropertyNonLeafNode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Translate;
import graphics.property.node.GraphicsPropertyLeafNode;

public class VfTransformGraphicsPropertyNodeFactory {
	/**
	 * value on a coordinate axis;
	 * exclusively used for transform coordinate's X,Y,...
	 * 
	 * has default value 0.0, thus optional;
	 * 
	 * different from {@link VfBasicGraphicsPropertyNodeFactory#LAYOUT_COORD} which has no default value;
	 * 
	 */
	public static final GraphicsPropertyLeafNode<Double> TRANSFORM_COORD = new GraphicsPropertyLeafNode<>(
			PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
					new SimpleName("TRANSFORMCOORD"),//SimpleName name, 
					VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
					e->{return true;}, //Predicate<T> nonNullValueConstraints,
					0.0,//T defaultValue, 
					false//boolean canBeNull
			));
	
    /**
     * "a coordinate on the x axis or a length/distance/value on x axis direction";
	 * exclusively used for transform of a Shape instance;
	 * 		{@link #TRANSFORM_CART_2D}
	 * for layout related coordinate use
	 * 		{@link VfBasicGraphicsPropertyNodeFactory#LAYOUT_X}
     */
    public static final GraphicsPropertyNonLeafNode TRANSFORM_X = 
    		new GraphicsPropertyNonLeafNode(
		    		new SimpleName("TRANSFORMX"), new VfNotes(), 
		    		ImmutableMap.of(
		    				TRANSFORM_COORD.getName(), TRANSFORM_COORD
		    		),
		    		
		    		ImmutableMap.of(
		    				TRANSFORM_COORD.getName(), ""
		    		)
	    	);

    /**
     * "a coordinate on the Y axis or a length/distance/value on x axis direction";
	 * exclusively used for transform of a Shape instance;
	 * 		{@link #TRANSFORM_CART_2D}
	 * for layout related coordinate use
	 * 		{@link VfBasicGraphicsPropertyNodeFactory#LAYOUT_Y}
     */
	public static final GraphicsPropertyNonLeafNode TRANSFORM_Y = 
			new GraphicsPropertyNonLeafNode(
					new SimpleName("TRANSFORMY"), new VfNotes(), 
					ImmutableMap.of(
							TRANSFORM_COORD.getName(), TRANSFORM_COORD
							),
					ImmutableMap.of(
							TRANSFORM_COORD.getName(), ""
							)
					);
    
	
    /**
     * Cartesian 2d coordinate for transform related properties of a Shape;
     * 
     * for layout related coordinate, use {@link VfBasicGraphicsPropertyNodeFactory#LAYOUT_CART_2D}
     */
    public static final GraphicsPropertyNonLeafNode TRANSFORM_CART_2D = 
    		new GraphicsPropertyNonLeafNode(
    				new SimpleName("CART2DTRANSFORMCOORD"), new VfNotes(),
    				ImmutableMap.of(
    						TRANSFORM_X.getName(), TRANSFORM_X,
    						TRANSFORM_Y.getName(), TRANSFORM_Y
		    				),
    				ImmutableMap.of(
    						TRANSFORM_X.getName(), "",
    						TRANSFORM_Y.getName(), ""
		    				)
    				);
    
    /////////////////////////
    /**
     * translate transformation
     * based on javafx {@link Translate}
     */
    public static final GraphicsPropertyNonLeafNode TRANSLATE = 
    		new GraphicsPropertyNonLeafNode(
    				new SimpleName("TRANSLATE"), new VfNotes(),
    				ImmutableMap.of(
    						TRANSFORM_X.getName(), TRANSFORM_X,
		    				TRANSFORM_Y.getName(), TRANSFORM_Y
		    				),
    				ImmutableMap.of(
    						TRANSFORM_X.getName(), "Distance by which coordinates are translated in the X axis direction",
    						TRANSFORM_Y.getName(), "Distance by which coordinates are translated in the Y axis direction"
		    				)
    				);
    
    ///////////////////
    /**
     * ratio used in transform; default is 1.0
     */
    public static final GraphicsPropertyLeafNode<Double> RATIO = 
    		new GraphicsPropertyLeafNode<>(
    				PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
							new SimpleName("RATIO"),//SimpleName name, 
							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
							e->{return true;}, //Predicate<T> nonNullValueConstraints, ??????
							1.0,//T defaultValue, 
							false//boolean canBeNull
					));
    
    /**
     * Defines the factor by which coordinates are scaled along the X axis direction. The default value is 1.0.
     * see {@link Scale#getX()}
     */
    public static final GraphicsPropertyNonLeafNode SCALEX
                = new GraphicsPropertyNonLeafNode(
                		new SimpleName("SCALEX"), new VfNotes(), 
                		ImmutableMap.of(
                				RATIO.getName(), RATIO
                		),
                		ImmutableMap.of(
                				RATIO.getName(), ""
                		)
                	);
    /**
     * Defines the factor by which coordinates are scaled along the Y axis direction. The default value is 1.0.
     * see {@link Scale#getY()}
     */
    public static final GraphicsPropertyNonLeafNode SCALEY
                = new GraphicsPropertyNonLeafNode(
                		new SimpleName("SCALEY"), new VfNotes(), 
                		ImmutableMap.of(
                				RATIO.getName(), RATIO
                		),
                		ImmutableMap.of(
                				RATIO.getName(), ""
                		)
                	);
    
    /**
     * 
     */
    public static final GraphicsPropertyNonLeafNode SCALEXY = 
    		new GraphicsPropertyNonLeafNode(
    				new SimpleName("SCALEXY"), new VfNotes(),
    				ImmutableMap.of(
    						SCALEX.getName(), SCALEX,
    						SCALEY.getName(), SCALEY
		    				),
    				ImmutableMap.of(
    						SCALEX.getName(), "",
    						SCALEY.getName(), ""
		    				)
    				);
    
    /**
     * based on javafx {@link Scale#Scale(double, double, double, double)} ??
     */
    public static final GraphicsPropertyNonLeafNode SCALE = 
    		new GraphicsPropertyNonLeafNode(
    				new SimpleName("SCALE"), new VfNotes(),
    				ImmutableMap.of(
    						SCALEXY.getName(), SCALEXY,
    						TRANSFORM_CART_2D.getName(), TRANSFORM_CART_2D
		    				),
    				ImmutableMap.of(
    						SCALEXY.getName(), "",
    						TRANSFORM_CART_2D.getName(), "SCALE PIVOT: Defines the coordinate about which point the scale occurs"
		    				)
    				);
    
    ///////////////////////////////
    /**
     * Defines the multiplier by which coordinates are shifted
     */
    public static final GraphicsPropertyLeafNode<Double> MULTIPLIER = 
    		new GraphicsPropertyLeafNode<>(
    				PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
							new SimpleName("MULTIPLIER"),//SimpleName name, 
							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
							e->{return e>-1 && e<1;}, //Predicate<T> nonNullValueConstraints, ??????
							0.0,//T defaultValue, 
							false//boolean canBeNull
					));
    
    /**
     * based on javafx {@link Shear#getX()}
     */
    public static final GraphicsPropertyNonLeafNode SHEARX
                = new GraphicsPropertyNonLeafNode(
                		new SimpleName("SHEARX"), new VfNotes(), 
                		ImmutableMap.of(
                				MULTIPLIER.getName(), MULTIPLIER
                		),
                		ImmutableMap.of(
                				MULTIPLIER.getName(), "Defines the multiplier by which coordinates are shifted in the direction of the positive X axis as a factor of their Y coordinate. Typical values are in the range -1 to 1, exclusive."
                		)
                	);
    /**
     * based on javafx {@link Shear#getY()}
     */
    public static final GraphicsPropertyNonLeafNode SHEARY
                = new GraphicsPropertyNonLeafNode(
                		new SimpleName("SHEARY"), new VfNotes(), 
                		ImmutableMap.of(
                				MULTIPLIER.getName(), MULTIPLIER
                		),
                		ImmutableMap.of(
                				MULTIPLIER.getName(), "Defines the multiplier by which coordinates are shifted in the direction of the positive Y axis as a factor of their X coordinate. Typical values are in the range -1 to 1, exclusive."
                		)
                	);
    /**
     * 
     */
    public static final GraphicsPropertyNonLeafNode SHEARXY = 
    		new GraphicsPropertyNonLeafNode(
    				new SimpleName("SHEARXY"), new VfNotes(),
    				ImmutableMap.of(
    						SHEARX.getName(), SHEARX,
    						SHEARY.getName(), SHEARY
		    				),
    				ImmutableMap.of(
    						SHEARX.getName(), "",
    						SHEARY.getName(), ""
		    				)
    				);
    
    /**
     * based on javafx {@link Shear#Shear(double, double, double, double)} ???
     */
    public static final GraphicsPropertyNonLeafNode SHEAR = 
    		new GraphicsPropertyNonLeafNode(
    				new SimpleName("SHEAR"), new VfNotes(),
    				ImmutableMap.of(
    						SHEARXY.getName(), SHEARXY,
    						TRANSFORM_CART_2D.getName(), TRANSFORM_CART_2D
		    				),
    				ImmutableMap.of(
    						SHEARXY.getName(), "",
    						TRANSFORM_CART_2D.getName(), "SHEAR PIVOT: Defines the coordinate about which point the SHEAR occurs."
		    				)
    				);
    
    //////////////////////////////////
    /**
     * based on javafx {@link Rotate} 
     */
    public static final GraphicsPropertyNonLeafNode ROTATE = 
    		new GraphicsPropertyNonLeafNode(
    				new SimpleName("ROTATE"), new VfNotes(),
    				ImmutableMap.of(
    						THETA_IN_DEGREE.getName(), THETA_IN_DEGREE,
    						TRANSFORM_CART_2D.getName(), TRANSFORM_CART_2D
		    				),
    				ImmutableMap.of(
    						THETA_IN_DEGREE.getName(), "",
    						TRANSFORM_CART_2D.getName(), "ROTATE PIVOT: Defines the coordinate about which point the ROTATE occurs"
		    				)
    				);
}
