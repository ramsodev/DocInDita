/**
 * 
 */
package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.ramso.docindita.db.DBConstants;

/**
 * @author ramso
 *
 */
public class SchemaMetadata extends AbstractMetadata {
	private ArrayList<TableMetadata> tables;

	public SchemaMetadata(String name, String catalog, String description, DatabaseMetaData metadata) {
		super(name, catalog, description, metadata);
	}

	public TableMetadata[] getTables() throws SQLException {
		ResultSet rs = getMetadata().getTables(getCatalog(), getName(), "*", null);
		while (rs.next()) {
			switch (rs.getString("TABLE_TYPE")) {
			case DBConstants.TABLE:
				tables.add(new TableMetadata(rs.getString("TABLE_NAME"), this, rs.getString("TABLE_TYPE"),
						rs.getString("REMARKS")));
				break;
			default:
				break;
			}
		}
		return null;

	}

}
