package net.ramso.docindita.xml;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Service;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wsdl.WSDLParserContext;

import net.ramso.docindita.xml.wsdl.graph.WSDLGraph;

class WSDLGraphTest {

	@BeforeEach
	void setUp() throws Exception {
		Config.start();

	}

	@Test
	@DisplayName("Procesar as400")
	void testGenerateWSDL5() {
		try {
			final URL url = Thread.currentThread().getContextClassLoader().getResource("iban.wsdl");
			final WSDLParser parser = new WSDLParser();
			final WSDLParserContext ctx = new WSDLParserContext();
			if (url.getProtocol().startsWith("file")) {
				final String p = url.getPath();
				ctx.setInput(p);
			} else {
				ctx.setInput(url.toExternalForm());
			}
			final Definitions desc = parser.parse(ctx);
			final List<Service> services = desc.getServices();
			for (final Service service : services) {
				WSDLGraph generate = new WSDLGraph(service);
				generate.generate();
			}
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		assertTrue(true);
	}

}
