package net.ramso.doc.dita.xml;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
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
		URL echo = Thread.currentThread().getContextClassLoader().getResource("echo.wsdl");
		try {
			generate.generateWSDL(echo);
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

	@Test
	@DisplayName("Procesar Simulacion")
	void testGenerateWSDL2() {
		URL echo = Thread.currentThread().getContextClassLoader().getResource("simulacion.wsdl");
		try {
			generate.generateWSDL(echo);
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

	@Test
	@DisplayName("Procesar Tarificador")
	void testGenerateWSDL3() {
		URL echo;
		try {
			echo = new URL("https://svd.almudenaseguros.es/services/tarificador?wsdl");
			generate.generateWSDL(echo);
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

	@Test
	@DisplayName("Procesar Sepa")
	void testGenerateWSDL4() {

		try {
			File f = new File(
					"/home/jjescudero/ownCloud/GBP/sepa/documentacion/iberpay/WebServices_Entities_v1.04/Entities/WSDLs/Exposed_WS_ENVIO_TRANSFERENCIA.wsdl");
			URL echo = f.toURI().toURL();

			generate.generateWSDL(echo);
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}
}
