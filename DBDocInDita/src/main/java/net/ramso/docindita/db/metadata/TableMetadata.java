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
public class TableMetadata extends AbstractMetadata implements IBaseMetadata{
	private String type;
	private List<ColumnMetadata> columns;
	private String pkName;
	private List<IndexMetadata> tableIdx;

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
			rs = getMetadata().getPrimaryKeys(getCatalog(), getSchema(), getName());

			while (rs.next()) {
				if (getPkName() != null)
					setPkName(rs.getString(DBConstants.METADATA_PK_NAME));
				columnsMap.get(rs.getString(DBConstants.METADATA_COLUMN)).setPrimaryKey(true);
			}
			rs = getMetadata().getImportedKeys(getCatalog(), getSchema(), getName());
			while (rs.next()) {
				columnsMap.get(rs.getString(DBConstants.METADATA_FKCOLUMN_NAME)).setForeingKey(
						rs.getString(DBConstants.METADATA_FK_NAME), rs.getString(DBConstants.METADATA_PKTABLE_CAT),
						rs.getString(DBConstants.METADATA_PKTABLE_SCHEM),
						rs.getString(DBConstants.METADATA_PKTABLE_NAME),
						rs.getString(DBConstants.METADATA_PKCOLUMN_NAME));
			}
			columns = new ArrayList<>(columnsMap.values());
			columns.sort((ColumnMetadata o1, ColumnMetadata o2) -> o1.getIdx() - o2.getIdx());
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

	public String getPkName() {
		return pkName;
	}

	public void setPkName(String pkName) {
		this.pkName = pkName;
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
		return "";
	}
}
