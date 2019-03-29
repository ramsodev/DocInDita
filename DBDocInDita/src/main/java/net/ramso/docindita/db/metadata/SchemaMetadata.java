/**
 * 
 */
package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

/**
 * @author ramso
 *
 */
public class SchemaMetadata extends AbstractMetadata {
	public SchemaMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	public SchemaMetadata(CatalogMetadata catalog) {
		super(catalog.getMetadata());
		setCatalog(catalog.getName());
		setName(catalog.getName());
	}

	private Collection<TableMetadata> tables = new ArrayList<>();

	@Override
	public void init(ResultSet resultSet) {
		try {
			setName(resultSet.getString(DBConstants.METADATA_SCHEMA));
			setCatalog(resultSet.getString(DBConstants.METADATA_CATALOG));
			setSchema(getName());
		} catch (SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	public Collection<TableMetadata> getTables() throws SQLException {
		ResultSet rs = getMetadata().getTables(getCatalog(), getSchema(), null, new String[] { DBConstants.TABLE });
		while (rs.next()) {
			TableMetadata tm = new TableMetadata(rs, getMetadata());
			tm.getColumns();
			tables.add(tm);
		}
		return tables;

	}

	@Override
	public String toString() {
		return getCatalog() + "." + getSchema();
	}

	@Override
	public String getDDL() {
		// TODO Auto-generated method stub
		return null;
	}

}
