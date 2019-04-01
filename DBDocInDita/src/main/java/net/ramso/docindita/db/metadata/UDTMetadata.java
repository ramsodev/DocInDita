package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

public class UDTMetadata extends AbstractMetadata {

	private String type;

	private List<AttributeMetadata> attributes;

	public UDTMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			loadLabels(resultSet.getMetaData());
			if (labelExist(DBConstants.METADATA_TYPE_SCHEM)) {
				setSchema(resultSet.getString(DBConstants.METADATA_TYPE_SCHEM));
			}
			if (labelExist(DBConstants.METADATA_TYPE_CAT)) {
				setCatalog(resultSet.getString(DBConstants.METADATA_TYPE_CAT));
			}
			if (labelExist(DBConstants.METADATA_TYPE_NAME)) {
				setName(resultSet.getString(DBConstants.METADATA_TYPE_NAME));
			}
			if (labelExist(DBConstants.METADATA_TYPE_NAME)) {
				setType(resultSet.getString(DBConstants.METADATA_TYPE_NAME));
			}
			if (labelExist(DBConstants.METADATA_REMARKS)) {
				setDoc(resultSet.getString(DBConstants.METADATA_REMARKS));
			}

		} catch (final SQLException e) {
			LogManager.warn("Error al preparar columna", e);
		}

	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<AttributeMetadata> getAttributes() throws SQLException {
		if (this.attributes == null) {
			final Map<String, AttributeMetadata> attributesMap = new HashMap<>();
			try {
				final ResultSet rs = getMetadata().getAttributes(getCatalog(), getSchema(), getName(), null);
				while (rs.next()) {
					final AttributeMetadata cm = new AttributeMetadata(rs, getMetadata());
					attributesMap.put(cm.getName(), cm);
				}
				this.attributes = new ArrayList<>(attributesMap.values());
				this.attributes.sort((BasicColumnMetadata o1, BasicColumnMetadata o2) -> o1.getIdx() - o2.getIdx());
			} catch (final SQLFeatureNotSupportedException e) {
				LogManager.info("UDT Attributes no soportado");
				this.attributes = new ArrayList<>();
			}

		}
		return this.attributes;
	}

	@Override
	public String toString() {
		final StringBuilder st = new StringBuilder();
		st.append("UTD: ");
		st.append(getCatalog());
		st.append(".");
		st.append(getSchema());
		st.append(".");
		st.append(getName());
		st.append(" ");
		st.append(getType());
		st.append(" Attributes:");
		try {
			for (final BasicColumnMetadata column : getAttributes()) {
				st.append(System.lineSeparator());
				st.append("---------->");
				st.append(column.toString());
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return st.toString();
	}

	@Override
	public String getId() {
		final StringBuilder st = new StringBuilder();
		st.append(super.getId());
		st.append(getName());
		return st.toString();
	}

	@Override
	public String getDDL() {
		return getName();
	}

}
