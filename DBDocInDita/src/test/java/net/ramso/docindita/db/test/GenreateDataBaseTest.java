package net.ramso.docindita.db.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.sql.Connection;

import org.apache.derby.catalog.GetProcedureColumns;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.ramso.docindita.db.Config;
import net.ramso.docindita.db.GenerateDataBase;
import net.ramso.docindita.tools.DitaConstants;

class GenreateDataBaseTest extends BaseTest {

	private GenerateDataBase generate;
	

	@BeforeEach
	void setUp() throws Exception {
		Config.start();
		Connection con = getConnection();
		this.generate = new GenerateDataBase(con);
	}

	@Test
	@DisplayName("Catalogos")
	void testGenerateCatalog() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "catalogs");
		clean();
		try {
			this.generate.generateCatalogs();
			assertTrue(valid(),"Fallo en la validación de los xml");
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

	@Test
	@DisplayName("schemas")
	void testGenerateSchemas() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "schemas");
		clean();
		try {
			this.generate.generateSchemas();
			assertTrue(valid(),"Fallo en la validación de los xml");
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@DisplayName("Default schema")
	void testGenerateSchema() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "schema");
		clean();
		try {
			this.generate.generateSchema();
			assertTrue(valid(),"Fallo en la validación de los xml");
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@DisplayName("Schema EMPEX")
	void testGenerateEMPEX() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "EMPEX");
		clean();
		try {
			this.generate.generateSchema("EMPEX");
			assertTrue(valid(),"Fallo en la validación de los xml");
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}


}
