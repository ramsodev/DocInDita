package net.ramso.doc.dita.xml.wsdl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import net.ramso.doc.dita.xml.schema.GenerateSchema;
import net.ramso.tools.Constants;

public class GenerateWsdl {

	public GenerateWsdl() {
		super();
		init();
	}

	private void init() {
		// TODO: Usar fichero de properties y Verificar configuracion
		String p = Thread.currentThread().getContextClassLoader().getResource("velocity.properties").getPath();
		Velocity.init(p);
	}

	public void generateWSDL(String url) throws MalformedURLException, IOException, URISyntaxException {
		generateWSDL(new URL(url));

	}

	public void generateWSDL(URL url) throws IOException, URISyntaxException {
		
		String fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
		if (fileName.contains("?")) {
			fileName = fileName.substring(0, fileName.lastIndexOf('?'));
		} else if  (fileName.contains(".")){
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		}
		WSDLParser parser = new WSDLParser();
		WSDLParserContext ctx = new WSDLParserContext();
		ctx.setInput( url.toExternalForm());
//		if(url.getProtocol().startsWith("file")) {
//			File f = new File(url.getPath());
//			ctx.setBaseDir(f.getParent());
//		}
		Definitions desc = parser.parse(ctx);
		String content = "Definición del Servicio Web " + fileName;
		if (desc.getDocumentation() != null) {
			if (!desc.getDocumentation().getContent().isEmpty()) {
				content = desc.getDocumentation().getContent();
			}
		}
		ArrayList<References> index = new ArrayList<References>();

		List<Service> services = desc.getServices();
		for (Service service : services) {
			content = "Definiciones del servicio " + service.getName();
			if (service.getDocumentation() != null) {
				content = service.getDocumentation().getContent();
			}
			CreatePortada cc = new CreatePortada(service.getName() + "Service",
					"Documentacion del Servicio " + service.getName(), content);
			References partService = new References(cc.create());
			cc = null;
			CreatePorts ce = new CreatePorts(service.getName());
			partService.addChild(new References(ce.create(service.getPorts())));
			ce = null;
			content = "Operaciones del servicio " + service.getName();

			cc = new CreatePortada(service.getName() + "Operations", "Operaciones de " + service.getName(), content);
			References chapter = new References(cc.create());
			cc = null;
			for (Operation operation : desc.getOperations()) {
				content = "Metodos de la operación " + operation.getName();
				if (operation.getDocumentation() != null) {
					content = operation.getDocumentation().getContent();
				}
				CreateOperation co = new CreateOperation(operation.getName() + Constants.SUFFIX_OPERATION,
						"Operation " + operation.getName(), content);
				chapter.addChild(new References(co.create(operation)));
			}

			partService.addChild(chapter);
			index.add(partService);
		}

		List<Schema> schemas = desc.getSchemas();
		for (Schema schema : schemas) {
			GenerateSchema gs = new GenerateSchema();
			index.addAll(gs.generateSchema(schema, true));
			
		}
		content = "";
		if (desc.getDocumentation() != null) {
			content = desc.getDocumentation().getContent();
		}
		CreateBookMap cb = new CreateBookMap(fileName, "Documentación  del WSDL " + fileName, content);
		cb.create(index);
	}
}
