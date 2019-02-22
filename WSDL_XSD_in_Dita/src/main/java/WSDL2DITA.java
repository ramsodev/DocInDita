import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.Velocity;
import org.ow2.easywsdl.schema.api.XmlException;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

import net.ramso.doc.dita.xml.wsdl.CreateChapter;
import net.ramso.doc.dita.xml.wsdl.CreateEndPoints;
import net.ramso.doc.dita.xml.wsdl.CreateOperations;

public class WSDL2DITA {
	public WSDL2DITA() {
		super();
		init();
	}

	private void init() {
		//TODO: Configurar
		String p = Thread.currentThread().getContextClassLoader().getResource("velocity.properties").getPath();
		Velocity.init(p);
	}
	
	private void generateWSDL(String url) {
		WSDLReader reader;
		ArrayList<String> index = new ArrayList<String>();
		try {
			reader = WSDLFactory.newInstance().newWSDLReader();
			Description desc = reader.read(new URL(url));
			List<Service> services = desc.getServices();
			for (Service service : services) {
				CreateChapter cc = new CreateChapter("Servicio " + service.getQName().getLocalPart(), service.getDocumentation().getContent());
				index.add(cc.create());
				cc=null;
				CreateEndPoints ce = new CreateEndPoints(service.getQName().getLocalPart());
				index.add(ce.create(service.getEndpoints()));
				ce = null;
				CreateOperations co = new CreateOperations(service.getQName().getLocalPart());
				index.add(co.create(service.getInterface().getOperations()));
				co = null;
			}
		} catch (IOException | URISyntaxException | XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		WSDL2DITA wsld2dita = new WSDL2DITA();		
		wsld2dita.generateWSDL("http://kubernetes:30280/services/echo?wsdl");
		
	}

}
