package dependency.dos;

import java.io.Serializable;

import metadata.MetadataID;

public interface DOSNode extends Serializable{
	MetadataID getMetadataID();
}
