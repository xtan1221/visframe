/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.shape.shape2D.fx;

import static graphics.property.shape2D.factory.VfBasicGraphicsPropertyNodeFactory.*;
import static graphics.property.shape2D.factory.VfTransformGraphicsPropertyNodeFactory.*;
import static graphics.property.tree.VfTransformPropertyTreeFactory.*;

import java.util.Map;

import basic.SimpleName;
import graphics.property.node.PropertyTreeNodePathNameBuilder;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * factory utility class that create and add {@link Transform} features to a given javafx {@link Node} object with the provided transform property values;
 * 
 * all the attribute name on the property tree are created only once and used for a batch of javafx node construction;
 * 
 * @author tanxu
 */
public class FXTransformFactory {
	/**
     * return the singleton instance of {@link FXTransformFactory}
     * @return 
     */
    public static FXTransformFactory singletonInstance(){
        if(SINGLETON==null){
            SINGLETON = new FXTransformFactory();
        }
        return SINGLETON;
    }
    
    /**
     * singleton instance
     */
    private static FXTransformFactory SINGLETON;
    
    ////////////////////////////
    //
    private Double translateX;
    private Double translateY;
    private final SimpleName translateXName;
    private final SimpleName translateYName;
    
    //
    private Double angle;
    private Double rotatePivotX;
    private Double rotatePivotY;
    private final SimpleName angleName;
    private final SimpleName rotatePivotXName;
    private final SimpleName rotatePivotYName;
    
    private Double scaleX;
    private Double scaleY;
    private Double scalePivotX;
    private Double scalePivotY;
    private final SimpleName scaleXName;
    private final SimpleName scaleYName;
    private final SimpleName scalePivotXName;
    private final SimpleName scalePivotYName;
    
    private Double shearX;
    private Double shearY;
    private Double shearPivotX;
    private Double shearPivotY;
    private final SimpleName shearXName;
    private final SimpleName shearYName;
    private final SimpleName shearPivotXName;
    private final SimpleName shearPivotYName;
    
    
    /**
     * constructor
     */
    private FXTransformFactory(){
        //translate
        translateXName = PropertyTreeNodePathNameBuilder.start(TRANSLATE_TREE.getName()).addNext(TRANSLATE).addNext(TRANSFORM_X).addNext(TRANSFORM_COORD).build();
        translateYName = PropertyTreeNodePathNameBuilder.start(TRANSLATE_TREE.getName()).addNext(TRANSLATE).addNext(TRANSFORM_Y).addNext(TRANSFORM_COORD).build();
        
        //rotate
        angleName = PropertyTreeNodePathNameBuilder.start(ROTATE_TREE.getName()).addNext(ROTATE).addNext(THETA_IN_DEGREE).build();
        rotatePivotXName = PropertyTreeNodePathNameBuilder.start(ROTATE_TREE.getName()).addNext(ROTATE).addNext(TRANSFORM_CART_2D).addNext(TRANSFORM_X).addNext(TRANSFORM_COORD).build();
        rotatePivotYName = PropertyTreeNodePathNameBuilder.start(ROTATE_TREE.getName()).addNext(ROTATE).addNext(TRANSFORM_CART_2D).addNext(TRANSFORM_Y).addNext(TRANSFORM_COORD).build();
        
        //scale
        scaleXName = PropertyTreeNodePathNameBuilder.start(SCALE_TREE.getName()).addNext(SCALE).addNext(SCALEXY).addNext(SCALEX).addNext(RATIO).build();
        scaleYName = PropertyTreeNodePathNameBuilder.start(SCALE_TREE.getName()).addNext(SCALE).addNext(SCALEXY).addNext(SCALEY).addNext(RATIO).build();
        scalePivotXName = PropertyTreeNodePathNameBuilder.start(SCALE_TREE.getName()).addNext(SCALE).addNext(TRANSFORM_CART_2D).addNext(TRANSFORM_X).addNext(TRANSFORM_COORD).build();
        scalePivotYName = PropertyTreeNodePathNameBuilder.start(SCALE_TREE.getName()).addNext(SCALE).addNext(TRANSFORM_CART_2D).addNext(TRANSFORM_Y).addNext(TRANSFORM_COORD).build();
        
        //shear
        shearXName = PropertyTreeNodePathNameBuilder.start(SHEAR_TREE.getName()).addNext(SHEAR).addNext(SHEARXY).addNext(SHEARX).addNext(MULTIPLIER).build();
        shearYName = PropertyTreeNodePathNameBuilder.start(SHEAR_TREE.getName()).addNext(SHEAR).addNext(SHEARXY).addNext(SHEARY).addNext(MULTIPLIER).build();
        shearPivotXName = PropertyTreeNodePathNameBuilder.start(SHEAR_TREE.getName()).addNext(SHEAR).addNext(TRANSFORM_CART_2D).addNext(TRANSFORM_X).addNext(TRANSFORM_COORD).build();
        shearPivotYName = PropertyTreeNodePathNameBuilder.start(SHEAR_TREE.getName()).addNext(SHEAR).addNext(TRANSFORM_CART_2D).addNext(TRANSFORM_Y).addNext(TRANSFORM_COORD).build();
        
    }
    
