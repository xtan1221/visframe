package operation.graph.layout.JUNG.other;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import operation.graph.layout.JUNG.SpringLayout2DOperation;

/**
 * delegate to {@link DAGLayout} in JUNG api
 * see http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/algorithms/layout/DAGLayout.html;
 * 
 * note that this layout is a sub-type of SpringLayout, thus all the parameters for SpringLayout should be included for this layout type;
 * 	see {@link SpringLayout2DOperation};
 * 
 * =====================================
 * main parameters for the class are:
 * 1. input Graph of DAG type
 *  	
 * 2. dimension of the available space for layout of type Dimension; (OPTIONAL????)
 * 
 * 3. 
 * =========================================
 * calculated layout are cartesian coordinates;
 * 
 * =========================================
 * OperationInputGraphTypeBoundary should be ?
 * 
 * =========================================
 * method to retrieve the calculated layout:
 * {@link AbstractLayout#getX(V)} //return the calculated x coordinate for vertex v
 * {@link AbstractLayout#getY(V)} //return the calculated y coordinate for vertex v
 * 
 * @author tanxu
 *
 */
class DAGLayout2DOperation {




}
