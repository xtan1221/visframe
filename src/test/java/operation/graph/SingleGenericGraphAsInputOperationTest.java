/**
 * 
 */
package operation.graph;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import metadata.DataType;
import metadata.MetadataID;
import metadata.MetadataName;

/**
 * @author tanxu
 *
 */
public class SingleGenericGraphAsInputOperationTest {
	public static Map<SimpleName, Object> singleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap;
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
	 * TransformGraphOperation 
	 * vftree to graph
	 * Test method for {@link operation.graph.SingleGenericGraphAsInputOperation#buildSingleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap(metadata.MetadataID)}.
	 */
	@Test
	public void testBuildSingleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap() {
//		MetadataID inputGenericGraphMetadataID = new MetadataID(new MetadataName("mono_1_2"), DataType.vfTREE);
		MetadataID inputGenericGraphMetadataID = new MetadataID(new MetadataName("mono_6813_3"), DataType.vfTREE);
		
		singleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap = 
				SingleGenericGraphAsInputOperation.buildSingleGenericGraphAsInputOperationLevelSpecificParameterNameValueObjectMap(inputGenericGraphMetadataID);
	}

}
