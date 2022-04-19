/**
 * 
 */
package context.project;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;

/**
 * @author tanxu
 *
 */
public class VisProjectDBContextTest {
	public static VisProjectDBContext TEST_PROJECT_1;
	private static VisProjectDBContext TEST_PROJECT_2;
	private static VisProjectDBContext TEST_PROJECT_3;
	public static VisProjectDBContext TEST_PROJECT_4;
	public static VisProjectDBContext TEST_PROJECT_5;
	
	static Path project1ParentDir = Paths.get("C:\\visframeUI-test");
	static SimpleName project1Name = new SimpleName("project1");
	
	static Path project2ParentDir = Paths.get("C:\\visframeUI-test");
	static SimpleName project2Name = new SimpleName("project2");
	
	static Path project3ParentDir = Paths.get("C:\\visframeUI-test");
	static SimpleName project3Name = new SimpleName("project3");
	
	static Path project4ParentDir = Paths.get("C:\\visframeUI-test");
	static SimpleName project4Name = new SimpleName("project4");
	
	static Path project5ParentDir = Paths.get("C:\\visframeUI-test");
	static SimpleName project5Name = new SimpleName("project5");
	
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
	 * Test method for {@link context.project.VisProjectDBContext#VisProjectDBContext(basic.SimpleName, java.nio.file.Path)}.
	 */
	@Test
	public void testVisProjectDBContext() {
		TEST_PROJECT_1 = new VisProjectDBContext(project1Name,project1ParentDir);
//		TEST_PROJECT_4 = new VisProjectDBContext(project4Name,project4ParentDir);
//		TEST_PROJECT_5 = new VisProjectDBContext(project5Name,project5ParentDir);
	}
	
	
	/**
	 * Test method for {@link context.project.VisProjectDBContext#connect()}.
	 * @throws SQLException 
	 */
	@Test
	public void testConnect() throws SQLException {
		this.testVisProjectDBContext();
		
		TEST_PROJECT_1.connect();
//		TEST_PROJECT_5.connect();
//		TEST_PROJECT_4.connect();
	}
	
	/**
	 * Test method for {@link context.project.VisProjectDBContext#disconnect()}.
	 * @throws SQLException 
	 */
	@Test
	public void testDisconnect() throws SQLException {
		this.testVisProjectDBContext();
		this.testConnect();
//		TEST_PROJECT_1.connect();
		TEST_PROJECT_1.disconnect();
//		TEST_PROJECT_4.connect();
//		TEST_PROJECT_4.disconnect();
	}
}
