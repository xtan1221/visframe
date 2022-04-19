/**
 * 
 */
package basic;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author tanxu
 *
 */
class SimpleNameTest {

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
	 * Test method for {@link basic.VfNameString#equals(java.lang.Object)}.
	 */
	@Test
	void testEqualsObject() {
		Set<SimpleName> nameSet = new HashSet<>();
		
		SimpleName name1 = new SimpleName("ID");
		nameSet.add(name1);
		
		SimpleName name2 = new SimpleName("id");
		
		System.out.println(nameSet.contains(name2));
		
		System.out.println(name1.equals(name2));
	}

}
