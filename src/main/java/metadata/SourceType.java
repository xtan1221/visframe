package metadata;

import importer.DataImporter;
import operation.Operation;

/**
 * types of sources regarding how a Metadata in a VisframeContext is generated
 * @author tanxu
 *
 */
public enum SourceType{
	/**
	 * imported from source data file by an {@link DataImporter}
	 */
	IMPORTED,
	/**
	 * produced by an {@link Operation}
	 */
	RESULT_FROM_OPERATION,//including build from existing component data operation
	/**
	 * derived from a {@link CompositeDataMetadata}; only applicable to {@link RecordDataMetadata};
	 */
	STRUCTURAL_COMPONENT;
}
