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
			setSchema(resultSet.getString(DBConstants.METADATA_FKTABLE_SCHEM));
			setCatalog(resultSet.getString(DBConstants.METADATA_FKTABLE_CAT));
			setTable(resultSet.getString(DBConstants.METADATA_FKTABLE_NAME));
			setName(resultSet.getString(DBConstants.METADATA_FKCOLUMN_NAME));
			setFkCatalog(resultSet.getString(DBConstants.METADATA_PKTABLE_CAT));
			setFkSchema(resultSet.getString(DBConstants.METADATA_PKTABLE_SCHEM));
			setFkTable(resultSet.getString(DBConstants.METADATA_PKTABLE_NAME));
			setFkColumn(resultSet.getString(DBConstants.METADATA_PKCOLUMN_NAME));
			setDoc("");
			setIdx(resultSet.getShort(DBConstants.METADATA_KEY_SEQ));
		} catch (SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
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
		return fkCatalog;
	}

	public String getFkSchema() {
		return fkSchema;
	}

	public String getFkTable() {
		return fkTable;
	}

	public String getFkName() {
		return fkName;
	}

	public String getFkColumn() {
		return fkColumn;
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
		StringBuilder st = new StringBuilder();
		if (getCatalog() != null && !getCatalog().isEmpty()) {
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
		StringBuilder st = new StringBuilder();
		if (getFkCatalog() != null && !getFkCatalog().isEmpty()) {
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
}
