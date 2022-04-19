/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.property.shape2D.factory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import basic.SimpleName;
import exception.VisframeException;
import graphics.property.node.GraphicsPropertyNode;
import graphics.property.node.GraphicsPropertyNonLeafNode;

/**
 * lookup for all visframe defined {@link GraphicsPropertyNode};
 * 
 * test whether duplicate names exist for visframe defined VfGraphicsPropertyNodeBase
 * 
 * @author tanxu
 */
public final class VfDefinedGraphicsPropertyNodeLookup {
    /**
     * all the defined PropertyNode in this utility class
     */
    public static final Map<SimpleName, GraphicsPropertyNode> PROPERTY_NODE_NAME_MAP = new LinkedHashMap<>();
    
    /**
     * set of GraphicsPropertyNode in each factory class that belongs to a specific type of graphics nodes;
     */
    public static final Map<String, Set<GraphicsPropertyNode>> PROPERTY_TYPE_SET_MAP = new LinkedHashMap<>();
    
    
    
    static {
    	System.out.println("initialize");
    	try {
    		//VfBasicGraphicsPropertyNodeFactory
    		PROPERTY_TYPE_SET_MAP.put("BasicGraphicsProperty", new LinkedHashSet<>());
    		for(Field field:VfBasicGraphicsPropertyNodeFactory.class.getFields()){
//	            System.out.println(field.getName());
	            if(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())){
	                if(GraphicsPropertyNode.class.isAssignableFrom(field.getType())){
//	                    System.out.println("======================");
//	                    System.out.println(field.getType().getName());
//	                    System.out.println(field.getName());
                    	GraphicsPropertyNode propertyNode = (GraphicsPropertyNode)field.get(GraphicsPropertyNode.class);
                            if(PROPERTY_NODE_NAME_MAP.keySet().contains(propertyNode.getName())){
                            	throw new VisframeException("GraphicsPropertyNodes with same name are found:"+propertyNode.getName().getStringValue());
                            }else {
                            	PROPERTY_NODE_NAME_MAP.put(propertyNode.getName(), propertyNode);
                            	PROPERTY_TYPE_SET_MAP.get("BasicGraphicsProperty").add(propertyNode);
                            }
	                }
	            }
	        }
	        
	        //VfTransformGraphicsPropertyNodeFactory
	        PROPERTY_TYPE_SET_MAP.put("TransformGraphicsProperty", new LinkedHashSet<>());
	        for(Field field:VfTransformGraphicsPropertyNodeFactory.class.getFields()){
//	            System.out.println(field.getName());
	            if(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())){
	                if(GraphicsPropertyNode.class.isAssignableFrom(field.getType())){
//	                    System.out.println("======================");
//	                    System.out.println(field.getType().getName());
//	                    System.out.println(field.getName());
                    	GraphicsPropertyNode propertyNode = (GraphicsPropertyNode)field.get(GraphicsPropertyNode.class);
                            if(PROPERTY_NODE_NAME_MAP.keySet().contains(propertyNode.getName())){
                            	throw new VisframeException("GraphicsPropertyNodes with same name are found:"+propertyNode.getName().getStringValue());
                            }else {
                            	PROPERTY_NODE_NAME_MAP.put(propertyNode.getName(), propertyNode);
                            	PROPERTY_TYPE_SET_MAP.get("TransformGraphicsProperty").add(propertyNode);
                            }
	                }
	            }
	        }
    		
