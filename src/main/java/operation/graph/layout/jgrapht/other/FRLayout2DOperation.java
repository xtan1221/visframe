package operation.graph.layout.jgrapht.other;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;

import basic.SimpleName;
import basic.VfNotes;
import context.project.VisProjectDBContext;
import context.scheme.appliedarchive.reproducedandinsertedinstance.VisSchemeAppliedArchiveReproducerAndInserter;
import metadata.MetadataID;
import metadata.graph.type.OperationInputGraphTypeBoundary;
import operation.graph.SingleGenericGraphAsInputOperation;
import operation.graph.layout.GraphNode2DLayoutOperationBase;
import rdb.table.data.DataTableColumnName;

/**
 * 
 * delegate for {@link FRLayoutAlgorithm2D}
 * see https://jgrapht.org/javadoc/org.jgrapht.core/org/jgrapht/alg/drawing/FRLayoutAlgorithm2D.html
 * 
 * 
 * parameter for FRLayoutAlgorithm2D
 * 
 * 1. input graph Graph<V,E>
 * 
 * 2. LayoutModel2D<V>
 * 
 * 3. iteration of int type (optional, has default value)
 * 
 * 4. normalizationFactor of double type (optional, has default value)
 * 
 * 5. temperatureModelSupplier of type BiFunction<LayoutModel2D<V>, Integer, FRLayoutAlgorithm2D.TemperatureModel>
 * 		 a simulated annealing temperature model supplier
 * 
 * 6. random number generator of type Random
 * 
 * 
 * 
 * @author tanxu
 *
 */
abstract class FRLayout2DOperation extends GraphNode2DLayoutOperationBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3910657235001370596L;
	
	/**
	 * constructor
	 * @param operationLevelParameterObjectValueMap
	 * @param singleGenericGraphAsInputOperationLevelParameterObjectValueMap
	 * @param inputGraphTypeBoundedOperationLevelParameterObjectValueMap
	 * @param graphNode2DLayoutBaseLevelParameterObjectValueMap
	 */
	protected FRLayout2DOperation(
//			boolean resultedFromReproducing,
			Map<SimpleName, Object> operationLevelParameterObjectValueMap,
			Map<SimpleName, Object> singleGenericGraphAsInputOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> inputGraphTypeBoundedOperationLevelParameterObjectValueMap,
			Map<SimpleName, Object> graphNode2DLayoutBaseLevelParameterObjectValueMap,
			boolean toCheckConstraintsRelatedWithParameterDependentOnInputDataTableContent) {
		super(operationLevelParameterObjectValueMap, singleGenericGraphAsInputOperationLevelParameterObjectValueMap,
				inputGraphTypeBoundedOperationLevelParameterObjectValueMap, graphNode2DLayoutBaseLevelParameterObjectValueMap);
		// TODO Auto-generated constructor stub
	}

	
	////////////////////////////////////////////////
	@Override
	public VfNotes getOperationTypeNotes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<MetadataID, Set<DataTableColumnName>> getInputRecordMetadataIDInputColumnNameSetMap() {
		// TODO Auto-generated method stub
		return null;
	}

	////////////////////////////////////////////////
	@Override
	public SingleGenericGraphAsInputOperation reproduce(
			VisProjectDBContext hostVisProjctDBContext,
			VisSchemeAppliedArchiveReproducerAndInserter VSAArchiveReproducerAndInserter,
			int copyIndex)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	///////////////////////////////////////////////
	@Override
	public OperationInputGraphTypeBoundary getOperationInputGraphTypeBoundary() {
		// TODO Auto-generated method stub
		return null;
	}
	
	////call() method related;
	@Override
	protected void buildLayoutAlgoPerformer() {
		//TODO
	}

}
