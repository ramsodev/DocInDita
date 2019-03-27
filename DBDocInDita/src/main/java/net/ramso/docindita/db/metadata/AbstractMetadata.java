package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public abstract class AbstractMetadata implements BasicMetadata {
	private String name;
	private String catalog;
	private String schema;
	private String doc;
	private DatabaseMetaData metadata;

	public AbstractMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super();
		this.metadata = metadata;
		init(resultSet);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCatalog() {
		return catalog;
	}

	@Override
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	@Override
	public DatabaseMetaData getMetadata() {
		return metadata;
	}

	@Override
	public void setMetadata(DatabaseMetaData metadata) {
		this.metadata = metadata;
	}

	@Override
	public String getSchema() {
		return schema;
	}

	@Override
	public void setSchema(String schema) {
		this.schema = schema;
	}

	@Override
	public String getDoc() {
		return doc;
	}

	@Override
	public void setDoc(String doc) {
		this.doc = doc;
	}

	public String getId() {
		StringBuilder st = new StringBuilder();
		if (getCatalog() != null && !getCatalog().isEmpty()) {
			st.append(getCatalog());
			st.append(".");
		}
		if (getSchema() != null && !getSchema().isEmpty()) {
			st.append(getSchema());
			st.append(".");
		}
		return st.toString();
	}

}
