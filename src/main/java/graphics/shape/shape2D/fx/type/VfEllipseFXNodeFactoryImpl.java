package graphics.shape.shape2D.fx.type;

import java.util.Map;
import basic.SimpleName;
import graphics.property.node.PropertyTreeNodePathNameBuilder;
import graphics.shape.shape2D.fx.VfShapeTypeFXNodeFactoryBase;
import graphics.shape.shape2D.fx.utils.VfStrokeDashType;
import graphics.shape.shape2D.type.VfEllipse;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.StrokeType;

import static graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfColorGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfStrokeGraphicsPropertyNodeFactory.*;
import static graphics.shape.shape2D.type.VfEllipse.*;
/**
 * 
 * @author tanxu
 *
 */
public class VfEllipseFXNodeFactoryImpl extends VfShapeTypeFXNodeFactoryBase<Ellipse, VfEllipse> {
	static VfEllipseFXNodeFactoryImpl SINGLETON;
	
	/**
	 * 
	 * @return
	 */
	public static VfEllipseFXNodeFactoryImpl getSingleton() {
		if(SINGLETON==null) {
			SINGLETON=new VfEllipseFXNodeFactoryImpl();
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
    private Double radiusX;
    private Double radiusY;
    private final SimpleName radiusXName;
    private final SimpleName radiusYName;
    
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
	private VfEllipseFXNodeFactoryImpl() {
		//initialize
		centerXName = PropertyTreeNodePathNameBuilder.start(CENTER_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_X).addNext(LAYOUT_COORD).build();
		centerYName = PropertyTreeNodePathNameBuilder.start(CENTER_PROPERTY_TREE.getName()).addNext(LAYOUT_CART_2D).addNext(LAYOUT_Y).addNext(LAYOUT_COORD).build();

		radiusXName = PropertyTreeNodePathNameBuilder.start(RADIUS_X_PROPERTY_TREE.getName()).addNext(LENDIST).build();
		radiusYName = PropertyTreeNodePathNameBuilder.start(RADIUS_Y_PROPERTY_TREE.getName()).addNext(LENDIST).build();
		
		transparentName = PropertyTreeNodePathNameBuilder.start(FILLCOLOR_PROPERTY_TREE.getName()).addNext(FILL_COLOR).addNext(FILL_IS_TRANSPARENT).build();
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
	public VfEllipse getVfShapeType() {
		return VfEllipse.SINGLETON;
	}
	
	////////////////////////////////////////////////////
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Ellipse makeFXNode(Map<SimpleName, String> leafGraphicsPropertyFullPathNameOnTreeStringValueMap) {
		this.initialize();
		//
		this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.putAll(leafGraphicsPropertyFullPathNameOnTreeStringValueMap);
		
		//
		Ellipse ellipse = new Ellipse();
		//
		boolean typeSpecificPropertySuccessfullySet = this.setTypeSpecificTreeProperty(ellipse);
		
		if(typeSpecificPropertySuccessfullySet) {
			boolean transformSuccessfullySet = this.setTransform(ellipse);
			if(!transformSuccessfullySet)
				ellipse = null;
		}else {
			ellipse = null;
		}
		
		
		return ellipse;
	}

	@Override
	public void initialize() {
		super.initialize();
		
		//
		centerX=LAYOUT_COORD.getDefaultValue();
	    centerY=LAYOUT_COORD.getDefaultValue();

	    //RADIUS
	    radiusX=LENDIST.getDefaultValue();
	    radiusY=LENDIST.getDefaultValue();
	    
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
	public boolean setTypeSpecificTreeProperty(Ellipse ellipse) {
		//center coordinate
		if(!this.setCenter(ellipse))
			return false;
			
        //radius coordinate
		if(!this.setRadius(ellipse))
			return false;
        
		//fill color
		if(!this.setFillColor(ellipse))
			return false;
		
		if(!this.setBorderColor(ellipse))
			return false;
		
		//stroke
		if(!this.setStrokeWidth(ellipse))
			return false;
		
		if(!this.setStrokeType(ellipse))
			return false;
		
		if(!this.setStrokeDashType(ellipse))
			return false;
		
		
		return true;
	}
	
	/**
	 * set the start x and y;
	 * 
	 * @param ellipse
	 * @return
	 */
	private boolean setCenter(Ellipse ellipse) {
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
			ellipse.setCenterX(this.centerX);
		
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
			ellipse.setCenterY(this.centerY);
		
		
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
	 * @param ellipse
	 * @return
	 */
	private boolean setRadius(Ellipse ellipse) {
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
			ellipse.setRadiusX(this.radiusX);
		
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
			ellipse.setRadiusY(this.radiusY);
		
		
		///////////////////////
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
	private boolean setFillColor(Ellipse ellipse) {
		//transparent
		if(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.containsKey(this.transparentName)
				&&this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(transparentName)!=null) {
			transparent = Boolean.parseBoolean(this.leafGraphicsPropertyFullPathNameOnTreeStringValueMap.get(transparentName));
			if(transparent) {
				ellipse.setFill(Color.TRANSPARENT);
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
			ellipse.setFill(c);
		}
        //////////
        return true;
	}
	
	/**
	 * 
	 * @param ellipse
	 * @return
	 */
	private boolean setBorderColor(Ellipse ellipse) {
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
			ellipse.setStroke(c);
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
	private boolean setStrokeWidth(Ellipse ellipse) {
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
			ellipse.setStrokeWidth(this.strokeWidth);
		
		
		return true;
	}
	
	/**
	 * 
	 * @param ellipse
	 * @return
	 */
	private boolean setStrokeType(Ellipse ellipse) {
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
			ellipse.setStrokeType(this.strokeType);
		
		
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
	private boolean setStrokeDashType(Ellipse ellipse) {
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
			ellipse.getStrokeDashArray().addAll(this.dashType.getDashArrayList());
		
		/////////
		return true;
	}
}
