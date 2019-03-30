package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class AttributeMetadata extends BasicColumnMetadata {

	private String type;

	private int size;
	private int decimal;
	private String defaultValue;
	private boolean isNullable = false;
	

	public AttributeMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			loadLabels(resultSet.getMetaData());
			if (labelExist(DBConstants.METADATA_TYPE_SCHEM))
				setSchema(resultSet.getString(DBConstants.METADATA_TYPE_SCHEM));
			if (labelExist(DBConstants.METADATA_TYPE_CAT))
				setCatalog(resultSet.getString(DBConstants.METADATA_TYPE_CAT));
			if (labelExist(DBConstants.METADATA_TYPE_NAME))
				setTable(resultSet.getString(DBConstants.METADATA_TYPE_NAME));
			if (labelExist(DBConstants.METADATA_ATTR_NAME))
				setName(resultSet.getString(DBConstants.METADATA_ATTR_NAME));
			if (labelExist(DBConstants.METADATA_ATTR_TYPE_NAME))
				setType(resultSet.getString(DBConstants.METADATA_ATTR_TYPE_NAME));
			if (labelExist(DBConstants.METADATA_ATTR_SIZE))
				setSize(resultSet.getInt(DBConstants.METADATA_ATTR_SIZE));
			if (labelExist(DBConstants.METADATA_DECIMAL_DIGITS))
				setDecimal(resultSet.getInt(DBConstants.METADATA_DECIMAL_DIGITS));
			if (labelExist(DBConstants.METADATA_ATTR_DEF))
				setDefaultValue(resultSet.getString(DBConstants.METADATA_ATTR_DEF));
			if (labelExist(DBConstants.METADATA_IS_NULLABLE))
				setNullable(resultSet.getString(DBConstants.METADATA_IS_NULLABLE).equalsIgnoreCase("YES"));
			if (labelExist(DBConstants.METADATA_REMARKS))
				setDoc(resultSet.getString(DBConstants.METADATA_REMARKS));
			if (labelExist(DBConstants.METADATA_ORDINAL_POSITION))
				setIdx(resultSet.getInt(DBConstants.METADATA_ORDINAL_POSITION));
		} catch (SQLException e) {
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

	

	public String getType() {
		StringBuilder st = new StringBuilder();
		st.append(type);
		switch (type.toUpperCase()) {
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
		return defaultValue != null ? defaultValue : "";
	}

	public boolean isNullable() {
		return isNullable;
	}

	

	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append(super.toString());
		st.append(" type: ");
		st.append(getType());
		st.append(" Position: " + getIdx());
		st.append(" Default Value: ");
		st.append(getDefaultValue());
		st.append(isNullable() ? " Nullable" : "");
		
		return st.toString();
	}

	@Override
	public String getDDL() {
		StringBuilder st = new StringBuilder();
		st.append(getName());
		st.append(' ');
		st.append(getType());
		if (isNullable()) {
			st.append(" NOT NULL");
		}
		if (defaultValue != null) {
			st.append(" DEFAULT ");
			st.append(getDefaultValue());
		}
		
		return st.toString();
	}

}