    /**
     * 
     * @param node
     * @param leafPropertyNameStringValueMap
     */
    public boolean setTransform(Node node, Map<SimpleName,String> leafPropertyNameStringValueMap) {
    	this.initialize();
    	
    	if(!this.setTranslate(node, leafPropertyNameStringValueMap))
    		return false;
    	
    	if(!this.setRotate(node, leafPropertyNameStringValueMap))
    		return false;
    	
    	if(!this.setScale(node, leafPropertyNameStringValueMap)) 
    		return false;
    	
    	if(!this.setShear(node, leafPropertyNameStringValueMap))
    		return false;
    	
    	return true;
    }

    /**
     * initialize the fields so that transform can be set for a new {@link Shape} instance;
     */
    private void initialize() {
    	//translate
    	this.translateX = TRANSFORM_COORD.getDefaultValue();
    	this.translateY = TRANSFORM_COORD.getDefaultValue();
    	
    	//rotate
    	this.angle = THETA_IN_DEGREE.getDefaultValue();
    	this.rotatePivotX = TRANSFORM_COORD.getDefaultValue();
    	this.rotatePivotY = TRANSFORM_COORD.getDefaultValue();
    	
    	//scale
    	this.scaleX = RATIO.getDefaultValue();
    	this.scaleY = RATIO.getDefaultValue();
    	this.scalePivotX = TRANSFORM_COORD.getDefaultValue();
    	this.scalePivotY = TRANSFORM_COORD.getDefaultValue();
    	
    	//shear
    	this.shearX = MULTIPLIER.getDefaultValue();
    	this.shearY = MULTIPLIER.getDefaultValue();
    	this.shearPivotX = TRANSFORM_COORD.getDefaultValue();
    	this.shearPivotY = TRANSFORM_COORD.getDefaultValue();
    }
    
    
    
