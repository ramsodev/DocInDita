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
import org.testcontainers.containers.MariaDBContainer;

import net.ramso.docindita.db.Config;
import net.ramso.docindita.db.GenerateDataBase;
import net.ramso.docindita.tools.DitaConstants;

/**
 * Test class with embebed derby
 * 
 * @author ramso
 *
 */
class GenerateDataBaseMariaDbContainerTest extends BaseTest {

	private static GenerateDataBase generate;

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
//		container.addFileSystemBind("DB/mysql", "/var/lib/mysql", BindMode.READ_WRITE);
		container.addEnv("MYSQL_ROOT_PASSWORD", "admin");
		container.withUsername("sakila");
		container.withPassword("sakila");
		container.withDatabaseName("sakila");
		container.start();
		String jdbcUrl = container.getJdbcUrl();
		String username = container.getUsername();
		String password = container.getPassword();
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
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "mysql/catalogs");
		clean();
		try {
			generate.generateCatalogs();
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
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "mysql/schemas");
		clean();
		try {
			generate.generateSchemas();
			assertTrue(valid(), "Fallo en la validación de los xml");
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@DisplayName("Default schema")
	void testGenerateSchema() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "mysql/currentSchema");
		clean();
		try {
			generate.generateSchema();
			assertTrue(valid(), "Fallo en la validación de los xml");
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

}
