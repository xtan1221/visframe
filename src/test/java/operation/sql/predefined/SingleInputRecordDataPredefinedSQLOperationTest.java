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
import metadata.DataType;
import metadata.MetadataID;
import metadata.MetadataName;

/**
 * @author tanxu
 *
 */
public class SingleInputRecordDataPredefinedSQLOperationTest {
	public static Map<SimpleName, Object> singleInputRecordDataPredefinedSQLOperationLevelSpecificParameterNameValueObjectMap;
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
	 * Test method for {@link operation.sql.predefined.SingleInputRecordDataPredefinedSQLOperation#buildSingleInputRecordDataPredefinedSQLOperationLevelSpecificParameterNameValueObjectMap(metadata.MetadataID)}.
	 */
	@Test
	public void testBuildSingleInputRecordDataPredefinedSQLOperationLevelSpecificParameterNameValueObjectMap() {
//		MetadataID inputRecordDataMetadataID = new MetadataID(new MetadataName("gff3_23record"), DataType.RECORD);
		MetadataID inputRecordDataMetadataID = new MetadataID(new MetadataName("gff3_23_2_multi_chrom"), DataType.RECORD);
		singleInputRecordDataPredefinedSQLOperationLevelSpecificParameterNameValueObjectMap = 
				SingleInputRecordDataPredefinedSQLOperation.buildSingleInputRecordDataPredefinedSQLOperationLevelSpecificParameterNameValueObjectMap(inputRecordDataMetadataID);
	}

}
