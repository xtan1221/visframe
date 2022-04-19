package graphics.shape.shape2D.fx.type;

import java.util.Map;
import basic.SimpleName;
import graphics.property.node.PropertyTreeNodePathNameBuilder;
import graphics.shape.shape2D.fx.VfShapeTypeFXNodeFactoryBase;
import graphics.shape.shape2D.type.VfText;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

import static graphics.property.shape2D.factory.VfTextShapeTypeSpecificGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfColorGraphicsPropertyNodeFactory.*;
import static graphics.shape.shape2D.type.VfText.*;

/**
 * 
 * @author tanxu
 *
 */
public class VfTextFXNodeFactoryImpl extends VfShapeTypeFXNodeFactoryBase<Text, VfText> {
	static VfTextFXNodeFactoryImpl SINGLETON;
	
	/**
	 * 
	 * @return
	 */
	public static VfTextFXNodeFactoryImpl getSingleton() {
		if(SINGLETON==null) {
			SINGLETON=new VfTextFXNodeFactoryImpl();
		}
		return SINGLETON;
	}
	
	//////////////////////////////////////
	private Double upperLeftX;
    private Double upperLeftY;
    private final SimpleName upperLeftXName;
    private final SimpleName upperLeftYName;            
    
    private String content; //text content
    private final SimpleName contentName;
    
    private Double red;
    private Double green;
    private Double blue;
    private Double opacity;
    private final SimpleName redName;
    private final SimpleName greenName;
    private final SimpleName blueName;
    private final SimpleName opacityName;
    
    private String fontFamily;
    private FontWeight fontWeight;
    private FontPosture fontPosture;
    private Double fontSize;
    private final SimpleName fontFamilyName;
    private final SimpleName fontWeightName;
    private final SimpleName fontPostureName;
    private final SimpleName fontSizeName;
    
    private VPos textOrigin;
    private final SimpleName textOriginName;
    
    private TextBoundsType boundsType;
    private final SimpleName boundsTypeName;
    
    private Double wrappingWidth;
    private final SimpleName wrappingWidthName;
    
    private Boolean isUnderlined;
    private final SimpleName isUnderlinedName;
    
    private Boolean isStrikeThrough;
    private final SimpleName isStrikeThroughName;
    
    private TextAlignment textAlignment;
    private final SimpleName textAlignmentName;
    
    private Double lineSpacing;
    private final SimpleName lineSpacingName;
    //////////////////////
    
