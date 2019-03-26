package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class ForeingKeyMetadata extends AbstractMetadata {

	private String table;
	private List<ForeingKeyColumnMetadata> columns;
	private String updateRule;
	private String deleteRule;
	private String deferrability;
	private String pkName;

	public ForeingKeyMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			setSchema(resultSet.getString(DBConstants.METADATA_SCHEMA));
			setCatalog(resultSet.getString(DBConstants.METADATA_TABLE_CATALOG));
			setTable(resultSet.getString(DBConstants.METADATA_TABLE));
			setName(resultSet.getString(DBConstants.METADATA_FK_NAME));
			setPkName(resultSet.getString(DBConstants.METADATA_PK_NAME));
			setDoc("");
			addColumn(resultSet);
		} catch (SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	public void addColumn(ResultSet resultSet) {
		columns.add(new ForeingKeyColumnMetadata(resultSet, getMetadata()));

	}

	public String getTable() {
		return table;
	}

	protected void setTable(String table) {
		this.table = table;
	}

	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append("FK " + getName());
		st.append(" From ");
		st.append(getPkName());
		st.append(" Update rule ");
		st.append(getUpdateRule());
		st.append(" Delete rule ");
		st.append(getDeleteRule());
		st.append(" Deferrability ");
		st.append(getDeferrability());
		st.append(" Columns:");
		for (BasicColumnMetadata column : getColumns()) {
			st.append("\\n-------->");
			st.append(column.toString());
		}
		return st.toString();
	}

	public List<ForeingKeyColumnMetadata> getColumns() {
		columns.sort((BasicColumnMetadata o1, BasicColumnMetadata o2) -> o1.getIdx() - o2.getIdx());
		return columns;
	}

	public String getUpdateRule() {
		return updateRule;
	}

	public void setUpdateRule(short rule) {
		switch (rule) {
		case DatabaseMetaData.importedKeyCascade:
			this.updateRule = DBConstants.CASCADE;
			break;
		case DatabaseMetaData.importedKeySetNull:
			this.updateRule = DBConstants.SET_NULL;
			break;
		case DatabaseMetaData.importedKeySetDefault:
			this.updateRule = DBConstants.SET_DEFAULT;
			break;
		case DatabaseMetaData.importedKeyRestrict:
			this.updateRule = DBConstants.RESTRICT;
			break;
		default:
			this.updateRule = DBConstants.NO_ACTION;
			break;
		}
	}

	public String getDeleteRule() {
		return deleteRule;
	}

	public void setDeleteRule(short rule) {
		switch (rule) {
		case DatabaseMetaData.importedKeyCascade:
			this.deleteRule = DBConstants.CASCADE;
			break;
		case DatabaseMetaData.importedKeySetNull:
			this.deleteRule = DBConstants.SET_NULL;
			break;
		case DatabaseMetaData.importedKeySetDefault:
			this.deleteRule = DBConstants.SET_DEFAULT;
			break;
		case DatabaseMetaData.importedKeyRestrict:
			this.deleteRule = DBConstants.RESTRICT;
			break;
		default:
			this.deleteRule = DBConstants.NO_ACTION;
			break;
		}
	}

	public String getDeferrability() {
		return deferrability;
	}

	public void setDeferrability(short deferrability) {
		switch (deferrability) {
		case DatabaseMetaData.importedKeyInitiallyDeferred:
			this.deferrability = DBConstants.INITIALLY_DEFERRED;
			break;
		case DatabaseMetaData.importedKeyInitiallyImmediate:
			this.deferrability = DBConstants.INITIALLY_INMEDIATE;
			break;
		default:
			this.deferrability = DBConstants.NOT_DEFERRABLE;
			break;
		}
		
	}

	public String getPkName() {
		return pkName;
	}

	public void setPkName(String pkName) {
		this.pkName = pkName;
	}

}
