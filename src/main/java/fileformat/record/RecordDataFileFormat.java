package fileformat.record;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import basic.SimpleName;
import basic.VfNameString;
import basic.VfNotes;
import exception.VisframeException;
import fileformat.record.between.BetweenRecordStringFormatBase;
import fileformat.record.within.WithinRecordAttributeStringFormatBase;
import importer.AbstractFileFormat;
import metadata.DataType;

/**
 * <p>
 * file format class for all types of file whose data content can be decomposed into single data point, each of which contains a set of attributes that
 * 		every data point should have the same set of attributes whose values cannot be null and collectively uniquely identify a single data point (same as the relational table's primary key);
 * 		besides the primary key attributes, each data point could have its own set of additional attributes;
 * </p>
 * also, there is no strong connection between those data points, otherwise, other types of data structure such as graph is more appropriate to represent it;
 * 
 * two major key structural information are needed to create a specific RecordDataFileFormat;
 * 1. how the data content of distinct data points are organized in the data file;
 * 2. how the attributes and values of a single data point are organized;
 * @author tanxu
 * 
 */
public class RecordDataFileFormat extends AbstractFileFormat {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1516488901621049558L;
	
	
	///////////////////////
	private final BetweenRecordStringFormatBase betweenRecordStringFormat;
	private final WithinRecordAttributeStringFormatBase withinRecordAttributeStringFormat;
	
	/**
	 * constructor
	 * 
	 * validations:
	 * 1. check the consistency of all StringMarkers defined in the betweenRecordStringFormatAndParser and recordAttributeStringFormatAndParser
	 * 
	 * 
	 * 2. if BetweenRecordStringFormatAndParser is InterleavingRecordFormatAndParser type, and everyRecordSegmentHasHeadingIDAttribute() method returns true, 
	 * the default PrimaryKeyAttributeNameSet of the RecordAttributeStringFormatAndParser must only contain the mandatory Simple attribute with index 0;
	 * 
	 * 
	 * @param name
	 * @param notes
	 * @param betweenRecordStringFormatAndParser cannot be null
	 * @param recordAttributeStringFormatAndParser cannot be null
	 */
	public RecordDataFileFormat(
			SimpleName name, VfNotes notes,
			BetweenRecordStringFormatBase betweenRecordStringFormat,
			WithinRecordAttributeStringFormatBase withinRecordAttributeStringFormat
			) {
		super(name, notes);
		
		
		this.betweenRecordStringFormat = betweenRecordStringFormat;
		this.withinRecordAttributeStringFormat = withinRecordAttributeStringFormat;
	}

	/**
	 * returns the BetweenRecordStringFormatAndParser of this RecordDataFileFormat
	 * @return
	 */
	public BetweenRecordStringFormatBase getBetweenRecordStringFormat() {
		return this.betweenRecordStringFormat;
	}
	
	/**
	 * returns the RecordAttributeStringFormatAndParser of this RecordDataFileFormat
	 * @return
	 */
	public WithinRecordAttributeStringFormatBase getWithinRecordAttributeStringFormat() {
		return this.withinRecordAttributeStringFormat;
	}

	
	/**
	 * {@inheritDoc}
	 */
	public DataType getDataType() {
		return DataType.RECORD;
	}
	
	@Override
	public RecordDataFileFormat rename(SimpleName newName) {
//		SimpleName name, VfNotes notes,
//		BetweenRecordStringFormat betweenRecordStringFormat,
//		WithinRecordAttributeStringFormat withinRecordAttributeStringFormat
		
		return new RecordDataFileFormat(newName, this.getNotes(), this.getBetweenRecordStringFormat(), this.getWithinRecordAttributeStringFormat());
	}