	/**
	 * private constructor
	 */
	private VfTextFXNodeFactoryImpl() {
		//initialize
		upperLeftXName = PropertyTreeNodePathNameBuilder.start(UPPERLEFT_COORDINATE_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
        upperLeftYName = PropertyTreeNodePathNameBuilder.start(UPPERLEFT_COORDINATE_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();
        
        contentName = PropertyTreeNodePathNameBuilder.start(CONTENT_PROPERTY_TREE.getName()).addNext(TEXT_CONTENT_10000).build();
        
        redName = PropertyTreeNodePathNameBuilder.start(COLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(R).addNext(PERCENTAGE0).build();
        greenName = PropertyTreeNodePathNameBuilder.start(COLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(G).addNext(PERCENTAGE0).build();
        blueName = PropertyTreeNodePathNameBuilder.start(COLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(B).addNext(PERCENTAGE0).build();
        opacityName = PropertyTreeNodePathNameBuilder.start(COLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(OPACITY).addNext(PERCENTAGE1).build();
        
        fontFamilyName = PropertyTreeNodePathNameBuilder.start(FONT_PROPERTY_TREE.getName()).addNext(FONT).addNext(FONT_FAMILY).build();
        fontWeightName = PropertyTreeNodePathNameBuilder.start(FONT_PROPERTY_TREE.getName()).addNext(FONT).addNext(FONT_WEIGHT).build();
        fontPostureName = PropertyTreeNodePathNameBuilder.start(FONT_PROPERTY_TREE.getName()).addNext(FONT).addNext(FONT_POSTURE).build();
        fontSizeName = PropertyTreeNodePathNameBuilder.start(FONT_PROPERTY_TREE.getName()).addNext(FONT).addNext(FONT_SIZE).build();
    	
        textOriginName = PropertyTreeNodePathNameBuilder.start(ORIGIN_PROPERTY_TREE.getName()).addNext(TEXT_ORIGIN).build();
    	
        boundsTypeName = PropertyTreeNodePathNameBuilder.start(BOUNDS_TYPE_PROPERTY_TREE.getName()).addNext(TEXT_BOUNDS_TYPE).build();
        
        wrappingWidthName = PropertyTreeNodePathNameBuilder.start(WRAPPING_WIDTH_PROPERTY_TREE.getName()).addNext(TEXT_WRAPPING_WIDTH).build();
        
        isUnderlinedName = PropertyTreeNodePathNameBuilder.start(IS_UNDERLINED_PROPERTY_TREE.getName()).addNext(TEXT_IS_UNDERLINED).build();
        
        isStrikeThroughName = PropertyTreeNodePathNameBuilder.start(IS_STRIKE_THROUGH_PROPERTY_TREE.getName()).addNext(TEXT_IS_STRIKE_THROUGH).build();
        
        textAlignmentName = PropertyTreeNodePathNameBuilder.start(ALIGNMENT_TYPE_PROPERTY_TREE.getName()).addNext(TEXT_ALIGNMENT_TYPE).build();
        
        lineSpacingName = PropertyTreeNodePathNameBuilder.start(LINE_SPACING_PROPERTY_TREE.getName()).addNext(TEXT_LINE_SPACING).build();
	}
	
	
	@Override
	public VfText getVfShapeType() {
		return VfText.SINGLETON;
	}
	
	////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Text makeFXNode(Map<SimpleName, String> leafGraphicsPropertyFullPathNameOnTreeStringValueMap) {
		this.initialize();
		//
		this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.putAll(leafGraphicsPropertyFullPathNameOnTreeStringValueMap);
		
		//
		Text text = new Text();
		
		//
		boolean typeSpecificPropertySuccessfullySet = this.setTypeSpecificTreeProperty(text);
		
		if(typeSpecificPropertySuccessfullySet) {
			boolean transformSuccessfullySet = this.setTransform(text);
			if(!transformSuccessfullySet)
				text = null;
		}else {
			text = null;
		}
		
		
		return text;
	}

	@Override
	public void initialize() {
		super.initialize();
		
		//
		upperLeftX=LAYOUT_COORD.getDefaultValue();
	    upperLeftY=LAYOUT_COORD.getDefaultValue();
	     
	    content = TEXT_CONTENT_10000.getDefaultValue();
	    
	    red=PERCENTAGE0.getDefaultValue();
	    green=PERCENTAGE0.getDefaultValue();
	    blue=PERCENTAGE0.getDefaultValue();
	    opacity=PERCENTAGE1.getDefaultValue();
	    
	    fontFamily = FONT_FAMILY.getDefaultValue();
	    fontWeight = FONT_WEIGHT.getDefaultValue();
	    fontPosture = FONT_POSTURE.getDefaultValue();
	    fontSize = FONT_SIZE.getDefaultValue();
	    
	    textOrigin = TEXT_ORIGIN.getDefaultValue();
	    
	    boundsType = TEXT_BOUNDS_TYPE.getDefaultValue();
	    
	    wrappingWidth = TEXT_WRAPPING_WIDTH.getDefaultValue();
	    
	    isUnderlined = TEXT_IS_UNDERLINED.getDefaultValue();
	    
	    isStrikeThrough = TEXT_IS_STRIKE_THROUGH.getDefaultValue();
	    
	    textAlignment = TEXT_ALIGNMENT_TYPE.getDefaultValue();
	    
	    lineSpacing = TEXT_LINE_SPACING.getDefaultValue();
	}
	
	
	/**
	 */
	@Override
	public boolean setTypeSpecificTreeProperty(Text text) {
		//coordinate
		if(!this.setUpperLeftCoordinate(text))
			return false;
		
		if(!this.setContent(text))
			return false;
        
		//color
		if(!this.setColor(text))
			return false;
	
		if(!this.setFont(text))
			return false;
		
		if(!this.setTextOrigin(text))
			return false;
		
		if(!this.setBoundsType(text))
			return false;
		
		if(!this.setWrappingWidth(text))
			return false;
		
		if(!this.setIsUnderLined(text))
			return false;
		
		if(!this.setIsStrikeThrough(text))
			return false;
		
		if(!this.setTextAlignmentType(text))
			return false;
		
		if(!this.setLineSpacing(text))
			return false;
		
		
		return true;
	}
	
	/**
	 * set the start x and y;
	 * 
	 * note that line's start x and y have default value as 0.0;
	 * 
	 * thus if there is no non-null value calculated, skip it and the default value will be used implicitly;
	 * 
	 * @param text
	 * @return
	 */
	private boolean setUpperLeftCoordinate(Text text) {
		///////////////////centerX
		//extract non-null value for centerX if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.upperLeftXName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(upperLeftXName)!=null) {
			try {
				this.upperLeftX = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(upperLeftXName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.upperLeftX))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.upperLeftX!=null)
			text.setX(this.upperLeftX);
		
		///////////////////////centerY
		//extract non-null value for centerY if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.upperLeftYName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(upperLeftYName)!=null) {
			try{
				this.upperLeftY = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(upperLeftYName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.upperLeftY))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.upperLeftY!=null)
			text.setY(this.upperLeftY);
		
		
		///////////////////////////
        return true;
	}
	
	/**
	 * @param text
	 * @return
	 */
	private boolean setContent(Text text) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.contentName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(contentName)!=null) {
			this.content = this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(contentName);
		}
		if(!TEXT_CONTENT_10000.isValidValue(this.content))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.content!=null)
			text.setText(content);
		
		return true;
	}
	
