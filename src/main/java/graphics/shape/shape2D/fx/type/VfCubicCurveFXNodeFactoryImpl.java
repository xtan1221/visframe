package graphics.shape.shape2D.fx.type;

import java.util.Map;
import basic.SimpleName;
import graphics.property.node.PropertyTreeNodePathNameBuilder;
import graphics.shape.shape2D.fx.VfShapeTypeFXNodeFactoryBase;
import graphics.shape.shape2D.fx.utils.VfStrokeDashType;
import graphics.shape.shape2D.type.VfCubicCurve;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeLineCap;

import static graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfColorGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfStrokeGraphicsPropertyNodeFactory.*;
import static graphics.shape.shape2D.type.VfCubicCurve.*;

/**
 * 
 * @author tanxu
 *
 */
public class VfCubicCurveFXNodeFactoryImpl extends VfShapeTypeFXNodeFactoryBase<CubicCurve, VfCubicCurve> {
	static VfCubicCurveFXNodeFactoryImpl SINGLETON;
	
	/**
	 * 
	 * @return
	 */
	public static VfCubicCurveFXNodeFactoryImpl getSingleton() {
		if(SINGLETON==null) {
			SINGLETON=new VfCubicCurveFXNodeFactoryImpl();
		}
		return SINGLETON;
	}
	
	//////////////////////////////////////
	private Double startX;
    private Double startY;
    private final SimpleName startXName;
    private final SimpleName startYName;            
    
    private Double endX;
    private Double endY;
    private final SimpleName endXName;
    private final SimpleName endYName;
    
    private Double controlX1;
    private Double controlY1;
    private final SimpleName controlX1Name;
    private final SimpleName controlY1Name;
    
    private Double controlX2;
    private Double controlY2;
    private final SimpleName controlX2Name;
    private final SimpleName controlY2Name;
    
    //////////////////
    private Double borderRed;
    private Double borderGreen;
    private Double borderBlue;
    private Double borderOpacity;
    private final SimpleName borderRedName;
    private final SimpleName borderGreenName;
    private final SimpleName borderBlueName;
    private final SimpleName borderOpacityName;
    
    private Double strokeWidth;
    private StrokeLineCap strokeLineCap;
    private VfStrokeDashType dashType; //see VfStrokeDashType
    private final SimpleName strokeWidthName;
    private final SimpleName strokeLineCapName;
    private final SimpleName dashTypeName; //
    
    ///////////////////////////////////
    
