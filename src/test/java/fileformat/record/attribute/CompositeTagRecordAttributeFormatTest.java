/**
 * 
 */
package fileformat.record.attribute;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import basic.VfNotes;
import fileformat.record.attribute.CompositeTagRecordAttributeFormat;
import fileformat.record.attribute.TagFormat;
import fileformat.record.utils.PlainStringMarker;
import fileformat.record.utils.StringMarker;

/**
 * @author tanxu
 *
 */
public class CompositeTagRecordAttributeFormatTest {
	public static CompositeTagRecordAttributeFormat GFF3_ATTRIBUTES_ATTRIBUTE_CompositeTagRecordAttributeFormat;
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
	 * Test method for {@link fileformat.record.attribute.CompositeTagRecordAttributeFormat#CompositeTagRecordAttributeFormat(basic.SimpleName, basic.VfNotes, fileformat.record.attribute.TagFormat, fileformat.record.utils.StringMarker)}.
	 */
	@Test
	public void testCompositeTagRecordAttributeFormat() {
		SimpleName name = new SimpleName("attributes");
		VfNotes notes = VfNotes.makeVisframeDefinedVfNotes();
		////
		TagFormatTest tagFormatTest = new TagFormatTest();
		tagFormatTest.testTagFormat();
		TagFormat tagFormat = TagFormatTest.GFF3_ATTRIBUTES_ATTRIBUTE_TAG_FORMAT;
		StringMarker tagDelimiter = new PlainStringMarker(";",false);
		
		
		GFF3_ATTRIBUTES_ATTRIBUTE_CompositeTagRecordAttributeFormat = new CompositeTagRecordAttributeFormat(
				name,notes,tagFormat,tagDelimiter
				);
	}

}
