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
		} catch (final SQLException e) {
			LogManager.warn("Error al preparar esquema", e);
		}

	}

	public Collection<TableMetadata> getTables() throws SQLException {
		if (this.tables == null) {
			this.tables = new ArrayList<>();
			final ResultSet rs = getMetadata().getTables(getCatalog(), getSchema(), null,
					new String[] { DBConstants.TABLE });
			while (rs.next()) {
				final TableMetadata tm = new TableMetadata(rs, getMetadata());
				this.tables.add(tm);
			}
		}
		return this.tables;

	}
	
	public Collection<TableMetadata> getViews() throws SQLException {
		if (this.tables == null) {
			this.tables = new ArrayList<>();
			final ResultSet rs = getMetadata().getTables(getCatalog(), getSchema(), null,
					new String[] { DBConstants.VIEW });
			while (rs.next()) {
				final TableMetadata tm = new TableMetadata(rs, getMetadata());
				this.tables.add(tm);
			}
		}
		return this.tables;

	}

	public Collection<FunctionMetadata> getFunctions() throws SQLException {
		if (this.functions == null) {
			this.functions = new ArrayList<>();
			final ResultSet rs = getMetadata().getFunctions(getCatalog(), getSchema(), null);
			while (rs.next()) {
				final FunctionMetadata fm = new FunctionMetadata(rs, getMetadata());
				this.functions.add(fm);
			}
		}
		return this.functions;
	}

	public Collection<UDTMetadata> getUDTs() throws SQLException {
		if (this.udts == null) {
			this.udts = new ArrayList<>();
			final ResultSet rs = getMetadata().getUDTs(getCatalog(), getSchema(), null, null);
			while (rs.next()) {
				final UDTMetadata um = new UDTMetadata(rs, getMetadata());
				this.udts.add(um);
			}
		}
		return this.udts;
	}

	public Collection<ProcedureMetadata> getProcedures() throws SQLException {
		if (this.procedures == null) {
			this.procedures = new ArrayList<>();
			final ResultSet rs = getMetadata().getProcedures(getCatalog(), getSchema(), null);
			while (rs.next()) {
				final ProcedureMetadata um = new ProcedureMetadata(rs, getMetadata());
				this.procedures.add(um);
			}
		}
		return this.procedures;
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
