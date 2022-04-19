/**
 * 
 */
package operation.sql.predefined.type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import operation.AbstractOperationTest;
import operation.sql.SQLOperationBaseTest;
import operation.sql.predefined.PredefinedSQLBasedOperationTest;
import operation.sql.predefined.SingleInputRecordDataPredefinedSQLOperationTest;
import operation.sql.predefined.utils.SqlSortOrderType;
import rdb.table.data.DataTableColumnName;

/**
 * @author tanxu
 *
 */
public class GroupAndBinCountOperationTest {
	public static Map<SimpleName, Object> groupAndBinCountOperationLevelSpecificParameterNameValueObjectMap;
	public static GroupAndBinCountOperation groupAndBinCountOperation;
	
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
	 * group gff3 by column 'chrom';
	 * then sort by column 'start' with sort order = ASC;
	 * 
	 * bin size = 1000;
	 * bin min = null;
	 * bin max = null
	 * Test method for {@link operation.sql.predefined.type.GroupAndBinCountOperation#buildGroupAndBinCountOperationLevelSpecificParameterNameValueObjectMap(java.util.LinkedHashSet, rdb.table.data.DataTableColumnName, operation.sql.predefined.utils.SqlSortOrderType, double, java.lang.Double, java.lang.Double)}.
	 */
	@Test
	void testBuildGroupAndBinCountOperationLevelSpecificParameterNameValueObjectMap() {
		LinkedHashSet<DataTableColumnName> groupByColumnNameSet = new LinkedHashSet<>();
		groupByColumnNameSet.add(new DataTableColumnName("seqid"));
		
		DataTableColumnName numericColumnNameToSortAndBin = new DataTableColumnName("start");
		SqlSortOrderType numericColumnSortType = SqlSortOrderType.ASCEND;
//		SqlSortOrderType numericColumnSortType = SqlSortOrderType.DESCEND;
		double binSize = 500;
		Double binMin = 0d; //can be null;
		Double binMax = 7500d;
		
		groupAndBinCountOperationLevelSpecificParameterNameValueObjectMap = 
				GroupAndBinCountOperation.buildGroupAndBinCountOperationLevelSpecificParameterNameValueObjectMap(
						groupByColumnNameSet, numericColumnNameToSortAndBin, numericColumnSortType, binSize, binMin, binMax);
	}
	
	/**
	 * Test method for {@link operation.sql.predefined.type.GroupAndBinCountOperation#GroupAndBinCountOperation(java.util.Map, java.util.Map, java.util.Map, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testGroupAndBinCountOperation() {
		//
		AbstractOperationTest abstractOperationTest = new AbstractOperationTest();
		abstractOperationTest.testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap3();
		
		SQLOperationBaseTest SQLOperationBaseTest = new SQLOperationBaseTest();
		SQLOperationBaseTest.testBuildSQLOperationBaseLevelSpecificParameterNameValueObjectMap();
		
		PredefinedSQLBasedOperationTest predefinedSQLBasedOperationTest = new PredefinedSQLBasedOperationTest();
		predefinedSQLBasedOperationTest.testBuildPredefinedSQLBasedOperationLevelSpecificParameterNameValueObjectMap();
		
		SingleInputRecordDataPredefinedSQLOperationTest singleInputRecordDataPredefinedSQLOperationTest = new SingleInputRecordDataPredefinedSQLOperationTest();
		singleInputRecordDataPredefinedSQLOperationTest.testBuildSingleInputRecordDataPredefinedSQLOperationLevelSpecificParameterNameValueObjectMap();
	
		
		this.testBuildGroupAndBinCountOperationLevelSpecificParameterNameValueObjectMap();
		
		groupAndBinCountOperation = new GroupAndBinCountOperation(
//				false,
				AbstractOperationTest.abstractOperationLevelSpecificParameterNameValueObjectMap,
				operation.sql.SQLOperationBaseTest.SQLOperationBaseLevelSpecificParameterNameValueObjectMap,
				PredefinedSQLBasedOperationTest.predefinedSQLBasedOperationLevelSpecificParameterNameValueObjectMap,
				SingleInputRecordDataPredefinedSQLOperationTest.singleInputRecordDataPredefinedSQLOperationLevelSpecificParameterNameValueObjectMap,
				
				groupAndBinCountOperationLevelSpecificParameterNameValueObjectMap,
				true
				);
	}

}
