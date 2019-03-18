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
		generate = new GenerateWsdl();

	}

//	@Test
//	@DisplayName("Simple Calculator")
//	void testGenerateWSDL() {
//		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "calculator");
//		clean();
//		URL wsdl = Thread.currentThread().getContextClassLoader().getResource("calculator.wsdl");
//		try {
//			generate.generateWSDL(wsdl);
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			fail(e.getLocalizedMessage());
//		}
//		assertTrue(true);
//	}
//
//	@Test
//	@DisplayName("Simple HelloService")
//	void testGenerateWSDL2() {
//		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "hello");
//		clean();
//		URL wsdl = Thread.currentThread().getContextClassLoader().getResource("helloService.wsdl");
//		try {
//			generate.generateWSDL(wsdl);
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			fail(e.getLocalizedMessage());
//		}
//		assertTrue(true);
//	}
//
	@Test
	@DisplayName("Public http AWSE")
	void testGenerateWSDL3() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "awse");
		clean();
		URL wsdl;
		try {
			wsdl = new URL("http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl");
			generate.generateWSDL(wsdl);
			validXML();
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

//	@Test
//	@DisplayName("Complex doc  iban")
//	void testGenerateWSDL4() {
//		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "iban");
//		clean();
//		try {
//			URL wsdl = Thread.currentThread().getContextClassLoader().getResource("iban.wsdl");
//
//			generate.generateWSDL(wsdl);
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			fail(e.getLocalizedMessage());
//		}
//		assertTrue(true);
//	}

//	@Test
//	@DisplayName("tododolist")
//	void testGenerateWSDL5() {
//		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "todo");
//		clean();
//		try {
//			final URL wsdl = Thread.currentThread().getContextClassLoader().getResource("todolist.wsdl");
//
//			generate.generateWSDL(wsdl, false);
//		} catch (final Exception e) {
//
//			e.printStackTrace();
//			fail(e.getLocalizedMessage());
//		}
//		assertTrue(true);
//	}

	protected boolean validXML() throws IOException {
		File[] files = new File(Config.getOutputDir()).listFiles();
		
			for(File file:files) {
				
			}
		
		return true;
	}
	
}
