package net.ramso.docindita.db.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Collection;

import net.ramso.tools.BundleManager;
import net.ramso.tools.LogManager;
import net.ramso.tools.TextTools;

public class ConnectionMetadata {

	private Connection connection;

	public ConnectionMetadata(Connection connection) {
		super();
		this.connection = connection;

	}

	public SchemaMetadata getSchema() throws SQLException {
		String schema = "";
		try {
			schema = connection.getSchema();
		} catch (SQLFeatureNotSupportedException e) {
			LogManager.warn("No se puede recuperar esquema por defecto. Se usa el nombre de usuario", e);
		} finally {
			if (schema == null || schema.isEmpty()) {
				schema = connection.getMetaData().getUserName();
			}
		}
		return getSchema(schema);
	}

	public SchemaMetadata getSchema(String name) throws SQLException {
		SchemaMetadata schema = null;
		DatabaseMetaData metadata = connection.getMetaData();

		ResultSet rs = metadata.getSchemas(null, name);
		boolean have = false;
		if (rs.next()) {
			schema = new SchemaMetadata(rs, metadata);
			have = true;
		}
		if (!have) {
			rs = metadata.getCatalogs();
			while (rs.next()) {
				CatalogMetadata catalog = new CatalogMetadata(rs, metadata);
				if (catalog.getName().equalsIgnoreCase(name)) {
					schema = catalog.getSchemas().iterator().next();
				}
			}
		}
		return schema;
	}

	private SchemaMetadata getSchemaFromList(String name) throws SQLException {
		for (SchemaMetadata schema : getSchemas()) {
			if (schema.getName().equalsIgnoreCase(name)) {
				return schema;
			}
		}
		return null;
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

	public String getDescription() throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();
		StringBuilder st = new StringBuilder();
		st.append(BundleManager.getString("Database.title", metadata.getDatabaseProductName(),
				metadata.getDatabaseProductVersion()));
		return st.toString();
	}

	public String getId() throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();
		return TextTools.cleanNonAlfaNumeric(
				metadata.getDatabaseProductName() + "." + metadata.getDatabaseProductVersion(), "_");
	}

	public void disconnect() throws SQLException {
		if (!connection.isClosed())
			connection.close();

	}
}
