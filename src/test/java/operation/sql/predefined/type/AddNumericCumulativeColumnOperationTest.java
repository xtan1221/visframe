/**
 * 
 */
package operation.sql.predefined.type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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
import operation.sql.predefined.utils.CumulativeColumnSymjaExpressionDelegate;
import operation.sql.predefined.utils.CumulativeColumnSymjaExpressionDelegateTest;
import operation.sql.predefined.utils.SqlSortOrderType;
import rdb.table.data.DataTableColumnName;

/**
 * @author tanxu
 *
 */
public class AddNumericCumulativeColumnOperationTest {
	public static Map<SimpleName, Object> addNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap;
	public static AddNumericCumulativeColumnOperation addNumericCumulativeColumnOperation;
	
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
	 * build an operation with empty group by, order by and other non-pk column sets;
	 * 
	 * also the symja expression simply calculate an index for each record with the original order;
	 * 
	 * Test method for {@link operation.sql.predefined.type.AddNumericCumulativeColumnOperation#buildAddNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap(java.util.LinkedHashSet, java.util.LinkedHashSet, java.util.ArrayList, java.util.LinkedHashSet, operation.sql.predefined.utils.CumulativeColumnSymjaExpressionDelegate, rdb.table.data.DataTableColumnName, java.lang.Double)}.
	 */
	@Test
	void testBuildGroupAndBinCountOperationLevelSpecificParameterNameValueObjectMap() {
		LinkedHashSet<DataTableColumnName> groupByColumnNameSet = new LinkedHashSet<>();
		LinkedHashSet<DataTableColumnName> orderByColumnNameSet = new LinkedHashSet<>();
		ArrayList<SqlSortOrderType> orderByColumnSortTypeList = new ArrayList<>();
		LinkedHashSet<DataTableColumnName> otherKeptNonPKColumnNameSet = new LinkedHashSet<>();
		
		CumulativeColumnSymjaExpressionDelegateTest cumulativeColumnSymjaExpressionDelegateTest = new CumulativeColumnSymjaExpressionDelegateTest();
		cumulativeColumnSymjaExpressionDelegateTest.testCumulativeColumnSymjaExpressionDelegate();
		
		DataTableColumnName cumulativeColumnNameInOutputDataTable = null; //can be null;
		double initialValue = 0;
		
		addNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap = 
				AddNumericCumulativeColumnOperation.buildAddNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap(
						groupByColumnNameSet, orderByColumnNameSet, orderByColumnSortTypeList, otherKeptNonPKColumnNameSet, 
						CumulativeColumnSymjaExpressionDelegateTest.cumulativeColumnSymjaExpressionDelegate, 
						cumulativeColumnNameInOutputDataTable, initialValue);
		
	}
	
	/**
	 * build an operation with 
	 * 1. gff3 record data as input and that
	 * 
	 * 2. group by 'strand', order by 'start' with ASC sort order
	 * 
	 * 3. other kept non-pk columns include 'seqid', 'name';
	 * 
	 * 4. symja expression calculate integer type order index starting from 0
	 * 
	 * also the symja expression simply calculate an index for each record with the original order;
	 * 
	 * Test method for {@link operation.sql.predefined.type.AddNumericCumulativeColumnOperation#buildAddNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap(java.util.LinkedHashSet, java.util.LinkedHashSet, java.util.ArrayList, java.util.LinkedHashSet, operation.sql.predefined.utils.CumulativeColumnSymjaExpressionDelegate, rdb.table.data.DataTableColumnName, java.lang.Double)}.
	 */
	@Test
	void testBuildGroupAndBinCountOperationLevelSpecificParameterNameValueObjectMap2() {
		LinkedHashSet<DataTableColumnName> groupByColumnNameSet = new LinkedHashSet<>();
		groupByColumnNameSet.add(new DataTableColumnName("strand"));
		LinkedHashSet<DataTableColumnName> orderByColumnNameSet = new LinkedHashSet<>();
		orderByColumnNameSet.add(new DataTableColumnName("start"));
		ArrayList<SqlSortOrderType> orderByColumnSortTypeList = new ArrayList<>();
		orderByColumnSortTypeList.add(SqlSortOrderType.ASCEND);
		LinkedHashSet<DataTableColumnName> otherKeptNonPKColumnNameSet = new LinkedHashSet<>();
		otherKeptNonPKColumnNameSet.add(new DataTableColumnName("seqid"));
		otherKeptNonPKColumnNameSet.add(new DataTableColumnName("name"));
		
		CumulativeColumnSymjaExpressionDelegateTest cumulativeColumnSymjaExpressionDelegateTest = new CumulativeColumnSymjaExpressionDelegateTest();
		cumulativeColumnSymjaExpressionDelegateTest.testCumulativeColumnSymjaExpressionDelegate();
		
		DataTableColumnName cumulativeColumnNameInOutputDataTable = null; //can be null;
		double initialValue = 0;
		
		addNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap = 
				AddNumericCumulativeColumnOperation.buildAddNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap(
						groupByColumnNameSet, orderByColumnNameSet, orderByColumnSortTypeList, otherKeptNonPKColumnNameSet, 
						CumulativeColumnSymjaExpressionDelegateTest.cumulativeColumnSymjaExpressionDelegate, 
						cumulativeColumnNameInOutputDataTable, initialValue);
	}
	
	/**
	 * build an operation with 
	 * 1. gff3 record data as input and that
	 * 
	 * 2. group by 'seqid', 'strand', order by 'start', 'end' with ASC sort order
	 * 
	 * 3. other kept non-pk columns include 'seqid', 'name';
	 * 
	 * 4. symja expression calculate integer type order index starting from 0
	 * 
	 * also the symja expression simply calculate an index for each record with the original order;
	 * 
	 * Test method for {@link operation.sql.predefined.type.AddNumericCumulativeColumnOperation#buildAddNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap(java.util.LinkedHashSet, java.util.LinkedHashSet, java.util.ArrayList, java.util.LinkedHashSet, operation.sql.predefined.utils.CumulativeColumnSymjaExpressionDelegate, rdb.table.data.DataTableColumnName, java.lang.Double)}.
	 */
	@Test
	void testBuildGroupAndBinCountOperationLevelSpecificParameterNameValueObjectMap3() {
		LinkedHashSet<DataTableColumnName> groupByColumnNameSet = new LinkedHashSet<>();
		groupByColumnNameSet.add(new DataTableColumnName("seqid"));
		groupByColumnNameSet.add(new DataTableColumnName("strand"));
		LinkedHashSet<DataTableColumnName> orderByColumnNameSet = new LinkedHashSet<>();
		orderByColumnNameSet.add(new DataTableColumnName("start"));
		orderByColumnNameSet.add(new DataTableColumnName("end"));
		ArrayList<SqlSortOrderType> orderByColumnSortTypeList = new ArrayList<>();
		orderByColumnSortTypeList.add(SqlSortOrderType.ASCEND);
		orderByColumnSortTypeList.add(SqlSortOrderType.ASCEND);
		LinkedHashSet<DataTableColumnName> otherKeptNonPKColumnNameSet = new LinkedHashSet<>();
		otherKeptNonPKColumnNameSet.add(new DataTableColumnName("name"));
		
		CumulativeColumnSymjaExpressionDelegateTest cumulativeColumnSymjaExpressionDelegateTest = new CumulativeColumnSymjaExpressionDelegateTest();
		cumulativeColumnSymjaExpressionDelegateTest.testCumulativeColumnSymjaExpressionDelegate();
		
		DataTableColumnName cumulativeColumnNameInOutputDataTable = null; //can be null;
		double initialValue = 0;
		
		addNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap = 
				AddNumericCumulativeColumnOperation.buildAddNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap(
						groupByColumnNameSet, orderByColumnNameSet, orderByColumnSortTypeList, otherKeptNonPKColumnNameSet, 
						CumulativeColumnSymjaExpressionDelegateTest.cumulativeColumnSymjaExpressionDelegate, 
						cumulativeColumnNameInOutputDataTable, initialValue);
	}
	/**
	 * Test method for {@link operation.sql.predefined.type.AddNumericCumulativeColumnOperation#AddNumericCumulativeColumnOperation(java.util.Map, java.util.Map, java.util.Map, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testAddNumericCumulativeColumnOperation() {
		//
		AbstractOperationTest abstractOperationTest = new AbstractOperationTest();
		abstractOperationTest.testBuildAbstractOperationLevelSpecificParameterNameValueObjectMap3();
		
		SQLOperationBaseTest SQLOperationBaseTest = new SQLOperationBaseTest();
		SQLOperationBaseTest.testBuildSQLOperationBaseLevelSpecificParameterNameValueObjectMap();
		
		PredefinedSQLBasedOperationTest predefinedSQLBasedOperationTest = new PredefinedSQLBasedOperationTest();
		predefinedSQLBasedOperationTest.testBuildPredefinedSQLBasedOperationLevelSpecificParameterNameValueObjectMap();
		
		SingleInputRecordDataPredefinedSQLOperationTest singleInputRecordDataPredefinedSQLOperationTest = new SingleInputRecordDataPredefinedSQLOperationTest();
		singleInputRecordDataPredefinedSQLOperationTest.testBuildSingleInputRecordDataPredefinedSQLOperationLevelSpecificParameterNameValueObjectMap();
	
		testBuildGroupAndBinCountOperationLevelSpecificParameterNameValueObjectMap3();
		
		
		addNumericCumulativeColumnOperation = new AddNumericCumulativeColumnOperation(
//				false,
				AbstractOperationTest.abstractOperationLevelSpecificParameterNameValueObjectMap,
				operation.sql.SQLOperationBaseTest.SQLOperationBaseLevelSpecificParameterNameValueObjectMap,
				PredefinedSQLBasedOperationTest.predefinedSQLBasedOperationLevelSpecificParameterNameValueObjectMap,
				SingleInputRecordDataPredefinedSQLOperationTest.singleInputRecordDataPredefinedSQLOperationLevelSpecificParameterNameValueObjectMap,
				
				addNumericCumulativeColumnOperationLevelSpecificParameterNameValueObjectMap,
				true
				);
	}

}
