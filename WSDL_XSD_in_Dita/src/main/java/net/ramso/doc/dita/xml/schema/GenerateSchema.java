package net.ramso.doc.dita.xml.schema;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.Velocity;

import com.predic8.schema.Attribute;
import com.predic8.schema.AttributeGroup;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.Group;
import com.predic8.schema.Schema;
import com.predic8.schema.SimpleType;

import net.ramso.doc.dita.CreateBookMap;
import net.ramso.doc.dita.CreatePortada;
import net.ramso.doc.dita.References;
import net.ramso.tools.Constants;
import net.ramso.tools.Tools;

public class GenerateSchema {

	private ArrayList<References> references;

	public GenerateSchema() {
		super();
		init();
	}

	private void init() {
		// TODO: Usar fichero de properties y Verificar configuracion
		String p = Thread.currentThread().getContextClassLoader().getResource("velocity.properties").getPath();
		Velocity.init(p);
	}

	public void generateSchema(String url) throws MalformedURLException, IOException, URISyntaxException {
		generateSchema(new URL(url));

	}

	public void generateSchema(URL url) throws IOException, URISyntaxException {

		// reader = SchemaFactory.newInstance().newSchemaReader();
		// Schema schema = reader.read(url);
		// generateSchema(schema, false);
	}

	public List<References> generateSchema(Schema schema, boolean portada) throws IOException {
		Tools.setSchema(schema);
		references = new ArrayList<References>();
		String idSchema = Tools.getSchemaId(schema.getTargetNamespace());
		CreatePortada cc = new CreatePortada(idSchema + "Elements", "Elementos del esquema ",
				"Elements del esquema XML");
		References cover = new References(cc.create());

		for (Element element : schema.getElements()) {
			CreateElement ce = new CreateElement(idSchema);
			cover.addChild(new References(ce.create(element)));
		}
		
		references.add(cover);
		cc = new CreatePortada(idSchema + "SimpleTypes", "Simple Types del esquema ", "Tipos simples del esquema XML");
		cover = new References(cc.create());

		for (SimpleType type : schema.getSimpleTypes()) {
			CreateSimpleType cs = new CreateSimpleType(idSchema);
			cover.addChild(new References(cs.create(type)));
		}
		references.add(cover);
		cc = new CreatePortada(idSchema + "ComplexTypes", "Complex Types del esquema ",
				"Tipos complejos del esquema XML");
		cover = new References(cc.create());

		for (ComplexType type : schema.getComplexTypes()) {
			CreateComplexType ct = new CreateComplexType(idSchema);
			cover.addChild(new References(ct.create(type)));
		}
		references.add(cover);
		cc = new CreatePortada(idSchema + Constants.SUFFIX_ATTRIBUTE+"s", "Attributes del esquema ",
				"Atributos del esquema XML");
		cover = new References(cc.create());
		for(Attribute attribute : schema.getAttributes()) {
			CreateAttribute ca = new CreateAttribute(idSchema);
			cover.addChild(new References(ca.create(attribute)));
		}
		for(AttributeGroup attributeGroup:schema.getAttributeGroups()) {
			CreateAttributeGroup cag = new CreateAttributeGroup(idSchema);
			cover.addChild(new References(cag.create(attributeGroup)));
		}
		references.add(cover);
	
//		List<Group> groups = schema.getGroups();
		if (portada) {
			cc = new CreatePortada(idSchema + "Schema", "Schema XML", "NameSpace:" + schema.getTargetNamespace());
			cover = new References(cc.create());
			cover.getChilds().addAll(references);
			references = new ArrayList<References>();
			references.add(cover);
		} else {
			CreateBookMap cb = new CreateBookMap("", "Documentación  del XSD " + "", "");
			cb.create(references);
		}
		return references;

	}

	public References getReferences() {
		// TODO Auto-generated method stub
		return null;
	}
}
