/**
 * 
 */
package operation.graph.transform;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import context.project.VisProjectDBContextTest;
import context.project.process.logtable.StatusType;
import metadata.MetadataName;
import metadata.graph.type.GraphTypeEnforcer;
import metadata.graph.type.GraphTypeEnforcer.DirectedEnforcingMode;
import operation.AbstractOperationTest;
import operation.graph.SingleGenericGraphAsInputOperationTest;
import operation.graph.build.BuildGraphFromExistingRecordOperationBaseTest;

/**
 * @author tanxu
 *
 */
public class TransformGraphOperationTest {
	public static Map<SimpleName, Object> transformGraphOperationLevelSpecificParameterNameValueObjectMap;
	public static TransformGraphOperation transformGraphOperation;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	
	

	/**
	 * transform a vftree to a generic graph 
	 * Test method for {@link operation.graph.transform.TransformGraphOperation#buildTransformGraphOperationLevelSpecificParameterNameValueObjectMap(metadata.graph.type.GraphTypeEnforcer, metadata.MetadataName)}.
	 */
	@Test
	void testBuildTransformGraphOperationLevelSpecificParameterNameValueObjectMap() {
		GraphTypeEnforcer graphTypeEnforcer = new GraphTypeEnforcer(
				true,// toForceDirected,
				DirectedEnforcingMode.BI_DIRECTION,// directedForcingMode,
				false,//boolean toForceUndirected,
				false,//boolean toForceNoParallelEdges,
				false// toForceNoSelfLoops
				);
		MetadataName outputGraphDataName = new MetadataName("graph_from_mono_6813_bi_DIRECTION");
		
		transformGraphOperationLevelSpecificParameterNameValueObjectMap = 
				TransformGraphOperation.buildTransformGraphOperationLevelSpecificParameterNameValueObjectMap(graphTypeEnforcer, outputGraphDataName);
		
	}

	
	/**
	 * Test method for {@link operation.graph.transform.TransformGraphOperation#TransformGraphOperation(java.util.Map, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testTransformGraphOperation() {
		//
		AbstractOperationTest abstractOperationTest = new AbstractOperationTest();
		abstractOperationTest.testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap3();
		//
		SingleGenericGraphAsInputOperationTest singleGenericGraphAsInputOperationTest = new SingleGenericGraphAsInputOperationTest();
		singleGenericGraphAsInputOperationTest.testBuildSingleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap();
		
		this.testBuildTransformGraphOperationLevelSpecificParameterNameValueObjectMap();
		
		transformGraphOperation = new TransformGraphOperation(
//				false,
				AbstractOperationTest.abstractOperationLevelSpecificParameterNameValueObjectMap,
				SingleGenericGraphAsInputOperationTest.singleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap,
				transformGraphOperationLevelSpecificParameterNameValueObjectMap,
				true);
		
	}

	/**
	 * Test method for {@link operation.graph.transform.TransformGraphOperation#call()}.
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Test
	void testCall() throws SQLException, IOException {
		
		VisProjectDBContextTest visProjectDBContextTest = new VisProjectDBContextTest();
		visProjectDBContextTest.testConnect();
		
		
		this.testTransformGraphOperation();
		
		
		transformGraphOperation.setHostVisProjectDBContext(VisProjectDBContextTest.TEST_PROJECT_1);
		
		
		StatusType st = transformGraphOperation.call();
		
		System.out.println(st);
	}

}
