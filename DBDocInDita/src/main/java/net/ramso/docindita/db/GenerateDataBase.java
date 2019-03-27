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

	private ConnectionMetadata connection;
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
		if ((id == null) || id.isEmpty()) {
			try {
				id = connection.getId();
			} catch (SQLException e) {
				id = "id" + hashCode();
			}
		}
		this.title = Config.getTitle();
		if ((title == null) || title.isEmpty()) {
			title = BundleManager.getString("Generator.title");
		}
		this.description = Config.getDescription();
		if ((description == null) || description.isEmpty()) {
			try {
				description = connection.getDescription();
			} catch (SQLException e) {
				description = BundleManager.getString("Database.description");
			}
		}
	}

	public void generateCatalogs() throws SQLException, IOException {
		List<References> refs = new ArrayList<>();
		for (CatalogMetadata catalog : connection.getCatalogs()) {
			refs.add(generateCatalog(catalog));
		}
		createBookmap(refs);
	}

	public void generateSchema() throws IOException, SQLException {
		SchemaMetadata schema = connection.getSchema();
		References ref = (generateSchema(schema));
		id = schema.getId();
		title = BundleManager.getString("Schema.title", schema.getName());
		description = schema.getDoc();
		createBookmap(ref.getChilds());
	}

	public void generateSchema(String name) throws IOException, SQLException {
		SchemaMetadata schema = connection.getSchema(name);
		References ref = (generateSchema(schema));
		id = schema.getId();
		title = BundleManager.getString("Schema.title", schema.getName());
		description = schema.getDoc();
		createBookmap(ref.getChilds());
	}

	public void generateSchemas() throws IOException, SQLException {
		List<References> refs = new ArrayList<>();
		for (SchemaMetadata schema : connection.getSchemas()) {
			refs.add(generateSchema(schema));
		}
		createBookmap(refs);
	}

	private void createBookmap(List<References> refs) throws IOException {
		final CreateBookMap cb = new CreateBookMap(id, title, description);
		cb.create(refs);
	}

	private References generateCatalog(CatalogMetadata catalog) throws IOException, SQLException {
		CreateCatalog create = new CreateCatalog();
		References ref = create.create(catalog);
		for (SchemaMetadata schema : catalog.getSchemas()) {
			ref.addChild(generateSchema(schema));
		}
		return ref;
	}

	private References generateSchema(SchemaMetadata schema) throws IOException, SQLException {
		CreateSchema create = new CreateSchema();
		References ref = create.create(schema);
		for (TableMetadata table : schema.getTables()) {
			ref.addChild(generateTable(table));
		}
		return ref;
	}

	private References generateTable(TableMetadata table) throws IOException {
		CreateTable create = new CreateTable();
		return create.create(table);
	}
}
