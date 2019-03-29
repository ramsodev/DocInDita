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
		} catch (SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}
	}

	public Collection<SchemaMetadata> getSchemas() throws SQLException {
		if (schemas == null) {
			schemas = new ArrayList<>();
			ResultSet rs = getMetadata().getSchemas(getCatalog(), null);
			while (rs.next()) {
				schemas.add(new SchemaMetadata(rs, getMetadata()));
			}
		}
		if(schemas.isEmpty()) {
			schemas.add(new SchemaMetadata(this));
		}
		return schemas;

	}

	

	public SchemaMetadata getSchema(String name) throws SQLException {
		SchemaMetadata schema = null;
		ResultSet rs = getMetadata().getSchemas(getCatalog(), name);
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
