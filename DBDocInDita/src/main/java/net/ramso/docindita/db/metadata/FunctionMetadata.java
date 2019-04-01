package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class FunctionMetadata extends AbstractMetadata {

	private String type;
	private String specificName;
	private List<FunctionColumnMetadata> columns;

	public FunctionMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			loadLabels(resultSet.getMetaData());
			if (labelExist(DBConstants.METADATA_FUNCTION_SCHEM)) {
				setSchema(resultSet.getString(DBConstants.METADATA_FUNCTION_SCHEM));
			}
			if (labelExist(DBConstants.METADATA_FUNCTION_CAT)) {
				setCatalog(resultSet.getString(DBConstants.METADATA_FUNCTION_CAT));
			}
			if (labelExist(DBConstants.METADATA_FUNCTION_NAME)) {
				setName(resultSet.getString(DBConstants.METADATA_FUNCTION_NAME));
			}
			if (labelExist(DBConstants.METADATA_SPECIFIC_NAME)) {
				setSpecificName(resultSet.getString(DBConstants.METADATA_SPECIFIC_NAME));
			}
			if (labelExist(DBConstants.METADATA_FUNCTION_TYPE)) {
				setType(resultSet.getShort(DBConstants.METADATA_FUNCTION_TYPE));
			}
			if (labelExist(DBConstants.METADATA_REMARKS)) {
				setDoc(resultSet.getString(DBConstants.METADATA_REMARKS));
			}

		} catch (final SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	protected void setType(short type) {
		switch (type) {
		case DatabaseMetaData.functionReturnsTable:
			this.type = DBConstants.FUNCTION_RESULT_TABLE;
			break;
		case DatabaseMetaData.functionNoTable:
			this.type = DBConstants.FUNCTION_RESULT_NOTABLE;
			break;
		default:
			this.type = DBConstants.FUNCTION_RESULT_UNKNOWN;
			;
			break;
		}
	}

	public String getType() {
		return this.type;
	}

	public String getSpecificName() {
		return this.specificName;
	}

	public void setSpecificName(String specificName) {
		this.specificName = specificName;
	}

	@Override
	public String toString() {
		final StringBuilder st = new StringBuilder();
		st.append("Function: ");
		st.append(getCatalog());
		st.append(".");
		st.append(getSchema());
		st.append(".");
		st.append(getName());
		st.append(" Specific Name: ");
		st.append(getSpecificName());
		st.append(" type: ");
		st.append(getType() + " ");
		st.append(" Columns:");
		try {
			for (final BasicColumnMetadata column : getColumns()) {
				st.append(System.lineSeparator());
				st.append("---------->");
				st.append(column.toString());
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return st.toString();
	}

	public List<FunctionColumnMetadata> getColumns() throws SQLException {
		if (this.columns == null) {
			final Map<String, FunctionColumnMetadata> columnsMap = new HashMap<>();
			final ResultSet rs = getMetadata().getFunctionColumns(getCatalog(), getSchema(), getName(), null);
			while (rs.next()) {
				final FunctionColumnMetadata cm = new FunctionColumnMetadata(rs, getMetadata());
				columnsMap.put(cm.getName(), cm);
			}
			this.columns = new ArrayList<>(columnsMap.values());
			this.columns.sort((BasicColumnMetadata o1, BasicColumnMetadata o2) -> o1.getIdx() - o2.getIdx());
		}
		return this.columns;
	}

	@Override
	public String getId() {
		final StringBuilder st = new StringBuilder();
		st.append("Index.");
		st.append(super.getId());
		st.append('.');
		st.append(getName());
		return st.toString();
	}

	@Override
	public String getDDL() {
		return "";
	}
}
