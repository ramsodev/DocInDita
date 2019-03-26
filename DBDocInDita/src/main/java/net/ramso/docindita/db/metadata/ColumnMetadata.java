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
	
	private String fkCatalog;
	private String fkSchema;
	private String fkTable;
	private String fkName;
	private String fkColumn;

	public ColumnMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			setSchema(resultSet.getString(DBConstants.METADATA_SCHEMA));
			setCatalog(resultSet.getString(DBConstants.METADATA_TABLE_CATALOG));
			setTable(resultSet.getString(DBConstants.METADATA_TABLE));
			setName(resultSet.getString(DBConstants.METADATA_COLUMN));
			setType(resultSet.getString(DBConstants.METADATA_COLUMN_TYPE));
			setSize(resultSet.getInt(DBConstants.METADATA_COLUMN_SIZE));
			setDecimal(resultSet.getInt(DBConstants.METADATA_DECIMAL_DIGITS));
			setDefaultValue(resultSet.getString(DBConstants.METADATA_COLUMN_DEF));
			setNullable(resultSet.getString(DBConstants.METADATA_IS_NULLABLE).equalsIgnoreCase("YES"));
			setAutoincrement(resultSet.getString(DBConstants.METADATA_IS_AUTOINCREMENT).equalsIgnoreCase("YES"));
			setGenerated(resultSet.getString(DBConstants.METADATA_IS_GENERATEDCOLUMN).equalsIgnoreCase("YES"));
			setDoc(resultSet.getString(DBConstants.METADATA_REMARKS));
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

	protected void setAutoincrement(boolean isAutoincrement) {
		this.isAutoincrement = isAutoincrement;
	}

	protected void setGenerated(boolean isGenerated) {
		this.isGenerated = isGenerated;
	}

	public String getType() {
		return type + "(" + this.size + "," + this.decimal + ")";
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public boolean isNullable() {
		return isNullable;
	}

	public boolean isAutoincrement() {
		return isAutoincrement;
	}

	public boolean isGenerated() {
		return isGenerated;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isForeingKey() {
		return foreingKey;
	}

	public void setForeingKey(boolean foreingKey) {
		this.foreingKey = foreingKey;
	}

	public void setForeingKey(String name, String catalog, String schema, String table, String column) {
		this.foreingKey = true;
		this.fkColumn = column;
		this.fkTable = table;
		this.fkName = name;
		this.fkCatalog = catalog;
		this.fkSchema = schema;
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
		st.append(isGenerated() ? " Generated" : "");
		st.append(isPrimaryKey() ? " PK " : "");
		if (isForeingKey()) {
			st.append(" (FK ");
			st.append(getFkName() + ": ");
			st.append(getFkCatalog());
			st.append(".");
			st.append(getFkSchema());
			st.append(".");
			st.append(getFkTable());
			st.append(".");
			st.append(getFkColumn() + ")");
		}
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
}
