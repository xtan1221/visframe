package generic.graph;

import basic.reproduce.SimpleReproducible;

/**
 * 
 * @author tanxu
 *
 */
public enum DirectedType implements SimpleReproducible{
	DIRECTED_FORWARD,
	DIRECTED_BACKWARD,
	UNDIRECTED,
	BI_DIRECTED;

	@Override
	public SimpleReproducible reproduce() {
		return this;
	}
}