    /**
     * set the translate related leaf properties of the given node with the given leafPropertyFullPathNameOnTreeStringValueMap;
     * 
     * note that it is possible some of the translate related leaf property are not in the map or have null value;
     * 
     * @param node
     * @param leafPropertyNameStringValueMap map from full path name on property tree to string value (may be null)
     */
    private boolean setTranslate(Node node, Map<SimpleName,String> leafPropertyNameStringValueMap) {
    	/////////////translate X
    	if(leafPropertyNameStringValueMap.containsKey(translateXName)&&leafPropertyNameStringValueMap.get(translateXName)!=null){
    		try {
    			translateX = Double.parseDouble(leafPropertyNameStringValueMap.get(translateXName));
    		}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
    	//if the property value is invalid, the shape instance cannot be created
		if(!TRANSFORM_COORD.isValidValue(this.translateX))
			return false;
		
    	
		///////////////translate Y
    	if(leafPropertyNameStringValueMap.containsKey(translateYName)&&leafPropertyNameStringValueMap.get(translateYName)!=null){
    		try {
    			translateY = Double.parseDouble(leafPropertyNameStringValueMap.get(translateYName));
    		}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
    	//if the property value is invalid, the shape instance cannot be created
		if(!TRANSFORM_COORD.isValidValue(this.translateY))
			return false;
		
		
		///////////////
		if(this.translateY!=null && this.translateX!=null)
			node.getTransforms().add(new Translate(translateX, translateY));
    	
		///////////////////
    	return true;
    }
    
    /**
     * set the rotate related leaf properties of the given node with the given leafPropertyFullPathNameOnTreeStringValueMap;
     * 
     * note that it is possible some of the rotate related leaf property are not in the map or have null value;
     * 
     * @param node
     * @param leafPropertyNameStringValueMap map from full path name on property tree to string value (may be null)
     */
    private boolean setRotate(Node node, Map<SimpleName,String> leafPropertyNameStringValueMap) {
    	/////////////angle
    	if (leafPropertyNameStringValueMap.containsKey(angleName)&&leafPropertyNameStringValueMap.get(angleName)!=null) {
    		try {
    			this.angle = Double.parseDouble(leafPropertyNameStringValueMap.get(angleName));
    		}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
    	if(!THETA_IN_DEGREE.isValidValue(this.angle))
    		return false;
    	
    	/////////////pivot X
    	if (leafPropertyNameStringValueMap.containsKey(rotatePivotXName)&&leafPropertyNameStringValueMap.get(rotatePivotXName)!=null) {
    		try {
    			rotatePivotX = Double.parseDouble(leafPropertyNameStringValueMap.get(rotatePivotXName));
    		}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
    	if(!TRANSFORM_COORD.isValidValue(rotatePivotX))
    		return false;
    	
    	/////////////pivot Y
        if (leafPropertyNameStringValueMap.containsKey(rotatePivotYName)&&leafPropertyNameStringValueMap.get(rotatePivotYName)!=null) {
        	try {
        		rotatePivotY = Double.parseDouble(leafPropertyNameStringValueMap.get(rotatePivotYName));
        	}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
        if(!TRANSFORM_COORD.isValidValue(rotatePivotY))
    		return false;
        
        
        ///////////////
        if(this.angle!=null && this.rotatePivotX!=null && this.rotatePivotY!=null) {
	        Rotate rotate = new Rotate(angle, rotatePivotX, rotatePivotY);
	        node.getTransforms().add(rotate);
        }
        
        
        return true;
    }
    
    /**
     * set the scale related leaf properties of the given node with the given leafPropertyFullPathNameOnTreeStringValueMap;
     * 
     * note that it is possible some of the scale related leaf property are not in the map or have null value;
     * @param node
     * @param leafPropertyNameStringValueMap map from full path name on property tree to string value (may be null)
     */
    private boolean setScale(Node node, Map<SimpleName,String> leafPropertyNameStringValueMap) {
    	//scaleX
    	if (leafPropertyNameStringValueMap.containsKey(scaleXName)&&leafPropertyNameStringValueMap.get(scaleXName)!=null) {
    		try {
    			scaleX = Double.parseDouble(leafPropertyNameStringValueMap.get(scaleXName));
    		}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
    	if(!RATIO.isValidValue(scaleX))
    		return false;
    	
    	
    	//scaleY
        if (leafPropertyNameStringValueMap.containsKey(scaleYName)&&leafPropertyNameStringValueMap.get(scaleYName)!=null) {
        	try {
        		scaleY = Double.parseDouble(leafPropertyNameStringValueMap.get(scaleYName));
        	}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
        if(!RATIO.isValidValue(scaleY))
    		return false;
        
        
        //pivotX
        if (leafPropertyNameStringValueMap.containsKey(scalePivotXName)&&leafPropertyNameStringValueMap.get(scalePivotXName)!=null) {
        	try {
        		scalePivotX = Double.parseDouble(leafPropertyNameStringValueMap.get(scalePivotXName));
        	}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
        if(!TRANSFORM_COORD.isValidValue(scalePivotX))
    		return false;
        
        //pivotY
        if (leafPropertyNameStringValueMap.containsKey(scalePivotYName)&&leafPropertyNameStringValueMap.get(scalePivotYName)!=null) {
        	try {
        		scalePivotY = Double.parseDouble(leafPropertyNameStringValueMap.get(scalePivotYName));
        	}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
        if(!TRANSFORM_COORD.isValidValue(scalePivotY))
    		return false;
        
        
        /////////
        if(this.scaleX!=null && this.scaleY!=null && this.scalePivotX!=null && this.scalePivotY!=null) {
	        Scale scale = new Scale(scaleX, scaleY, scalePivotX, scalePivotY);
	        node.getTransforms().add(scale);
        }
        
        return true;
    }
    
    /**
     * set the shear related leaf properties of the given node with the given leafPropertyFullPathNameOnTreeStringValueMap;
     * 
     * note that it is possible some of the shear related leaf property are not in the map or have null value;
     * @param node
     * @param leafPropertyNameStringValueMap
     */
    private boolean setShear(Node node, Map<SimpleName,String> leafPropertyNameStringValueMap) {
    	//shearX
    	if (leafPropertyNameStringValueMap.containsKey(shearXName)&&leafPropertyNameStringValueMap.get(shearXName)!=null) {
    		try {
    			shearX = Double.parseDouble(leafPropertyNameStringValueMap.get(scaleXName));
    		}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
    	if(!MULTIPLIER.isValidValue(shearX))
    		return false;
    	
    	//shearY
        if (leafPropertyNameStringValueMap.containsKey(shearYName)&&leafPropertyNameStringValueMap.get(shearYName)!=null) {
        	try {
        		shearY = Double.parseDouble(leafPropertyNameStringValueMap.get(scaleYName));
        	}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
        if(!MULTIPLIER.isValidValue(shearY))
    		return false;
        
        //pivotX
        if (leafPropertyNameStringValueMap.containsKey(shearPivotXName)&&leafPropertyNameStringValueMap.get(shearPivotXName)!=null) {
        	try {
        		shearPivotX = Double.parseDouble(leafPropertyNameStringValueMap.get(shearPivotXName));
        	}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
        if(!TRANSFORM_COORD.isValidValue(shearPivotX))
    		return false;
        
        //pivotY
        if (leafPropertyNameStringValueMap.containsKey(shearPivotYName)&&leafPropertyNameStringValueMap.get(shearPivotYName)!=null) {
        	try {
        		shearPivotY = Double.parseDouble(leafPropertyNameStringValueMap.get(shearPivotYName));
        	}catch(NumberFormatException e) {
				return false;//the string value is invalid
			}
        }
        if(!TRANSFORM_COORD.isValidValue(shearPivotY))
    		return false;
        
        
        //////////////
        if(this.shearX!=null && this.shearY!=null && this.shearPivotX!=null && this.shearPivotY!=null) {
	        Shear shear = new Shear(shearX, shearY, shearPivotX, shearPivotY);
	        node.getTransforms().add(shear);
        }
        
        
        return true;
    }
}
