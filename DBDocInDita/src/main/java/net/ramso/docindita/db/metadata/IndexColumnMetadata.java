package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class IndexColumnMetadata extends BasicColumnMetadata {

	public String order;

	public IndexColumnMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		super.init(resultSet);
		try {
			setOrder(resultSet.getString(DBConstants.METADATA_ASC_OR_DESC));
		} catch (SQLException e) {
			LogManager.warn("Error al preparar columna", e);
		}
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append(super.toString());
		st.append(" (");
		st.append(getOrder());
		st.append(')');
		return st.toString();
	}

}
