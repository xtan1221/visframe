package graphics.property.shape2D.factory;

import com.google.common.collect.ImmutableMap;


import basic.SimpleName;
import basic.VfNotes;
import basic.attribute.PrimitiveTypeVfAttributeFactory;
import basic.attribute.VfAttributeImpl;
import graphics.property.node.GraphicsPropertyNode;
import graphics.property.node.GraphicsPropertyNonLeafNode;
import graphics.shape.shape2D.fx.utils.VfStrokeDashType;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import graphics.property.node.GraphicsPropertyLeafNode;
import rdb.sqltype.SQLStringType;

/**
 * 
 * @author tanxu
 *
 */
public class VfStrokeGraphicsPropertyNodeFactory {
	/**
     * based on JavaFX Shape class strokeType property {@link Shape#getStrokeType()};
     * 
     * see {@link StrokeType}
     * 
     * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/shape/StrokeType.html
     */
    public static final GraphicsPropertyLeafNode<StrokeType> STROKE_TYPE
                = new GraphicsPropertyLeafNode<>(
    					new VfAttributeImpl<>(
    							new SimpleName("STROKETYPE"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							StrokeType.class,//Class<T> valueType, 
    							i->{return i.toString();},//toStringFunction,
    							i->{return StrokeType.valueOf(i);},//fromStringFunction,
    							e->{return true;}, //Predicate<T> nonNullValueConstraints,
    							new SQLStringType(10,false),//???SQLDataType SQLDataType,
    							StrokeType.CENTERED,//T defaultValue, 
    							false//boolean canBeNull
    					));
    /**
     * based on JavaFX Shape class strokeWidth property {@link Shape#getStrokeWidth()};
     */
    public static final GraphicsPropertyLeafNode<Double> STROKE_WIDTH
                = new GraphicsPropertyLeafNode<>(
                		PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
    							new SimpleName("STROKEWIDTH"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							e->{return e>=0;}, //Predicate<T> nonNullValueConstraints,
    							1.0,//T defaultValue, 
    							false//boolean canBeNull
    					));

    /**
     * based on JavaFX Shape class strokeLineJoin property {@link Shape#getStrokeLineJoin()};
     * 
     * see {@link StrokeLineJoin}
     * 
     * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/shape/StrokeLineJoin.html
     */
    public static final GraphicsPropertyLeafNode<StrokeLineJoin> STROKE_LINE_JOIN
                = new GraphicsPropertyLeafNode<>(
    					new VfAttributeImpl<>(
    							new SimpleName("STROKELINEJOIN"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							StrokeLineJoin.class,//Class<T> valueType, 
    							i->{return i.toString();},//toStringFunction,
    							i->{return StrokeLineJoin.valueOf(i);},//fromStringFunction,
    							e->{return true;}, //Predicate<T> nonNullValueConstraints,
    							new SQLStringType(10,false),//???SQLDataType SQLDataType,
    							StrokeLineJoin.MITER,//T defaultValue, 
    							false//boolean canBeNull
    					));
    
    /**
     * based on JavaFX Shape class strokeLineCap property {@link Shape#getStrokeLineCap()};
     * 
     * see {@link StrokeLineCap}
     * 
     * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/shape/StrokeLineCap.html
     */
    public static final GraphicsPropertyLeafNode<StrokeLineCap> STROKE_LINE_CAP
                = new GraphicsPropertyLeafNode<>(
    					new VfAttributeImpl<>(
    							new SimpleName("STROKELINECAP"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							StrokeLineCap.class,//Class<T> valueType, 
    							i->{return i.toString();},//toStringFunction,
    							i->{return StrokeLineCap.valueOf(i);},//fromStringFunction,
    							e->{return true;}, //Predicate<T> nonNullValueConstraints,
    							new SQLStringType(10,false),//???SQLDataType SQLDataType,
    							StrokeLineCap.SQUARE,//T defaultValue, 
    							false//boolean canBeNull
    					));

    public static final GraphicsPropertyLeafNode<Double> STROKE_MITER_LIMIT
                = new GraphicsPropertyLeafNode<>(
                		PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
    							new SimpleName("STROKEMITERLIMIT"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							e->{return e>=1;}, //Predicate<T> nonNullValueConstraints,
    							10.0,//T defaultValue, 
    							false//boolean canBeNull
    					));
    
    ///////////////////////////////
    /**
     * stroke dash pattern related;
     * in javafx, stroke dash is defined by using Shape.getStrokeDashArray().addAll(double[] dashArray);
     * in visframe, a set of predefined dash pattern is available to choose from;
     * see {@link VfStrokeDashType}
     */
    public static final GraphicsPropertyLeafNode<VfStrokeDashType> STROKE_DASH_TYPE
                = new GraphicsPropertyLeafNode<>(
    					new VfAttributeImpl<>(
    							new SimpleName("STROKEDASHTYPE"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							VfStrokeDashType.class,//Class<T> valueType, 
    							i->{return i.toString();},//toStringFunction,
    							i->{return VfStrokeDashType.valueOf(i);},//fromStringFunction,
    							e->{return true;}, //Predicate<T> nonNullValueConstraints,
    							new SQLStringType(10,false),//???SQLDataType SQLDataType,
    							null,//T defaultValue, 
    							true//boolean canBeNull
    					));


    ////////////////////////////////////////////////////
    //stroke related
    public static final GraphicsPropertyNonLeafNode CURVE_LINE_STROKE
                = new GraphicsPropertyNonLeafNode(
                            new SimpleName("CURVELINESTROKE"), new VfNotes(), 
//                            "stroke for curve and line shape types",
                            ImmutableMap.of( //notes for children nodes
                                        STROKE_WIDTH.getName(),STROKE_WIDTH,
                                        STROKE_DASH_TYPE.getName(),STROKE_DASH_TYPE,
                                        STROKE_LINE_CAP.getName(),STROKE_LINE_CAP
                            ),
                            ImmutableMap.of( //notes for children nodes
	                            		STROKE_WIDTH.getName(),"",
	                                    STROKE_DASH_TYPE.getName(),"",
	                                    STROKE_LINE_CAP.getName(),""
                            ));

    /**
     * 
     */
    public static final GraphicsPropertyNonLeafNode ARC_RECTANGLE_STROKE
                = new GraphicsPropertyNonLeafNode(
                            new SimpleName("ARCRECTANGLESTROKE"),  new VfNotes(), 
//                            "stroke for arc and rectangle shape types",
                            ImmutableMap.<SimpleName, GraphicsPropertyNode>builder()
                            	    .put(STROKE_WIDTH.getName(), STROKE_WIDTH)
                            	    .put(STROKE_TYPE.getName(), STROKE_TYPE)
                            	    .put(STROKE_DASH_TYPE.getName(), STROKE_DASH_TYPE)
                            	    .put(STROKE_LINE_CAP.getName(), STROKE_LINE_CAP)
                            	    .put(STROKE_LINE_JOIN.getName(), STROKE_LINE_JOIN)
                            	    .put(STROKE_MITER_LIMIT.getName(), STROKE_MITER_LIMIT)
                            	    .build(),
                    	  
                            ImmutableMap.<SimpleName, String>builder()
                            	    .put(STROKE_WIDTH.getName(), "")
                            	    .put(STROKE_TYPE.getName(), "")
                            	    .put(STROKE_DASH_TYPE.getName(), "")
                            	    .put(STROKE_LINE_CAP.getName(), "")
                            	    .put(STROKE_LINE_JOIN.getName(), "")
                            	    .put(STROKE_MITER_LIMIT.getName(), "")
                            	    .build()      
                            );
    /**
     * arc/circle
     */
    public static final GraphicsPropertyNonLeafNode CIRCLE_ELLIPSE_STROKE
                = new GraphicsPropertyNonLeafNode(
                            new SimpleName("CIRCLEELLIPSESTROKE"), new VfNotes(), 
//                            "stroke for circle and ellipse shape types",
                            ImmutableMap.of(
                                        STROKE_WIDTH.getName(),STROKE_WIDTH,
                                        STROKE_TYPE.getName(),STROKE_TYPE,
                                        STROKE_DASH_TYPE.getName(),STROKE_DASH_TYPE
                            ),
                            ImmutableMap.of( //notes for children nodes
                            		STROKE_WIDTH.getName(),"",
                                    STROKE_TYPE.getName(),"",
                                    STROKE_DASH_TYPE.getName(),""
                            ));

	
	
	
	
	
	
}
