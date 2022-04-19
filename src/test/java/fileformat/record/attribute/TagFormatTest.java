/**
 * 
 */
package fileformat.record.attribute;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fileformat.record.attribute.TagFormat;
import fileformat.record.utils.PlainStringMarker;
import fileformat.record.utils.StringMarker;
import rdb.sqltype.SQLDataType;
import rdb.sqltype.SQLStringType;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * @author tanxu
 *
 */
public class TagFormatTest {
	public static TagFormat GFF3_ATTRIBUTES_ATTRIBUTE_TAG_FORMAT;
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
	 * Test method for {@link fileformat.record.attribute.TagFormat#TagFormat(boolean, java.util.Map, rdb.sqltype.SQLDataType, fileformat.record.utils.StringMarker, int, int, java.lang.Integer)}.
	 */
	@Test
	public void testTagFormat() {
		boolean hasDataTypeIndicatorComponent = false;
//		boolean dataTypeIndicatorComponentStringCaseSensitive,
		Map<String,VfDefinedPrimitiveSQLDataType> dataTypeIndicatorComponentStringSQLDataTypeMap = null;
		VfDefinedPrimitiveSQLDataType defaultSQLDataType = new SQLStringType(100,false);
		StringMarker componentDelimiter = new PlainStringMarker("=",false); //ID=mrna0001
		int nameComponentStringIndex = 0;
		int valueComponentStringIndex = 1;
		Integer dataTypeIndicatorComponentStringIndex = null;
		
		GFF3_ATTRIBUTES_ATTRIBUTE_TAG_FORMAT = new TagFormat(
				hasDataTypeIndicatorComponent,
				dataTypeIndicatorComponentStringSQLDataTypeMap,
				defaultSQLDataType,
				componentDelimiter,
				nameComponentStringIndex,
				valueComponentStringIndex,
				dataTypeIndicatorComponentStringIndex
				);
	}

}
