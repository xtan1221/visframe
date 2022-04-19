package basic.lookup.scheme.type;

import java.util.Map;

import basic.lookup.scheme.VisSchemeLookup;
import metadata.Metadata;
import metadata.MetadataID;


/**
 * 
 * @author tanxu
 *
 */
public class VisSchemeMetadataLookup implements VisSchemeLookup<Metadata,MetadataID>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5576787198368236578L;
	
	///////////////
	private final Map<MetadataID, Metadata> metadataIDMap;
	
	/**
	 * constructor
	 * @param metadataIDMap
	 */
	public VisSchemeMetadataLookup(Map<MetadataID, Metadata> metadataIDMap){
		this.metadataIDMap = metadataIDMap;
	}
	
	
	@Override
	public Map<MetadataID, Metadata> getMap() {
		return metadataIDMap;
	}
	
}
