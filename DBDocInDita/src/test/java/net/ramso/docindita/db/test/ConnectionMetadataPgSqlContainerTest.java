package net.ramso.docindita.db.test;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import net.ramso.docindita.db.Config;
import net.ramso.docindita.db.metadata.CatalogMetadata;
import net.ramso.docindita.db.metadata.ColumnMetadata;
import net.ramso.docindita.db.metadata.ConnectionMetadata;
import net.ramso.docindita.db.metadata.ForeingKeyMetadata;
import net.ramso.docindita.db.metadata.FunctionMetadata;
import net.ramso.docindita.db.metadata.IndexMetadata;
import net.ramso.docindita.db.metadata.PrimaryKeyMetadata;
import net.ramso.docindita.db.metadata.ProcedureMetadata;
import net.ramso.docindita.db.metadata.SchemaMetadata;
import net.ramso.docindita.db.metadata.TableMetadata;
import net.ramso.docindita.db.metadata.UDTMetadata;

/**
 * Class Test with Postgres SQL container
 * 
 * @author ramso
 *
 */
class ConnectionMetadataPgSqlContainerTest extends BaseTest {

	private static ConnectionMetadata meta;

	@ClassRule
	public static PostgreSQLContainer container;

	@BeforeAll
	static void setUp() throws Exception {
		Config.start();
		// Connection con = getConnection();
		container = new PostgreSQLContainer<>();
		container.withClasspathResourceMapping("pgsql", "/docker-entrypoint-initdb.d", BindMode.READ_ONLY);
//		container.addFileSystemBind("DB/pgsql", "/var/lib/postgresql/data", BindMode.READ_WRITE);
		container.addEnv("POSTGRES_PASSWORD", "admin");
//		container.withUsername("postgres");
//		container.withPassword("postgres");
//		container.withDatabaseName("sakila");
		container.start();
		String jdbcUrl = container.getJdbcUrl();

		String username = container.getUsername();
		String password = container.getPassword();
		System.out.println(jdbcUrl + "-" + username + "-" + password + "-" + container.getDatabaseName());
		try {
			Connection con = DriverManager.getConnection(jdbcUrl, username, password);
			meta = new ConnectionMetadata(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	@AfterAll
	static void tearDown() throws Exception {
		meta.disconnect();

	}
//	@Test
//	@DisplayName("Get Catalogs")
//	void testGetCatalogs() {
//		try {
//			Collection<CatalogMetadata> c = meta.getCatalogs();
//			System.out.println("Test Catalogos:");
//			for (CatalogMetadata catalog : c) {
//				System.out.println(catalog);
//				for (SchemaMetadata schema : catalog.getSchemas()) {
//					System.out.println("-->" + schema);
//					for (TableMetadata table : schema.getTables()) {
//						System.out.println("---->" + table);
//						for (ColumnMetadata column : table.getColumns()) {
//							System.out.println("------>" + column);
//						}
//						for (PrimaryKeyMetadata pk : table.getPrimaryKeys()) {
//							System.out.println("------> " + pk);
//						}
//						for (ForeingKeyMetadata fk : table.getForeingKeys()) {
//							System.out.println("------>" + fk);
//						}
//						for (IndexMetadata index : table.getIndex()) {
//							System.out.println("------>" + index);
//						}
//					}
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail("Por exception");
//		}
//	}
//
//	@Test
//	@DisplayName("Get Schemas")
//	void testGetschemass() {
//		try {
//			System.out.println("Test schemas");
//			for (SchemaMetadata schema : meta.getSchemas()) {
//				System.out.println("-->" + schema);
//				for (TableMetadata table : schema.getTables()) {
//					System.out.println("---->" + table);
//					for (ColumnMetadata column : table.getColumns()) {
//						System.out.println("------>" + column);
//					}
//					for (PrimaryKeyMetadata pk : table.getPrimaryKeys()) {
//						System.out.println("------>" + pk);
//					}
//					for (ForeingKeyMetadata fk : table.getForeingKeys()) {
//						System.out.println("------>" + fk);
//					}
//					for (IndexMetadata index : table.getIndex()) {
//						System.out.println("------>" + index);
//					}
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail("Por exception");
//		}
//	}
//
//	@Test
//	@DisplayName("Get Schema")
//	void getSchema() {
//		System.out.println("Test Current Schema");
//		try {
//			SchemaMetadata schema = meta.getSchema();
//			System.out.println("-->" + schema);
//			for (TableMetadata table : schema.getTables()) {
//				System.out.println("---->" + table);
//				for (ColumnMetadata column : table.getColumns()) {
//					System.out.println("------>" + column);
//				}
//				for (PrimaryKeyMetadata pk : table.getPrimaryKeys()) {
//					System.out.println("------>" + pk);
//				}
//				for (ForeingKeyMetadata fk : table.getForeingKeys()) {
//					System.out.println("------>" + fk);
//				}
//				for (IndexMetadata index : table.getIndex()) {
//					System.out.println("------>" + index);
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail("Por exception");
//		}
//
//	}
	@Test
	@DisplayName("Get Schema Functions")
	void getSchema() {
		System.out.println("Test Current Schema for functions");
		try {
			SchemaMetadata schema = meta.getSchema();
			System.out.println("-->" + schema);
			for(FunctionMetadata function:schema.getFunctions()) {
				System.out.println("---->" + function);
			}
			for(ProcedureMetadata procedure:schema.getProcedures()) {
				System.out.println("---->" + procedure);
			}
			for(UDTMetadata udt:schema.getUDTs()) {
				System.out.println("---->" + udt);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Por exception");
		}

	}
}
