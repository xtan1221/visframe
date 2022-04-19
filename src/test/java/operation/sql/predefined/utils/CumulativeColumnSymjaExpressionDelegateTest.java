/**
 * 
 */
package operation.sql.predefined.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import rdb.sqltype.SQLDataType;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;
import rdb.table.data.DataTableColumnName;
import symja.VfSymjaExpressionString;
import symja.VfSymjaSinglePrimitiveOutputExpression;
import symja.VfSymjaVariableName;

/**
 * @author tanxu
 *
 */
public class CumulativeColumnSymjaExpressionDelegateTest {
	public static CumulativeColumnSymjaExpressionDelegate cumulativeColumnSymjaExpressionDelegate;
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
	 * create a CumulativeColumnSymjaExpressionDelegate with a expression that simply add 1 to the previous value;
	 * 
	 * Test method for {@link operation.sql.predefined.utils.CumulativeColumnSymjaExpressionDelegate#CumulativeColumnSymjaExpressionDelegate(symja.VfSymjaSinglePrimitiveOutputExpression, java.util.Map, symja.VfSymjaVariableName)}.
	 */
	@Test
	public void testCumulativeColumnSymjaExpressionDelegate() {
		//build VfSymjaSinglePrimitiveOutputExpression
		VfDefinedPrimitiveSQLDataType sqlDataType = SQLDataTypeFactory.integerType();
		VfSymjaExpressionString expressionString = new VfSymjaExpressionString("prc+1");
		Map<VfSymjaVariableName, VfDefinedPrimitiveSQLDataType> variableNameSQLDataTypeMap = new HashMap<>();
		variableNameSQLDataTypeMap.put(new VfSymjaVariableName("prc"), SQLDataTypeFactory.integerType());
		
		VfSymjaSinglePrimitiveOutputExpression symjaExpression = new VfSymjaSinglePrimitiveOutputExpression(
				sqlDataType, 
				expressionString,
				variableNameSQLDataTypeMap
				);
		
		//
		BiMap<DataTableColumnName,VfSymjaVariableName> columnSymjaVariableNameMap = HashBiMap.create();
		
		VfSymjaVariableName previouseRecordCumulativeColumnSymjaVariableName = new VfSymjaVariableName("prc");
		
		cumulativeColumnSymjaExpressionDelegate = new CumulativeColumnSymjaExpressionDelegate(
				symjaExpression,
				columnSymjaVariableNameMap,
				previouseRecordCumulativeColumnSymjaVariableName
				);
	}

}
