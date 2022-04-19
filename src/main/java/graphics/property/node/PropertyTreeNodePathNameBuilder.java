/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.property.node;

import basic.SimpleName;
import graphics.property.VfGraphicsPropertyUtils;

/**
 * build a path name of a property node on a property tree;
 * 
 * the built path name starts with the tree name;
 * 
 * this is used by value table column names of graphics instances of shape type;
 * 
 * @author tanxu
 */
public class PropertyTreeNodePathNameBuilder {
    
    /**
     * 
     */
    private final SimpleName currentPathName;
    
    /**
     * constructor to make a new builder
     * @param startingPropertyNode 
     */
    private PropertyTreeNodePathNameBuilder(SimpleName treeName){
        this.currentPathName = treeName;
    }
    
    
    /**
     * start a new path name builder
     * @param treeName
     * @return 
     */
    public static PropertyTreeNodePathNameBuilder start(SimpleName treeName){
        return new PropertyTreeNodePathNameBuilder(treeName);
    }
    
    
    /**
     * build a new PropertyNodePathNameBuilder by adding the current path name with the given property node;
     * @param pn
     * @return 
     */
    public PropertyTreeNodePathNameBuilder addNext(GraphicsPropertyNode pn){
        return new PropertyTreeNodePathNameBuilder(new SimpleName(this.currentPathName.getStringValue().concat(VfGraphicsPropertyUtils.GRAPHICS_PROPERTY_FULL_PATH_NAME_CONNECTOR).concat(pn.getName().getStringValue())));
    }
    
    
    /**
     * build a path name of the property node on the tree;
     * @return 
     */
    public SimpleName build(){
        return this.currentPathName;
    }
}
