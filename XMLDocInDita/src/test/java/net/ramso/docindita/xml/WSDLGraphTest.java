package net.ramso.docindita.xml;

import org.junit.jupiter.api.BeforeEach;

import net.ramso.docindita.xml.wsdl.graph.WSDLGraph;

class WSDLGraphTest {

	private WSDLGraph generate;

	@BeforeEach
	void setUp() throws Exception {
		Config.start();

	}

//	@Test
//	@DisplayName("Procesar as400")
//	void testGenerateWSDL5() {
//
//		try {
//			final URL url = Thread.currentThread().getContextClassLoader().getResource("echo.wsdl");
//			final WSDLParser parser = new WSDLParser();
//			final WSDLParserContext ctx = new WSDLParserContext();
//			if (url.getProtocol().startsWith("file")) {
//				final String p = url.getPath();
//				ctx.setInput(p);
//			} else {
//				ctx.setInput(url.toExternalForm());
//			}
//			final Definitions desc = parser.parse(ctx);
//			final List<Service> services = desc.getServices();
//			for (final Service service : services) {
//				generate = new WSDLGraph(service);
//				generate.generate();
//			}
//		} catch (final Exception e) {
//
//			e.printStackTrace();
//			fail(e.getLocalizedMessage());
//		}
//		assertTrue(true);
//	}

}
