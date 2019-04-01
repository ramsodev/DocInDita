package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class IndexMetadata extends AbstractMetadata {

	private String type;
	private String table;
	private List<IndexColumnMetadata> columns;
	private boolean unique = false;
	private String qualifier;
	private String filter;

	public IndexMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
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
			if (labelExist(DBConstants.METADATA_INDEX_NAME)) {
				setName(resultSet.getString(DBConstants.METADATA_INDEX_NAME));
			}
			if (labelExist(DBConstants.METADATA_INDEX_QUALIFIER)) {
				setQualifier(resultSet.getString(DBConstants.METADATA_INDEX_QUALIFIER));
			}
			if (labelExist(DBConstants.METADATA_FILTER_CONDITION)) {
				setFilter(resultSet.getString(DBConstants.METADATA_FILTER_CONDITION));
			}
			if (labelExist(DBConstants.METADATA_NON_UNIQUE)) {
				setUnique(!resultSet.getBoolean(DBConstants.METADATA_NON_UNIQUE));
			}
			if (labelExist(DBConstants.METADATA_TYPE)) {
				setType(resultSet.getShort(DBConstants.METADATA_TYPE));
			}
			setDoc("");
			addColumn(resultSet);
		} catch (final SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	public void addColumn(ResultSet resultSet) {
		if (this.columns == null) {
			this.columns = new ArrayList<>();
		}
		this.columns.add(new IndexColumnMetadata(resultSet, getMetadata()));

	}

	protected void setType(short type) {
		switch (type) {
		case DatabaseMetaData.tableIndexClustered:
			this.type = DBConstants.INDEX_CLUSTERED;
			break;
		case DatabaseMetaData.tableIndexHashed:
			this.type = DBConstants.INDEX_HASHED;
			break;
		case DatabaseMetaData.tableIndexStatistic:
			this.type = DBConstants.INDEX_STADISTIC;
			break;
		default:
			this.type = DBConstants.INDEX_OTHER;
			break;
		}
	}

	public String getType() {
		return this.type;
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
		st.append("Index: ");
		st.append(getCatalog());
		st.append(".");
		st.append(getSchema());
		st.append(".");
		st.append(getTable());
		st.append(".");
		st.append(getName());
		st.append(" type: ");
		st.append(getType() + " ");
		st.append(isUnique() ? " Unique" : "");
		st.append(" Qualifier: ");
		st.append(getQualifier());
		st.append(" Filter: ");
		st.append(getFilter());
		st.append(" Columns:");
		for (final BasicColumnMetadata column : getColumns()) {
			st.append(System.lineSeparator());
			st.append("---------->");
			st.append(column.toString());
		}

		return st.toString();
	}

	public boolean isUnique() {
		return this.unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public String getQualifier() {
		return this.qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getFilter() {
		return this.filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public List<IndexColumnMetadata> getColumns() {
		this.columns.sort((BasicColumnMetadata o1, BasicColumnMetadata o2) -> o1.getIdx() - o2.getIdx());
		return this.columns;
	}

	@Override
	public String getId() {
		final StringBuilder st = new StringBuilder();
		st.append("Index.");
		st.append(super.getId());
		st.append(getTable());
		st.append('.');
		st.append(getName());
		return st.toString();
	}

	@Override
	public String getDDL() {
		final StringBuilder st = new StringBuilder();
		st.append("CREATE INDEX ");
		if (isUnique()) {
			st.append("UNIQUE ");
		}
		st.append(super.getId());
		st.append(getName());
		st.append(" ON ");
		st.append(super.getId());
		st.append(getTable());
		st.append("(");
		boolean comma = false;
		for (final IndexColumnMetadata col : getColumns()) {
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