	/**
	 * private constructor
	 */
	private VfCubicCurveFXNodeFactoryImpl() {
		//initialize
		startXName = PropertyTreeNodePathNameBuilder.start(START_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
        startYName = PropertyTreeNodePathNameBuilder.start(START_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();
        
        endXName = PropertyTreeNodePathNameBuilder.start(END_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
        endYName = PropertyTreeNodePathNameBuilder.start(END_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();
       
        //Defines the X coordinate of the first control point of the cubic curve segment.
        controlX1Name = PropertyTreeNodePathNameBuilder.start(CONTROL1_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
        //Defines the Y coordinate of the first control point of the cubic curve segment.
        controlY1Name = PropertyTreeNodePathNameBuilder.start(CONTROL1_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();
        //Defines the X coordinate of the second control point of the cubic curve segment.
        controlX2Name = PropertyTreeNodePathNameBuilder.start(CONTROL2_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
        //Defines the Y coordinate of the second control point of the cubic curve segment.
        controlY2Name = PropertyTreeNodePathNameBuilder.start(CONTROL2_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();
        
       
        borderRedName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(R).addNext(PERCENTAGE0).build();
        borderGreenName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(G).addNext(PERCENTAGE0).build();
        borderBlueName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(B).addNext(PERCENTAGE0).build();
        borderOpacityName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(OPACITY).addNext(PERCENTAGE1).build();
        
        strokeWidthName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(CURVE_LINE_STROKE).addNext(STROKE_WIDTH).build();
        strokeLineCapName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(CURVE_LINE_STROKE).addNext(STROKE_LINE_CAP).build();
        dashTypeName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(CURVE_LINE_STROKE).addNext(STROKE_DASH_TYPE).build();
	}
	
	
	@Override
	public VfCubicCurve getVfShapeType() {
		return VfCubicCurve.SINGLETON;
	}
	
	////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CubicCurve makeFXNode(Map<SimpleName, String> leafGraphicsPropertyFullPathNameOnTreeStringValueMap) {
		this.initialize();
		//
		this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.putAll(leafGraphicsPropertyFullPathNameOnTreeStringValueMap);
		
		//
		CubicCurve cubicCurve = new CubicCurve();
		//
		boolean typeSpecificPropertySuccessfullySet = this.setTypeSpecificTreeProperty(cubicCurve);
		
		if(typeSpecificPropertySuccessfullySet) {
			boolean transformSuccessfullySet = this.setTransform(cubicCurve);
			if(!transformSuccessfullySet)
				cubicCurve = null;
		}else {
			cubicCurve = null;
		}
		
		return cubicCurve;
	}

	@Override
	public void initialize() {
		super.initialize();
		
		//
		startX=LAYOUT_COORD.getDefaultValue();
	    startY=LAYOUT_COORD.getDefaultValue();
	     
	    endX=LAYOUT_COORD.getDefaultValue();
	    endY=LAYOUT_COORD.getDefaultValue();
	    
	    controlX1=LAYOUT_COORD.getDefaultValue();
	    controlY1=LAYOUT_COORD.getDefaultValue();
	    
	    controlX2=LAYOUT_COORD.getDefaultValue();
	    controlY2=LAYOUT_COORD.getDefaultValue();
	    
	    //////////////////
	    borderRed=PERCENTAGE0.getDefaultValue();
	    borderGreen=PERCENTAGE0.getDefaultValue();
	    borderBlue=PERCENTAGE0.getDefaultValue();
	    borderOpacity=PERCENTAGE1.getDefaultValue();
	    
	    strokeWidth = STROKE_WIDTH.getDefaultValue();
	    strokeLineCap = STROKE_LINE_CAP.getDefaultValue();
	    dashType = STROKE_DASH_TYPE.getDefaultValue();
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
	public boolean setTypeSpecificTreeProperty(CubicCurve cubicCurve) {
		//START coordinate
		if(!this.setStartCoordinate(cubicCurve))
			return false;
			
        //END coordinate
		if(!this.setEndCoordinate(cubicCurve))
			return false;
        
		if(!this.setControl1(cubicCurve))
			return false;
		
		if(!this.setControl2(cubicCurve))
			return false;
		
		//color
		if(!this.setBorderColor(cubicCurve))
			return false;
		
		//stroke
		if(!this.setStrokeWidth(cubicCurve))
			return false;
		
		if(!this.setStrokeLineCap(cubicCurve))
			return false;
		
		if(!this.setStrokeDashType(cubicCurve))
			return false;
		
		
		return true;
	}
	
	/**
	 * set the start x and y;
	 * 
	 * @param cubicCurve
	 * @return
	 */
	private boolean setStartCoordinate(CubicCurve cubicCurve) {
		///////////////////startX
		//extract non-null value for startX if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.startXName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(startXName)!=null) {
			try {
				this.startX = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(startXName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.startX))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.startX!=null)
			cubicCurve.setStartX(this.startX);
		
		///////////////////////startY
		//extract non-null value for startY if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.startYName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(startYName)!=null) {
			try{
				this.startY = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(startYName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.startY))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.startY!=null)
			cubicCurve.setStartY(this.startY);
		
		
		///////////////////////////
        return true;
       
	}
	
	/**
	 * set the end x and y;
	 * 
	 * note that line's end x and y have default value as 0.0;
	 * 
	 * thus if there is no non-null value calculated, skip it and the default value will be used implicitly;
	 * 
	 * @param cubicCurve
	 * @return
	 */
	private boolean setEndCoordinate(CubicCurve cubicCurve) {
		///////////////////endX
		//extract non-null value for endX if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.endXName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(endXName)!=null) {
			try {
				this.endX = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(endXName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.endX))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.endX!=null)
			cubicCurve.setEndX(this.endX);
		
		///////////////////////endY
		//extract non-null value for endY if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.endYName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(endYName)!=null) {
			try{
				this.endY = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(endYName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.endY))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.endY!=null)
			cubicCurve.setEndY(this.endY);
		
		
		///////////////////////////
        return true;
	}
	
	
	private boolean setControl1(CubicCurve cubicCurve) {
		///////////////////controlX1
		//extract non-null value for controlX1 if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.controlX1Name)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(controlX1Name)!=null) {
			try {
				this.controlX1 = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(controlX1Name));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.controlX1))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.controlX1!=null)
			cubicCurve.setControlX1(this.controlX1);
		
		///////////////////////controlY1
		//extract non-null value for controlY1 if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.controlY1Name)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(controlY1Name)!=null) {
			try{
				this.controlY1 = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(controlY1Name));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.controlY1))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.controlY1!=null)
			cubicCurve.setControlY1(this.controlY1);
		
		
		///////////////////////////
        return true;
	}
	
	private boolean setControl2(CubicCurve cubicCurve) {
		///////////////////controlX2
		//extract non-null value for controlX2 if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.controlX2Name)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(controlX2Name)!=null) {
			try {
				this.controlX2 = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(controlX2Name));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.controlX2))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.controlX2!=null)
			cubicCurve.setControlX2(this.controlX2);
		
		///////////////////////controlY2
		//extract non-null value for controlY2 if in the leafGraphicsPropertyFullPathNameOnTreeStringValueMap
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.controlY2Name)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(controlY2Name)!=null) {
			try{
				this.controlY2 = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(controlY2Name));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		//if the property value is invalid, the shape instance cannot be created
		if(!LAYOUT_COORD.isValidValue(this.controlY2))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.controlY2!=null)
			cubicCurve.setControlY2(this.controlY2);
		
		
		///////////////////////////
        return true;
	}
	
	
	/**
	 * @param cubicCurve
	 * @return
	 */
	private boolean setBorderColor(CubicCurve cubicCurve) {
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
			cubicCurve.setStroke(c);
		}
		
		///////////
        return true;
	}
	
	/**
	 * set the stroke width;
	 * @param cubicCurve
	 * @return
	 */
	private boolean setStrokeWidth(CubicCurve cubicCurve) {
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
			cubicCurve.setStrokeWidth(this.strokeWidth);
		
		
		return true;
	}

	/**
	 * set the stroke line cap;
	 * 
	 * @param cubicCurve
	 * @return
	 */
	private boolean setStrokeLineCap(CubicCurve cubicCurve) {
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
			cubicCurve.setStrokeLineCap(this.strokeLineCap);
		
		return true;
	}
	
	/**
	 * StrokeDashType's default value is null and can be null;
	 * thus, if no non-null valid value is given, do nothing;
	 * 
	 * set StrokeDashType if the value string is non-null and recognized as one of VfStrokeDashType;
	 * do not set StrokeDashType otherwise;
	 * @param cubicCurve
	 * @return
	 */
	private boolean setStrokeDashType(CubicCurve cubicCurve) {
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
			cubicCurve.getStrokeDashArray().addAll(this.dashType.getDashArrayList());
		
		/////////
		return true;
	}

	
}