	        //VfColorGraphicsPropertyNodeFactory
    		PROPERTY_TYPE_SET_MAP.put("ColorGraphicsProperty", new LinkedHashSet<>());
	        for(Field field:VfColorGraphicsPropertyNodeFactory.class.getFields()){
//	            System.out.println(field.getName());
	            if(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())){
	                if(GraphicsPropertyNode.class.isAssignableFrom(field.getType())){
//	                    System.out.println("======================");
//	                    System.out.println(field.getType().getName());
//	                    System.out.println(field.getName());
                    	GraphicsPropertyNode propertyNode = (GraphicsPropertyNode)field.get(GraphicsPropertyNode.class);
                            if(PROPERTY_NODE_NAME_MAP.keySet().contains(propertyNode.getName())){
                            	throw new VisframeException("GraphicsPropertyNodes with same name are found:"+propertyNode.getName().getStringValue());
                            }else {
                            	PROPERTY_NODE_NAME_MAP.put(propertyNode.getName(), propertyNode);
                            	PROPERTY_TYPE_SET_MAP.get("ColorGraphicsProperty").add(propertyNode);
                            }
	                }
	            }
	        }
	        //VfStrokeGraphicsPropertyNodeFactory
	        PROPERTY_TYPE_SET_MAP.put("StrokeGraphicsProperty", new LinkedHashSet<>());
	        for(Field field:VfStrokeGraphicsPropertyNodeFactory.class.getFields()){
//	            System.out.println(field.getName());
	            if(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())){
	                if(GraphicsPropertyNode.class.isAssignableFrom(field.getType())){
//	                    System.out.println("======================");
//	                    System.out.println(field.getType().getName());
//	                    System.out.println(field.getName());
                    	GraphicsPropertyNode propertyNode = (GraphicsPropertyNode)field.get(GraphicsPropertyNode.class);
                            if(PROPERTY_NODE_NAME_MAP.keySet().contains(propertyNode.getName())){
                            	throw new VisframeException("GraphicsPropertyNodes with same name are found:"+propertyNode.getName().getStringValue());
                            }else {
                            	PROPERTY_NODE_NAME_MAP.put(propertyNode.getName(), propertyNode);
                            	PROPERTY_TYPE_SET_MAP.get("StrokeGraphicsProperty").add(propertyNode);
                            }
	                }
	            }
	        }
	        
            //VfArcClosureTypeGraphicsPropertyNodeFactory
	        PROPERTY_TYPE_SET_MAP.put("ArcClosureTypeGraphicsProperty", new LinkedHashSet<>());
	        for(Field field:VfArcClosureTypeGraphicsPropertyNodeFactory.class.getFields()){
//	            System.out.println(field.getName());
	            if(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())){
	                if(GraphicsPropertyNode.class.isAssignableFrom(field.getType())){
//	                    System.out.println("======================");
//	                    System.out.println(field.getType().getName());
//	                    System.out.println(field.getName());
                    	GraphicsPropertyNode propertyNode = (GraphicsPropertyNode)field.get(GraphicsPropertyNode.class);
                            if(PROPERTY_NODE_NAME_MAP.keySet().contains(propertyNode.getName())){
                            	throw new VisframeException("GraphicsPropertyNodes with same name are found:"+propertyNode.getName().getStringValue());
                            }else {
                            	PROPERTY_NODE_NAME_MAP.put(propertyNode.getName(), propertyNode);
                            	PROPERTY_TYPE_SET_MAP.get("ArcClosureTypeGraphicsProperty").add(propertyNode);
                            }
	                }
	            }
	        }
	        
            //VfArcClosureTypeGraphicsPropertyNodeFactory
	        PROPERTY_TYPE_SET_MAP.put("RectangleShapeTypeSpecificGraphicsPropertyNodeFactory", new LinkedHashSet<>());
	        for(Field field:VfRectangleShapeTypeSpecificGraphicsPropertyNodeFactory.class.getFields()){
//	            System.out.println(field.getName());
	            if(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())){
	                if(GraphicsPropertyNode.class.isAssignableFrom(field.getType())){
//	                    System.out.println("======================");
//	                    System.out.println(field.getType().getName());
//	                    System.out.println(field.getName());
                    	GraphicsPropertyNode propertyNode = (GraphicsPropertyNode)field.get(GraphicsPropertyNode.class);
                            if(PROPERTY_NODE_NAME_MAP.keySet().contains(propertyNode.getName())){
                            	throw new VisframeException("GraphicsPropertyNodes with same name are found:"+propertyNode.getName().getStringValue());
                            }else {
                            	PROPERTY_NODE_NAME_MAP.put(propertyNode.getName(), propertyNode);
                            	PROPERTY_TYPE_SET_MAP.get("RectangleShapeTypeSpecificGraphicsPropertyNodeFactory").add(propertyNode);
                            }
	                }
	            }
	        }

	        
	        //VfTextShapeTypeSpecificGraphicsPropertyNodeFactory
	        PROPERTY_TYPE_SET_MAP.put("TextShapeTypeSpecificGraphicsProperty", new LinkedHashSet<>());
	        for(Field field:VfTextShapeTypeSpecificGraphicsPropertyNodeFactory.class.getFields()){
//	            System.out.println(field.getName());
	            if(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())){
	                if(GraphicsPropertyNode.class.isAssignableFrom(field.getType())){
//	                    System.out.println("======================");
//	                    System.out.println(field.getType().getName());
//	                    System.out.println(field.getName());
                    	GraphicsPropertyNode propertyNode = (GraphicsPropertyNode)field.get(GraphicsPropertyNode.class);
                            if(PROPERTY_NODE_NAME_MAP.keySet().contains(propertyNode.getName())){
                            	throw new VisframeException("GraphicsPropertyNodes with same name are found:"+propertyNode.getName().getStringValue());
                            }else {
                            	PROPERTY_NODE_NAME_MAP.put(propertyNode.getName(), propertyNode);
                            	PROPERTY_TYPE_SET_MAP.get("TextShapeTypeSpecificGraphicsProperty").add(propertyNode);
                            }
	                }
	            }
	        }

        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(VfDefinedGraphicsPropertyNodeLookup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    private static Map<String, Set<GraphicsPropertyNode>> TYPE_ROOT_NODE_SET_MAP;
    /**
     * return the map from the graphics type to the set of GraphicsPropertyNode that are not used as child/descendant nodes of any other nodes of the same type (thus root node)
     * @return
     */
    public static Map<String, Set<GraphicsPropertyNode>> getTypeRootNodeSetMap(){
    	if(TYPE_ROOT_NODE_SET_MAP==null) {
    		TYPE_ROOT_NODE_SET_MAP = new LinkedHashMap<>();
    		
    		for(String type:PROPERTY_TYPE_SET_MAP.keySet()) {
    			TYPE_ROOT_NODE_SET_MAP.put(type, new LinkedHashSet<>());
    			
    			//find out nodes that are descendant node of another node of the same type
    			Set<SimpleName> nodeNameSetAsDescendantNodeOfAnotherNode = new HashSet<>();
    			for(GraphicsPropertyNode node: PROPERTY_TYPE_SET_MAP.get(type)) {
    				if(node instanceof GraphicsPropertyNonLeafNode) {
    					GraphicsPropertyNonLeafNode nonLeafNode = (GraphicsPropertyNonLeafNode)node;
    					
    					//getDescendantNodeFullPathNameOnTreeMap will include the node itself
    					nonLeafNode.getDescendantNodeFullPathNameOnTreeMap("").values().forEach(e->{
    						if(!e.getName().equals(node.getName()))
    							nodeNameSetAsDescendantNodeOfAnotherNode.add(e.getName());
    					});
    				}
    			}
    			//find out nodes that are not descendant node of any other node
    			for(GraphicsPropertyNode node: PROPERTY_TYPE_SET_MAP.get(type)) {
    				if(!nodeNameSetAsDescendantNodeOfAnotherNode.contains(node.getName())) {
    					TYPE_ROOT_NODE_SET_MAP.get(type).add(node);
    				}
    			}
    			
    		}
    		
    	}
    	
    	
    	return TYPE_ROOT_NODE_SET_MAP;
    }
    
    
    
    
    
    
    
    
    //////////////////////////
    @SuppressWarnings("unused")
	private static void print(){
        for(SimpleName name:PROPERTY_NODE_NAME_MAP.keySet()){
            System.out.println(name+"=================="+PROPERTY_NODE_NAME_MAP.get(name));
        }
    }
    
    @SuppressWarnings("unused")
	private static void print3(){
        for(String type:PROPERTY_TYPE_SET_MAP.keySet()){
        	System.out.println("type:"+type);
        	for(GraphicsPropertyNode node:PROPERTY_TYPE_SET_MAP.get(type)){
                System.out.println("\t"+node.getName().getStringValue()+"=================="+node);
            }
        }
    }
    
    private static void print2(){
        for(String type:getTypeRootNodeSetMap().keySet()){
        	System.out.println("type:"+type);
        	for(GraphicsPropertyNode node:getTypeRootNodeSetMap().get(type)){
                System.out.println("\t"+node.getName().getStringValue()+"=================="+node);
            }
        }
    }
    
    public static void main(String[] args){
//        System.out.println(PropertyNodeTypeMap.PROPERTY_NODE_FULL_NAME_MAP.size());
//        print();
        
        print2();
        
//        print3();
    }
}
