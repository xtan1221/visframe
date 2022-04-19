package operation.graph.layout.utils;

import java.util.function.Function;

import org.jgrapht.alg.drawing.model.Point2D;

/**
 * utility class for visframe defined features for JGraphT 2D layout algorithms
 * 
 * @author tanxu
 *
 */
public class JGraphT2DLayoutInitialLayoutFunctionUtils {
	
	/**
	 * vertex initializer for {@link LayoutModel2D}
	 * @param <V>
	 * @param vertexType
	 * @return
	 */
	public static <V> Function<V,Point2D> origin(Class<V> vertexType){
		return v->{return new Point2D(0,0);};
	}
	
	/**
	 * vertex initializer for {@link LayoutModel2D}
	 * @param <V>
	 * @param vertexType
	 * @return
	 */
	public static <V> Function<V,Point2D> random(Class<V> vertexType, double width, double height){
		return v->{return new Point2D(0,0);};
	}
	
	/**
	 * vertex initializer for {@link LayoutModel2D}
	 * @param <V>
	 * @param vertexType
	 * @return
	 */
	public static <V> Function<V,Point2D> center(Class<V> vertexType, double width, double height){
		return v->{return new Point2D(0,0);};
	}
}
