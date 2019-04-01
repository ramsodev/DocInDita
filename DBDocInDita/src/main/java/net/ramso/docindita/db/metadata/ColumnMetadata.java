package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class ColumnMetadata extends BasicColumnMetadata {

	private String type;

	private int size;
	private int decimal;
	private String defaultValue;
	private boolean isNullable = false;
	private boolean isAutoincrement = false;
	private boolean isGenerated = false;
	private boolean primaryKey = false;
	private boolean foreingKey = false;

	public ColumnMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
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
			if (labelExist(DBConstants.METADATA_TYPE_NAME)) {
				setType(resultSet.getString(DBConstants.METADATA_TYPE_NAME));
			}
			if (labelExist(DBConstants.METADATA_COLUMN_SIZE)) {
				setSize(resultSet.getInt(DBConstants.METADATA_COLUMN_SIZE));
			}
			if (labelExist(DBConstants.METADATA_DECIMAL_DIGITS)) {
				setDecimal(resultSet.getInt(DBConstants.METADATA_DECIMAL_DIGITS));
			}
			if (labelExist(DBConstants.METADATA_COLUMN_DEF)) {
				setDefaultValue(resultSet.getString(DBConstants.METADATA_COLUMN_DEF));
			}
			if (labelExist(DBConstants.METADATA_IS_NULLABLE)) {
				setNullable(resultSet.getString(DBConstants.METADATA_IS_NULLABLE).equalsIgnoreCase("YES"));
			}
			if (labelExist(DBConstants.METADATA_IS_AUTOINCREMENT)) {
				setAutoincrement(resultSet.getString(DBConstants.METADATA_IS_AUTOINCREMENT).equalsIgnoreCase("YES"));
			}
			if (labelExist(DBConstants.METADATA_IS_GENERATEDCOLUMN)) {
				setGenerated(resultSet.getString(DBConstants.METADATA_IS_GENERATEDCOLUMN).equalsIgnoreCase("YES"));
			}
			if (labelExist(DBConstants.METADATA_REMARKS)) {
				setDoc(resultSet.getString(DBConstants.METADATA_REMARKS));
			}
			if (labelExist(DBConstants.METADATA_ORDINAL_POSITION)) {
				setIdx(resultSet.getInt(DBConstants.METADATA_ORDINAL_POSITION));
			}
		} catch (final SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	protected void setType(String type) {
		this.type = type;
	}

	protected void setSize(int size) {
		this.size = size;
	}

	protected void setDecimal(int decimal) {
		this.decimal = decimal;
	}

	protected void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	protected void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	protected void setAutoincrement(boolean isAutoincrement) {
		this.isAutoincrement = isAutoincrement;
	}

	protected void setGenerated(boolean isGenerated) {
		this.isGenerated = isGenerated;
	}

	public String getType() {
		final StringBuilder st = new StringBuilder();
		st.append(this.type);
		switch (this.type.toUpperCase()) {
		case DBConstants.SMALLINT:
		case DBConstants.INTEGER:
		case DBConstants.INT:
		case DBConstants.BIGINT:
		case DBConstants.REAL:
		case DBConstants.DOUBLE:
		case DBConstants.DOUBLE_PRECISION:
		case DBConstants.BOOLEAN:
		case DBConstants.DATE:
		case DBConstants.TIME:
		case DBConstants.TIMESTAMP:
			break;
		case DBConstants.NUMERIC:
		case DBConstants.DECIMAL:
		case DBConstants.DEC:
			st.append('(');
			st.append(this.size);
			st.append(',');
			st.append(this.decimal);
			st.append(')');
			break;
		default:
			if (this.size > 0) {
				st.append('(');
				st.append(this.size);
				st.append(')');
			}
			break;
		}
		return st.toString();
	}

	public String getDefaultValue() {
		return this.defaultValue != null ? this.defaultValue : "";
	}

	public boolean isNullable() {
		return this.isNullable;
	}

	public boolean isAutoincrement() {
		return this.isAutoincrement;
	}

	public boolean isGenerated() {
		return this.isGenerated;
	}

	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isForeingKey() {
		return this.foreingKey;
	}

	public void setForeingKey(boolean foreingKey) {
		this.foreingKey = foreingKey;
	}

	@Override
	public String toString() {
		final StringBuilder st = new StringBuilder();
		st.append(super.toString());
		st.append(" type: ");
		st.append(getType());
		st.append(" Position: " + getIdx());
		st.append(" Default Value: ");
		st.append(getDefaultValue());
		st.append(isNullable() ? " Nullable" : "");
		st.append(isGenerated() ? " Generated" : "");
		st.append(isPrimaryKey() ? " PK " : "");
		st.append(isForeingKey() ? " FK " : "");
		return st.toString();
	}

	@Override
	public String getDDL() {
		final StringBuilder st = new StringBuilder();
		st.append(getName());
		st.append(' ');
		st.append(getType());
		if (isNullable()) {
			st.append(" NOT NULL");
		}
		if (this.defaultValue != null) {
			st.append(" DEFAULT ");
			st.append(getDefaultValue());
		}
		if (this.isGenerated) {
			st.append(" GENERATED ALWAYS AS IDENTITY");
		}
		return st.toString();
	}

}
