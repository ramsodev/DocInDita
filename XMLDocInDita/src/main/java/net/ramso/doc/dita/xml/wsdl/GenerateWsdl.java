package net.ramso.doc.dita.xml.wsdl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.Schema;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Service;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wsdl.WSDLParserContext;

import net.ramso.doc.Config;
import net.ramso.doc.dita.CreateBookMap;
import net.ramso.doc.dita.CreatePortada;
import net.ramso.doc.dita.References;
import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.tools.DitaTools;
import net.ramso.doc.dita.xml.schema.GenerateSchema;
import net.ramso.doc.dita.xml.wsdl.graph.WSDLGraph;
import net.ramso.tools.LogManager;

public class GenerateWsdl {

	public GenerateWsdl() {
		super();
	}

	private References findRef(String idSchema, ArrayList<References> index) {
		for (final References ref : index) {
			if (ref.getId().equalsIgnoreCase(idSchema))
				return ref;
		}
		return null;
	}

	public References generateWSDL(String url) throws MalformedURLException, IOException, URISyntaxException {
		return generateWSDL(new URL(url), false);
	}

	public References generateWSDL(URL url) throws IOException, URISyntaxException {
		return generateWSDL(url, false);
	}

	public References generateWSDL(URL url, boolean one) throws IOException, URISyntaxException {
		final String fileName = DitaTools.getName(url.toExternalForm());
		LogManager.info("Inicio de Procesado de " + url);
		final WSDLParser parser = new WSDLParser();
		final WSDLParserContext ctx = new WSDLParserContext();
		if (url.getProtocol().startsWith("file")) {
			final String p = url.getPath();
			ctx.setInput(p);
		} else {
			ctx.setInput(url.toExternalForm());
		}
		final Definitions desc = parser.parse(ctx);
		String content = "Definición del Servicio Web " + fileName;
		if (desc.getDocumentation() != null) {
			if (!desc.getDocumentation().getContent().isEmpty()) {
				content = desc.getDocumentation().getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			}
		}
		final ArrayList<References> index = new ArrayList<>();
		final List<Service> services = desc.getServices();
		for (final Service service : services) {
			content = "Definiciones del servicio " + service.getName();
			if (service.getDocumentation() != null) {
				content = service.getDocumentation().getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			}
			final WSDLGraph graph = new WSDLGraph(service);
			CreatePortada cc = new CreatePortada(service.getName() + DitaConstants.SUFFIX_SERVICE,
					"Documentacion del Servicio " + service.getName(), content);
			cc.setDiagram(graph.generate());
			final References chapterService = new References(cc.create());
			cc = null;
			CreatePorts ce = new CreatePorts(service.getName());
			chapterService.addChild(new References(ce.create(service.getPorts())));
			ce = null;
			content = "Operaciones del servicio " + service.getName();

			cc = new CreatePortada(service.getName() + DitaConstants.SUFFIX_OPERATION + "s",
					"Operaciones de " + service.getName(), content);
			final References chapter = new References(cc.create());
			cc = null;
			for (final Operation operation : desc.getOperations()) {
				content = "Metodos de la operación " + operation.getName();
				if (operation.getDocumentation() != null) {
					content = operation.getDocumentation().getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
				}
				final CreateOperation co = new CreateOperation(operation.getName() + DitaConstants.SUFFIX_OPERATION,
						"Operation " + operation.getName(), content);
				if (chapter.searchChild(co.getId()) == null) {
					chapter.addChild(new References(co.create(operation)));
				}
			}

			chapterService.addChild(chapter);
			index.add(chapterService);
		}
		final List<Schema> schemas = desc.getSchemas();
		for (final Schema schema : schemas) {
			final String idSchema = (DitaTools.getSchemaId(schema.getTargetNamespace()) + DitaConstants.SUFFIX_SERVICE)
					.replaceAll("\\s+", "_");
			;
			final References cover = findRef(idSchema, index);
			final GenerateSchema gs = new GenerateSchema();
			if (cover == null) {
				final References ref = gs.generateSchema(schema, fileName, false);
				index.add(ref);
			} else {
				gs.append2Schema(cover, schema);
			}
		}
		content = "";
		if (desc.getDocumentation() != null) {
			content = desc.getDocumentation().getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		}
		LogManager.info("Fin procesado " + url);
		if (!one) {
			String id = Config.getId();
			if ((id == null) || id.isEmpty()) {
				id = fileName;
			}
			String title = Config.getTitle();
			if ((title == null) || title.isEmpty()) {
				title = "Documentación  del WSDL " + fileName;
			}
			String description = Config.getDescription();
			if ((description == null) || description.isEmpty()) {
				description = content;
			}
			final CreateBookMap cb = new CreateBookMap(id, title, description);
			cb.create(index);
			return null;
		} else {
			final CreatePortada cc = new CreatePortada(fileName + DitaConstants.SUFFIX_WSDL,
					"Documentación  del WSDL " + fileName, content);

			final References ref = new References(cc.create());
			ref.getChilds().addAll(index);
			return ref;
		}
	}
}
