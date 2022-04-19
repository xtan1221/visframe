package basic.reproduce;

import java.io.Serializable;

/**
 * simply and directly clone and return new object;
 * 
 * a generic reproducible type is {@link SimpleReproducible} only if all of its attributes to be reproduced are of {@link SimpleReproducible} type;
 * 
 * 
 * @author tanxu
 * 
 */
public interface SimpleReproducible extends Serializable{
	/**
	 * 
	 * @return
	 */
	SimpleReproducible reproduce();
}
