package graphics.property.shape2D.factory;

import java.util.LinkedHashSet;
import java.util.Set;

import basic.SimpleName;
import basic.VfNotes;
import basic.attribute.VfAttributeImpl;
import graphics.property.node.GraphicsPropertyLeafNode;
import graphics.property.node.GraphicsPropertyNode;
import javafx.scene.shape.ArcType;
import rdb.sqltype.SQLStringType;

/**
 * Arc shape type specific
 * @author tanxu
 *
 */
public class VfArcClosureTypeGraphicsPropertyNodeFactory {
	private static Set<GraphicsPropertyNode> ROOT_GRAPHICS_PROPERTY_NODE_SET;
	/**
	 * return the set of root GraphicsPropertyNode defined in this factory class
	 * @return
	 */
	public static Set<GraphicsPropertyNode> getRootGraphicsPropertyNodeSet(){
		if(ROOT_GRAPHICS_PROPERTY_NODE_SET==null) {
			ROOT_GRAPHICS_PROPERTY_NODE_SET = new LinkedHashSet<>();
			ROOT_GRAPHICS_PROPERTY_NODE_SET.add(ARC_CLOSURE_TYPE);
		}
		return ROOT_GRAPHICS_PROPERTY_NODE_SET;
	}
	
	/**
	 * based on javafx {@link ArcType}
	 */
	public static final GraphicsPropertyLeafNode<ArcType> ARC_CLOSURE_TYPE = 
			new GraphicsPropertyLeafNode<ArcType>(
					new VfAttributeImpl<>(
							new SimpleName("ARCCLOSURETYPE"),//SimpleName name, 
							VfNotes.makeVisframeDefinedVfNotes(),//VfNotes notes,
							ArcType.class,//Class<T> valueType, 
							v->{return v.toString();},//toStringFunction
							s->{return ArcType.valueOf(s);},//fromStringFunction
							e->{return e!=null && (e.equals(ArcType.CHORD)||e.equals(ArcType.OPEN)||e.equals(ArcType.ROUND));}, //Predicate<T> nonNullValueConstraints,
							new SQLStringType(10,false),//???SQLDataType SQLDataType,
							ArcType.OPEN,//T defaultValue, 
							false//boolean canBeNull
					));
	
	
	
	
	
}
