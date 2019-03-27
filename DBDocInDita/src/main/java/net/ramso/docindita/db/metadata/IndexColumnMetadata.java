package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class IndexColumnMetadata extends BasicColumnMetadata {

	public String order;

	public IndexColumnMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {		
		try {
			setSchema(resultSet.getString(DBConstants.METADATA_SCHEMA));
			setCatalog(resultSet.getString(DBConstants.METADATA_TABLE_CATALOG));
			setTable(resultSet.getString(DBConstants.METADATA_TABLE));
			setName(resultSet.getString(DBConstants.METADATA_COLUMN));
			setIdx(resultSet.getInt(DBConstants.METADATA_ORDINAL_POSITION));
			setOrder(resultSet.getString(DBConstants.METADATA_ASC_OR_DESC));
		} catch (SQLException e) {
			LogManager.warn("Error al preparar columna", e);
		}
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append(super.toString());
		st.append(" (");
		st.append(getOrder());
		st.append(')');
		return st.toString();
	}
	@Override
	public String getId() {
		StringBuilder st = new StringBuilder();
		st.append("IndexColumn.");
		st.append(super.getId());
		return st.toString();
	}
}
