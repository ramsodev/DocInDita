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

	private final Connection connection;

	public ConnectionMetadata(Connection connection) {
		super();
		this.connection = connection;

	}

	public SchemaMetadata getSchema() throws SQLException {
		String schema = "";
		try {
			schema = this.connection.getSchema();
		} catch (final SQLFeatureNotSupportedException e) {
			LogManager.warn("No se puede recuperar esquema por defecto. Se usa el nombre de usuario", e);
		} finally {
			if ((schema == null) || schema.isEmpty()) {
				schema = this.connection.getMetaData().getUserName();
			}
		}
		return getSchema(schema);
	}

	public SchemaMetadata getSchema(String name) throws SQLException {
		SchemaMetadata schema = null;
		final DatabaseMetaData metadata = this.connection.getMetaData();

		ResultSet rs = metadata.getSchemas(null, name);
		boolean have = false;
		if (rs.next()) {
			schema = new SchemaMetadata(rs, metadata);
			have = true;
		}
		if (!have) {
			rs = metadata.getCatalogs();
			while (rs.next()) {
				final CatalogMetadata catalog = new CatalogMetadata(rs, metadata);
				if (catalog.getName().equalsIgnoreCase(name)) {
					schema = catalog.getSchemas().iterator().next();
				}
			}
		}
		return schema;
	}

	public Collection<CatalogMetadata> getCatalogs() throws SQLException {
		final DatabaseMetaData metadata = this.connection.getMetaData();
		final Collection<CatalogMetadata> catalogs = new ArrayList<>();
		final ResultSet rs = metadata.getCatalogs();
		while (rs.next()) {
			catalogs.add(new CatalogMetadata(rs, metadata));
		}
		return catalogs;
	}

	public Collection<SchemaMetadata> getSchemas() throws SQLException {
		final DatabaseMetaData metadata = this.connection.getMetaData();
		final Collection<SchemaMetadata> schemas = new ArrayList<>();
		final ResultSet rs = metadata.getSchemas();
		while (rs.next()) {
			schemas.add(new SchemaMetadata(rs, metadata));
		}
		return schemas;
	}

	public String getDescription() throws SQLException {
		final DatabaseMetaData metadata = this.connection.getMetaData();
		final StringBuilder st = new StringBuilder();
		st.append(BundleManager.getString("Database.title", metadata.getDatabaseProductName(),
				metadata.getDatabaseProductVersion()));
		return st.toString();
	}

	public String getId() throws SQLException {
		final DatabaseMetaData metadata = this.connection.getMetaData();
		return TextTools.cleanNonAlfaNumeric(
				metadata.getDatabaseProductName() + "." + metadata.getDatabaseProductVersion(), "_");
	}

	public void disconnect() throws SQLException {
		if (!this.connection.isClosed()) {
			this.connection.close();
		}

	}
}
