package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class ForeingKeyColumnMetadata extends BasicColumnMetadata {

	private String fkCatalog;
	private String fkSchema;
	private String fkTable;
	private String fkName;
	private String fkColumn;

	public ForeingKeyColumnMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			loadLabels(resultSet.getMetaData());
			if (labelExist(DBConstants.METADATA_FKTABLE_SCHEM)) {
				setSchema(resultSet.getString(DBConstants.METADATA_FKTABLE_SCHEM));
			}
			if (labelExist(DBConstants.METADATA_FKTABLE_CAT)) {
				setCatalog(resultSet.getString(DBConstants.METADATA_FKTABLE_CAT));
			}
			if (labelExist(DBConstants.METADATA_FKTABLE_NAME)) {
				setTable(resultSet.getString(DBConstants.METADATA_FKTABLE_NAME));
			}
			if (labelExist(DBConstants.METADATA_FKCOLUMN_NAME)) {
				setName(resultSet.getString(DBConstants.METADATA_FKCOLUMN_NAME));
			}
			if (labelExist(DBConstants.METADATA_PKTABLE_CAT)) {
				setFkCatalog(resultSet.getString(DBConstants.METADATA_PKTABLE_CAT));
			}
			if (labelExist(DBConstants.METADATA_PKTABLE_SCHEM)) {
				setFkSchema(resultSet.getString(DBConstants.METADATA_PKTABLE_SCHEM));
			}
			if (labelExist(DBConstants.METADATA_PKTABLE_NAME)) {
				setFkTable(resultSet.getString(DBConstants.METADATA_PKTABLE_NAME));
			}
			if (labelExist(DBConstants.METADATA_PKCOLUMN_NAME)) {
				setFkColumn(resultSet.getString(DBConstants.METADATA_PKCOLUMN_NAME));
			}
			setDoc("");
			setIdx(resultSet.getShort(DBConstants.METADATA_KEY_SEQ));
		} catch (final SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	@Override
	public String toString() {
		final StringBuilder st = new StringBuilder();
		st.append(super.toString());
		st.append(" (FK ");
		st.append(getFkName() + ": ");
		st.append(getFkCatalog());
		st.append(".");
		st.append(getFkSchema());
		st.append(".");
		st.append(getFkTable());
		st.append(".");
		st.append(getFkColumn() + ")");
		return st.toString();
	}

	public String getFkCatalog() {
		return this.fkCatalog;
	}

	public String getFkSchema() {
		return this.fkSchema;
	}

	public String getFkTable() {
		return this.fkTable;
	}

	public String getFkName() {
		return this.fkName;
	}

	public String getFkColumn() {
		return this.fkColumn;
	}

	public void setFkCatalog(String fkCatalog) {
		this.fkCatalog = fkCatalog;
	}

	public void setFkSchema(String fkSchema) {
		this.fkSchema = fkSchema;
	}

	public void setFkTable(String fkTable) {
		this.fkTable = fkTable;
	}

	public void setFkName(String fkName) {
		this.fkName = fkName;
	}

	public void setFkColumn(String fkColumn) {
		this.fkColumn = fkColumn;
	}

	public String getPkTxt() {
		final StringBuilder st = new StringBuilder();
		if ((getCatalog() != null) && !getCatalog().isEmpty()) {
			st.append(getCatalog());
			st.append(".");
		}
		st.append(getSchema());
		st.append(".");
		st.append(getTable());
		st.append(".");
		st.append(getName());
		return st.toString();
	}

	public String getFkTxt() {
		final StringBuilder st = new StringBuilder();
		if ((getFkCatalog() != null) && !getFkCatalog().isEmpty()) {
			st.append(getFkCatalog());
			st.append(".");
		}
		st.append(getFkSchema());
		st.append(".");
		st.append(getFkTable());
		st.append(".");
		st.append(getFkColumn());
		return st.toString();
	}

	@Override
	public String getId() {
		final StringBuilder st = new StringBuilder();
		st.append("FKColumn.");
		st.append(super.getId());
		return st.toString();
	}
}
