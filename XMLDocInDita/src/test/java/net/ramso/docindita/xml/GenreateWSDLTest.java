package net.ramso.docindita.xml;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.xml.wsdl.GenerateWsdl;

class GenreateWSDLTest extends BaseTest {

	private GenerateWsdl generate;

	@BeforeEach
	void setUp() throws Exception {
		Config.start();
		this.generate = new GenerateWsdl();

	}

	@Test
	@DisplayName("Simple Calculator")
	void testGenerateWSDL() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "calculator");
		clean();
		final URL wsdl = Thread.currentThread().getContextClassLoader().getResource("calculator.wsdl");
		try {
			this.generate.generateWSDL(wsdl);
		} catch (final Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

	@Test
	@DisplayName("Simple HelloService")
	void testGenerateWSDL2() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "hello");
		clean();
		final URL wsdl = Thread.currentThread().getContextClassLoader().getResource("helloService.wsdl");
		try {
			this.generate.generateWSDL(wsdl);
		} catch (final Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

	@Test
	@DisplayName("Public http AWSE")
	void testGenerateWSDL3() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "awse");
		clean();
		URL wsdl;
		try {
			wsdl = new URL("http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl");
			this.generate.generateWSDL(wsdl);
			validXML();
		} catch (final Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

	@Test
	@DisplayName("Complex doc  iban")
	void testGenerateWSDL4() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "iban");
		clean();
		try {
			final URL wsdl = Thread.currentThread().getContextClassLoader().getResource("iban.wsdl");

			this.generate.generateWSDL(wsdl);
		} catch (final Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

	@Test
	@DisplayName("tododolist")
	void testGenerateWSDL5() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "todo");
		clean();
		try {
			final URL wsdl = Thread.currentThread().getContextClassLoader().getResource("todolist.wsdl");

			this.generate.generateWSDL(wsdl, false);
		} catch (final Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

	protected boolean validXML() throws IOException {
		final File[] files = new File(Config.getOutputDir()).listFiles();

		for (final File file : files) {

		}

		return true;
	}

}
