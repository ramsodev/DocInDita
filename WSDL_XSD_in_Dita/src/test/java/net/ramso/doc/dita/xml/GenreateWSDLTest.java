package net.ramso.doc.dita.xml;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.ramso.doc.dita.xml.wsdl.GenerateWsdl;

class GenreateWSDLTest {

	private GenerateWsdl generate;

	@BeforeEach
	void setUp() throws Exception {
		generate = new GenerateWsdl();
	}

	@Test
	@DisplayName("Procesar echo")
	void testGenerateWSDL() {
		URL echo = Thread.currentThread().getContextClassLoader().getResource("simulacion.wsdl");
		try {
			generate.generateWSDL(echo);
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

}
