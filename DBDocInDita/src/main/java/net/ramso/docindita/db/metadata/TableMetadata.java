/**
 * 
 */
package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

/**
 * @author ramso
 *
 */
public class TableMetadata extends AbstractMetadata {
	private String type;
	private Collection<ColumnsMetadata> columns;

	public TableMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			setSchema(resultSet.getString(DBConstants.METADATA_SCHEMA));
			setCatalog(resultSet.getString(DBConstants.METADATA_TABLE_CATALOG));
			setName(resultSet.getString(DBConstants.METADATA_TABLE));
			setType(resultSet.getString(DBConstants.METADATA_TABLE_TYPE));
			setDoc(resultSet.getString(DBConstants.METADATA_REMARKS));
		} catch (SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	public Collection<ColumnsMetadata> getColumns() throws SQLException {
		if (columns == null) {
			Map<String, ColumnsMetadata> columnsMap = new HashMap<>();
			ResultSet rs = getMetadata().getColumns(getCatalog(), getSchema(), getName(), null);
			while (rs.next()) {
				ColumnsMetadata cm = new ColumnsMetadata(rs, getMetadata());
				columnsMap.put(cm.getName(), cm);
			}
			rs = getMetadata().getPrimaryKeys(getCatalog(), getSchema(), getName());
			while (rs.next()) {
				columnsMap.get(rs.getString(DBConstants.METADATA_COLUMN)).setPrimaryKey(true);
			}
			rs = getMetadata().getImportedKeys(getCatalog(), getSchema(), getName());
			while (rs.next()) {
				columnsMap.get(rs.getString(DBConstants.METADATA_COLUMN)).setForeingKey(true);
			}
			columns = columnsMap.values();
		}
		return columns;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return getCatalog() + "." + getSchema() + "." + getName() + " (" + getType() + ")";
	}
}
