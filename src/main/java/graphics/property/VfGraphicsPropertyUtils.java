package graphics.property;

import basic.SimpleName;

public class VfGraphicsPropertyUtils {
	public static String GRAPHICS_PROPERTY_FULL_PATH_NAME_CONNECTOR = "_";
	
	
	public static SimpleName buildFullPathNameOnTree(String parentNodeFullPathNameOnTree, SimpleName nodeName) {
		if(parentNodeFullPathNameOnTree.isEmpty()) {
			return nodeName;
		}else {
			return new SimpleName(parentNodeFullPathNameOnTree.concat(GRAPHICS_PROPERTY_FULL_PATH_NAME_CONNECTOR).concat(nodeName.getStringValue()));
		}
		
	}
}
