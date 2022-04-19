/**
 * 
 */
package fileformat.record.between;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fileformat.record.between.SequentialBetweenRecordStringFormat;
import fileformat.record.utils.PlainStringMarker;
import fileformat.record.utils.RegexStringMarker;
import fileformat.record.utils.StringMarker;

/**
 * @author tanxu
 *
 */
public class SequentialBetweenRecordStringFormatTest {
	public static SequentialBetweenRecordStringFormat GFF3_SequentialBetweenRecordStringFormat;
	public static SequentialBetweenRecordStringFormat HMMER_TBLOUT_PROTEIN_SequentialBetweenRecordStringFormat;
	public static SequentialBetweenRecordStringFormat HMMER_DOMOUT_SequentialBetweenRecordStringFormat;
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
	 * Test method for {@link fileformat.record.between.SequentialBetweenRecordStringFormat#SequentialBetweenRecordStringFormat(int, fileformat.record.utils.PlainStringMarker, fileformat.record.utils.StringMarker, boolean)}.
	 */
	@Test
	public void testSequentialBetweenRecordStringFormat_gff3() {
		int numberOfHeadingLinesToSkip = 0;
		PlainStringMarker commentStringMarker = new PlainStringMarker("#",false);
		StringMarker recordDelimiter = new RegexStringMarker("\n",false);
		boolean toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord = false;
		
		GFF3_SequentialBetweenRecordStringFormat = new SequentialBetweenRecordStringFormat(
				numberOfHeadingLinesToSkip,commentStringMarker,
				recordDelimiter,toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord
				);
		
	}
	
	/**
	 * Test method for {@link fileformat.record.between.SequentialBetweenRecordStringFormat#SequentialBetweenRecordStringFormat(int, fileformat.record.utils.PlainStringMarker, fileformat.record.utils.StringMarker, boolean)}.
	 */
	@Test
	public void testSequentialBetweenRecordStringFormat_hmm_tblout_protein() {
		int numberOfHeadingLinesToSkip = 0;
		PlainStringMarker commentStringMarker = new PlainStringMarker("#",false);
		StringMarker recordDelimiter = new RegexStringMarker("\\n",false);
		boolean toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord = false;
		
		HMMER_TBLOUT_PROTEIN_SequentialBetweenRecordStringFormat = new SequentialBetweenRecordStringFormat(
				numberOfHeadingLinesToSkip,commentStringMarker,
				recordDelimiter,toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord
				);
		
	}
	
	/**
	 * Test method for {@link fileformat.record.between.SequentialBetweenRecordStringFormat#SequentialBetweenRecordStringFormat(int, fileformat.record.utils.PlainStringMarker, fileformat.record.utils.StringMarker, boolean)}.
	 */
	@Test
	public void testSequentialBetweenRecordStringFormat_hmm_DOMOUT() {
		int numberOfHeadingLinesToSkip = 0;
		PlainStringMarker commentStringMarker = new PlainStringMarker("#",false);
		StringMarker recordDelimiter = new RegexStringMarker("\\n",false);
		boolean toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord = false;
		
		HMMER_DOMOUT_SequentialBetweenRecordStringFormat = new SequentialBetweenRecordStringFormat(
				numberOfHeadingLinesToSkip,commentStringMarker,
				recordDelimiter,toKeepNewLineCharactersBetweenStringPiecesOfTheSameRecord
				);
		
	}

}
