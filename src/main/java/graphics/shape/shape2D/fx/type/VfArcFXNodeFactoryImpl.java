package graphics.shape.shape2D.fx.type;

import java.util.Map;
import basic.SimpleName;
import graphics.property.node.PropertyTreeNodePathNameBuilder;
import graphics.shape.shape2D.fx.VfShapeTypeFXNodeFactoryBase;
import graphics.shape.shape2D.fx.utils.VfStrokeDashType;
import graphics.shape.shape2D.type.VfArc;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

import static graphics.property.shape2D.factory.VfArcClosureTypeGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfColorGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfStrokeGraphicsPropertyNodeFactory.*;
import static graphics.shape.shape2D.type.VfArc.*;

/**
 * mandatory properties include
 * 1. center coordinate
 * 		x and y
 * 2. radius
 * 		x and y
 * 
 * @author tanxu
 * 
 */
public class VfArcFXNodeFactoryImpl extends VfShapeTypeFXNodeFactoryBase<Arc, VfArc> {
	static VfArcFXNodeFactoryImpl SINGLETON;
	
	/**
	 * @return
	 */
	public static VfArcFXNodeFactoryImpl getSingleton() {
		if(SINGLETON==null) {
			SINGLETON=new VfArcFXNodeFactoryImpl();
		}
		return SINGLETON;
	}
	
	//////////////////////////////////////
	//CENTER
	private Double centerX;
    private Double centerY;
    private final SimpleName centerXName;
    private final SimpleName centerYName;            
    
    //RADIUS
    private Double radiusX; //
    private Double radiusY; //
    private final SimpleName radiusXName;
    private final SimpleName radiusYName;
    
    
    //StartAngle
    private Double startAngle;
    private final SimpleName startAngleName;
    
