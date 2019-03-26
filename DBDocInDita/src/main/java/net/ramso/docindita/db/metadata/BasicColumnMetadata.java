package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class BasicColumnMetadata extends AbstractMetadata {

	
	private String table;
	private int idx;
	

	public BasicColumnMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			setSchema(resultSet.getString(DBConstants.METADATA_SCHEMA));
			setCatalog(resultSet.getString(DBConstants.METADATA_TABLE_CATALOG));
			setTable(resultSet.getString(DBConstants.METADATA_TABLE));
			setName(resultSet.getString(DBConstants.METADATA_COLUMN));
			setIdx(resultSet.getInt(DBConstants.METADATA_ORDINAL_POSITION));			
		} catch (SQLException e) {
			LogManager.warn("Error al preparar columna", e);
		}

	}

	
	public String getTable() {
		return table;
	}

	protected void setTable(String table) {
		this.table = table;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append(getIdx()+") ");
		st.append(getCatalog());
		st.append(".");
		st.append(getSchema());
		st.append(".");
		st.append(getTable());
		st.append(".");
		st.append(getName());		
		return st.toString();
	}


}
