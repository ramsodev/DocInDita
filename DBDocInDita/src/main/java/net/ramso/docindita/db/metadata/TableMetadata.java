/**
 *
 */
package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

/**
 * @author ramso
 *
 */
public class TableMetadata extends AbstractMetadata implements IBaseMetadata {
	private String type;
	private List<ColumnMetadata> columns;

	private List<IndexMetadata> tableIdx;
	private List<ForeingKeyMetadata> foreingKeys;
	private List<PrimaryKeyMetadata> primaryKeys;

	public TableMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
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
				setName(resultSet.getString(DBConstants.METADATA_TABLE));
			}
			if (labelExist(DBConstants.METADATA_TABLE_TYPE)) {
				setType(resultSet.getString(DBConstants.METADATA_TABLE_TYPE));
			}
			if (labelExist(DBConstants.METADATA_REMARKS)) {
				setDoc(resultSet.getString(DBConstants.METADATA_REMARKS));
			}
		} catch (final SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	public Collection<ColumnMetadata> getColumns() throws SQLException {
		if (this.columns == null) {
			final Map<String, ColumnMetadata> columnsMap = new HashMap<>();
			ResultSet rs = getMetadata().getColumns(getCatalog(), getSchema(), getName(), null);
			while (rs.next()) {
				final ColumnMetadata cm = new ColumnMetadata(rs, getMetadata());
				columnsMap.put(cm.getName(), cm);
			}
			final Map<String, PrimaryKeyMetadata> pksMap = new HashMap<>();
			rs = getMetadata().getPrimaryKeys(getCatalog(), getSchema(), getName());
			while (rs.next()) {
				columnsMap.get(rs.getString(DBConstants.METADATA_COLUMN)).setPrimaryKey(true);
				PrimaryKeyMetadata pk = pksMap.get(rs.getString(DBConstants.METADATA_PK_NAME));
				if (pk == null) {
					pk = new PrimaryKeyMetadata(rs, getMetadata());
					pksMap.put(pk.getName(), pk);
				} else {
					pk.addColumn(rs);
				}
			}
			this.primaryKeys = new ArrayList<>(pksMap.values());
			final Map<String, ForeingKeyMetadata> fksMap = new HashMap<>();
			rs = getMetadata().getImportedKeys(getCatalog(), getSchema(), getName());
			while (rs.next()) {
				columnsMap.get(rs.getString(DBConstants.METADATA_FKCOLUMN_NAME)).setForeingKey(true);
				ForeingKeyMetadata fk = fksMap.get(rs.getString(DBConstants.METADATA_FK_NAME));
				if (fk == null) {
					fk = new ForeingKeyMetadata(rs, getMetadata());
					fksMap.put(fk.getName(), fk);
				} else {
					fk.addColumn(rs);
				}
			}
			this.foreingKeys = new ArrayList<>(fksMap.values());
			this.columns = new ArrayList<>(columnsMap.values());
			this.columns.sort((BasicColumnMetadata o1, BasicColumnMetadata o2) -> o1.getIdx() - o2.getIdx());
		}
		return this.columns;
	}

	public Collection<IndexMetadata> getIndex() throws SQLException {
		if (this.tableIdx == null) {
			final Map<String, IndexMetadata> index = new HashMap<>();
			final ResultSet rs = getMetadata().getIndexInfo(getCatalog(), getSchema(), getName(), false, false);
			while (rs.next()) {
				IndexMetadata idx = index.get(rs.getString(DBConstants.METADATA_INDEX_NAME));
				if (idx == null) {
					idx = new IndexMetadata(rs, getMetadata());
					index.put(idx.getName(), idx);
				} else {
					idx.addColumn(rs);
				}
			}
			this.tableIdx = new ArrayList<>(index.values());
		}
		return this.tableIdx;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append(getType());
		st.append(": ");
		st.append(getId());
		st.append(System.lineSeparator());
		st.append("------->Columns:");
		try {
			for (final ColumnMetadata column : getColumns()) {
				st.append(System.lineSeparator());
				st.append("---------->");
				st.append(column.toString());
			}
			st.append(System.lineSeparator());
			st.append("------->PrimaryKeys:");
			for (PrimaryKeyMetadata pk : getPrimaryKeys()) {
				st.append(System.lineSeparator());
				st.append("---------->");
				st.append(pk);
			}
			st.append(System.lineSeparator());
			st.append("------->ForeingKeys:");
			for (ForeingKeyMetadata fk : getForeingKeys()) {
				st.append(System.lineSeparator());
				st.append("---------->");
				st.append(fk);
			}
			st.append(System.lineSeparator());
			st.append("------->Index:");
			for (IndexMetadata index : getIndex()) {
				st.append(System.lineSeparator());
				st.append("---------->");
				st.append(index);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return st.toString();
	}

	@Override
	public String getDiagram() {
		return "";
	}

	@Override
	public boolean isScaleDiagram() {
		return false;
	}

	@Override
	public String getDDL() {
		final StringBuilder st = new StringBuilder();
		try {
			if (getType().equals(DBConstants.TABLE)) {
				st.append("CREATE TABLE ");
				st.append(super.getId());
				st.append(getName());
				st.append(" (");
				boolean comma = false;
				for (final ColumnMetadata col : getColumns()) {
					if (comma) {
						st.append(", ");
					}
					comma = true;
					st.append(System.lineSeparator());
					st.append(col.getDDL());
				}
				st.append(System.lineSeparator());
				st.append(");");
				for (final PrimaryKeyMetadata pk : getPrimaryKeys()) {
					st.append(System.lineSeparator());
					st.append(pk.getDDL());
				}
				for (final IndexMetadata idx : getIndex()) {
					st.append(System.lineSeparator());
					st.append(idx.getDDL());
				}
				for (final ForeingKeyMetadata fk : getForeingKeys()) {
					st.append(System.lineSeparator());
					st.append(fk.getDDL());
				}
			}else if (getType().equals(DBConstants.VIEW)) {
				st.append("CREATE VIEW ");
				st.append(super.getId());
				st.append(System.lineSeparator());
				st.append(" SELECT ");
				boolean comma = false;
				for (final ColumnMetadata col : getColumns()) {
					if (comma) {
						st.append(", ");
					}
					comma = true;
					st.append(col.getName());
				}
				st.append(System.lineSeparator());
				st.append(" FROM ....");
				st.append(System.lineSeparator());
				st.append(" WHERE ....");
			}
			
		} catch (final SQLException e) {
			LogManager.warn("Fallo al crear el ddl de la tabla " + getName(), e);
		}
		return st.toString();
	}

	public List<ForeingKeyMetadata> getForeingKeys() {
		return this.foreingKeys;
	}

	public List<PrimaryKeyMetadata> getPrimaryKeys() {
		return this.primaryKeys;
	}

	@Override
	public String getId() {
		final StringBuilder st = new StringBuilder();
		st.append(super.getId());
		st.append(getName());
		return st.toString();
	}
}
