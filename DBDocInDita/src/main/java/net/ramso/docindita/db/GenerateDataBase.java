package net.ramso.docindita.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.ramso.docindita.CreateBookMap;
import net.ramso.docindita.References;
import net.ramso.docindita.db.metadata.CatalogMetadata;
import net.ramso.docindita.db.metadata.ConnectionMetadata;
import net.ramso.docindita.db.metadata.SchemaMetadata;
import net.ramso.docindita.db.metadata.TableMetadata;
import net.ramso.tools.BundleManager;

public class GenerateDataBase {

	private final ConnectionMetadata connection;
	private String id;
	private String title;
	private String description;

	public GenerateDataBase(Connection connection) {
		super();
		this.connection = new ConnectionMetadata(connection);
		init();
	}

	private void init() {
		this.id = Config.getId();
		if ((this.id == null) || this.id.isEmpty()) {
			try {
				this.id = this.connection.getId();
			} catch (final SQLException e) {
				this.id = "id" + hashCode();
			}
		}
		this.title = Config.getTitle();
		if ((this.title == null) || this.title.isEmpty()) {
			this.title = BundleManager.getString("Generator.title");
		}
		this.description = Config.getDescription();
		if ((this.description == null) || this.description.isEmpty()) {
			try {
				this.description = this.connection.getDescription();
			} catch (final SQLException e) {
				this.description = BundleManager.getString("Database.description");
			}
		}
	}

	public void generateCatalogs() throws SQLException, IOException {
		final List<References> refs = new ArrayList<>();
		for (final CatalogMetadata catalog : this.connection.getCatalogs()) {
			refs.add(generateCatalog(catalog));
		}
		createBookmap(refs);
	}

	public void generateSchema() throws IOException, SQLException {
		final SchemaMetadata schema = this.connection.getSchema();
		final References ref = (generateSchema(schema));
		this.id = schema.getId();
		this.title = BundleManager.getString("Schema.title", schema.getName());
		this.description = schema.getDoc();
		createBookmap(ref.getChilds());
	}

	public void generateSchema(String name) throws IOException, SQLException {
		final SchemaMetadata schema = this.connection.getSchema(name);
		final References ref = (generateSchema(schema));
		this.id = schema.getId();
		this.title = BundleManager.getString("Schema.title", schema.getName());
		this.description = schema.getDoc();
		createBookmap(ref.getChilds());
	}

	public void generateSchemas() throws IOException, SQLException {
		final List<References> refs = new ArrayList<>();
		for (final SchemaMetadata schema : this.connection.getSchemas()) {
			refs.add(generateSchema(schema));
		}
		createBookmap(refs);
	}

	private void createBookmap(List<References> refs) throws IOException {
		final CreateBookMap cb = new CreateBookMap(this.id, this.title, this.description);
		cb.create(refs);
	}

	private References generateCatalog(CatalogMetadata catalog) throws IOException, SQLException {
		final CreateCatalog create = new CreateCatalog();
		final References ref = create.create(catalog);
		for (final SchemaMetadata schema : catalog.getSchemas()) {
			ref.addChild(generateSchema(schema));
		}
		return ref;
	}

	private References generateSchema(SchemaMetadata schema) throws IOException, SQLException {
		final CreateSchema create = new CreateSchema();
		final References ref = create.create(schema);
		for (final TableMetadata table : schema.getTables()) {
			ref.addChild(generateTable(table));
		}
		for (final TableMetadata table : schema.getViews()) {
			ref.addChild(generateTable(table));
		}
		return ref;
	}

	private References generateTable(TableMetadata table) throws IOException {
		final CreateTable create = new CreateTable();
		return create.create(table);
	}

	public void disconnect() throws SQLException {
		this.connection.disconnect();
	}
}
