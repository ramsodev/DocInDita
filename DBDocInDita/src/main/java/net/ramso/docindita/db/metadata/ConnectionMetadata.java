package net.ramso.docindita.db.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class ConnectionMetadata {

	private Connection connection;

	public ConnectionMetadata(Connection connection) {
		super();
		this.connection = connection;

	}

	public SchemaMetadata getSchema() throws SQLException  {
		return getSchema(connection.getSchema());
	}

	public SchemaMetadata getSchema(String name) throws SQLException {
		SchemaMetadata schema = null;
		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet rs = metadata.getSchemas(null, name);
		if (rs.next()) {
			schema = new SchemaMetadata(rs, metadata);
		}
		return schema;
	}

	public Collection<CatalogMetadata> getCatalogs() throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();
		Collection<CatalogMetadata> catalogs = new ArrayList<>();
		ResultSet rs = metadata.getCatalogs();
		while (rs.next()) {
			catalogs.add(new CatalogMetadata(rs, metadata));
		}
		return catalogs;
	}

	public Collection<SchemaMetadata> getSchemas() throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();
		Collection<SchemaMetadata> schemas = new ArrayList<>();
		ResultSet rs = metadata.getSchemas();
		while (rs.next()) {
			schemas.add(new SchemaMetadata(rs, metadata));
		}

		return schemas;

	}
}
