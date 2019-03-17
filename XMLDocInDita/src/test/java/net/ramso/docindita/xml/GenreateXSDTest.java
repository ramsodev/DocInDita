package net.ramso.docindita.xml;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.xml.Config;
import net.ramso.docindita.xml.schema.GenerateXSD;

class GenreateXSDTest extends BaseTest {

	private GenerateXSD generate;

	@BeforeEach
	void setUp() throws Exception {
		Config.start();
		generate = new GenerateXSD();

	}

	@Test
	@DisplayName("Procesar enumattr")
	void testGenerateXSD0() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "enumattr");
		clean();
		URL xsd = Thread.currentThread().getContextClassLoader().getResource("enumattr.xsd");
		try {
			generate.generateXsd(xsd);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@DisplayName("Procesar EnumAttr2")
	void testGenerateXSD1() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "EnumAttr2");
		clean();
		URL xsd = Thread.currentThread().getContextClassLoader().getResource("EnumAttr2.xsd");
		try {
			generate.generateXsd(xsd);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@DisplayName("Procesar GlobalAttr")
	void testGenerateXSD2() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "GlobalAttr");
		clean();
		URL xsd = Thread.currentThread().getContextClassLoader().getResource("GlobalAttr.xsd");
		try {
			generate.generateXsd(xsd);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@DisplayName("Procesar order")
	void testGenerateXSD3() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "order");
		clean();
		URL xsd = Thread.currentThread().getContextClassLoader().getResource("order.xsd");
		try {
			generate.generateXsd(xsd);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@DisplayName("Procesar CustomerOrders")
	void testGenerateXSD4() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "CustomerOrders");
		clean();
		URL xsd = Thread.currentThread().getContextClassLoader().getResource("CustomerOrders.xsd");
		try {
			generate.generateXsd(xsd);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	@DisplayName("Procesar pacs0008")
	void testGenerateXSD() {
		Config.setOutputDir(DitaConstants.OUTDIR_DEFAULT + File.separator + "pacs008");
		URL xsd = Thread.currentThread().getContextClassLoader().getResource("pacs.008.001.08.xsd");
		try {
			generate.generateXsd(xsd);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

}
