package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class IndexColumnMetadata extends BasicColumnMetadata {

	private String order;

	public IndexColumnMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			loadLabels(resultSet.getMetaData());
			setIdx(0);
			setOrder("");
			if (labelExist(DBConstants.METADATA_SCHEMA))
				setSchema(resultSet.getString(DBConstants.METADATA_SCHEMA));
			if (labelExist(DBConstants.METADATA_TABLE_CATALOG))
				setCatalog(resultSet.getString(DBConstants.METADATA_TABLE_CATALOG));
			if (labelExist(DBConstants.METADATA_TABLE))
				setTable(resultSet.getString(DBConstants.METADATA_TABLE));
			if (labelExist(DBConstants.METADATA_COLUMN))
				setName(resultSet.getString(DBConstants.METADATA_COLUMN));
			if (labelExist(DBConstants.METADATA_ORDINAL_POSITION))
				setIdx(resultSet.getInt(DBConstants.METADATA_ORDINAL_POSITION));
			if (labelExist(DBConstants.METADATA_ASC_OR_DESC))
				setOrder(resultSet.getString(DBConstants.METADATA_ASC_OR_DESC));
		} catch (SQLException e) {
			LogManager.warn("Error al preparar columna", e);
		}
	}

	public String getOrder() {
		if (order == null) {
			order = "";
		}
		return order.startsWith("A") ? "ASC" : order.startsWith("D") ? "DES" : "";
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

	@Override
	public String getDDL() {
		StringBuilder st = new StringBuilder();
		st.append(super.getDDL());
		st.append(" (");
		st.append(getOrder());
		st.append(')');
		return st.toString();
	}
}
