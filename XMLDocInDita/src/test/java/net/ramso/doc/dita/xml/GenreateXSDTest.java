package net.ramso.doc.dita.xml;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.ramso.docindita.xml.Config;
import net.ramso.docindita.xml.schema.GenerateXSD;

class GenreateXSDTest {

	private GenerateXSD generate;

	@BeforeEach
	void setUp() throws Exception {
		Config.start();
		generate = new GenerateXSD();

	}

	@Test
	@DisplayName("Procesar test.xsd")
	void testGenerateWADL1() {
		URL xsd = Thread.currentThread().getContextClassLoader().getResource("test.xsd");
		try {
			generate.generateXsd(xsd);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}



}
