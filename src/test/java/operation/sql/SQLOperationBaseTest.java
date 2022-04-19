/**
 * 
 */
package operation.sql;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import metadata.MetadataName;

/**
 * @author tanxu
 *
 */
public class SQLOperationBaseTest {
	public static Map<SimpleName, Object> SQLOperationBaseLevelSpecificParameterNameValueObjectMap;
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
	 * Test method for {@link operation.sql.SQLOperationBase#buildSQLOperationBaseLevelSpecificParameterNameValueObjectMap(metadata.MetadataName)}.
	 */
	@Test
	public void testBuildSQLOperationBaseLevelSpecificParameterNameValueObjectMap() {
//		MetadataName outputRecordDataName = new MetadataName("group_and_bin_count_operation_min0_max7500_2");
		MetadataName outputRecordDataName = new MetadataName("add_numeric_col_3");
		SQLOperationBaseLevelSpecificParameterNameValueObjectMap = 
				SQLOperationBase.buildSQLOperationBaseLevelSpecificParameterNameValueObjectMap(outputRecordDataName);
	}

}
