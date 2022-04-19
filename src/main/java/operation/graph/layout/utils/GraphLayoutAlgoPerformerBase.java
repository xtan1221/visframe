package operation.graph.layout.utils;

import generic.graph.VfGraphVertex;
import utils.Pair;

/**
 * 
 * @author tanxu
 *
 */
public abstract class GraphLayoutAlgoPerformerBase<V> {
	private final int drawingAreaHeight;
	private final int drawingAreaWidth;
	
	/**
	 * Constructor
	 * @param hostVisProjectDBContext
	 * @param inputGraphMetadataID
	 */
	GraphLayoutAlgoPerformerBase(int drawingAreaHeight, int drawingAreaWidth){
		
		if(drawingAreaHeight<=0||drawingAreaWidth<=0) {
			throw new IllegalArgumentException("drawingAreaHeight and drawingAreaWidth cannot be negative value!");
		}
		this.drawingAreaHeight = drawingAreaHeight;
		this.drawingAreaWidth = drawingAreaWidth;
	}
	
	/**
	 * @return the height
	 */
	public int getDrawingAreaHeight() {
		return drawingAreaHeight;
	}

	/**
	 * @return the width
	 */
	public int getDrawingAreaWidth() {
		return drawingAreaWidth;
	}
	
	//////////////////////////////
	/**
	 * return true if the calculated coordinate are based on Cartesian system;
	 * false if the calculated coordinate are based on polar system;
	 * 
	 * this is based on the underlying applied algorithm;
	 * 
	 * @return
	 */
	public abstract boolean isCartesianCoordSystem();
	
	
	/**
	 * initialize the algorithm
	 */
	public abstract void initialize();
	
	
	/**
	 * return the calculated coordinate for next VfGraphVertex;
	 * return null if there is no more vertex;
	 * @return
	 */
	public abstract Pair<VfGraphVertex, Coord2D> nextVertexCoord();
	
	
	///////////////////////////////////////
	/**
	 * contains a coordinate for a 2D system;
	 * can be either x and y or radius and theta;
	 * 
	 * @author tanxu
	 *
	 */
	public static class Coord2D{
		/**
		 * x in cartesian system; radius in polar system;
		 */
		private final double coord1;
		/**
		 * y in cartesian system; theta in polar system;
		 */
		private final double coord2;
		/**
		 * constructor
		 * @param coord1
		 * @param coord2
		 */
		Coord2D(double coord1, double coord2){
			this.coord1 = coord1;
			this.coord2 = coord2;
		}
		

		public double getCoord1() {
			return coord1;
		}

		public double getCoord2() {
			return coord2;
		}
		
		
		@Override
		public String toString() {
			return "Coord2D [coord1=" + coord1 + ", coord2=" + coord2 + "]";
		}
	}
}
