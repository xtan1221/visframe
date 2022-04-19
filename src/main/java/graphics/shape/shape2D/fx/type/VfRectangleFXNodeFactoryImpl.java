package graphics.shape.shape2D.fx.type;

import java.util.Map;
import basic.SimpleName;
import graphics.property.node.PropertyTreeNodePathNameBuilder;
import graphics.shape.shape2D.fx.VfShapeTypeFXNodeFactoryBase;
import graphics.shape.shape2D.fx.utils.VfStrokeDashType;
import graphics.shape.shape2D.type.VfRectangle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

import static graphics.property.shape2D.factory.VfRectangleShapeTypeSpecificGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfColorGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfStrokeGraphicsPropertyNodeFactory.*;
import static graphics.shape.shape2D.type.VfRectangle.*;


/**
 * 
 * @author tanxu
 * 
 */
public class VfRectangleFXNodeFactoryImpl extends VfShapeTypeFXNodeFactoryBase<Rectangle, VfRectangle> {
	static VfRectangleFXNodeFactoryImpl SINGLETON;
	
	/**
	 * @return
	 */
	public static VfRectangleFXNodeFactoryImpl getSingleton() {
		if(SINGLETON==null) {
			SINGLETON=new VfRectangleFXNodeFactoryImpl();
		}
		return SINGLETON;
	}
	
	//////////////////////////////////////
	//UPPERLEFT
	private Double upperLeftX;
    private Double upperLeftY;
    private final SimpleName upperLeftXName;
    private final SimpleName upperLeftYName;            
    //HEIGHT
    private Double height;
    private final SimpleName heightName;
    
    //WIDTH
    private Double width;
    private final SimpleName widthName;
    
    //ARCHEIGHT
    private Double arcHeight;
    private final SimpleName arcHeightName;
    
    //ARCHWIDTH
    private Double arcWidth;
    private final SimpleName arcWidthName;
    
    //FILLCOLOR
    private Boolean transparent;
    private Double fillRed;
    private Double fillGreen;
    private Double fillBlue;
    private Double fillOpacity;
    private final SimpleName transparentName;
    private final SimpleName fillRedName;
    private final SimpleName fillGreenName;
    private final SimpleName fillBlueName;
    private final SimpleName fillOpacityName;
    
    //BORDERCOLOR
    private Double borderRed;
    private Double borderGreen;
    private Double borderBlue;
    private Double borderOpacity;
    private final SimpleName borderRedName;
    private final SimpleName borderGreenName;
    private final SimpleName borderBlueName;
    private final SimpleName borderOpacityName;
    
    //STROKE
    private Double strokeWidth;
    private StrokeType strokeType;
    private VfStrokeDashType dashType; //see VfStrokeDashType
    private StrokeLineCap strokeLineCap;
    private StrokeLineJoin strokeLineJoin;
    private Double strokeMiterLimit;
    
    private final SimpleName strokeWidthName;
    private final SimpleName strokeTypeName;
    private final SimpleName dashTypeName;
    private final SimpleName strokeLineCapName;
    private final SimpleName strokeLineJoinName; //
    private final SimpleName strokeMiterLimitName;
    ///////////////////////////////////
    
