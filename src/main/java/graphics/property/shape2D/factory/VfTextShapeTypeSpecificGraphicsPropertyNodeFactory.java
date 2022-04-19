package graphics.property.shape2D.factory;

import com.google.common.collect.ImmutableMap;

import basic.SimpleName;
import basic.VfNotes;
import basic.attribute.PrimitiveTypeVfAttributeFactory;
import basic.attribute.VfAttributeImpl;
import graphics.property.node.GraphicsPropertyNonLeafNode;
import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import graphics.property.node.GraphicsPropertyLeafNode;
import rdb.sqltype.SQLStringType;


public class VfTextShapeTypeSpecificGraphicsPropertyNodeFactory {
	/**
	 * Text content string that is to be displayed(length <=10000 characters and >0)
	 * 
	 */
    public static final GraphicsPropertyLeafNode<String> TEXT_CONTENT_10000
                = new GraphicsPropertyLeafNode<>(
                		PrimitiveTypeVfAttributeFactory.stringTypeVfAttribute(
    							new SimpleName("TEXT10000"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							e->{return e.length()<=10000 && e.length()>0;}, //Predicate<T> nonNullValueConstraints,
    							new SQLStringType(10000,false),//???SQLDataType SQLDataType,
    							null,//T defaultValue, 
    							false//boolean canBeNull
    					));

    /**
     * based on javafx {@link Text#getTextOrigin()} property;
     * 
     * see {@link VPos}
     */
    public static final GraphicsPropertyLeafNode<VPos> TEXT_ORIGIN
                = new GraphicsPropertyLeafNode<>(
                		new VfAttributeImpl<>(
    							new SimpleName("TEXTORIGIN"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							VPos.class,//Class<T> valueType, 
    							i->{return i.toString();},//toStringFunction,
    							i->{return VPos.valueOf(i);},//fromStringFunction,
    							e->{return true;}, //Predicate<T> nonNullValueConstraints,
    							new SQLStringType(10,false),//???SQLDataType SQLDataType,
    							VPos.BASELINE,//T defaultValue, 
    							false//boolean canBeNull
    					));

    
    /**
     * based on javafx {@link Text#getBoundsType()} property;
     * 
     * see {@link TextBoundsType}
     */
    public static final GraphicsPropertyLeafNode<TextBoundsType> TEXT_BOUNDS_TYPE
                = new GraphicsPropertyLeafNode<>(
                		new VfAttributeImpl<>(
    							new SimpleName("TEXTBOUNDSTYPE"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							TextBoundsType.class,//Class<T> valueType, 
    							i->{return i.toString();},//toStringFunction,
    							i->{return TextBoundsType.valueOf(i);},//fromStringFunction,
    							e->{return true;}, //Predicate<T> nonNullValueConstraints,
    							new SQLStringType(30,false),//???SQLDataType SQLDataType,
    							TextBoundsType.LOGICAL,//T defaultValue, 
    							false//boolean canBeNull
    					));
    
    /**
     * based on javafx {@link Text#getWrappingWidth()} property;
     */
    public static final GraphicsPropertyLeafNode<Double> TEXT_WRAPPING_WIDTH
                = new GraphicsPropertyLeafNode<>(
                		PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
    							new SimpleName("TEXTWRAPPINGWIDTH"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							e->{return e>=0;}, //Predicate<T> nonNullValueConstraints,
    							0.0,//T defaultValue, 
    							false//boolean canBeNull
    					));
      
    /**
     * based on javafx {@link Text#isUnderline()} property;
     */
    public static final GraphicsPropertyLeafNode<Boolean> TEXT_IS_UNDERLINED
                = new GraphicsPropertyLeafNode<>(
                		PrimitiveTypeVfAttributeFactory.booleanTypeVfAttribute(
    							new SimpleName("TEXTISUNDERLINED"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							e->{return true;}, //Predicate<T> nonNullValueConstraints,
    							false,//T defaultValue, 
    							false//boolean canBeNull
    					));
    
    /**
     * based on javafx {@link Text#isStrikethrough()} property;
     */
    public static final GraphicsPropertyLeafNode<Boolean> TEXT_IS_STRIKE_THROUGH
                = new GraphicsPropertyLeafNode<>(
                		PrimitiveTypeVfAttributeFactory.booleanTypeVfAttribute(
    							new SimpleName("TEXTISSTRIKETHROUGH"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							e->{return true;}, //Predicate<T> nonNullValueConstraints,
    							false,//T defaultValue, 
    							false//boolean canBeNull
    					));
    
    /**
     * based on javafx {@link Text#getTextAlignment()} property;
     * 
     * see {@link TextAlignment}
     */
    public static final GraphicsPropertyLeafNode<TextAlignment> TEXT_ALIGNMENT_TYPE
                = new GraphicsPropertyLeafNode<>(
                		new VfAttributeImpl<>(
        							new SimpleName("TEXTALIGNMENTTYPE"),//SimpleName name, 
        							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
        							TextAlignment.class,//Class<T> valueType, 
        							i->{return i.toString();},//toStringFunction,
        							i->{return TextAlignment.valueOf(i);},//fromStringFunction,
        							e->{return true;}, //Predicate<T> nonNullValueConstraints,
        							new SQLStringType(10,false),//???SQLDataType SQLDataType,
        							TextAlignment.LEFT,//T defaultValue, 
        							false//boolean canBeNull
        					));
    
    /**
     * based on javafx {@link Text#getLineSpacing()} property;
     */
    public static final GraphicsPropertyLeafNode<Double> TEXT_LINE_SPACING
                = new GraphicsPropertyLeafNode<>(
                		PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
    							new SimpleName("TEXTLINESPACING"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							e->{return e>=0;}, //Predicate<T> nonNullValueConstraints,
    							0.0,//T defaultValue, 
    							false//boolean canBeNull
    					));
    
    
    ////todo
    //group of text related settings
    
    
    ///////////////////////////////////////////////////////
    //Font class
    //Note that if you wish to locate a font by font family and style then you can use one of the 
    //font(java.lang.String, javafx.scene.text.FontWeight, javafx.scene.text.FontPosture, double) factory methods defined in this class.
    /**
     * The point size for a font. This may be a fractional value such as 11.5.
     * If the specified value is {@literal <} 0 the default size will be used.
     * 
     * based on javafx {@link Font#getSize()} property;
     */
    public static final GraphicsPropertyLeafNode<Double> FONT_SIZE
                = new GraphicsPropertyLeafNode<>(
                		PrimitiveTypeVfAttributeFactory.doubleTypeVfAttribute(
    							new SimpleName("FONTSIZE"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							e->{return e>0;}, //Predicate<T> nonNullValueConstraints,
    							Font.getDefault().getSize(),//T defaultValue, 
    							false//boolean canBeNull
    					));
    
    /**
     * based on javafx {@link Font#getFamily()} property;
     */
    public static final GraphicsPropertyLeafNode<String> FONT_FAMILY
                = new GraphicsPropertyLeafNode<>(
                		PrimitiveTypeVfAttributeFactory.stringTypeVfAttribute(
    							new SimpleName("FONTFAMILY"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							e->{return Font.getFamilies().contains(e);}, //Predicate<T> nonNullValueConstraints,
    							new SQLStringType(20,false),//???SQLDataType SQLDataType,
    							Font.getDefault().getFamily(),//T defaultValue, 
    							false//boolean canBeNull
    					));
    
    /**
     * based on weight property of javafx {@link Font}
     * note that there is no gettor and settor method for this property in {@link Font} class???
     */
    public static final GraphicsPropertyLeafNode<FontWeight> FONT_WEIGHT
                = new GraphicsPropertyLeafNode<>(
                		new VfAttributeImpl<>(
    							new SimpleName("FONTWEIGHT"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							FontWeight.class,//Class<T> valueType, 
    							i->{return i.toString();},//toStringFunction,
    							i->{return FontWeight.valueOf(i);},//fromStringFunction,
    							e->{return true;}, //Predicate<T> nonNullValueConstraints, ????
    							new SQLStringType(15,false),//???SQLDataType SQLDataType,
    							FontWeight.NORMAL,//T defaultValue, ??????
    							true//boolean canBeNull ???
    					));
    /**
     * based on posture property of javafx {@link Font}
     * note that there is no gettor and settor method for this property in {@link Font} class???
     */
    public static final GraphicsPropertyLeafNode<FontPosture> FONT_POSTURE
                = new GraphicsPropertyLeafNode<>(
                		new VfAttributeImpl<>(
    							new SimpleName("FONTPOSTURE"),//SimpleName name, 
    							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
    							FontPosture.class,//Class<T> valueType, 
    							i->{return i.toString();},//toStringFunction,
    							i->{return FontPosture.valueOf(i);},//fromStringFunction,
    							e->{return true;}, //Predicate<T> nonNullValueConstraints,
    							new SQLStringType(10,false),//???SQLDataType SQLDataType,
    							FontPosture.REGULAR,//T defaultValue, ??????
    							false//boolean canBeNull ???
    					));

    //JAVAFX Font class related
    public static final GraphicsPropertyNonLeafNode FONT
                = new GraphicsPropertyNonLeafNode(
                            new SimpleName("FONT"), new VfNotes(),
//                            "all font related settings",
//                            Arrays.asList(
//                                        FONT_FAMILY,
//                                        FONT_WEIGHT,
//                                        FONT_POSTURE,
//                                        FONT_SIZE
//                            ),
                            ImmutableMap.of(
                                        FONT_FAMILY.getName(),FONT_FAMILY,
                                        FONT_WEIGHT.getName(),FONT_WEIGHT,
                                        FONT_POSTURE.getName(),FONT_POSTURE,
                                        FONT_SIZE.getName(),FONT_SIZE
                            ),
                            ImmutableMap.of( //notes for children nodes
                            		FONT_FAMILY.getName(),"",
                                    FONT_WEIGHT.getName(),"",
                                    FONT_POSTURE.getName(),"",
                                    FONT_SIZE.getName(),""
                            )
                );

}
