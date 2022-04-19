package operation.sql.generic.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import operation.sql.generic.utils.GenericSQLQueryProcessor.DottedFullColumnName;

class GenericSQLQueryProcessorTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}


	/**
	 * Test method for {@link utils.visframe.GenericSQLQueryProcessor#GenericSqlQueryProcessor(java.lang.String)}.
	 */
	@Test
	void testGenericSqlQueryProcessor() {
//		String sqlString = "SELECT A.C1, B.C2, B.C3, D.C5 FROM A, B, D WHERE A.C3=2 AND B.C4<D.C5 OR B.A=D.C";
		
		//contain *
//		String sqlString = "SELECT B.*, A.C1, B.C2, B.C3, D.C5 FROM A, B, D WHERE A.C3=2 AND B.C4<D.C5";
		
		//contain AS
//		String sqlString = "SELECT A.C1 AS K, B.C2, B.C3, D.C5 FROM A, B, D WHERE A.C3=2 AND B.C4<D.C5";
		
		//some table name of columns not in FROM clause
//		String sqlString = "SELECT A.C1, B.C2, B.C3, D.C5 FROM A, B WHERE A.C3=2 AND B.C4<D.C5";
		
		//lower case
		
		String sqlString = "SELECT a.C1, B.c2, B.C3, d.c5 FROM a, B, d WHERE a.C3=2 AND B.C4<d.c5 OR B.A=d.C";
		
		
		GenericSQLQueryProcessor processor = new GenericSQLQueryProcessor(sqlString);
		
		
		
		System.out.println(processor.getTableNameSet());
		System.out.println(processor.getDottedFullColumnNamePosPairList());
	}

	/**
	 * Test method for {@link utils.visframe.GenericSQLQueryProcessor#getSelectElementNum(int)}.
	 */
	@Test
	void testGetSelectElementNum() {
		String sqlString = "SELECT a.C1, B.c2, B.C3, d.c5 FROM a, B, d WHERE a.C3=2 AND B.C4<d.c5 OR B.A=d.C AND D.F='Sdfs'";
		
		GenericSQLQueryProcessor processor = new GenericSQLQueryProcessor(sqlString);
		
		assertEquals(processor.getSelectElementNum(), 4);
	}
	
	/**
	 * Test method for {@link utils.visframe.GenericSQLQueryProcessor#replace(java.util.Map, java.util.Map)}.
	 */
	@Test
	void testReplace() {
		//input sql contain lower case table/column names
		String sqlString = "SELECT a.C1, B.c2, B.C3, d.c5 FROM a, B, d WHERE a.C3=2 AND B.C4<d.c5 OR B.A=d.C AND D.F='Sdfs'";
		
//		String sqlString = "SELECT A.C1, B.C2, B.C3, D.C5 FROM A, B, D WHERE A.C3=2 AND B.C4<D.C5 OR B.A=D.C";
		GenericSQLQueryProcessor processor = new GenericSQLQueryProcessor(sqlString);
		
		Map<String, String> tableNameReplacementMap = new HashMap<>();
		tableNameReplacementMap.put("A", "T_A");
		tableNameReplacementMap.put("B", "T_B");
		tableNameReplacementMap.put("D", "T_D");
		
		
		Map<DottedFullColumnName, String> dottedFullColumnNameReplacementMap = new HashMap<>();
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("A","C1"), "T_A.C_C1");
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("B","C2"), "T_B.C_C2");
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("B","C3"), "T_B.C_C3");
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("D","C5"), "T_D.C_C5");
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("A","C3"), "T_A.C_C3");
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("B","C4"), "T_B.C_C4");
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("B","A"), "T_B.C_A");
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("D","C"), "T_D.C_C");
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("D","F"), "T_D.C_F");
		
		String replaced = processor.replace(tableNameReplacementMap, dottedFullColumnNameReplacementMap);
		
		
		System.out.println(replaced);
	}

	/**
	 * Test method for {@link utils.visframe.GenericSQLQueryProcessor#replace(java.util.Map, java.util.Map)}.
	 */
	@Test
	void testReplace2() {
		String sqlString = "SELECT A.C1, B.C2 FROM A, B WHERE A.C3=2";
		GenericSQLQueryProcessor processor = new GenericSQLQueryProcessor(sqlString);
		
		Map<String, String> tableNameReplacementMap = new HashMap<>();
		tableNameReplacementMap.put("A", "T_A");
		tableNameReplacementMap.put("B", "T_B");
		
		
		Map<DottedFullColumnName, String> dottedFullColumnNameReplacementMap = new HashMap<>();
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("A","C1"), "T_A.C_C1");
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("B","C2"), "T_B.C_C2");
		dottedFullColumnNameReplacementMap.put(new DottedFullColumnName("A","C3"), "T_A.C_C3");
		
		
		String replaced = processor.replace(tableNameReplacementMap, dottedFullColumnNameReplacementMap);
		
		
		System.out.println(replaced);
	}
}