	/**
	 * private constructor
	 */
	private VfRectangleFXNodeFactoryImpl() {
		//UPPERLEFT
		upperLeftXName = PropertyTreeNodePathNameBuilder.start(UPPERLEFT_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
		upperLeftYName = PropertyTreeNodePathNameBuilder.start(UPPERLEFT_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();
        
		heightName = PropertyTreeNodePathNameBuilder.start(HEIGHT_PROPERTY_TREE.getName()).addNext(LENDIST).build();
		widthName = PropertyTreeNodePathNameBuilder.start(WIDTH_PROPERTY_TREE.getName()).addNext(LENDIST).build();
        
		arcHeightName = PropertyTreeNodePathNameBuilder.start(ARCHEIGHT_PROPERTY_TREE.getName()).addNext(ARC_DIAMETER).build();
		arcWidthName = PropertyTreeNodePathNameBuilder.start(ARCHWIDTH_PROPERTY_TREE.getName()).addNext(ARC_DIAMETER).build();
		
		transparentName = PropertyTreeNodePathNameBuilder.start(FILLCOLOR_PROPERTY_TREE.getName()).addNext(FILL_COLOR).addNext(FILL_IS_TRANSPARENT).build();
		fillRedName = PropertyTreeNodePathNameBuilder.start(FILLCOLOR_PROPERTY_TREE.getName()).addNext(FILL_COLOR).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(R).addNext(PERCENTAGE0).build();
		fillGreenName = PropertyTreeNodePathNameBuilder.start(FILLCOLOR_PROPERTY_TREE.getName()).addNext(FILL_COLOR).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(G).addNext(PERCENTAGE0).build();
		fillBlueName = PropertyTreeNodePathNameBuilder.start(FILLCOLOR_PROPERTY_TREE.getName()).addNext(FILL_COLOR).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(B).addNext(PERCENTAGE0).build();
		fillOpacityName = PropertyTreeNodePathNameBuilder.start(FILLCOLOR_PROPERTY_TREE.getName()).addNext(FILL_COLOR).addNext(RGB_OPACITY_COLOR).addNext(OPACITY).addNext(PERCENTAGE1).build();
        
		borderRedName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(R).addNext(PERCENTAGE0).build();
		borderGreenName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(G).addNext(PERCENTAGE0).build();
		borderBlueName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(B).addNext(PERCENTAGE0).build();
		borderOpacityName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(OPACITY).addNext(PERCENTAGE1).build();
        
        strokeWidthName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(ARC_RECTANGLE_STROKE).addNext(STROKE_WIDTH).build();
        strokeTypeName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(ARC_RECTANGLE_STROKE).addNext(STROKE_TYPE).build();
        dashTypeName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(ARC_RECTANGLE_STROKE).addNext(STROKE_DASH_TYPE).build();
        strokeLineCapName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(ARC_RECTANGLE_STROKE).addNext(STROKE_LINE_CAP).build();
        strokeLineJoinName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(ARC_RECTANGLE_STROKE).addNext(STROKE_LINE_JOIN).build();
        strokeMiterLimitName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(ARC_RECTANGLE_STROKE).addNext(STROKE_MITER_LIMIT).build();
	}
	
	
	@Override
	public VfRectangle getVfShapeType() {
		return VfRectangle.SINGLETON;
	}
	
	////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle makeFXNode(Map<SimpleName, String> leafGraphicsPropertyFullPathNameOnTreeStringValueMap) {
		this.initialize();
		//
		this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.putAll(leafGraphicsPropertyFullPathNameOnTreeStringValueMap);
		
		//
		Rectangle rectangle = new Rectangle();
		//
		boolean typeSpecificPropertySuccessfullySet = this.setTypeSpecificTreeProperty(rectangle);
		
		if(typeSpecificPropertySuccessfullySet) {
			boolean transformSuccessfullySet = this.setTransform(rectangle);
			if(!transformSuccessfullySet)
				rectangle = null;
		}else {
			rectangle = null;
		}
		
		return rectangle;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		//UPPERLEFT
		upperLeftX=LAYOUT_COORD.getDefaultValue();
	    upperLeftY=LAYOUT_COORD.getDefaultValue();       
	    //HEIGHT
	    height=LENDIST.getDefaultValue();
	    
	    //WIDTH
	    width=LENDIST.getDefaultValue();
	    
	    //ARCHEIGHT
	    arcHeight=ARC_DIAMETER.getDefaultValue();
	    
	    //ARCHWIDTH
	    arcWidth=ARC_DIAMETER.getDefaultValue();
	    
	    //FILLCOLOR
	    transparent = FILL_IS_TRANSPARENT.getDefaultValue();
	    fillRed=PERCENTAGE0.getDefaultValue();
	    fillGreen=PERCENTAGE0.getDefaultValue();
	    fillBlue=PERCENTAGE0.getDefaultValue();
	    fillOpacity=PERCENTAGE1.getDefaultValue();
	    
	    //BORDERCOLOR
	    borderRed=PERCENTAGE0.getDefaultValue();
	    borderGreen=PERCENTAGE0.getDefaultValue();
	    borderBlue=PERCENTAGE0.getDefaultValue();
	    borderOpacity=PERCENTAGE1.getDefaultValue();
	    
	    //STROKE
	    strokeWidth = STROKE_WIDTH.getDefaultValue();//default value
	    strokeType = STROKE_TYPE.getDefaultValue();
	    dashType = STROKE_DASH_TYPE.getDefaultValue(); //see VfStrokeDashType
	    strokeLineCap = STROKE_LINE_CAP.getDefaultValue(); //default value
	    strokeLineJoin = STROKE_LINE_JOIN.getDefaultValue();
	    strokeMiterLimit = STROKE_MITER_LIMIT.getDefaultValue();
	}
	
	
	
	
	/**
	 * set mandatory trees of line;
	 * 
	 * START_PROPERTY_TREE_NAME and END_PROPERTY_TREE_NAME;
	 * 
	 * if any of the mandatory tree's leaf node is not in the leafNodeFullPathNameOnTreeStringValueMap, the Line object cannot be created;
	 * 
	 * return true if all properties are successfully set; false otherwise;
	 */
	@Override
	public boolean setTypeSpecificTreeProperty(Rectangle rectangle) {
		if(!this.setUpperLeft(rectangle))
			return false;
			
		if(!this.setHeight(rectangle))
			return false;
		
		if(!this.setWidth(rectangle))
			return false;
		
		if(!this.setArchHeight(rectangle))
			return false;
		
		if(!this.setArchWidth(rectangle))
			return false;
		
		if(!this.setFillColor(rectangle))
			return false;
		
		if(!this.setBorderColor(rectangle))
			return false;
		
		if(!this.setStrokeWidth(rectangle))
			return false;
		
		if(!this.setStrokeType(rectangle))
			return false;
		
		if(!this.setStrokeDashType(rectangle))
			return false;
		
		if(!this.setStrokeLineCap(rectangle))
			return false;
		
		if(!this.setStrokeLineJoin(rectangle))
			return false;
		
		if(!this.setStrokeMiterLimit(rectangle))
			return false;
		
		
		return true;
	}
	
	/**
	 * @param line
	 * @return
	 */
	private boolean setUpperLeft(Rectangle rectangle) {
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
			rectangle.setX(this.upperLeftX);
		
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
			rectangle.setY(this.upperLeftY);
		
		
		///////////////////////////
        return true;
	}
	
	
	/**
	 * 
	 * @param line
	 * @return
	 */
	private boolean setHeight(Rectangle rectangle) {
		//extract non-null value for centerY if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.heightName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(heightName)!=null) {
			try{
				this.height = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(heightName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.height))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.height!=null)
			rectangle.setHeight(this.height);
		
		
		///////////////////////////
        return true;
	}
	