    //Length
    private Double length;
    private final SimpleName lengthName;
    
    
    //ArcClosureType
    private ArcType arcClosureType;//
    private final SimpleName arcClosureTypeName;
    
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
    private Double strokeWidth;//default value
    private StrokeType strokeType;
    private VfStrokeDashType dashType; //see VfStrokeDashType
    private StrokeLineCap strokeLineCap; //default value
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
	private VfArcFXNodeFactoryImpl() {
		//
		centerXName = PropertyTreeNodePathNameBuilder.start(CENTER_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
		centerYName = PropertyTreeNodePathNameBuilder.start(CENTER_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();
        
		radiusXName = PropertyTreeNodePathNameBuilder.start(RADIUS_X_PROPERTY_TREE.getName()).addNext(LENDIST).build();
		radiusYName = PropertyTreeNodePathNameBuilder.start(RADIUS_Y_PROPERTY_TREE.getName()).addNext(LENDIST).build();
		
		startAngleName = PropertyTreeNodePathNameBuilder.start(START_ANGLE_PROPERTY_TREE.getName()).addNext(THETA_IN_DEGREE).build();
		lengthName = PropertyTreeNodePathNameBuilder.start(LENGTH_PROPERTY_TREE.getName()).addNext(THETA_IN_DEGREE).build();
        
		arcClosureTypeName = PropertyTreeNodePathNameBuilder.start(ARC_CLOSURE_TYPE_PROPERTY_TREE.getName()).addNext(ARC_CLOSURE_TYPE).build();
		
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
	public VfArc getVfShapeType() {
		return VfArc.SINGLETON;
	}
	
	////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Arc makeFXNode(Map<SimpleName, String> leafGraphicsPropertyFullPathNameOnTreeStringValueMap) {
		this.initialize();
		//
		this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.putAll(leafGraphicsPropertyFullPathNameOnTreeStringValueMap);
		
		//
		Arc arc = new Arc();
		//
		boolean typeSpecificPropertySuccessfullySet = this.setTypeSpecificTreeProperty(arc);
		
		if(typeSpecificPropertySuccessfullySet) {
			boolean transformSuccessfullySet = this.setTransform(arc);
			if(!transformSuccessfullySet)
				arc = null;
		}else {
			arc = null;
		}
		
		return arc;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		//CENTER 
		centerX=LAYOUT_COORD.getDefaultValue();
	    centerY=LAYOUT_COORD.getDefaultValue();        
	    
	    //RADIUS
	    radiusX=LENDIST.getDefaultValue();
	    radiusY=LENDIST.getDefaultValue();
	    
	    //StartAngle
	    startAngle=THETA_IN_DEGREE.getDefaultValue();
	    
	    //Length
	    length=THETA_IN_DEGREE.getDefaultValue();
	    
	    //ArcClosureType
	    arcClosureType = ARC_CLOSURE_TYPE.getDefaultValue();//
	    
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
	 * 
	 * if any of the mandatory tree's leaf node is not in the leafNodeFullPathNameOnTreeStringValueMap, the Line object cannot be created;
	 * 
	 * return true if all properties are successfully set; false otherwise;
	 */
	@Override
	public boolean setTypeSpecificTreeProperty(Arc arc) {
		if(!this.setCenter(arc))
			return false;
			
		if(!this.setRadius(arc))
			return false;
		
		if(!this.setStartAngle(arc))
			return false;
		
		if(!this.setLength(arc))
			return false;
		
		if(!this.setArcClosureType(arc))
			return false;
		
		if(!this.setFillColor(arc))
			return false;
		
		if(!this.setBorderColor(arc))
			return false;
		
		if(!this.setStrokeWidth(arc))
			return false;
		
		if(!this.setStrokeType(arc))
			return false;
		
		if(!this.setStrokeDashType(arc))
			return false;
		
		if(!this.setStrokeLineCap(arc))
			return false;
		
		if(!this.setStrokeLineJoin(arc))
			return false;
		
		if(!this.setStrokeMiterLimit(arc))
			return false;
		
		
		return true;
	}
	
	/**
	 * 
	 * @param arc
	 * @return
	 */
	private boolean setCenter(Arc arc) {
		///////////////////centerX
		//extract non-null value for centerX if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.centerXName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(centerXName)!=null) {
			try {
				this.centerX = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(centerXName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.centerX))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.centerX!=null)
			arc.setCenterX(this.centerX);
		
		///////////////////////centerY
		//extract non-null value for centerY if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.centerYName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(centerYName)!=null) {
			try{
				this.centerY = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(centerYName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.centerY))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.centerY!=null)
			arc.setCenterY(this.centerY);
		
		
		///////////////////////////
        return true;
	}
	
	/**
	 * set radius properties
	 * 
	 * @param arc
	 * @return
	 */
	private boolean setRadius(Arc arc) {
		////////////////////////radiusX
		//extract non-null value from leafGraphicsPropertyFullPathNameOnTreeStringValueMap if present
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.radiusXName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(radiusXName)!=null) {
			try {
				this.radiusX = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(radiusXName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//check if the value is valid by checking the corresponding GraphicsPropertyLeafNode's features;
		if(!LENDIST.isValidValue(this.radiusX))
			return false;
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.radiusX!=null)
			arc.setRadiusX(this.radiusX);
		
		///////////////////////////radiusY
		//extract non-null value from leafGraphicsPropertyFullPathNameOnTreeStringValueMap if present
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.radiusYName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(radiusYName)!=null) {
			try {
				this.radiusY = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(radiusYName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		
		if(!LENDIST.isValidValue(this.radiusY))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.radiusY!=null)
			arc.setRadiusY(this.radiusY);
		
		
		///////////////////////
        return true;
	}
	
	
	/**
	 * note that whether or not the corresponding property is mandatory or not should not be explicitly referred to;
	 * @param arc
	 * @return
	 */
	private boolean setStartAngle(Arc arc) {
		//////////////
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.startAngleName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(startAngleName)!=null) {
			//the property is explicitly calculated
			try {
				this.startAngle = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(startAngleName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		
		//either a calculated value or the default value of corresponding GraphicsPropertyLeafNode (which may be invalid value if default value is null and the GraphicsPropertyLeafNode cannot be null - thus mandatory!);
		if(!THETA_IN_DEGREE.isValidValue(this.startAngle)) //calculated value is invalid in this case;
			return false;
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.startAngle!=null)
			arc.setStartAngle(this.startAngle);
		
		//////////////
		return true;
	}
	
	/**
	 * set the length (angular extent) of the arc;
	 * 
	 * @param arc
	 * @return
	 */
	private boolean setLength(Arc arc) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.lengthName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(lengthName)!=null) {
			try {
				this.length = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(lengthName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//
		if(!THETA_IN_DEGREE.isValidValue(this.length))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.length!=null)
			arc.setLength(this.length);
		
		/////////////
        return true;
	}
	
	/**
	 * 
	 * @param arc
	 * @return
	 */
	private boolean setArcClosureType(Arc arc) {
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.arcClosureTypeName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(arcClosureTypeName)!=null) {
			try {
				this.arcClosureType = ArcType.valueOf(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(arcClosureTypeName));
			}catch(IllegalArgumentException e) {
				return false;//the string value is invalid
			}
		}
		
		if(!ARC_CLOSURE_TYPE.isValidValue(this.arcClosureType))
			return false;
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.arcClosureType!=null)
			arc.setType(this.arcClosureType);
        
		////////////////
		return true;
	}
	

	/**
	 * note that if transparent is true, no need to set fill color;
	 * 
	 * @param line
	 * @return
	 */
	private boolean setFillColor(Arc arc) {
		//transparent
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.transparentName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(transparentName)!=null) {
			transparent = Boolean.parseBoolean(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(transparentName));
			if(transparent) {
				arc.setFill(Color.TRANSPARENT);
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
	        arc.setFill(c);
		}
        //////////
        return true;
	}
	
	/**
	 * 
	 * @param arc
	 * @return
	 */
	private boolean setBorderColor(Arc arc) {
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
			arc.setStroke(c);
		}
		
		///////////
        return true;
	}
	
	
	/**
	 * set the stroke width;
	 * 
	 * @param line
	 * @return
	 */
	private boolean setStrokeWidth(Arc arc) {
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
			arc.setStrokeWidth(this.strokeWidth);
		
		
		return true;
	}
	
	/**
	 * 
	 * @param arc
	 * @return
	 */
	private boolean setStrokeType(Arc arc) {
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
			arc.setStrokeType(this.strokeType);
		
		
		return true;
	}
	
	/**
	 * StrokeDashType's default value is null and can be null;
	 * thus, if no non-null valid value is given, do nothing;
	 * 
	 * set StrokeDashType if the value string is non-null and recognized as one of VfStrokeDashType;
	 * do not set StrokeDashType otherwise;
	 * @param line
	 * @return
	 */
	private boolean setStrokeDashType(Arc arc) {
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
			arc.getStrokeDashArray().addAll(this.dashType.getDashArrayList());
		
		/////////
		return true;
	}
	
	/**
	 * set the stroke line cap;
	 * 
	 * @param line
	 * @return
	 */
	private boolean setStrokeLineCap(Arc arc) {
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
			arc.setStrokeLineCap(this.strokeLineCap);
		
		return true;
	}
	
	/**
	 * 
	 * @param arc
	 * @return
	 */
	private boolean setStrokeLineJoin(Arc arc) {
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
			arc.setStrokeLineJoin(this.strokeLineJoin);
		
		return true;
	}

	/**
	 * 
	 * @param arc
	 * @return
	 */
	private boolean setStrokeMiterLimit(Arc arc) {
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
			arc.setStrokeMiterLimit(strokeMiterLimit);
		
		
		return true;
	}
	
}
