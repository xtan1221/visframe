package graphics.shape.shape2D.fx.type;

import java.util.Map;
import basic.SimpleName;
import graphics.property.node.PropertyTreeNodePathNameBuilder;
import graphics.shape.shape2D.fx.VfShapeTypeFXNodeFactoryBase;
import graphics.shape.shape2D.fx.utils.VfStrokeDashType;
import graphics.shape.shape2D.type.VfLine;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

import static graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfColorGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfStrokeGraphicsPropertyNodeFactory.*;
import static graphics.shape.shape2D.type.VfLine.*;

/**
 * 
 * @author tanxu
 *
 */
public class VfLineFXNodeFactoryImpl extends VfShapeTypeFXNodeFactoryBase<Line, VfLine> {
	static VfLineFXNodeFactoryImpl SINGLETON;
	
	/**
	 * 
	 * @return
	 */
	public static VfLineFXNodeFactoryImpl getSingleton() {
		if(SINGLETON==null) {
			SINGLETON=new VfLineFXNodeFactoryImpl();
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
    
    //////////////////
    private Double red;
    private Double green;
    private Double blue;
    private Double opacity;
    private final SimpleName redName;
    private final SimpleName greenName;
    private final SimpleName blueName;
    private final SimpleName opacityName;
    
    private Double strokeWidth;
    private StrokeLineCap strokeLineCap;
    private VfStrokeDashType dashType;
    private final SimpleName strokeWidthName;
    private final SimpleName strokeLineCapName;
    private final SimpleName dashTypeName;
    
    ///////////////////////////////////
    
	/**
	 * private constructor
	 */
	private VfLineFXNodeFactoryImpl() {
		//initialize
		startXName = PropertyTreeNodePathNameBuilder.start(START_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
        startYName = PropertyTreeNodePathNameBuilder.start(START_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();
        
        endXName = PropertyTreeNodePathNameBuilder.start(END_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
        endYName = PropertyTreeNodePathNameBuilder.start(END_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();
        
        redName = PropertyTreeNodePathNameBuilder.start(COLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(R).addNext(PERCENTAGE0).build();
        greenName = PropertyTreeNodePathNameBuilder.start(COLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(G).addNext(PERCENTAGE0).build();
        blueName = PropertyTreeNodePathNameBuilder.start(COLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(B).addNext(PERCENTAGE0).build();
        opacityName = PropertyTreeNodePathNameBuilder.start(COLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(OPACITY).addNext(PERCENTAGE1).build();
        
        strokeWidthName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(CURVE_LINE_STROKE).addNext(STROKE_WIDTH).build();
        strokeLineCapName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(CURVE_LINE_STROKE).addNext(STROKE_LINE_CAP).build();
        dashTypeName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(CURVE_LINE_STROKE).addNext(STROKE_DASH_TYPE).build();
	}
	
	
	@Override
	public VfLine getVfShapeType() {
		return VfLine.SINGLETON;
	}
	
	////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Line makeFXNode(Map<SimpleName, String> leafGraphicsPropertyFullPathNameOnTreeStringValueMap) {
		this.initialize();
		//
		this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.putAll(leafGraphicsPropertyFullPathNameOnTreeStringValueMap);
		
		//
		Line line = new Line();
		//
		boolean typeSpecificPropertySuccessfullySet = this.setTypeSpecificTreeProperty(line);
		
		if(typeSpecificPropertySuccessfullySet) {
			boolean transformSuccessfullySet = this.setTransform(line);
			if(!transformSuccessfullySet)
				line = null;
		}else {
			line = null;
		}
		
		
		return line;
	}

	@Override
	public void initialize() {
		super.initialize();
		
		//
		startX=0.0;
	    startY=0.0;
	     
	    endX=0.0;
	    endY=0.0;
	    
	    //////////////////
	    red=0.0;
	    green=0.0;
	    blue=0.0;
	    opacity=1.0;
	    
	    strokeWidth = 1.0;//default value
	    strokeLineCap = StrokeLineCap.SQUARE; //default value
	    dashType = null; //see VfStrokeDashType
		
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
	public boolean setTypeSpecificTreeProperty(Line line) {
		//START coordinate
		if(!this.setStartCoordinate(line))
			return false;
			
        //END coordinate
		if(!this.setEndCoordinate(line))
			return false;
        
		//color
		if(!this.setColor(line))
			return false;
		
		//stroke
		if(!this.setStrokeWidth(line))
			return false;
		
		if(!this.setStrokeLineCap(line))
			return false;
		
		if(!this.setStrokeDashType(line))
			return false;
		
		
		return true;
	}
	
	/**
	 * set the start x and y;
	 * @param line
	 * @return
	 */
	private boolean setStartCoordinate(Line line) {
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
			line.setStartX(this.startX);
		
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
			line.setStartY(this.startY);
		
		
		///////////////////////////
        return true;
	}
	
	/**
	 * set the end x and y;
	 * 
	 * @param line
	 * @return
	 */
	private boolean setEndCoordinate(Line line) {
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
			line.setEndX(this.endX);
		
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
			line.setEndY(this.endY);
		
		
		///////////////////////////
        return true;
	}
	
	/**
	 * @param line
	 * @return
	 */
	private boolean setColor(Line line) {
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
			line.setStroke(c);
		}
		
		///////////
        return true;
	}
	
	/**
	 * set the stroke width;
	 * @param line
	 * @return
	 */
	private boolean setStrokeWidth(Line line) {
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
			line.setStrokeWidth(this.strokeWidth);
		
		
		return true;
	}

	/**
	 * set the stroke line cap;
	 * 
	 * @param line
	 * @return
	 */
	private boolean setStrokeLineCap(Line line) {
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
			line.setStrokeLineCap(this.strokeLineCap);
		
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
	private boolean setStrokeDashType(Line line) {
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
			line.getStrokeDashArray().addAll(this.dashType.getDashArrayList());
		
		/////////
		return true;
	}

	
}
