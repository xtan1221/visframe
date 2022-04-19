/**
 * 
 */
package sql.derby;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author tanxu
 *
 */
class DerbyDBUtilsTest {
	Path dbDir = Paths.get("C:\\visframe\\project2");
	String dbName = "db";
	boolean createDBIfNotExist = true;
	
	
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
	 * Test method for {@link sql.derby.DerbyDBUtils#dbExists(java.nio.file.Path, java.lang.String)}.
	 */
	@Test
	void testDbExists() {
		try {
			System.out.println(DerbyDBUtils.dbExists(dbDir, dbName));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test method for {@link sql.derby.DerbyDBUtils#retrieveAllUserDefinedTableNames(java.sql.Connection)}.
	 */
	@Test
	void testRetrieveAllUserDefinedTableNames() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link sql.derby.DerbyDBUtils#getEmbeddedDBConnection(java.nio.file.Path, java.lang.String, boolean)}.
	 */
	@Test
	void testGetEmbeddedDBConnection() {
		try {
			Connection dbCon = DerbyDBUtils.getEmbeddedDBConnection(dbDir, dbName, createDBIfNotExist);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Test method for {@link sql.derby.DerbyDBUtils#shutDownDerbyEngine()}.
	 */
	@Test
	void testShutDownDerbyEngine() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link sql.derby.DerbyDBUtils#shutDownEmbeddedDB(java.io.File, java.lang.String)}.
	 */
	@Test
	void testShutDownEmbeddedDB() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link sql.derby.DerbyDBUtils#deleteEmbeddedDB(java.io.File, java.lang.String)}.
	 */
	@Test
	void testDeleteEmbeddedDB() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link sql.derby.DerbyDBUtils#printSQLException(java.sql.SQLException)}.
	 */
	@Test
	void testPrintSQLException() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link sql.derby.DerbyDBUtils#makeURLStringOfNewFolderUnderParentDir(java.lang.String, java.lang.String)}.
	 */
	@Test
	void testMakeURLStringOfNewFolderUnderParentDirStringString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link sql.derby.DerbyDBUtils#makeURLStringOfNewFolderUnderParentDir(java.io.File, java.lang.String)}.
	 */
	@Test
	void testMakeURLStringOfNewFolderUnderParentDirFileString() {
		fail("Not yet implemented");
	}

}
