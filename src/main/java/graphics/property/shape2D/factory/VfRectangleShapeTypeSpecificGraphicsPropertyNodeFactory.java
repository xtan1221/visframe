package graphics.property.shape2D.factory;

import basic.SimpleName;
import basic.VfNotes;
import basic.attribute.PrimitiveTypeVfAttributeFactory;
import graphics.property.node.GraphicsPropertyLeafNode;

public final class VfRectangleShapeTypeSpecificGraphicsPropertyNodeFactory {
    /**
     * for the vertical/horizontal diameter of the arc at the four corners of the rectangle. 
     * 
     * The rectangle will have rounded corners if and only if both of the arc width and arc height properties are greater than 0.0.
     * 
     * default will be 0.0;
     */
    public static final GraphicsPropertyLeafNode<Double> ARC_DIAMETER = new GraphicsPropertyLeafNode<>(
    		PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
    				new SimpleName("ARCDIAMETER"),//SimpleName name, 
    				VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    				e->{return e>=0;}, //Predicate<T> nonNullValueConstraints,
    				0.0,//T defaultValue, 
    				false//boolean canBeNull
    				));
}
