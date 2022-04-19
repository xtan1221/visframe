/**
 * 
 */
package fileformat.record;

import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import basic.VfNotes;
import fileformat.record.RecordDataFileFormat;
import fileformat.record.between.BetweenRecordStringFormatBase;
import fileformat.record.between.SequentialBetweenRecordStringFormatTest;
import fileformat.record.within.StringDelimitedRecordAttributeStringFormatTest;
import fileformat.record.within.WithinRecordAttributeStringFormatBase;
import utils.SerializationUtils;

/**
 * @author tanxu
 *
 */
public class RecordDataFileFormatTest {
	public static RecordDataFileFormat GFF3_RecordDataFileFormat;
	public static RecordDataFileFormat HMMER_TBLOUT_PROTEIN_RecordDataFileFormat;
	public static RecordDataFileFormat HMMER_DOMOUT_RecordDataFileFormat;
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
	 * Test method for {@link fileformat.record.RecordDataFileFormat#RecordDataFileFormat(basic.SimpleName, basic.VfNotes, fileformat.record.between.BetweenRecordStringFormatBase, fileformat.record.within.WithinRecordAttributeStringFormatBase)}.
	 */
	@Test
	public void testRecordDataFileFormat_GFF3() {
		SimpleName name = new SimpleName("GFF3");
		VfNotes notes = VfNotes.makeVisframeDefinedVfNotes();
		
		////
		SequentialBetweenRecordStringFormatTest sequentialBetweenRecordStringFormatTest = new SequentialBetweenRecordStringFormatTest();
		sequentialBetweenRecordStringFormatTest.testSequentialBetweenRecordStringFormat_gff3();
		BetweenRecordStringFormatBase betweenRecordStringFormat = SequentialBetweenRecordStringFormatTest.GFF3_SequentialBetweenRecordStringFormat;
		
		
		StringDelimitedRecordAttributeStringFormatTest stringDelimitedRecordAttributeStringFormatTest = new StringDelimitedRecordAttributeStringFormatTest();
		stringDelimitedRecordAttributeStringFormatTest.testStringDelimitedRecordAttributeStringFormat_GFF3();
		WithinRecordAttributeStringFormatBase withinRecordAttributeStringFormat = StringDelimitedRecordAttributeStringFormatTest.GFF3_stringDelimitedRecordAttributeStringFormat;
		
		GFF3_RecordDataFileFormat = new RecordDataFileFormat(
				name,notes,
				betweenRecordStringFormat,
				withinRecordAttributeStringFormat
				);
		
		SerializationUtils.serializeToFile(Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\gff3\\GFF3_template.VFF"), GFF3_RecordDataFileFormat);
	}

	/**
	 * Test method for {@link fileformat.record.RecordDataFileFormat#RecordDataFileFormat(basic.SimpleName, basic.VfNotes, fileformat.record.between.BetweenRecordStringFormatBase, fileformat.record.within.WithinRecordAttributeStringFormatBase)}.
	 */
	@Test
	public void testRecordDataFileFormat_HMMER_TBLOUT_PROTEIN() {
		SimpleName name = new SimpleName("HMMER_TBLOUT_PROTEIN");
		VfNotes notes = new VfNotes("The --tblout output option produces the target hits table. The target hits table consists of one line for each different query/target comparison that met the reporting thresholds, ranked by decreasing statistical significance (increasing E-value).\n tblout fields for protein search programs In the protein search programs, each line consists of 18 space-delimited fields followed by a free text target sequence description. \n Version 3.1b2; \n see http://hmmer.org/");
		
		////
		SequentialBetweenRecordStringFormatTest sequentialBetweenRecordStringFormatTest = new SequentialBetweenRecordStringFormatTest();
		sequentialBetweenRecordStringFormatTest.testSequentialBetweenRecordStringFormat_hmm_tblout_protein();
		BetweenRecordStringFormatBase betweenRecordStringFormat = SequentialBetweenRecordStringFormatTest.HMMER_TBLOUT_PROTEIN_SequentialBetweenRecordStringFormat;
		
		
		StringDelimitedRecordAttributeStringFormatTest stringDelimitedRecordAttributeStringFormatTest = new StringDelimitedRecordAttributeStringFormatTest();
		stringDelimitedRecordAttributeStringFormatTest.testStringDelimitedRecordAttributeStringFormat_hmmer_tblout_protein();
		WithinRecordAttributeStringFormatBase withinRecordAttributeStringFormat = StringDelimitedRecordAttributeStringFormatTest.HMMER_TBLOUT_PROTEIN_stringDelimitedRecordAttributeStringFormat;
		
		HMMER_TBLOUT_PROTEIN_RecordDataFileFormat = new RecordDataFileFormat(
				name,notes,
				betweenRecordStringFormat,
				withinRecordAttributeStringFormat
				);
		
		SerializationUtils.serializeToFile(Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\hmm\\tblout\\HMMER_TBLOUT_PROTEIN_template.VFF"), HMMER_TBLOUT_PROTEIN_RecordDataFileFormat);
	}
	
	/**
	 * Test method for {@link fileformat.record.RecordDataFileFormat#RecordDataFileFormat(basic.SimpleName, basic.VfNotes, fileformat.record.between.BetweenRecordStringFormatBase, fileformat.record.within.WithinRecordAttributeStringFormatBase)}.
	 */
	@Test
	public void testRecordDataFileFormat_HMMER_DOMOUT() {
		SimpleName name = new SimpleName("HMMER_DOMOUT");
		VfNotes notes = new VfNotes("protein search only; \n In protein search programs, the --domtblout option produces the domain hits table. There is one line for each domain. There may be more than one domain per sequence. The domain table has 22 whitespacedelimited fields followed by a free text target sequence description.\n Version 3.1b2; \n see http://hmmer.org/");
		
		////
		SequentialBetweenRecordStringFormatTest sequentialBetweenRecordStringFormatTest = new SequentialBetweenRecordStringFormatTest();
		sequentialBetweenRecordStringFormatTest.testSequentialBetweenRecordStringFormat_hmm_DOMOUT();
		BetweenRecordStringFormatBase betweenRecordStringFormat = SequentialBetweenRecordStringFormatTest.HMMER_DOMOUT_SequentialBetweenRecordStringFormat;
		
		
		StringDelimitedRecordAttributeStringFormatTest stringDelimitedRecordAttributeStringFormatTest = new StringDelimitedRecordAttributeStringFormatTest();
		stringDelimitedRecordAttributeStringFormatTest.testStringDelimitedRecordAttributeStringFormat_hmmer_DOMOUT();
		WithinRecordAttributeStringFormatBase withinRecordAttributeStringFormat = StringDelimitedRecordAttributeStringFormatTest.HMMER_DOMOUT_stringDelimitedRecordAttributeStringFormat;
		
		HMMER_DOMOUT_RecordDataFileFormat = new RecordDataFileFormat(
				name,notes,
				betweenRecordStringFormat,
				withinRecordAttributeStringFormat
				);
		
		SerializationUtils.serializeToFile(Paths.get("C:\\Users\\tanxu\\Desktop\\Visframe_testing_data\\record\\hmm\\domout\\HMMER_DOMOUT_template.VFF"), HMMER_DOMOUT_RecordDataFileFormat);
	}
}
