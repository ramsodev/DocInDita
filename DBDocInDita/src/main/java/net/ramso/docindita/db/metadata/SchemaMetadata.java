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
		ResultSet rs = getMetadata().getTables(getCatalog(), getName(), null, null);
		while (rs.next()) {
			TableMetadata tm = new TableMetadata(rs, getMetadata());
			tm.getColumns();
			tables.add(tm);
		}
		return tables;

	}
	
	@Override
	public String toString() {
		return getCatalog()+"."+getSchema();
	}

}
