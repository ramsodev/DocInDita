package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;

public abstract class AbstractMetadata implements BasicMetadata {
	private String name;
	private String catalog;
	private String description;
	private DatabaseMetaData metadata;

	public AbstractMetadata(String name, String catalog, String description, DatabaseMetaData metadata) {
		super();
		this.name = name;
		this.catalog = catalog;
		this.description = description;
		this.metadata = metadata;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ramso.doc.dita.db.metadata.BasicMetadata#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.ramso.doc.dita.db.metadata.BasicMetadata#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	
	public DatabaseMetaData getMetadata() {
		return metadata;
	}

	
	public void setMetadata(DatabaseMetaData metadata) {
		this.metadata = metadata;
	}

}