	/**
	 * @param text
	 * @return
	 */
	private boolean setColor(Text text) {
		//red
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.redName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(redName)!=null) {
			try {
				this.red = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(redName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE0.isValidValue(this.red))
			return false;
		
		//green
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.greenName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(greenName)!=null) {
			try {
				this.green = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(greenName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE0.isValidValue(this.green))
			return false;
		
		//blue
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.blueName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(blueName)!=null) {
			try {
				this.blue = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(blueName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE0.isValidValue(this.blue))
			return false;
		
		//opacity
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.opacityName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(opacityName)!=null) {
			try {
				this.opacity = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(opacityName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE1.isValidValue(this.opacity))
			return false;
		
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.red!=null && this.green!=null && this.blue!=null && this.opacity!=null) {
			Color c = new Color(red, green, blue, opacity);
			text.setStroke(c);
		}
		
		///////////
        return true;
	}
	
	/**
	 * @param text
	 * @return
	 */
	private boolean setFont(Text text) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.fontFamilyName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fontFamilyName)!=null) {
			this.fontFamily = this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fontFamilyName).trim();
		}
		if(!FONT_FAMILY.isValidValue(this.fontFamily))
			return false;
		
		
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.fontWeightName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fontWeightName)!=null) {
			try {
				this.fontWeight = FontWeight.valueOf(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fontWeightName).trim());
			}catch(IllegalArgumentException e) {
				return false;
			}
		}
		if(!FONT_WEIGHT.isValidValue(this.fontWeight))
			return false;
		
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.fontPostureName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fontPostureName)!=null) {
			try {
				this.fontPosture = FontPosture.valueOf(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fontPostureName).trim());
			}catch(IllegalArgumentException e) {
				return false;
			}
		}
		if(!FONT_POSTURE.isValidValue(this.fontPosture))
			return false;
		
		
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.fontSizeName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fontSizeName)!=null) {
			try {
				this.fontSize = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fontSizeName).trim());
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!FONT_SIZE.isValidValue(this.fontSize))
			return false;
		
		
		/////////////////////
		if(this.fontFamily!=null && this.fontWeight!=null && this.fontPosture!=null && this.fontSize!=null) {
			Font f = Font.font(fontFamily, fontWeight, fontPosture, fontSize);
			text.setFont(f);
		}
		
		
		return true;
	}

	/**
	 * @param text
	 * @return
	 */
	private boolean setTextOrigin(Text text) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.textOriginName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(textOriginName)!=null) {
			try {
				this.textOrigin = VPos.valueOf(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(textOriginName));
			}catch(IllegalArgumentException e) {
				return false;
			}
		}
		if(!TEXT_ORIGIN.isValidValue(this.textOrigin))
			return false;
		
		if(this.textOrigin!=null)
			text.setTextOrigin(this.textOrigin);
		
		return true;
	}
	
	/**
	 * @param text
	 * @return
	 */
	private boolean setBoundsType(Text text) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.boundsTypeName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(boundsTypeName)!=null) {
			try {
				this.boundsType = TextBoundsType.valueOf(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(boundsTypeName));
			}catch(IllegalArgumentException e) {
				return false;
			}
		}
		if(!TEXT_BOUNDS_TYPE.isValidValue(this.boundsType))
			return false;
		
		if(this.boundsType!=null)
			text.setBoundsType(this.boundsType);
		
		return true;
	}
	
	private boolean setWrappingWidth(Text text) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.wrappingWidthName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(wrappingWidthName)!=null) {
			try{
				this.wrappingWidth = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(wrappingWidthName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!TEXT_WRAPPING_WIDTH.isValidValue(this.wrappingWidth))
			return false;
		
		if(this.wrappingWidth!=null)
			text.setWrappingWidth(this.wrappingWidth);
		
		return true;
	}
	
	private boolean setIsUnderLined(Text text) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.isUnderlinedName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(isUnderlinedName)!=null) {
			try{
				this.isUnderlined = Boolean.parseBoolean(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(isUnderlinedName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		
		if(!TEXT_IS_UNDERLINED.isValidValue(this.isUnderlined))
			return false;
		
		if(this.isUnderlined!=null)
			text.setUnderline(this.isUnderlined);
		
		return true;
	}
	
	private boolean setIsStrikeThrough(Text text) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.isStrikeThroughName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(isStrikeThroughName)!=null) {
			this.isStrikeThrough = Boolean.parseBoolean(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(isStrikeThroughName));
		}
		
		if(!TEXT_IS_STRIKE_THROUGH.isValidValue(this.isStrikeThrough))
			return false;
		
		if(this.isStrikeThrough!=null)
			text.setStrikethrough(this.isStrikeThrough);
		
		return true;
	}
	
	private boolean setTextAlignmentType(Text text) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.textAlignmentName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(textAlignmentName)!=null) {
			try {
				this.textAlignment = TextAlignment.valueOf(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(textAlignmentName));
			}catch(IllegalArgumentException e) {
				return false;
			}
		}
		if(!TEXT_ALIGNMENT_TYPE.isValidValue(this.textAlignment))
			return false;
		
		if(this.textAlignment!=null)
			text.setTextAlignment(this.textAlignment);
		
		return true;
	}
	
	private boolean setLineSpacing(Text text) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.lineSpacingName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(lineSpacingName)!=null) {
			try{
				this.lineSpacing = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(lineSpacingName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!TEXT_LINE_SPACING.isValidValue(this.lineSpacing))
			return false;
		
		if(this.lineSpacing!=null)
			text.setLineSpacing(this.lineSpacing);
		
		return true;
	}
}
