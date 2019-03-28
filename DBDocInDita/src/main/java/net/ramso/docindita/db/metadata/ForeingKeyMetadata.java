package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
			loadLabels(resultSet.getMetaData());
			if (labelExist(DBConstants.METADATA_FKTABLE_SCHEM))
				setSchema(resultSet.getString(DBConstants.METADATA_FKTABLE_SCHEM));
			if (labelExist(DBConstants.METADATA_FKTABLE_CAT))
				setCatalog(resultSet.getString(DBConstants.METADATA_FKTABLE_CAT));
			if (labelExist(DBConstants.METADATA_FKTABLE_NAME))
				setTable(resultSet.getString(DBConstants.METADATA_FKTABLE_NAME));
			if (labelExist(DBConstants.METADATA_FK_NAME))
				setName(resultSet.getString(DBConstants.METADATA_FK_NAME));
			if (labelExist(DBConstants.METADATA_PK_NAME))
				setPkName(resultSet.getString(DBConstants.METADATA_PK_NAME));
			if (labelExist(DBConstants.METADATA_UPDATE_RULE))
				setUpdateRule(resultSet.getShort(DBConstants.METADATA_UPDATE_RULE));
			if (labelExist(DBConstants.METADATA_DELETE_RULE))
				setDeleteRule(resultSet.getShort(DBConstants.METADATA_DELETE_RULE));
			if (labelExist(DBConstants.METADATA_DEFERRABILITY))
				setDeferrability(resultSet.getShort(DBConstants.METADATA_DEFERRABILITY));
			setDoc("");
			addColumn(resultSet);
		} catch (SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	public void addColumn(ResultSet resultSet) {
		if (this.columns == null)
			this.columns = new ArrayList<>();
		this.columns.add(new ForeingKeyColumnMetadata(resultSet, getMetadata()));

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
		for (ForeingKeyColumnMetadata column : getColumns()) {
			st.append(System.lineSeparator());
			st.append("----------->");
			st.append(column.toString());
		}
		return st.toString();
	}

	public List<ForeingKeyColumnMetadata> getColumns() {
		this.columns.sort((BasicColumnMetadata o1, BasicColumnMetadata o2) -> o1.getIdx() - o2.getIdx());
		return this.columns;
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

	@Override
	public String getId() {
		StringBuilder st = new StringBuilder();
		st.append("FK.");
		st.append(super.getId());
		st.append(getTable());
		st.append('.');
		st.append(getName());
		return st.toString();
	}

	@Override
	public String getDDL() {
		StringBuilder st = new StringBuilder();
		st.append("ALTER TABLE ");
		st.append(super.getId());
		st.append(getTable());
		st.append(" ADD CONSTRAINT ");
		st.append(super.getId());
		st.append(getName());
		st.append(System.lineSeparator());
		st.append("	 FOREIGN KEY (");
		boolean comma = false;
		for (ForeingKeyColumnMetadata col : getColumns()) {
			if (comma) {
				st.append(", ");
			}
			comma = true;
			st.append(col.getName());
		}
		st.append(")");
		st.append(System.lineSeparator());
		st.append("	 REFERENCES ");
		comma = false;
		for (ForeingKeyColumnMetadata col : getColumns()) {
			if (!comma) {
				if (col.getFkCatalog() != null && !col.getFkCatalog().isEmpty()) {
					st.append(col.getFkCatalog());
					st.append(".");
				}
				if (col.getFkSchema() != null && !col.getFkSchema().isEmpty()) {
					st.append(col.getFkSchema());
					st.append(".");
				}
				st.append(col.getFkTable());
				st.append(System.lineSeparator());
				st.append("	 	(");
			} else {
				st.append(", ");
			}
			comma = true;
			st.append(col.getName());
		}

		st.append(")");
		st.append(System.lineSeparator());
		st.append("	 ON UPDATE ");
		st.append(getUpdateRule().toUpperCase());
		st.append(System.lineSeparator());
		st.append("	 ON DELETE ");
		st.append(getDeleteRule().toUpperCase());
		st.append(';');
		return st.toString();
	}
}
