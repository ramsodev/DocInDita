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
 * Class Test with mariadb container
 * 
 * @author ramso
 *
 */
class ConnectionMetadataMariaDBContainerTest extends BaseTest {

	private static ConnectionMetadata meta;

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static MariaDBContainer container;

	@SuppressWarnings("rawtypes")
	@BeforeAll
	static void setUp() throws Exception {
		Config.start();
		// Connection con = getConnection();
		container = new MariaDBContainer();
		container.withClasspathResourceMapping("mysql", "/docker-entrypoint-initdb.d", BindMode.READ_ONLY);
		// container.addFileSystemBind("DB/mysql", "/var/lib/mysql",
		// BindMode.READ_WRITE);
		container.addEnv("MYSQL_ROOT_PASSWORD", "admin");
		container.withUsername("sakila");
		container.withPassword("sakila");
		container.withDatabaseName("sakila");
		container.start();
		final String jdbcUrl = container.getJdbcUrl();
		final String username = container.getUsername();
		final String password = container.getPassword();
		try {
			final Connection con = DriverManager.getConnection(jdbcUrl, username, password);
			meta = new ConnectionMetadata(con);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	@AfterAll
	static void tearDown() throws Exception {
		meta.disconnect();
	}
//
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
//	for (final FunctionMetadata function : schema.getFunctions()) {
//	System.out.println("---->" + function);
//}
//for (final ProcedureMetadata procedure : schema.getProcedures()) {
//	System.out.println("---->" + procedure);
//}
//for (final UDTMetadata udt : schema.getUDTs()) {
//	System.out.println("---->" + udt);
//}
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
//					
//				}
//	for (final FunctionMetadata function : schema.getFunctions()) {
//		System.out.println("---->" + function);
//	}
//	for (final ProcedureMetadata procedure : schema.getProcedures()) {
//		System.out.println("---->" + procedure);
//	}
//	for (final UDTMetadata udt : schema.getUDTs()) {
//		System.out.println("---->" + udt);
//	}
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
//				
//			}
//	for (final FunctionMetadata function : schema.getFunctions()) {
//		System.out.println("---->" + function);
//	}
//	for (final ProcedureMetadata procedure : schema.getProcedures()) {
//		System.out.println("---->" + procedure);
//	}
//	for (final UDTMetadata udt : schema.getUDTs()) {
//		System.out.println("---->" + udt);
//	}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail("Por exception");
//		}
//
//	}

	@Test
	@DisplayName("Get Schema Functions")
	void getSchemaTest() {
		System.out.println("Test Current Schema for functions");
		try {
			final SchemaMetadata schema = meta.getSchema();
			System.out.println("-->" + schema);
			for (TableMetadata table : schema.getViews()) {
				System.out.println(table);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			fail("Por exception");
		}

	}

}
