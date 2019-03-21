package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public interface BasicMetadata {
	void init(ResultSet resultSet);
	String getName();
	void setName(String name);
	String getDoc();
	void setDoc(String description);
	String getCatalog();
	void setCatalog(String catalog);
	String getSchema();
	void setSchema(String schema);
	DatabaseMetaData getMetadata();
	void setMetadata(DatabaseMetaData metadata);
	
}
