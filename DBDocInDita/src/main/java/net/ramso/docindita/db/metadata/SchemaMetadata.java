/**
 * 
 */
package net.ramso.docindita.db.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import net.ramso.docindita.db.DBConstants;
import net.ramso.tools.LogManager;

/**
 * @author ramso
 *
 */
public class SchemaMetadata extends AbstractMetadata {
	private Collection<TableMetadata> tables;
	private Collection<FunctionMetadata> functions;
	private Collection<UDTMetadata> udts;
	private Collection<ProcedureMetadata> procedures;

	public SchemaMetadata(ResultSet resultSet, DatabaseMetaData metadata) {
		super(resultSet, metadata);
	}

	public SchemaMetadata(CatalogMetadata catalog) {
		super(catalog.getMetadata());
		setCatalog(catalog.getName());
		setName(catalog.getName());
	}

	@Override
	public void init(ResultSet resultSet) {
		try {
			setName(resultSet.getString(DBConstants.METADATA_SCHEMA));
			setCatalog(resultSet.getString(DBConstants.METADATA_CATALOG));
			setSchema(getName());
		} catch (SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	public Collection<TableMetadata> getTables() throws SQLException {
		if (tables == null) {
			tables = new ArrayList<>();
			ResultSet rs = getMetadata().getTables(getCatalog(), getSchema(), null, new String[] { DBConstants.TABLE });
			while (rs.next()) {
				TableMetadata tm = new TableMetadata(rs, getMetadata());
				tables.add(tm);
			}
		}
		return tables;

	}

	public Collection<FunctionMetadata> getFunctions() throws SQLException {
		if (functions == null) {
			functions = new ArrayList<>();
			ResultSet rs = getMetadata().getFunctions(getCatalog(), getSchema(), null);
			while (rs.next()) {
				FunctionMetadata fm = new FunctionMetadata(rs, getMetadata());
				functions.add(fm);
			}
		}
		return functions;
	}
	
	public Collection<UDTMetadata> getUDTs() throws SQLException {
		if (udts == null) {
			udts = new ArrayList<>();
			ResultSet rs = getMetadata().getUDTs(getCatalog(), getSchema(),null, null);
			while (rs.next()) {
				UDTMetadata um = new UDTMetadata(rs, getMetadata());
				udts.add(um);
			}
		}
		return udts;
	}
	public Collection<ProcedureMetadata> getProcedures() throws SQLException {
		if (procedures == null) {
			procedures = new ArrayList<>();
			ResultSet rs = getMetadata().getProcedures(getCatalog(), getSchema(),null);
			while (rs.next()) {
				ProcedureMetadata um = new ProcedureMetadata(rs, getMetadata());
				procedures.add(um);
			}
		}
		return procedures;
	}
	@Override
	public String toString() {
		return getCatalog() + "." + getSchema();
	}

	@Override
	public String getDDL() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
