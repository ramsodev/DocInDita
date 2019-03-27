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
			setSchema(resultSet.getString(DBConstants.METADATA_SCHEMA));
			setCatalog(resultSet.getString(DBConstants.METADATA_TABLE_CATALOG));
			setName(resultSet.getString(DBConstants.METADATA_TABLE));
			setType(resultSet.getString(DBConstants.METADATA_TABLE_TYPE));
			setDoc(resultSet.getString(DBConstants.METADATA_REMARKS));
		} catch (SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	public Collection<ColumnMetadata> getColumns() throws SQLException {
		if (columns == null) {
			Map<String, ColumnMetadata> columnsMap = new HashMap<>();
			ResultSet rs = getMetadata().getColumns(getCatalog(), getSchema(), getName(), null);
			while (rs.next()) {
				ColumnMetadata cm = new ColumnMetadata(rs, getMetadata());
				columnsMap.put(cm.getName(), cm);
			}
			Map<String, PrimaryKeyMetadata> pksMap = new HashMap<>();
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
			primaryKeys = new ArrayList<>(pksMap.values());
			Map<String, ForeingKeyMetadata> fksMap = new HashMap<>();
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
			foreingKeys = new ArrayList<>(fksMap.values());
			columns = new ArrayList<>(columnsMap.values());
			columns.sort((BasicColumnMetadata o1, BasicColumnMetadata o2) -> o1.getIdx() - o2.getIdx());
		}
		return columns;
	}

	public Collection<IndexMetadata> getIndex() throws SQLException {
		if (tableIdx == null) {
			Map<String, IndexMetadata> index = new HashMap<>();
			ResultSet rs = getMetadata().getIndexInfo(getCatalog(), getSchema(), getName(), false, false);
			while (rs.next()) {
				IndexMetadata idx = index.get(rs.getString(DBConstants.METADATA_INDEX_NAME));
				if (idx == null) {
					idx = new IndexMetadata(rs, getMetadata());
					index.put(idx.getName(), idx);
				} else {
					idx.addColumn(rs);
				}
			}
			tableIdx = new ArrayList<>(index.values());
		}
		return tableIdx;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return getCatalog() + "." + getSchema() + "." + getName() + " (" + getType() + ")";
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
		StringBuilder st = new StringBuilder();
		try {
			if (getType().equals(DBConstants.TABLE)) {
				st.append("CREATE TABLE ");
				st.append(super.getId());
				st.append(getName());
				st.append(" (");
				boolean comma = false;
				for (ColumnMetadata col : getColumns()) {
					if (comma) {
						st.append(", ");
					}
					comma = true;
					st.append(System.lineSeparator());
					st.append(col.getDDL());
				}
				st.append(System.lineSeparator());
				st.append(");");
				for(PrimaryKeyMetadata pk:getPrimaryKeys()) {
					st.append(System.lineSeparator());
					st.append(pk.getDDL());
				}
				for(IndexMetadata idx:getIndex()) {
					st.append(System.lineSeparator());
					st.append(idx.getDDL());
				}
				for(ForeingKeyMetadata fk:getForeingKeys()) {
					st.append(System.lineSeparator());
					st.append(fk.getDDL());
				}
			}
		} catch (SQLException e) {
			LogManager.warn("Fallo al crear el ddl de la tabla " + getName(), e);
		}
		return st.toString();
	}

	public List<ForeingKeyMetadata> getForeingKeys() {
		return foreingKeys;
	}

	public List<PrimaryKeyMetadata> getPrimaryKeys() {
		return primaryKeys;
	}

	@Override
	public String getId() {
		StringBuilder st = new StringBuilder();
		st.append(super.getId());
		st.append(getName());
		return st.toString();
	}
}