	/**
	 * 
	 * @param line
	 * @return
	 */
	private boolean setWidth(Rectangle rectangle) {
		//extract non-null value for centerY if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.widthName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(widthName)!=null) {
			try{
				this.width = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(widthName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.width))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.width!=null)
			rectangle.setWidth(this.width);
		
		
		///////////////////////////
        return true;
	}
	
	
	/**
	 * 
	 * @param rectangle
	 * @return
	 */
	private boolean setArchHeight(Rectangle rectangle) {
		//extract non-null value for centerY if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.arcHeightName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(arcHeightName)!=null) {
			try{
				this.arcHeight = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(arcHeightName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.arcHeight))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.arcHeight!=null)
			rectangle.setArcHeight(this.arcHeight);
		
		
		///////////////////////////
        return true;
	}
	
	/**
	 * 
	 * @param rectangle
	 * @return
	 */
	private boolean setArchWidth(Rectangle rectangle) {
		//extract non-null value for centerY if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.arcWidthName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(arcWidthName)!=null) {
			try{
				this.arcWidth = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(arcWidthName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.arcWidth))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.arcWidth!=null)
			rectangle.setArcWidth(this.arcWidth);
		
		
		///////////////////////////
        return true;
	}
	
	
	/**
	 * all components of javafx Color have default value equal to 0.0;
	 * 
	 * thus, if any component has no non-null value calculated, skip it and the default value will be used implicitly;
	 * 
	 * @param line
	 * @return
	 */
	private boolean setFillColor(Rectangle rectangle) {
		//transparent
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.transparentName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(transparentName)!=null) {
			transparent = Boolean.parseBoolean(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(transparentName));
			if(transparent) {
				rectangle.setFill(Color.TRANSPARENT);
				return true;
			}
		}
		
		//red
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.fillRedName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fillRedName)!=null) {
			try {
				this.fillRed = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fillRedName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE0.isValidValue(this.fillRed))
			return false;
		
		//green
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.fillGreenName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fillGreenName)!=null) {
			try {
				this.fillGreen = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fillGreenName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE0.isValidValue(this.fillGreen))
			return false;
		
		//blue
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.fillBlueName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fillBlueName)!=null) {
			try {
				this.fillBlue = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fillBlueName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE0.isValidValue(this.fillBlue))
			return false;
		
		//opacity
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.fillOpacityName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fillOpacityName)!=null) {
			try {
				this.fillOpacity = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fillOpacityName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE1.isValidValue(this.fillOpacity))
			return false;
		
		////////////
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.fillRed!=null && this.fillBlue!=null && this.fillGreen!=null && this.fillOpacity!=null) {
			Color c = new Color(this.fillRed, this.fillGreen, this.fillBlue, this.fillOpacity);
			rectangle.setFill(c);
		}
        //////////
        return true;
	}
	
	/**
	 * 
	 * @param rectangle
	 * @return
	 */
	private boolean setBorderColor(Rectangle rectangle) {
		//red
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.borderRedName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(borderRedName)!=null) {
			try {
				this.borderRed = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(borderRedName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE0.isValidValue(this.borderRed))
			return false;
		
		//green
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.borderGreenName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(borderGreenName)!=null) {
			try {
				this.borderGreen = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(borderGreenName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE0.isValidValue(this.borderGreen))
			return false;
		
		//blue
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.borderBlueName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(borderBlueName)!=null) {
			try {
				this.borderBlue = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(borderBlueName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE0.isValidValue(this.borderBlue))
			return false;
		
		//opacity
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.borderOpacityName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(borderOpacityName)!=null) {
			try {
				this.borderOpacity = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(borderOpacityName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!PERCENTAGE1.isValidValue(this.borderOpacity))
			return false;
		
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.borderRed!=null && this.borderGreen!=null && this.borderBlue!=null && this.borderOpacity!=null) {
			Color c = new Color(borderRed, borderGreen, borderBlue, borderOpacity);
			rectangle.setStroke(c);
		}
		
		///////////
        return true;
	}
	
	
	/**
	 * set the stroke width;
	 * 
	 * note that the default value 1.0 is implicitly set if there is no non-null value calculated;
	 * @param line
	 * @return
	 */
	private boolean setStrokeWidth(Rectangle rectangle) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.strokeWidthName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(strokeWidthName)!=null) {
			try{
				this.strokeWidth = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(strokeWidthName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		
		if(!STROKE_WIDTH.isValidValue(this.strokeWidth))
			return false;
		
		////////////
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.strokeWidth!=null)
			rectangle.setStrokeWidth(this.strokeWidth);
		
		
		return true;
	}
	
	/**
	 * 
	 * @param rectangle
	 * @return
	 */
	private boolean setStrokeType(Rectangle rectangle) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.strokeTypeName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(strokeTypeName)!=null) {
			try {
				this.strokeType = StrokeType.valueOf(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(strokeTypeName));
			}catch(IllegalArgumentException e) {
				return false;
			}
		}
		if(!STROKE_TYPE.isValidValue(this.strokeType))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.strokeType!=null)
			rectangle.setStrokeType(this.strokeType);
		
		
		return true;
	}
	
	/**
	 * StrokeDashType has no default;
	 * 
	 * set StrokeDashType if the value string is non-null and recognized as one of VfStrokeDashType;
	 * do not set StrokeDashType otherwise;
	 * @param line
	 * @return
	 */
	private boolean setStrokeDashType(Rectangle rectangle) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.dashTypeName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(dashTypeName)!=null) {
			try {
				this.dashType = VfStrokeDashType.valueOf(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(dashTypeName));
			}catch(IllegalArgumentException e) {
				return false;
			}
		}
		
		if(!STROKE_DASH_TYPE.isValidValue(this.dashType))
			return false;
		
		if(this.dashType!=null)
			rectangle.getStrokeDashArray().addAll(this.dashType.getDashArrayList());
		
		/////////
		return true;
	}
	
	/**
	 * set the stroke line cap;
	 * 
	 * note that the default value {@link StrokeLineCap#SQUARE} is implicitly set if there is no non-null value calculated;
	 * 
	 * @param line
	 * @return
	 */
	private boolean setStrokeLineCap(Rectangle rectangle) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.strokeLineCapName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(strokeLineCapName)!=null) {
			try {
				this.strokeLineCap = StrokeLineCap.valueOf(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(strokeLineCapName));
			}catch(IllegalArgumentException e) {
				return false;
			}
		}
		if(!STROKE_LINE_CAP.isValidValue(this.strokeLineCap))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.strokeLineCap!=null)
			rectangle.setStrokeLineCap(this.strokeLineCap);
		
		return true;
	}
	
	/**
	 * 
	 * @param rectangle
	 * @return
	 */
	private boolean setStrokeLineJoin(Rectangle rectangle) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.strokeLineJoinName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(strokeLineJoinName)!=null) {
			try {
				this.strokeLineJoin = StrokeLineJoin.valueOf(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(strokeLineJoinName));
			}catch(IllegalArgumentException e) {
				return false;
			}
		}
		if(!STROKE_LINE_JOIN.isValidValue(this.strokeLineJoin))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.strokeLineJoin!=null)
			rectangle.setStrokeLineJoin(this.strokeLineJoin);
		
		return true;
	}

	/**
	 * 
	 * @param rectangle
	 * @return
	 */
	private boolean setStrokeMiterLimit(Rectangle rectangle) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.strokeMiterLimitName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(strokeMiterLimitName)!=null) {
			try{
				this.strokeMiterLimit = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(strokeMiterLimitName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		if(!STROKE_MITER_LIMIT.isValidValue(this.strokeMiterLimit))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.strokeMiterLimit!=null)
			rectangle.setStrokeMiterLimit(strokeMiterLimit);
		
		
		return true;
	}
	
}
