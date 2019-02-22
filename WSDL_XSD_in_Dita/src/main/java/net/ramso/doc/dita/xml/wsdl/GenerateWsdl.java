package net.ramso.doc.dita.xml.wsdl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.Velocity;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

import net.ramso.doc.dita.CreateBookMap;

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

	public void generateWSDL(String url) throws WSDLException, MalformedURLException, IOException, URISyntaxException {
		generateWSDL(new URL(url));

	}

	public void generateWSDL(URL url) throws WSDLException, IOException, URISyntaxException {
		
		WSDLReader reader;
		ArrayList<String> index = new ArrayList<String>();
		reader = WSDLFactory.newInstance().newWSDLReader();
		Description desc = reader.read(url);
		List<Service> services = desc.getServices();
		for (Service service : services) {
			CreateService cc = new CreateService("Servicio " + service.getQName().getLocalPart(),
					service.getDocumentation().getContent());
			index.add(cc.create());
			cc = null;
			CreateEndPoints ce = new CreateEndPoints(service.getQName().getLocalPart());
			ce.create(service.getEndpoints());
			ce = null;
			CreateOperations co = new CreateOperations(service.getQName().getLocalPart());
			co.create(service.getInterface().getOperations());
			co = null;
		}
		List<Schema> schemas = desc.getTypes().getSchemas();
		String fileName = url.getPath().substring(url.getPath().lastIndexOf('/')+1);
		fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		String content = "";
		if(desc.getDocumentation()!=null) {
			content = desc.getDocumentation().getContent();
		}
		CreateBookMap cb = new CreateBookMap("Documentación  del WSDL " + fileName, content, fileName+".ditamap");
		cb.create(index);
	}
}