	//////////////////////////////////////
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((betweenRecordStringFormat == null) ? 0 : betweenRecordStringFormat.hashCode());
		result = prime * result
				+ ((withinRecordAttributeStringFormat == null) ? 0 : withinRecordAttributeStringFormat.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof RecordDataFileFormat))
			return false;
		RecordDataFileFormat other = (RecordDataFileFormat) obj;
		if (betweenRecordStringFormat == null) {
			if (other.betweenRecordStringFormat != null)
				return false;
		} else if (!betweenRecordStringFormat.equals(other.betweenRecordStringFormat))
			return false;
		if (withinRecordAttributeStringFormat == null) {
			if (other.withinRecordAttributeStringFormat != null)
				return false;
		} else if (!withinRecordAttributeStringFormat.equals(other.withinRecordAttributeStringFormat))
			return false;
		return true;
	}


	/////////////////////////////////////
	/**
	 * container class that contains the full set of attribute names that are to be used to make the primary key in the data table schema for a data source file of a RecordDataFileFormat
	 * @author tanxu
	 */
	public static class PrimaryKeyAttributeNameSet implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 7479486219336082804L;
		
		///////////////////
		private final Set<SimpleName> simpleMandatoryAttributeNameSet;
		private final Set<SimpleName> tagAttributeNameSet;
		
		/////////////////////////
		private transient Set<SimpleName> fullNameSet;
		/**
		 * constructor for PrimaryKeyAttributeNameSet;
		 * <p>Given simpleMandatoryAttributeNameSet and tagAttributeNameSet cannot be both empty;</p>
		 * <p>No duplicate attribute names are allowed in simpleMandatoryAttributeNameSet and tagAttributeNameSet;</p>
		 * 
		 * @param simpleMandatoryAttributeNameSet mandatory SimpleAttributes that are to be included in the primary key of the resulting data table schema; cannot be null; could be empty
		 * @param tagAttributeNameSet cannot be null; could be empty;
		 */
		public PrimaryKeyAttributeNameSet(
				Set<SimpleName> simpleMandatoryAttributeNameSet, 
				Set<SimpleName> tagAttributeNameSet
				) {
			if(simpleMandatoryAttributeNameSet==null || tagAttributeNameSet == null) {
				throw new VisframeException("simpleMandatoryAttributeNameSet and tagAttributeNameSet cannot be null");
			}
			
			if(simpleMandatoryAttributeNameSet.isEmpty() && tagAttributeNameSet.isEmpty()) {
				throw new VisframeException("simpleMandatoryAttributeNameSet and tagAttributeNameSet cannot both be empty");
			}
			
			
			Set<VfNameString> intersection = new HashSet<VfNameString>(simpleMandatoryAttributeNameSet); // use the copy constructor
			intersection.retainAll(tagAttributeNameSet);
			if(! intersection.isEmpty()) {
				throw new VisframeException("duplicate name is found in given simpleMandatoryAttributeNameSet and tagAttributeNameSet");
			}
			
			//
			this.simpleMandatoryAttributeNameSet = simpleMandatoryAttributeNameSet;
			this.tagAttributeNameSet = tagAttributeNameSet;
		}
		
		public Set<SimpleName> getSimpleMandatoryAttributeNameSet(){
			return this.simpleMandatoryAttributeNameSet;
		}
		public Set<SimpleName> getTagAttributeNameSet(){
			return this.tagAttributeNameSet;
		}
		
		
		/**
		 * return the full set of attribute name in this {@link PrimaryKeyAttributeNameSet}
		 * @return
		 */
		public Set<SimpleName> getFullNameSet(){
			if(this.fullNameSet==null) {
				this.fullNameSet = new HashSet<>();
				this.fullNameSet.addAll(this.getSimpleMandatoryAttributeNameSet());
				this.fullNameSet.addAll(this.getTagAttributeNameSet());
			}
			
			return this.fullNameSet;
			
		}

		///////////////////////////////
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((simpleMandatoryAttributeNameSet == null) ? 0 : simpleMandatoryAttributeNameSet.hashCode());
			result = prime * result + ((tagAttributeNameSet == null) ? 0 : tagAttributeNameSet.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof PrimaryKeyAttributeNameSet))
				return false;
			PrimaryKeyAttributeNameSet other = (PrimaryKeyAttributeNameSet) obj;
			if (simpleMandatoryAttributeNameSet == null) {
				if (other.simpleMandatoryAttributeNameSet != null)
					return false;
			} else if (!simpleMandatoryAttributeNameSet.equals(other.simpleMandatoryAttributeNameSet))
				return false;
			if (tagAttributeNameSet == null) {
				if (other.tagAttributeNameSet != null)
					return false;
			} else if (!tagAttributeNameSet.equals(other.tagAttributeNameSet))
				return false;
			return true;
		}
		
	}

}
