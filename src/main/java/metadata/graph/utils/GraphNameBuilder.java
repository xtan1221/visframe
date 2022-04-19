package metadata.graph.utils;

import java.sql.SQLException;

import context.project.VisProjectDBContext;
import metadata.DataType;
import metadata.MetadataName;
import rdb.table.data.DataTableName;

/**
 * build the vertex and edge record data name as well as data table names for a {@link DataType#GRAPH} or {@link DataType#vfTREE} data in a host {@link VisProjectDBContext}
 * 
 * facilitate graph/vftree data importing and operation that result in a new graph/vftree data in a host {@link VisProjectDBContext};
 * 
 * @author tanxu
 *
 */
public class GraphNameBuilder {
	private final VisProjectDBContext hostVisProjectDBContext;
	private final MetadataName graphMetadataName;
	
	
	////////////////////
	private MetadataName vertexRecordMetadataName;
	private MetadataName edgeRecordMetadataName;
	
	private DataTableName vertexDataTableName;
	private DataTableName edgeDataTableName;
	
	/**
	 * constructor
	 * @param hostVisProjectDBContext
	 * @param graphMetadataName
	 * @throws SQLException
	 */
	public GraphNameBuilder(VisProjectDBContext hostVisProjectDBContext, MetadataName graphMetadataName) throws SQLException {
		this.hostVisProjectDBContext = hostVisProjectDBContext;
		this.graphMetadataName = graphMetadataName;
		this.build();
	}
	
	private void build() throws SQLException {
		this.vertexRecordMetadataName = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().findNextAvailableMetadataName(
						new MetadataName(this.getGraphMetadataName().getStringValue().concat("_NODE")), 
						DataType.RECORD);
		this.edgeRecordMetadataName = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().getMetadataManager().findNextAvailableMetadataName(
						new MetadataName(this.getGraphMetadataName().getStringValue().concat("_EDGE")), 
						DataType.RECORD);
		
		this.vertexDataTableName = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().findNextAvailableName(
						new DataTableName(this.getVertexRecordMetadataName().getStringValue()));
		
		this.edgeDataTableName = 
				this.getHostVisProjectDBContext().getHasIDTypeManagerController().getDataTableSchemaManager().findNextAvailableName(
						new DataTableName(this.getEdgeRecordMetadataName().getStringValue()));
	}
	
	/**
	 * @return the hostVisProjectDBContext
	 */
	VisProjectDBContext getHostVisProjectDBContext() {
		return hostVisProjectDBContext;
	}
	
	/**
	 * @return the graphMetadataName
	 */
	MetadataName getGraphMetadataName() {
		return graphMetadataName;
	}
	
	/**
	 * @return the vertexRecordMetadataName
	 */
	public MetadataName getVertexRecordMetadataName() {
		return vertexRecordMetadataName;
	}

	/**
	 * @return the edgeRecordMetadataName
	 */
	public MetadataName getEdgeRecordMetadataName() {
		return edgeRecordMetadataName;
	}

	/**
	 * @return the vertexDataTableName
	 */
	public DataTableName getVertexDataTableName() {
		return vertexDataTableName;
	}

	/**
	 * @return the edgeDataTableName
	 */
	public DataTableName getEdgeDataTableName() {
		return edgeDataTableName;
	}
	
	
	
}
