package net.ramso.doc.dita.xml.schema;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.Velocity;
import org.ow2.easywsdl.schema.SchemaFactory;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.SchemaException;
import org.ow2.easywsdl.schema.api.SchemaReader;

import net.ramso.doc.dita.xml.wsdl.CreateService;

public class GenerateSchema {

	public GenerateSchema() {
		super();
		init();
	}

	private void init() {
		// TODO: Usar fichero de properties y Verificar configuracion
		String p = Thread.currentThread().getContextClassLoader().getResource("velocity.properties").getPath();
		Velocity.init(p);
	}

	public void generateSchema(String url) throws SchemaException, MalformedURLException, IOException, URISyntaxException
			{
		generateSchema(new URL(url));

	}

	public void generateSchema(URL url) throws  IOException, URISyntaxException, SchemaException {
		SchemaReader	
		reader = SchemaFactory.newInstance().newSchemaReader();
		Schema schema = reader.read(url);
		generateSchema(schema, false);
	}	
	
	public String generateSchema(Schema schema, boolean portada) throws IOException {
		ArrayList<String> index = new ArrayList<String>();
		if(portada) {
			CreateService cc = new CreateService("Schema XML ",
					"Elemento, tipos. atributos Directivas y grupos del schema para el wsdl");
			index.add(cc.create());
			cc = null;
		}
		List<Element> elements = schema.getElements();
		return null;
		
	}
}
