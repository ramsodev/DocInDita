package net.ramso.doc.dita.xml;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Service;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wsdl.WSDLParserContext;

import net.ramso.doc.Config;
import net.ramso.doc.dita.xml.wsdl.GenerateWsdl;
import net.ramso.doc.dita.xml.wsdl.graph.WSDLGraph;

class WSDLGraphTest {

	private WSDLGraph generate;

	@BeforeEach
	void setUp() throws Exception {
		Config.start();

	}


	@Test
	@DisplayName("Procesar as400")
	void testGenerateWSDL5() {

		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource("echo.wsdl");
			WSDLParser parser = new WSDLParser();
			WSDLParserContext ctx = new WSDLParserContext();
			if (url.getProtocol().startsWith("file")) {
				String p = url.getPath();
				ctx.setInput(p);
			} else {
				ctx.setInput(url.toExternalForm());
			}
			Definitions desc = parser.parse(ctx);
			List<Service> services = desc.getServices();
			for (Service service : services) {
				generate = new WSDLGraph(service);
				generate.generate();
			}
		} catch (Exception e) {

			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

}
