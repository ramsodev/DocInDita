package net.ramso.doc.dita.xml;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.ramso.docindita.xml.Config;
import net.ramso.docindita.xml.wadl.GenerateWadl;

class GenreateWADLTest {

	private GenerateWadl generate;

	@BeforeEach
	void setUp() throws Exception {
		Config.start();
		generate = new GenerateWadl();

	}

	@Test
	@DisplayName("Procesar storage")
	void testGenerateWADL() {
		URL storage = Thread.currentThread().getContextClassLoader().getResource("storage.wadl");
		try {
			generate.generateWADL(storage);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}


}
