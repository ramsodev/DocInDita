package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;

public interface BasicMetadata {
	String getName();
	void setName(String name);
	String getDescription();
	void setDescription(String description);
	String getCatalog();
	void setCatalog(String catalog);
	DatabaseMetaData getMetadata();
	void setMetadata(DatabaseMetaData metadata);
	
}
