package sql.derby;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;

class TableSchemaUtilsTest {
	Path dbDir = Paths.get("C:\\visframe\\project2");
	String dbName = "db";
	boolean createDBIfNotExist = true;
	SimpleName schemaName = new SimpleName("APP_VF_MANAGEMENT");
	SimpleName tableName = new SimpleName("DataImporter");
	
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
	void testDoesTableExists() {
		try {
			System.out.println(TableSchemaUtils.doesTableExists(
					DerbyDBUtils.getEmbeddedDBConnection(dbDir, dbName, createDBIfNotExist), 
					schemaName, tableName));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
