/**
 * 
 */
package net.ramso.doc.dita.db.metadata;

import java.sql.DatabaseMetaData;

/**
 * @author ramso
 *
 */
public class TableMetadata extends AbstractMetadata{
	
	private SchemaMetadata schema;
	private String type;
//	private ColumnsMetadata[] columns;
//	private PKMetadata[] pks;

	public TableMetadata(String name, SchemaMetadata schema, String type, String description) {
		super(name, schema.getCatalog(), description, schema.getMetadata());
		this.schema = schema;
		this.type= type;
	}
}
