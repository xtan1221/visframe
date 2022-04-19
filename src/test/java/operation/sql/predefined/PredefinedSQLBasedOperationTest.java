/**
 * 
 */
package operation.sql.predefined;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;

/**
 * @author tanxu
 *
 */
public class PredefinedSQLBasedOperationTest {
	public static Map<SimpleName, Object> predefinedSQLBasedOperationLevelSpecificParameterNameValueObjectMap;
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
	 * Test method for {@link operation.sql.predefined.PredefinedSQLBasedOperation#buildPredefinedSQLBasedOperationLevelSpecificParameterNameValueObjectMap()}.
	 */
	@Test
	public void testBuildPredefinedSQLBasedOperationLevelSpecificParameterNameValueObjectMap() {
		predefinedSQLBasedOperationLevelSpecificParameterNameValueObjectMap = PredefinedSQLBasedOperation.buildPredefinedSQLBasedOperationLevelSpecificParameterNameValueObjectMap();
	}

}
