package net.ramso.docindita.db.test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.ramso.docindita.db.metadata.CatalogMetadata;
import net.ramso.docindita.db.metadata.ColumnMetadata;
import net.ramso.docindita.db.metadata.ConnectionMetadata;
import net.ramso.docindita.db.metadata.IndexMetadata;
import net.ramso.docindita.db.metadata.SchemaMetadata;
import net.ramso.docindita.db.metadata.TableMetadata;

class ConnectionMetadataTest extends BaseTest {

	private Connection con;
	private ConnectionMetadata meta;

	@BeforeEach
	void setUp() throws Exception {
		con = getConnection();
		meta = new ConnectionMetadata(con);
	}

	@AfterEach
	void tearDown() throws Exception {
		disconnect(con);
	}

	@Test
	void testGetCatalogs() {
		try {
			Collection<CatalogMetadata> c = meta.getCatalogs();
			for (CatalogMetadata catalog : c) {
				System.out.println(catalog);
				for (SchemaMetadata schema : catalog.getSchemas()) {
					System.out.println("-->" + schema);
					for (TableMetadata table : schema.getTables()) {
						System.out.println("---->" + table);
						for (ColumnMetadata column : table.getColumns()) {
							System.out.println("------>" + column);
						}
						for(IndexMetadata index:table.getIndex()) {
							System.out.println("------>" + index);
						}
					}

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Por exception");
		}
	}

	@Test
	void testGetschemass() {
		try {
			for (SchemaMetadata schema : meta.getSchemas()) {
				System.out.println("-->" + schema);
				for (TableMetadata table : schema.getTables()) {
					System.out.println("---->" + table);
					for (ColumnMetadata column : table.getColumns()) {
						System.out.println("------>" + column);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Por exception");
		}
	}
	@Test
	void getSchema() {
		System.out.println("Current Schema Test");
		try {
			System.out.println("--> " + meta.getSchema());
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Por exception");
		}
		
	}

}
