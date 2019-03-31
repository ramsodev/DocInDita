package net.ramso.docindita.db.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;

import net.ramso.docindita.db.Config;
import net.ramso.docindita.db.GenerateDataBase;
import net.ramso.docindita.tools.DitaConstants;

/**
 * Test class with embebed derby
 * 
 * @author ramso
 *
 */
class GenerateDataBasePgSqlContainerTest extends BaseTest {

	private static GenerateDataBase generate;
	@SuppressWarnings("rawtypes")
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
			generate = new GenerateDataBase(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@AfterAll
	static void tearDown() throws Exception {
		generate.disconnect();

	}

	@Test
	@DisplayName("Catalogos")
	void testGenerateCatalog() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "pgsql/catalogs");
		clean();
		try {
			GenerateDataBasePgSqlContainerTest.generate.generateCatalogs();
			assertTrue(valid(), "Fallo en la validación de los xml");
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

	@Test
	@DisplayName("schemas")
	void testGenerateSchemas() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "pgsql/schemas");
		clean();
		try {
			GenerateDataBasePgSqlContainerTest.generate.generateSchemas();
			assertTrue(valid(), "Fallo en la validación de los xml");
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@DisplayName("Default schema")
	void testGenerateSchema() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "pgsql/currentSchema");
		clean();
		try {
			GenerateDataBasePgSqlContainerTest.generate.generateSchema();
			assertTrue(valid(), "Fallo en la validación de los xml");
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	

}
