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

public class VfColorGraphicsPropertyNodeFactory {
	private static Set<GraphicsPropertyNode> ROOT_GRAPHICS_PROPERTY_NODE_SET;
	/**
	 * return the set of root GraphicsPropertyNode defined in this factory class
	 * @return
	 */
	public static Set<GraphicsPropertyNode> getRootGraphicsPropertyNodeSet(){
		if(ROOT_GRAPHICS_PROPERTY_NODE_SET==null) {
			ROOT_GRAPHICS_PROPERTY_NODE_SET = new LinkedHashSet<>();
			ROOT_GRAPHICS_PROPERTY_NODE_SET.add(RGB_OPACITY_COLOR);
		}
		return ROOT_GRAPHICS_PROPERTY_NODE_SET;
	}
	
	
	////////////////////////////////////////
	/**
	 * for color rgb
	 * default value is 0.0
	 */
	public static final GraphicsPropertyLeafNode<Double> PERCENTAGE0 = new GraphicsPropertyLeafNode<>(
			PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
					new SimpleName("PERCENTAGE0"),//SimpleName name, 
					VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
					e->{return e>=0 && e<=1;}, //Predicate<T> nonNullValueConstraints,
					0.0,//T defaultValue, 
					false//boolean canBeNull
			));
	
	/**
	 * for color opacity;
	 * default is 1.0?
	 */
	public static final GraphicsPropertyLeafNode<Double> PERCENTAGE1 = new GraphicsPropertyLeafNode<>(
			PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
					new SimpleName("PERCENTAGE1"),//SimpleName name, 
					VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
					e->{return e>=0 && e<=1;}, //Predicate<T> nonNullValueConstraints,
					1.0,//T defaultValue, 
					false//boolean canBeNull
			));
	
	/**
	 * whether the fill is transparent rather than a specific color
	 * default is true?
	 */
	public static final GraphicsPropertyLeafNode<Boolean> FILL_IS_TRANSPARENT = new GraphicsPropertyLeafNode<>(
			PrimitiveTypeVfAttributeFactory.booleanTypeVfAttribute(
					new SimpleName("FILLISTRANSPARENT"),//SimpleName name, 
					VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
					e->{return true;}, //Predicate<T> nonNullValueConstraints,
					true,//T defaultValue, 
					false//boolean canBeNull
			));
	
  	public static final GraphicsPropertyNonLeafNode R = 
  			new GraphicsPropertyNonLeafNode(
	    		new SimpleName("R"), new VfNotes(), 
	    		ImmutableMap.of(
	    				PERCENTAGE0.getName(), PERCENTAGE0
	    		),
	    		ImmutableMap.of(
	    				PERCENTAGE0.getName(), ""
	    		)
	    	);
  	public static final GraphicsPropertyNonLeafNode G = 
  			new GraphicsPropertyNonLeafNode(
	    		new SimpleName("G"), new VfNotes(), 
	    		ImmutableMap.of(
	    				PERCENTAGE0.getName(), PERCENTAGE0
	    		),
	    		ImmutableMap.of(
	    				PERCENTAGE0.getName(), ""
	    		)
	    	);
  	
  	public static final GraphicsPropertyNonLeafNode B = 
  			new GraphicsPropertyNonLeafNode(
	    		new SimpleName("B"), new VfNotes(), 
	    		ImmutableMap.of(
	    				PERCENTAGE0.getName(), PERCENTAGE0
	    		),
	    		ImmutableMap.of(
	    				PERCENTAGE0.getName(), ""
	    		)
	    	);
  	public static final GraphicsPropertyNonLeafNode OPACITY = 
  			new GraphicsPropertyNonLeafNode(
	    		new SimpleName("OPACITY"), new VfNotes(), 
	    		ImmutableMap.of(
	    				PERCENTAGE1.getName(), PERCENTAGE1
	    		),
	    		ImmutableMap.of(
	    				PERCENTAGE1.getName(), ""
	    		)
	    	);
  	
  	/**
  	 * 
  	 */
  	public static final GraphicsPropertyNonLeafNode RGB = 
    		new GraphicsPropertyNonLeafNode(
    				new SimpleName("RGB"), new VfNotes(),
    				ImmutableMap.of(
    						R.getName(), R,
    						G.getName(), G,
    						B.getName(), B
		    				),
    				ImmutableMap.of(
    						R.getName(), "",
    						G.getName(), "",
    						B.getName(), ""
    					)
    				);
  	
  	/**
  	 * 
  	 */
  	public static final GraphicsPropertyNonLeafNode RGB_OPACITY_COLOR = 
    		new GraphicsPropertyNonLeafNode(
    				new SimpleName("RGB_OPACITY_COLOR"), new VfNotes(),
    				ImmutableMap.of(
    						RGB.getName(), RGB,
    						OPACITY.getName(), OPACITY
		    				),
    				ImmutableMap.of(
    						RGB.getName(), "",
    						OPACITY.getName(), ""
    					)
    				);
  	
  	
  	/**
  	 * fill color that is either transparent or a specific valid rgb color
  	 */
  	public static final GraphicsPropertyNonLeafNode FILL_COLOR = 
  			new GraphicsPropertyNonLeafNode(
  				new SimpleName("FILL_COLOR"), new VfNotes(),
  				ImmutableMap.of(
  						RGB_OPACITY_COLOR.getName(), RGB_OPACITY_COLOR,
  						FILL_IS_TRANSPARENT.getName(), FILL_IS_TRANSPARENT
	    				),
				ImmutableMap.of(
						RGB_OPACITY_COLOR.getName(), "",
						FILL_IS_TRANSPARENT.getName(), ""
					)
  			);
}
