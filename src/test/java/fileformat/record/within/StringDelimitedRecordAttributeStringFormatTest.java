/**
 * 
 */
package fileformat.record.within;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import basic.SimpleName;
import basic.VfNotes;
import fileformat.record.RecordDataFileFormat.PrimaryKeyAttributeNameSet;
import fileformat.record.attribute.AbstractRecordAttributeFormat;
import fileformat.record.attribute.CompositeTagRecordAttributeFormatTest;
import fileformat.record.attribute.PrimitiveRecordAttributeFormat;
import fileformat.record.attribute.TagFormat;
import fileformat.record.utils.PlainStringMarker;
import fileformat.record.utils.RegexStringMarker;
import fileformat.record.utils.StringMarker;
import fileformat.record.within.StringDelimitedRecordAttributeStringFormat;
import rdb.sqltype.SQLDataTypeFactory;
import rdb.sqltype.SQLStringType;

/**
 * @author tanxu
 *
 */
public class StringDelimitedRecordAttributeStringFormatTest {
	public static StringDelimitedRecordAttributeStringFormat GFF3_stringDelimitedRecordAttributeStringFormat;
	public static StringDelimitedRecordAttributeStringFormat HMMER_TBLOUT_PROTEIN_stringDelimitedRecordAttributeStringFormat;
	public static StringDelimitedRecordAttributeStringFormat HMMER_DOMOUT_stringDelimitedRecordAttributeStringFormat;
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
	 * Test method for {@link fileformat.record.within.StringDelimitedRecordAttributeStringFormat#StringDelimitedRecordAttributeStringFormat(java.util.List, fileformat.record.RecordDataFileFormat.PrimaryKeyAttributeNameSet, fileformat.record.utils.PlainStringMarker, boolean, fileformat.record.attribute.TagFormat, boolean, fileformat.record.utils.PlainStringMarker, boolean, fileformat.record.utils.StringMarker, java.util.List, boolean)}.
	 */
	@Test
	public void testStringDelimitedRecordAttributeStringFormat_GFF3() {
		///
		List<AbstractRecordAttributeFormat> orderedListOfMandatoryAttribute = new ArrayList<>();
		PrimitiveRecordAttributeFormat seqidAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("seqid"), VfNotes.makeVisframeDefinedVfNotes(), new SQLStringType(256,false));
		PrimitiveRecordAttributeFormat sourceAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("source"), VfNotes.makeVisframeDefinedVfNotes(), new SQLStringType(256,false));
		PrimitiveRecordAttributeFormat typeAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("type"), VfNotes.makeVisframeDefinedVfNotes(), new SQLStringType(256,false));
		PrimitiveRecordAttributeFormat startAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("start"), VfNotes.makeVisframeDefinedVfNotes(), SQLDataTypeFactory.integerType());
		PrimitiveRecordAttributeFormat endAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("end"), VfNotes.makeVisframeDefinedVfNotes(), SQLDataTypeFactory.integerType());
		PrimitiveRecordAttributeFormat scoreAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("score"), VfNotes.makeVisframeDefinedVfNotes(), SQLDataTypeFactory.doubleType());
		PrimitiveRecordAttributeFormat strandAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("strand"), VfNotes.makeVisframeDefinedVfNotes(), new SQLStringType(1,false));
		PrimitiveRecordAttributeFormat phaseAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("phase"), VfNotes.makeVisframeDefinedVfNotes(), SQLDataTypeFactory.shortIntegerType());
		
		
		///
		CompositeTagRecordAttributeFormatTest compositeTagRecordAttributeFormatTest = new CompositeTagRecordAttributeFormatTest();
		compositeTagRecordAttributeFormatTest.testCompositeTagRecordAttributeFormat();
		orderedListOfMandatoryAttribute.add(seqidAttribute);
		orderedListOfMandatoryAttribute.add(sourceAttribute);
		orderedListOfMandatoryAttribute.add(typeAttribute);
		orderedListOfMandatoryAttribute.add(startAttribute);
		orderedListOfMandatoryAttribute.add(endAttribute);
		orderedListOfMandatoryAttribute.add(scoreAttribute);
		orderedListOfMandatoryAttribute.add(strandAttribute);
		orderedListOfMandatoryAttribute.add(phaseAttribute);
		orderedListOfMandatoryAttribute.add(CompositeTagRecordAttributeFormatTest.GFF3_ATTRIBUTES_ATTRIBUTE_CompositeTagRecordAttributeFormat);
		
		
		///
		Set<SimpleName> simpleMandatoryAttributeNameSet = new HashSet<>();
		Set<SimpleName> tagAttributeNameSet = new HashSet<>();
		tagAttributeNameSet.add(new SimpleName("ID"));
		PrimaryKeyAttributeNameSet defaultPrimaryKeyAttributeNameSet = new PrimaryKeyAttributeNameSet(simpleMandatoryAttributeNameSet, tagAttributeNameSet);
		
		
		/////////////
		PlainStringMarker nullValueMandatoryAttributeString = new PlainStringMarker(".",false);
		boolean hasTailingTagAttributes = false;
		TagFormat tailingTagAttributesFormatAndParser = null;
		boolean toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute = false;
		PlainStringMarker concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute = null;
		///////
		boolean hasSingleMandatoryAttributeDelimiter = true;
		StringMarker singleMandatoryAttributeDelimiter = new RegexStringMarker("\\s+",false);
		List<StringMarker> mandatoryAttributeDelimiterList = null;
		boolean recordStringStartingWithMandatoryAttributeDelimiter = false;
		
		GFF3_stringDelimitedRecordAttributeStringFormat = new StringDelimitedRecordAttributeStringFormat(
				orderedListOfMandatoryAttribute, defaultPrimaryKeyAttributeNameSet,
				nullValueMandatoryAttributeString, hasTailingTagAttributes, tailingTagAttributesFormatAndParser,
				toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute,
				concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute,
				
				hasSingleMandatoryAttributeDelimiter,
				singleMandatoryAttributeDelimiter,
				mandatoryAttributeDelimiterList,
				recordStringStartingWithMandatoryAttributeDelimiter
				);
	}
	
	/**
	 * Test method for {@link fileformat.record.within.StringDelimitedRecordAttributeStringFormat#StringDelimitedRecordAttributeStringFormat(java.util.List, fileformat.record.RecordDataFileFormat.PrimaryKeyAttributeNameSet, fileformat.record.utils.PlainStringMarker, boolean, fileformat.record.attribute.TagFormat, boolean, fileformat.record.utils.PlainStringMarker, boolean, fileformat.record.utils.StringMarker, java.util.List, boolean)}.
	 */
	@Test
	public void testStringDelimitedRecordAttributeStringFormat_hmmer_tblout_protein() {
		///
		List<AbstractRecordAttributeFormat> orderedListOfMandatoryAttribute = new ArrayList<>();
		//1
		PrimitiveRecordAttributeFormat targetNameAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("target_name"), 
				new VfNotes("The name of the target sequence or profile."), new SQLStringType(50,false));
		orderedListOfMandatoryAttribute.add(targetNameAttribute);
		//2
		PrimitiveRecordAttributeFormat targetAccessionAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("target_accession"), 
				new VfNotes("The accession of the target sequence or profile, or ’-’ if none."), 
				new SQLStringType(50,false));
		orderedListOfMandatoryAttribute.add(targetAccessionAttribute);
		//3
		PrimitiveRecordAttributeFormat queryNameAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("query_name"), new VfNotes("The name of the query sequence or profile."), 
				new SQLStringType(50,false));
		orderedListOfMandatoryAttribute.add(queryNameAttribute);
		//4
		PrimitiveRecordAttributeFormat queryAccessionAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("query_accession"), new VfNotes("The accession of the query sequence or profile, or ’-’ if none"), 
				new SQLStringType(50,false));
		orderedListOfMandatoryAttribute.add(queryAccessionAttribute);
		//5
		PrimitiveRecordAttributeFormat eValueFullSeqAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("E_value_full_seq"), 
				new VfNotes("The expectation value (statistical significance) of the target. This is a per query E-value; i.e. calculated as the expected number of false positives achieving this comparison’s score for a single query against the Z sequences in the target dataset. If you search with multiple queries and if you want to control the overall false positive rate of that search rather than the false positive rate per query, you will want to multiply this per-query E-value by how many queries you’re doing."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(eValueFullSeqAttribute);
		//6
		PrimitiveRecordAttributeFormat scoreFullSeqAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("score_full_seq"), 
				new VfNotes("The score (in bits) for this target/query comparison. It includes the biasedcomposition correction (the “null2” model)."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(scoreFullSeqAttribute);
		//7
		PrimitiveRecordAttributeFormat biasFullSeqAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("bias_full_seq"), 
				new VfNotes("The biased-composition correction: the bit score difference contributed by the null2 model. High bias scores may be a red flag for a false positive, especially when the bias score is as large or larger than the overall bit score. It is difficult to correct for all possible ways in which a nonrandom but nonhomologous biological sequences can appear to be similar, such as short-period tandem repeats, so there are cases where the bias correction is not strong enough (creating false positives)."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(biasFullSeqAttribute);
		//8
		PrimitiveRecordAttributeFormat eValueBest1DomainAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("E_value_best_1_domain"), 
				new VfNotes("The E-value if only the single best-scoring domain envelope were found in the sequence, and none of the others. If this E-value isn’t good, but the full sequence E-value is good, this is a potential red flag. Weak hits, none of which are good enough on their own, are summing up to lift the sequence up to a high score. Whether this is Good or Bad is not clear; the sequence may contain several weak homologous domains, or it might contain a repetitive sequence that is hitting by chance (i.e. once one repeat hits, all the repeats hit)."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(eValueBest1DomainAttribute);
		//9
		PrimitiveRecordAttributeFormat scoreBest1DomainAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("score_best_1_domain"), 
				new VfNotes("The bit score if only the single best-scoring domain envelope were found in the sequence, and none of the others. (Inclusive of the null2 bias correction.]"), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(scoreBest1DomainAttribute);
		//10
		PrimitiveRecordAttributeFormat biasBest1DomainAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("bias_best_1_domain"), 
				new VfNotes("The null2 bias correction that was applied to the bit score of the single best-scoring domain."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(biasBest1DomainAttribute);
		//11
		PrimitiveRecordAttributeFormat expAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("exp"), 
				new VfNotes("Expected number of domains, as calculated by posterior decoding on the mean number of begin states used in the alignment ensemble."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(expAttribute);
		//12
		PrimitiveRecordAttributeFormat regAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("reg"), 
				new VfNotes("Number of discrete regions defined, as calculated by heuristics applied to posterior decoding of begin/end state positions in the alignment ensemble. The number of regions will generally be close to the expected number of domains. The more different the two numbers are, the less discrete the regions appear to be, in terms of probability mass. This usually means one of two things. On the one hand, weak homologous domains may be difficult for the heuristics to identify clearly. On the other hand, repetitive sequence may appear to have a high expected domain number (from lots of crappy possible alignments in the ensemble, no one of which is very convincing on its own, so no one region is discretely well-defined)."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(regAttribute);
		//13
		PrimitiveRecordAttributeFormat cluAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("clu"), 
				new VfNotes("Number of regions that appeared to be multidomain, and therefore were passed to stochastic traceback clustering for further resolution down to one or more envelopes. This number is often zero."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(cluAttribute);
		//14
		PrimitiveRecordAttributeFormat ovAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("ov"), 
				new VfNotes("For envelopes that were defined by stochastic traceback clustering, how many of them overlap other envelopes."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(ovAttribute);
		//15
		PrimitiveRecordAttributeFormat envAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("env"), 
				new VfNotes("The total number of envelopes defined, both by single envelope regions and by stochastic traceback clustering into one or more envelopes per region."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(envAttribute);
		//16
		PrimitiveRecordAttributeFormat domAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("dom"), 
				new VfNotes("Number of domains defined. In general, this is the same as the number of envelopes: for each envelope, we find an MEA (maximum expected accuracy) alignment, which defines the endpoints of the alignable domain."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(domAttribute);
		//17
		PrimitiveRecordAttributeFormat repAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("rep"), 
				new VfNotes("Number of domains satisfying reporting thresholds. If you’ve also saved a --domtblout file, there will be one line in it for each reported domain."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(repAttribute);
		//18
		PrimitiveRecordAttributeFormat incAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("inc"), 
				new VfNotes("Number of domains satisfying inclusion thresholds."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(incAttribute);
		//19
		PrimitiveRecordAttributeFormat descriptionAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("description_of_target"), 
				new VfNotes("The remainder of the line is the target’s description line, as free text."), 
				new SQLStringType(200,false));
		orderedListOfMandatoryAttribute.add(descriptionAttribute);
		
		
		
		///
		Set<SimpleName> simpleMandatoryAttributeNameSet = new HashSet<>();
		simpleMandatoryAttributeNameSet.add(targetNameAttribute.getName());
		simpleMandatoryAttributeNameSet.add(queryNameAttribute.getName());
		Set<SimpleName> tagAttributeNameSet = new HashSet<>();
		PrimaryKeyAttributeNameSet defaultPrimaryKeyAttributeNameSet = new PrimaryKeyAttributeNameSet(simpleMandatoryAttributeNameSet, tagAttributeNameSet);
		
		
		/////////////
		PlainStringMarker nullValueMandatoryAttributeString = new PlainStringMarker("-",false);
		boolean hasTailingTagAttributes = false;
		TagFormat tailingTagAttributesFormatAndParser = null;
		boolean toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute = true;
		PlainStringMarker concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute = new PlainStringMarker(" ",false);;
		
		
		///////
		boolean hasSingleMandatoryAttributeDelimiter = true;
		StringMarker singleMandatoryAttributeDelimiter = new RegexStringMarker("\\s+",false);
		List<StringMarker> mandatoryAttributeDelimiterList = null;
		boolean recordStringStartingWithMandatoryAttributeDelimiter = false;
		
		HMMER_TBLOUT_PROTEIN_stringDelimitedRecordAttributeStringFormat = new StringDelimitedRecordAttributeStringFormat(
				orderedListOfMandatoryAttribute, defaultPrimaryKeyAttributeNameSet,
				nullValueMandatoryAttributeString, hasTailingTagAttributes, tailingTagAttributesFormatAndParser,
				toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute,
				concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute,
				
				hasSingleMandatoryAttributeDelimiter,
				singleMandatoryAttributeDelimiter,
				mandatoryAttributeDelimiterList,
				recordStringStartingWithMandatoryAttributeDelimiter
				);
	}

	/**
	 * Test method for {@link fileformat.record.within.StringDelimitedRecordAttributeStringFormat#StringDelimitedRecordAttributeStringFormat(java.util.List, fileformat.record.RecordDataFileFormat.PrimaryKeyAttributeNameSet, fileformat.record.utils.PlainStringMarker, boolean, fileformat.record.attribute.TagFormat, boolean, fileformat.record.utils.PlainStringMarker, boolean, fileformat.record.utils.StringMarker, java.util.List, boolean)}.
	 */
	@Test
	public void testStringDelimitedRecordAttributeStringFormat_hmmer_DOMOUT() {
		///
		List<AbstractRecordAttributeFormat> orderedListOfMandatoryAttribute = new ArrayList<>();
		//1
		PrimitiveRecordAttributeFormat targetNameAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("target_name"), 
				new VfNotes("The name of the target sequence or profile."), 
				new SQLStringType(50,false));
		orderedListOfMandatoryAttribute.add(targetNameAttribute);
		//2
		PrimitiveRecordAttributeFormat targetAccessionAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("target_accession"), 
				new VfNotes("The accession of the target sequence or profile, or ’-’ if none."), 
				new SQLStringType(50,false));
		orderedListOfMandatoryAttribute.add(targetAccessionAttribute);
		//3
		PrimitiveRecordAttributeFormat tlenAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("tlen"), new VfNotes("Length of the target sequence or profile, in residues. This (together with the query length) is useful for interpreting where the domain coordinates (in subsequent columns) lie in the sequence."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(tlenAttribute);
		//4
		PrimitiveRecordAttributeFormat queryNameAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("query_name"), new VfNotes("The name of the query sequence or profile."), 
				new SQLStringType(50,false));
		orderedListOfMandatoryAttribute.add(queryNameAttribute);
		//5
		PrimitiveRecordAttributeFormat queryAccessionAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("query_accession"), new VfNotes("The accession of the query sequence or profile, or ’-’ if none"), 
				new SQLStringType(50,false));
		orderedListOfMandatoryAttribute.add(queryAccessionAttribute);
		//6
		PrimitiveRecordAttributeFormat qlenAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("qlen"), 
				new VfNotes("Length of the query sequence or profile, in residues"), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(qlenAttribute);
				
		//7
		PrimitiveRecordAttributeFormat eValueAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("E_value_full_seq"), 
				new VfNotes("E-value of the overall sequence/profile comparison (including all domains)."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(eValueAttribute);
		//8
		PrimitiveRecordAttributeFormat scoreFullSeqAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("score_full_seq"), 
				new VfNotes("Bit score of the overall sequence/profile comparison (including all domains), inclusive of a null2 bias composition correction to the score."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(scoreFullSeqAttribute);
		//9
		PrimitiveRecordAttributeFormat biasFullSeqAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("bias_full_seq"), 
				new VfNotes("The biased composition score correction that was applied to the bit score."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(biasFullSeqAttribute);
		
		//10
		PrimitiveRecordAttributeFormat domainNumAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("domain_num"), 
				new VfNotes("This domain’s number (1..ndom)."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(domainNumAttribute);
		
		//11
		PrimitiveRecordAttributeFormat ofAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("of"), 
				new VfNotes("The total number of domains reported in the sequence, ndom."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(ofAttribute);
		//12
		PrimitiveRecordAttributeFormat cEvalueAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("c_E_value"), 
				new VfNotes("The “conditional E-value”, a permissive measure of how reliable this particular domain may be. The conditional E-value is calculated on a smaller search space than the independent Evalue. The conditional E-value uses the number of targets that pass the reporting thresholds. The null hypothesis test posed by the conditional E-value is as follows. Suppose that we believe that there is already sufficient evidence (from other domains) to identify the set of reported sequences as homologs of our query; now, how many additional domains would we expect to find with at least this particular domain’s bit score, if the rest of those reported sequences were random nonhomologous sequence (i.e. outside the other domain(s) that were sufficient to identified them as homologs in the first place)?"), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(cEvalueAttribute);
		//13
		PrimitiveRecordAttributeFormat iEValueAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("i_E_value"), 
				new VfNotes("The “independent E-value”, the E-value that the sequence/profile comparison would have received if this were the only domain envelope found in it, excluding any others. This is a stringent measure of how reliable this particular domain may be. The independent E-value uses the total number of targets in the target database."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(iEValueAttribute);
		
		//14
		PrimitiveRecordAttributeFormat scoreAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("score"), 
				new VfNotes("The bit score for this domain."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(scoreAttribute);
		//15
		PrimitiveRecordAttributeFormat biasAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("bias"), 
				new VfNotes("The biased composition (null2) score correction that was applied to the domain bit score."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(biasAttribute);
		
		//16
		PrimitiveRecordAttributeFormat fromHmmCoordAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("from_hmm_coord"), 
				new VfNotes("The start of the MEA alignment of this domain with respect to the profile, numbered 1..N for a profile of N consensus positions."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(fromHmmCoordAttribute);
		//17
		PrimitiveRecordAttributeFormat toHmmCoordAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("to_hmm_coord"), 
				new VfNotes("The end of the MEA alignment of this domain with respect to the profile, numbered 1..N for a profile of N consensus positions."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(toHmmCoordAttribute);
		
		//18
		PrimitiveRecordAttributeFormat fromAliCoordAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("from_ali_coord"), 
				new VfNotes("The start of the MEA alignment of this domain with respect to the sequence, numbered 1..L for a sequence of L residues."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(fromAliCoordAttribute);
		//19
		PrimitiveRecordAttributeFormat toAliCoordAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("to_ali_coord"), 
				new VfNotes("The end of the MEA alignment of this domain with respect to the sequence, numbered 1..L for a sequence of L residues."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(toAliCoordAttribute);
		
		//20
		PrimitiveRecordAttributeFormat fromEnvCoordAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("from_env_coord"), 
				new VfNotes("The start of the domain envelope on the sequence, numbered 1..L for a sequence of L residues. The envelope defines a subsequence for which their is substantial probability mass supporting a homologous domain, whether or not a single discrete alignment can be identified. The envelope may extend beyond the endpoints of the MEA alignment, and in fact often does, for weakly scoring domains."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(fromEnvCoordAttribute);
		//21
		PrimitiveRecordAttributeFormat toEnvCoordAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("to_env_coord"), 
				new VfNotes("The end of the domain envelope on the sequence, numbered 1..L for a sequence of L residues."), 
				SQLDataTypeFactory.shortIntegerType());
		orderedListOfMandatoryAttribute.add(toEnvCoordAttribute);
				
		//22
		PrimitiveRecordAttributeFormat accAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("acc"), 
				new VfNotes("The mean posterior probability of aligned residues in the MEA alignment; a measure of how reliable the overall alignment is (from 0 to 1, with 1.00 indicating a completely reliable alignment according to the model)."), 
				SQLDataTypeFactory.doubleType());
		orderedListOfMandatoryAttribute.add(accAttribute);
		
		
		//23
		PrimitiveRecordAttributeFormat descriptionAttribute = new PrimitiveRecordAttributeFormat(
				new SimpleName("description_of_target"), 
				new VfNotes("The remainder of the line is the target’s description line, as free text."), 
				new SQLStringType(200,false));
		orderedListOfMandatoryAttribute.add(descriptionAttribute);
		
		
		///
		Set<SimpleName> simpleMandatoryAttributeNameSet = new HashSet<>();
		simpleMandatoryAttributeNameSet.add(targetNameAttribute.getName());
		simpleMandatoryAttributeNameSet.add(queryNameAttribute.getName());
		simpleMandatoryAttributeNameSet.add(domainNumAttribute.getName());
		Set<SimpleName> tagAttributeNameSet = new HashSet<>();
		PrimaryKeyAttributeNameSet defaultPrimaryKeyAttributeNameSet = new PrimaryKeyAttributeNameSet(simpleMandatoryAttributeNameSet, tagAttributeNameSet);
		
		
		/////////////
		PlainStringMarker nullValueMandatoryAttributeString = new PlainStringMarker("-",false);
		boolean hasTailingTagAttributes = false;
		TagFormat tailingTagAttributesFormatAndParser = null;
		boolean toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute = true;
		PlainStringMarker concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute = new PlainStringMarker(" ",false);;
		
		
		///////
		boolean hasSingleMandatoryAttributeDelimiter = true;
		StringMarker singleMandatoryAttributeDelimiter = new RegexStringMarker("\\s+",false);
		List<StringMarker> mandatoryAttributeDelimiterList = null;
		boolean recordStringStartingWithMandatoryAttributeDelimiter = false;
		
		HMMER_DOMOUT_stringDelimitedRecordAttributeStringFormat = new StringDelimitedRecordAttributeStringFormat(
				orderedListOfMandatoryAttribute, defaultPrimaryKeyAttributeNameSet,
				nullValueMandatoryAttributeString, hasTailingTagAttributes, tailingTagAttributesFormatAndParser,
				toMergeTailingExtraDelimitedStringsToLastMandatoryAttribute,
				concatenatingStringToMergeTailingDelimitedStringToLastMandatoryAttribute,
				
				hasSingleMandatoryAttributeDelimiter,
				singleMandatoryAttributeDelimiter,
				mandatoryAttributeDelimiterList,
				recordStringStartingWithMandatoryAttributeDelimiter
				);
	}
}
