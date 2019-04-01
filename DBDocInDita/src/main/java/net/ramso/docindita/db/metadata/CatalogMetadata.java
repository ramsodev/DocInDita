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
public class CatalogMetadata extends AbstractMetadata {
	Collection<SchemaMetadata> schemas;

	public CatalogMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			setCatalog(resultSet.getString(DBConstants.METADATA_TABLE_CATALOG));
			setName(getCatalog());
		} catch (final SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}
	}

	public Collection<SchemaMetadata> getSchemas() throws SQLException {
		if (this.schemas == null) {
			this.schemas = new ArrayList<>();
			final ResultSet rs = getMetadata().getSchemas(getCatalog(), null);
			while (rs.next()) {
				this.schemas.add(new SchemaMetadata(rs, getMetadata()));
			}
		}
		if (this.schemas.isEmpty()) {
			this.schemas.add(new SchemaMetadata(this));
		}
		return this.schemas;

	}

	public SchemaMetadata getSchema(String name) throws SQLException {
		SchemaMetadata schema = null;
		final ResultSet rs = getMetadata().getSchemas(getCatalog(), name);
		if (rs.first()) {
			schema = new SchemaMetadata(rs, getMetadata());
		}
		return schema;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getDDL() {
		// TODO Auto-generated method stub
		return null;
	}

}
