package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class BasicColumnMetadata extends AbstractMetadata {

	private String table;
	private int idx;

	public BasicColumnMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			loadLabels(resultSet.getMetaData());
			if (labelExist(DBConstants.METADATA_SCHEMA)) {
				setSchema(resultSet.getString(DBConstants.METADATA_SCHEMA));
			}
			if (labelExist(DBConstants.METADATA_TABLE_CATALOG)) {
				setCatalog(resultSet.getString(DBConstants.METADATA_TABLE_CATALOG));
			}
			if (labelExist(DBConstants.METADATA_TABLE)) {
				setTable(resultSet.getString(DBConstants.METADATA_TABLE));
			}
			if (labelExist(DBConstants.METADATA_COLUMN)) {
				setName(resultSet.getString(DBConstants.METADATA_COLUMN));
			}
			if (labelExist(DBConstants.METADATA_KEY_SEQ)) {
				setIdx(resultSet.getShort(DBConstants.METADATA_KEY_SEQ));
			}

		} catch (final SQLException e) {
			LogManager.warn("Error al preparar columna", e);
		}

	}

	public String getTable() {
		return this.table;
	}

	protected void setTable(String table) {
		this.table = table;
	}

	public int getIdx() {
		return this.idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	@Override
	public String toString() {
		final StringBuilder st = new StringBuilder();
		st.append(getIdx() + ") ");
		st.append(getCatalog());
		st.append(".");
		st.append(getSchema());
		st.append(".");
		st.append(getTable());
		st.append(".");
		st.append(getName());
		return st.toString();
	}

	@Override
	public String getId() {
		final StringBuilder st = new StringBuilder();
		st.append(super.getId());
		st.append(getTable());
		st.append('.');
		st.append(getName());
		return st.toString();
	}

	@Override
	public String getDDL() {
		return getName();
	}

}
