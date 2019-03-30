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

public class ProcedureMetadata extends AbstractMetadata {

	private String type;
	private String specificName;
	private List<ProcedureColumnMetadata> columns;

	public ProcedureMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			loadLabels(resultSet.getMetaData());
			if (labelExist(DBConstants.METADATA_PROCEDURE_SCHEM))
				setSchema(resultSet.getString(DBConstants.METADATA_PROCEDURE_SCHEM));
			if (labelExist(DBConstants.METADATA_PROCEDURE_CAT))
				setCatalog(resultSet.getString(DBConstants.METADATA_PROCEDURE_CAT));
			if (labelExist(DBConstants.METADATA_PROCEDURE_NAME))
				setName(resultSet.getString(DBConstants.METADATA_PROCEDURE_NAME));
			if (labelExist(DBConstants.METADATA_SPECIFIC_NAME))
				setSpecificName(resultSet.getString(DBConstants.METADATA_SPECIFIC_NAME));
			if (labelExist(DBConstants.METADATA_PROCEDURE_TYPE))
				setType(resultSet.getShort(DBConstants.METADATA_PROCEDURE_TYPE));
			if (labelExist(DBConstants.METADATA_REMARKS))
				setDoc(resultSet.getString(DBConstants.METADATA_REMARKS));

		} catch (SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	protected void setType(short type) {
		switch (type) {
		case DatabaseMetaData.procedureNoResult:
			this.type = DBConstants.PROCEDURE_NO_RESULT;
			break;
		case DatabaseMetaData.procedureReturnsResult:
			this.type = DBConstants.PROCEDURE_RETUNS_RESULT;
			break;
		default:
			this.type = DBConstants.PROCEDURE_RESULT_UNKNOWN;
			;
			break;
		}
	}

	public String getType() {
		return type;
	}

	public String getSpecificName() {
		return specificName;
	}

	public void setSpecificName(String specificName) {
		this.specificName = specificName;
	}

	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append("Procedure: ");
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
			for (BasicColumnMetadata column : getColumns()) {
				st.append(System.lineSeparator());
				st.append("---------->");
				st.append(column.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return st.toString();
	}

	public List<ProcedureColumnMetadata> getColumns() throws SQLException {
		if (columns == null) {
			Map<String, ProcedureColumnMetadata> columnsMap = new HashMap<>();
			ResultSet rs = getMetadata().getProcedureColumns(getCatalog(), getSchema(), getName(), null);
			while (rs.next()) {
				ProcedureColumnMetadata cm = new ProcedureColumnMetadata(rs, getMetadata());
				columnsMap.put(cm.getName(), cm);
			}
			columns = new ArrayList<>(columnsMap.values());
			columns.sort((BasicColumnMetadata o1, BasicColumnMetadata o2) -> o1.getIdx() - o2.getIdx());
		}
		return columns;
	}

	@Override
	public String getId() {
		StringBuilder st = new StringBuilder();
		st.append("Procedure.");
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
