package net.ramso.doc.dita.xml.wsdl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.Velocity;

import com.predic8.schema.Schema;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Service;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wsdl.WSDLParserContext;

import net.ramso.doc.dita.CreateBookMap;
import net.ramso.doc.dita.CreatePortada;
import net.ramso.doc.dita.References;
import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.tools.DitaTools;
import net.ramso.doc.dita.xml.schema.GenerateSchema;
import net.ramso.doc.dita.xml.wsdl.graph.WSDLGraph;

public class GenerateWsdl {

	public GenerateWsdl() {
		super();
	}

	public References generateWSDL(String url) throws MalformedURLException, IOException, URISyntaxException {
		return generateWSDL(new URL(url), false);
	}

	public References generateWSDL(URL url) throws IOException, URISyntaxException {
		return generateWSDL(url, false);
	}

	public References generateWSDL(URL url, boolean multi) throws IOException, URISyntaxException {
		String fileName = DitaTools.getName(url.toExternalForm());
		WSDLParser parser = new WSDLParser();
		WSDLParserContext ctx = new WSDLParserContext();
		if (url.getProtocol().startsWith("file")) {
			String p = url.getPath();
			ctx.setInput(p);
		} else {
			ctx.setInput(url.toExternalForm());
		}
		Definitions desc = parser.parse(ctx);
		String content = "Definición del Servicio Web " + fileName;
		if (desc.getDocumentation() != null) {
			if (!desc.getDocumentation().getContent().isEmpty()) {
				content = desc.getDocumentation().getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			}
		}
		ArrayList<References> index = new ArrayList<References>();
		List<Service> services = desc.getServices();
		for (Service service : services) {
			content = "Definiciones del servicio " + service.getName();
			if (service.getDocumentation() != null) {
				content = service.getDocumentation().getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			}
			WSDLGraph graph = new WSDLGraph(service);
			CreatePortada cc = new CreatePortada(service.getName() + DitaConstants.SUFFIX_SERVICE,
					"Documentacion del Servicio " + service.getName(), content);
			cc.setDiagram(graph.generate());
			References chapterService = new References(cc.create());
			cc = null;
			CreatePorts ce = new CreatePorts(service.getName());
			chapterService.addChild(new References(ce.create(service.getPorts())));
			ce = null;
			content = "Operaciones del servicio " + service.getName();

			cc = new CreatePortada(service.getName() + DitaConstants.SUFFIX_OPERATION + "s",
					"Operaciones de " + service.getName(), content);
			References chapter = new References(cc.create());
			cc = null;
			for (Operation operation : desc.getOperations()) {
				content = "Metodos de la operación " + operation.getName();
				if (operation.getDocumentation() != null) {
					content = operation.getDocumentation().getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
				}
				CreateOperation co = new CreateOperation(operation.getName() + DitaConstants.SUFFIX_OPERATION,
						"Operation " + operation.getName(), content);
				if (chapter.searchChild(co.getId()) == null) {
					chapter.addChild(new References(co.create(operation)));
				}
			}

			chapterService.addChild(chapter);
			index.add(chapterService);
		}
		List<Schema> schemas = desc.getSchemas();
		for (Schema schema : schemas) {
			String idSchema = (DitaTools.getSchemaId(schema.getTargetNamespace()) + DitaConstants.SUFFIX_SERVICE)
					.replaceAll("\\s+", "_");
			;
			References cover = findRef(idSchema, index);
			GenerateSchema gs = new GenerateSchema();
			if (cover == null) {
				References ref = gs.generateSchema(schema, true);
				index.add(ref);
			} else {
				gs.append2Schema(cover, schema);
			}
		}
		content = "";
		if (desc.getDocumentation() != null) {
			content = desc.getDocumentation().getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		}
		if (!multi) {
			CreateBookMap cb = new CreateBookMap(fileName, "Documentación  del WSDL " + fileName, content);
			cb.create(index);
			return null;
		} else {
			CreatePortada cc = new CreatePortada(fileName + DitaConstants.SUFFIX_WSDL,
					"Documentación  del WSDL " + fileName, content);
			return new References(cc.create());
		}

	}

	private References findRef(String idSchema, ArrayList<References> index) {
		for (References ref : index) {
			if (ref.getId().equalsIgnoreCase(idSchema)) {
				return ref;
			}
		}
		return null;
	}
}
