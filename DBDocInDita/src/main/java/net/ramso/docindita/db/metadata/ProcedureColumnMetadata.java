package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class ProcedureColumnMetadata extends BasicColumnMetadata {

	private String type;

	private int size;
	private int decimal;
	private String specificName;
	private boolean isNullable = false;
	private String columnType;
	private String defaultValue;

	public ProcedureColumnMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			loadLabels(resultSet.getMetaData());
			if (labelExist(DBConstants.METADATA_PROCEDURE_SCHEM)) {
				setSchema(resultSet.getString(DBConstants.METADATA_PROCEDURE_SCHEM));
			}
			if (labelExist(DBConstants.METADATA_PROCEDURE_CAT)) {
				setCatalog(resultSet.getString(DBConstants.METADATA_PROCEDURE_CAT));
			}
			if (labelExist(DBConstants.METADATA_PROCEDURE_NAME)) {
				setTable(resultSet.getString(DBConstants.METADATA_PROCEDURE_NAME));
			}
			if (labelExist(DBConstants.METADATA_COLUMN)) {
				setName(resultSet.getString(DBConstants.METADATA_COLUMN));
			}
			if (labelExist(DBConstants.METADATA_TYPE_NAME)) {
				setType(resultSet.getString(DBConstants.METADATA_TYPE_NAME));
			}
			if (labelExist(DBConstants.METADATA_METADATA_PRECISION)) {
				setSize(resultSet.getInt(DBConstants.METADATA_METADATA_PRECISION));
			}
			if (labelExist(DBConstants.METADATA_METADATA_SCALE)) {
				setDecimal(resultSet.getInt(DBConstants.METADATA_METADATA_SCALE));
			}
			if (labelExist(DBConstants.METADATA_IS_NULLABLE)) {
				setNullable(resultSet.getString(DBConstants.METADATA_IS_NULLABLE).equalsIgnoreCase("YES"));
			}
			if (labelExist(DBConstants.METADATA_REMARKS)) {
				setDoc(resultSet.getString(DBConstants.METADATA_REMARKS));
			}
			if (labelExist(DBConstants.METADATA_ORDINAL_POSITION)) {
				setIdx(resultSet.getInt(DBConstants.METADATA_ORDINAL_POSITION));
			}
			if (labelExist(DBConstants.METADATA_SPECIFIC_NAME)) {
				setSpecificName(resultSet.getString(DBConstants.METADATA_SPECIFIC_NAME));
			}
			if (labelExist(DBConstants.METADATA_COLUMN_TYPE)) {
				setColumnType(resultSet.getShort(DBConstants.METADATA_COLUMN_TYPE));
			}
			if (labelExist(DBConstants.METADATA_COLUMN_DEF)) {
				setDefaultValue(resultSet.getString(DBConstants.METADATA_COLUMN_DEF));
			}
		} catch (final SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

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

	public void setType(String type) {
		this.type = type;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getDecimal() {
		return this.decimal;
	}

	public void setDecimal(int decimal) {
		this.decimal = decimal;
	}

	public boolean isNullable() {
		return this.isNullable;
	}

	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	public String getSpecificName() {
		return this.specificName;
	}

	public void setSpecificName(String specificName) {
		this.specificName = specificName;
	}

	public String getColumnType() {
		return this.columnType;
	}

	public void setColumnType(Short columnType) {
		switch (columnType) {
		case DatabaseMetaData.procedureColumnIn:
			this.columnType = DBConstants.IN;
			break;
		case DatabaseMetaData.procedureColumnInOut:
			this.columnType = DBConstants.INOUT;
			break;
		case DatabaseMetaData.procedureColumnOut:
			this.columnType = DBConstants.OUT;
			break;
		case DatabaseMetaData.procedureColumnUnknown:
			this.columnType = DBConstants.PROCEDURE_RESULT_UNKNOWN;
			break;
		case DatabaseMetaData.procedureColumnResult:
			this.columnType = DBConstants.RESULT;
			break;
		default:
			this.columnType = DBConstants.RETURN;
			break;
		}

	}

	public String getDefaultValue() {
		return this.defaultValue != null ? this.defaultValue : "";
	}

	protected void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		final StringBuilder st = new StringBuilder();
		st.append(super.toString());
		st.append(" Specific Name: ");
		st.append(getSpecificName());
		st.append(" type: ");
		st.append(getType());
		st.append(" Position: " + getIdx());
		st.append(" Uso: ");
		st.append(getColumnType());
		st.append(isNullable() ? " Nullable" : "");
		st.append(" Default Value: ");
		st.append(getDefaultValue());
		return st.toString();
	}

	@Override
	public String getDDL() {
		return "";
	}
}
