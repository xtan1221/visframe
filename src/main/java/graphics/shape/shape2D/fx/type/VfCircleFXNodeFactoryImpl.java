package graphics.shape.shape2D.fx.type;

import java.util.Map;
import basic.SimpleName;
import graphics.property.node.PropertyTreeNodePathNameBuilder;
import graphics.shape.shape2D.fx.VfShapeTypeFXNodeFactoryBase;
import graphics.shape.shape2D.fx.utils.VfStrokeDashType;
import graphics.shape.shape2D.type.VfCircle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

import static graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfColorGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfStrokeGraphicsPropertyNodeFactory.*;
import static graphics.shape.shape2D.type.VfCircle.*;
/**
 * 
 * @author tanxu
 *
 */
public class VfCircleFXNodeFactoryImpl extends VfShapeTypeFXNodeFactoryBase<Circle, VfCircle> {
	static VfCircleFXNodeFactoryImpl SINGLETON;
	
	/**
	 * 
	 * @return
	 */
	public static VfCircleFXNodeFactoryImpl getSingleton() {
		if(SINGLETON==null) {
			SINGLETON=new VfCircleFXNodeFactoryImpl();
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
    private Double radius; //
    private final SimpleName radiusName;
    
    //FILLCOLOR
    private Boolean fillTransparent; //whether or not fill is transparent
    private Double fillRed;
    private Double fillGreen;
    private Double fillBlue;
    private Double fillOpacity;
    private final SimpleName fillTransparentName;
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
    
    //////////////////
    //STROKE
    private Double strokeWidth;
    private StrokeType strokeType;
    private VfStrokeDashType dashType; //see VfStrokeDashType
    private final SimpleName strokeWidthName;
    private final SimpleName strokeTypeName;
    private final SimpleName dashTypeName;
    
    ///////////////////////////////////
    
	/**
	 * private constructor
	 */
	private VfCircleFXNodeFactoryImpl() {
		//initialize
		centerXName = PropertyTreeNodePathNameBuilder.start(CENTER_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
		centerYName = PropertyTreeNodePathNameBuilder.start(CENTER_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();
        
		radiusName = PropertyTreeNodePathNameBuilder.start(RADIUS_PROPERTY_TREE.getName()).addNext(LENDIST).build();
		
		fillTransparentName = PropertyTreeNodePathNameBuilder.start(FILLCOLOR_PROPERTY_TREE.getName()).addNext(FILL_COLOR).addNext(FILL_IS_TRANSPARENT).build();
		fillRedName = PropertyTreeNodePathNameBuilder.start(FILLCOLOR_PROPERTY_TREE.getName()).addNext(FILL_COLOR).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(R).addNext(PERCENTAGE0).build();
		fillGreenName = PropertyTreeNodePathNameBuilder.start(FILLCOLOR_PROPERTY_TREE.getName()).addNext(FILL_COLOR).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(G).addNext(PERCENTAGE0).build();
		fillBlueName = PropertyTreeNodePathNameBuilder.start(FILLCOLOR_PROPERTY_TREE.getName()).addNext(FILL_COLOR).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(B).addNext(PERCENTAGE0).build();
		fillOpacityName = PropertyTreeNodePathNameBuilder.start(FILLCOLOR_PROPERTY_TREE.getName()).addNext(FILL_COLOR).addNext(RGB_OPACITY_COLOR).addNext(OPACITY).addNext(PERCENTAGE1).build();
        
		borderRedName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(R).addNext(PERCENTAGE0).build();
		borderGreenName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(G).addNext(PERCENTAGE0).build();
		borderBlueName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(RGB).addNext(B).addNext(PERCENTAGE0).build();
		borderOpacityName = PropertyTreeNodePathNameBuilder.start(BORDERCOLOR_PROPERTY_TREE.getName()).addNext(RGB_OPACITY_COLOR).addNext(OPACITY).addNext(PERCENTAGE1).build();
       
        strokeWidthName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(CIRCLE_ELLIPSE_STROKE).addNext(STROKE_WIDTH).build();
        strokeTypeName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(CIRCLE_ELLIPSE_STROKE).addNext(STROKE_TYPE).build();
        dashTypeName = PropertyTreeNodePathNameBuilder.start(STROKE_PROPERTY_TREE.getName()).addNext(CIRCLE_ELLIPSE_STROKE).addNext(STROKE_DASH_TYPE).build();
	}
	
	
	@Override
	public VfCircle getVfShapeType() {
		return VfCircle.SINGLETON;
	}
	
	////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Circle makeFXNode(Map<SimpleName, String> leafGraphicsPropertyFullPathNameOnTreeStringValueMap) {
		this.initialize();
		//
		this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.putAll(leafGraphicsPropertyFullPathNameOnTreeStringValueMap);
		
		//
		Circle circle = new Circle();
		//
		boolean typeSpecificPropertySuccessfullySet = this.setTypeSpecificTreeProperty(circle);
		
		if(typeSpecificPropertySuccessfullySet) {
			boolean transformSuccessfullySet = this.setTransform(circle);
			if(!transformSuccessfullySet)
				circle = null;
		}else {
			circle = null;
		}
		
		
		return circle;
	}

	@Override
	public void initialize() {
		super.initialize();
		
		//
		centerX=LAYOUT_COORD.getDefaultValue();
	    centerY=LAYOUT_COORD.getDefaultValue();
	    
	    //RADIUS
	    radius=LENDIST.getDefaultValue();
	    
	    //FILLCOLOR
	    fillTransparent = FILL_IS_TRANSPARENT.getDefaultValue();
	    fillRed=PERCENTAGE0.getDefaultValue();
	    fillGreen=PERCENTAGE0.getDefaultValue();
	    fillBlue=PERCENTAGE0.getDefaultValue();
	    fillOpacity=PERCENTAGE1.getDefaultValue();
	    
	    //BORDERCOLOR
	    borderRed=PERCENTAGE0.getDefaultValue();
	    borderGreen=PERCENTAGE0.getDefaultValue();
	    borderBlue=PERCENTAGE0.getDefaultValue();
	    borderOpacity=PERCENTAGE1.getDefaultValue();
	    
	    //////////////////
	    //STROKE
	    strokeWidth = STROKE_WIDTH.getDefaultValue();//default value
	    strokeType = STROKE_TYPE.getDefaultValue();
	    dashType = STROKE_DASH_TYPE.getDefaultValue(); //see VfStrokeDashType
		
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
	public boolean setTypeSpecificTreeProperty(Circle circle) {
		//center coordinate
		if(!this.setCenter(circle))
			return false;
			
        //radius coordinate
		if(!this.setRadius(circle))
			return false;
        
		//fill color
		if(!this.setFillColor(circle))
			return false;
		
		if(!this.setBorderColor(circle))
			return false;
		
		//stroke
		if(!this.setStrokeWidth(circle))
			return false;
		
		if(!this.setStrokeType(circle))
			return false;
		
		if(!this.setStrokeDashType(circle))
			return false;
		
		
		return true;
	}
	
	/**
	 * the same with {@link VfArcFXNodeFactoryImpl}
	 * @param circle
	 * @return
	 */
	private boolean setCenter(Circle circle) {
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
			circle.setCenterX(this.centerX);
		
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
			circle.setCenterY(this.centerY);
		
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
	 * @param circle
	 * @return
	 */
	private boolean setRadius(Circle circle) {
		///////////////////////////radius
		//extract non-null value from leafGraphicsPropertyFullPathNameOnTreeStringValueMap if present
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.radiusName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(radiusName)!=null) {
			try {
				this.radius = Double.parseDouble(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(radiusName));
			}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
		}
		
		if(!LENDIST.isValidValue(this.radius))
			return false;
		
		//only set the property value if not null; otherwise, do nothing (probably a default value will be set in JAVAFX);
		if(this.radius!=null)
			circle.setRadius(this.radius);
		
		
		///////////////////////
        return true;
	}
	
	/**
	 * the same with {@link VfArcFXNodeFactoryImpl}
	 * @param line
	 * @return
	 */
	private boolean setFillColor(Circle circle) {
		//transparent
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.fillTransparentName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fillTransparentName)!=null) {
			fillTransparent = Boolean.parseBoolean(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(fillTransparentName));
		}
		//if explicitly set to transparent or by default(see {@link VfColorGraphicsPropertyNodeFactory#FILL_IS_TRANSPARENT})
		if(fillTransparent) {
			circle.setFill(Color.TRANSPARENT);
			return true;
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
			circle.setFill(c);
		}
        //////////
        return true;
	}
	
	/**
	 * the same with {@link VfArcFXNodeFactoryImpl}
	 * @param circle
	 * @return
	 */
	private boolean setBorderColor(Circle circle) {
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
			circle.setStroke(c);
		}
		
		///////////
        return true;
	}
	
	
	/**
	 * the same with {@link VfArcFXNodeFactoryImpl}
	 * @param line
	 * @return
	 */
	private boolean setStrokeWidth(Circle circle) {
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
			circle.setStrokeWidth(this.strokeWidth);
		
		
		return true;
	}
	
	/**
	 * the same with {@link VfArcFXNodeFactoryImpl}
	 * @param circle
	 * @return
	 */
	private boolean setStrokeType(Circle circle) {
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
			circle.setStrokeType(this.strokeType);
		
		
		return true;
	}
	
	/**
	 * the same with {@link VfArcFXNodeFactoryImpl#setStrokeDashType(Arc)}
	 * @param line
	 * @return
	 */
	private boolean setStrokeDashType(Circle circle) {
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
			circle.getStrokeDashArray().addAll(this.dashType.getDashArrayList());
		
		/////////
		return true;
	}
}
