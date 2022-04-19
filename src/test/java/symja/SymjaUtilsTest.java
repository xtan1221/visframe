/**
 * 
 */
package symja;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import rdb.sqltype.SQLDataType;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.VfDefinedPrimitiveSQLDataType;

/**
 * @author tanxu
 *
 */
class SymjaUtilsTest {

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
	 * Test method for {@link symja.SymjaUtils#validateExpressionString(java.lang.String)}.
	 */
	@Test
	void testIsValidExpressionString() {
		String expression2 = "a+b-c*kdfjadf-2klj+_r4s";
		
		SymjaUtils.validateExpressionString(expression2);
	}

	/**
	 * Test method for {@link symja.SymjaUtils#expressionDataType(java.lang.String)}.
	 */
	@Test
	void testExpressionDataType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link symja.SymjaUtils#extractVariableNameSet(java.lang.String)}.
	 */
	@Test
	void testExtractVariableNameSet() {
//		String expression = "a+b-c*kdfjadf-2klj+r4s";
//		SymjaUtils.extractVariableNameSet(expression).forEach(e->{
//			System.out.println(e);
//		});
		
		String expression2 = "a+b-c*kdfjadf-2klj+2r4s";
		String expression3 = "a+b-c*kdfjadf-2klj+2r4s2dww22";
		SymjaUtils.extractVariableNameSet(expression3).forEach(e->{
			System.out.println(e);
		});
	}

	/**
	 * Test method for {@link symja.SymjaUtils#evaluate(java.lang.String, java.util.Map)}.
	 */
	@Test
	void testEvaluate() {
//		String expression2 = "a+b-c*kdfjadf-2klj+_r4s";
//		String expression = "a+b-c*kdfjadf-2klj+2r4s2dww22";
//		
//		Map<SimpleName,String> map = new HashMap<>();
//		map.put(new SimpleName("a"), "1");
//		map.put(new SimpleName("b"), "1");
//		map.put(new SimpleName("c"), "1");
//		map.put(new SimpleName("kdfjadf"), "1");
//		map.put(new SimpleName("klj"), "1");
//		map.put(new SimpleName("r4s"), "1");
//		
//		System.out.println(SymjaUtils.evaluate(expression, map));
		
//		String expression = "a+b+A";
		String expression = "Binomial(a,b)";
		
		
		Map<VfSymjaVariableName,String> variableNameValueStringMap = new HashMap<>();
		variableNameValueStringMap.put(new VfSymjaVariableName("a"), "100");
		variableNameValueStringMap.put(new VfSymjaVariableName("b"), "40");
		variableNameValueStringMap.put(new VfSymjaVariableName("A"), "4.4");
		
		
		Map<VfSymjaVariableName,VfDefinedPrimitiveSQLDataType> variableNameDataTypeMap = new HashMap<>();
		variableNameDataTypeMap.put(new VfSymjaVariableName("a"), SQLDataTypeFactory.integerType());
		variableNameDataTypeMap.put(new VfSymjaVariableName("b"), SQLDataTypeFactory.integerType());
		variableNameDataTypeMap.put(new VfSymjaVariableName("A"), SQLDataTypeFactory.doubleType());
		
		System.out.println(SymjaUtils.evaluate(expression, variableNameValueStringMap, variableNameDataTypeMap));
	}

}
