package net.ramso.docindita.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.ramso.docindita.db.metadata.SchemaMetadata;

public class MetadaManager {

	private Connection connection;
	private String catalog;
	

	public MetadaManager(Connection connection) {
		super();
		this.connection = connection;

	}

	public DatabaseMetaData getMetadata() throws SQLException {
		setCatalog(getConnection().getCatalog());
		return getConnection().getMetaData();
	}

	public String[] getSchemas() throws SQLException {
		ResultSet rs = getMetadata().getSchemas();
		ArrayList<String> schemas = new ArrayList<String>();
		while (rs.next()) {
			String c = rs.getString("CATALOG_NAME");
			if (c == null || c.equalsIgnoreCase(getCatalog())) {
				schemas.add(rs.getString("SCHEMA_NAME"));
			}
		}
		return schemas.toArray(new String[schemas.size()]);
	}

	public SchemaMetadata getSchema(String schema) throws SQLException {
		return new SchemaMetadata(schema,getCatalog(),null,getMetadata());
	}
	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @return the catalog
	 */
	public String getCatalog() {
		return catalog;
	}

	/**
	 * @param catalog the catalog to set
	 */
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
}
