package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class PrimaryKeyMetadata extends AbstractMetadata {

	private String table;
	private List<BasicColumnMetadata> columns;

	public PrimaryKeyMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
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
			if (labelExist(DBConstants.METADATA_PK_NAME)) {
				setName(resultSet.getString(DBConstants.METADATA_PK_NAME));
			}
			setDoc("");
			addColumn(resultSet);
		} catch (final SQLException e) {
			LogManager.warn("Error al preparar la PK", e);
		}

	}

	public void addColumn(ResultSet resultSet) {
		if (this.columns == null) {
			this.columns = new ArrayList<>();
		}
		this.columns.add(new BasicColumnMetadata(resultSet, getMetadata()));

	}

	public String getTable() {
		return this.table;
	}

	protected void setTable(String table) {
		this.table = table;
	}

	@Override
	public String toString() {
		final StringBuilder st = new StringBuilder();
		st.append("PK " + getName());
		st.append(" Columns:");
		for (final BasicColumnMetadata column : getColumns()) {
			st.append(System.lineSeparator());
			st.append("---------->");
			st.append(column.toString());
		}
		return st.toString();
	}

	public List<BasicColumnMetadata> getColumns() {
		this.columns.sort((BasicColumnMetadata o1, BasicColumnMetadata o2) -> o1.getIdx() - o2.getIdx());
		return this.columns;
	}

	@Override
	public String getId() {
		final StringBuilder st = new StringBuilder();
		st.append("PK.");
		st.append(super.getId());
		st.append(getTable());
		st.append('.');
		st.append(getName());
		return st.toString();
	}

	@Override
	public String getDDL() {
		final StringBuilder st = new StringBuilder();
		st.append("ALTER TABLE ");
		st.append(super.getId());
		st.append(getTable());
		st.append(" ADD CONSTRAINT ");
		st.append(super.getId());
		st.append(getName());
		st.append(" PRIMARY KEY (");
		boolean comma = false;
		for (final BasicColumnMetadata col : getColumns()) {
			if (comma) {
				st.append(", ");
			}
			comma = true;
			st.append(col.getDDL());
		}
		st.append(");");
		return st.toString();
	}
}
