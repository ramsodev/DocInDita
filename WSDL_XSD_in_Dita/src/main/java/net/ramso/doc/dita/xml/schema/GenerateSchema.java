package net.ramso.doc.dita.xml.schema;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.Attribute;
import com.predic8.schema.AttributeGroup;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.Group;
import com.predic8.schema.Schema;
import com.predic8.schema.SchemaParser;
import com.predic8.schema.SchemaParserContext;
import com.predic8.schema.SimpleType;

import net.ramso.doc.dita.CreateBookMap;
import net.ramso.doc.dita.CreatePortada;
import net.ramso.doc.dita.References;
import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.tools.DitaTools;

public class GenerateSchema {

	public GenerateSchema() {
		super();

	}

	public void generateSchema(String url) throws MalformedURLException, IOException, URISyntaxException {
		generateSchema(new URL(url));
	}

	public void generateSchema(URL url) throws IOException, URISyntaxException {
		SchemaParser parser = new SchemaParser();
		SchemaParserContext ctx = new SchemaParserContext();
		if (url.getProtocol().startsWith("file")) {
			String p = url.getPath();
			ctx.setInput(p);
		} else {
			ctx.setInput(url.toExternalForm());
		}
		Schema schema = parser.parse(ctx);
		generateSchema(schema, false);
	}

	public References generateSchema(Schema schema, boolean portada) throws IOException {
		List<References> references = generate(schema);
		String idSchema = DitaTools.getSchemaId(schema.getTargetNamespace());
		if (portada) {
			String name = schema.getName();
			if (name == null || name.isEmpty()) {
				name = DitaTools.getName(schema.getTargetNamespace());
			}
			CreatePortada cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_SERVICE, "Schema XML " + name,
					"NameSpace:" + schema.getTargetNamespace());
			References cover = new References(cc.create());
			cover.getChilds().addAll(references);
			return cover;
		} else {
			CreateBookMap cb = new CreateBookMap(idSchema, "Documentación  del XSD " + "", "");
			cb.create(references);
		}
		return null;
	}

	public void append2Schema(References cover, Schema schema) throws IOException {
		List<References> references = generate(schema);
		for (References ref : references) {
			References child = cover.searchChild(ref.getId());
			if (child != null) {
				for (References c1 : ref.getChilds()) {
					if (child.searchChild(c1.getId()) == null) {
						child.addChild(c1);
					}
				}
			} else {
				cover.addChild(ref);
			}
		}

	}

	public List<References> generate(Schema schema) throws IOException {
		DitaTools.setSchema(schema);
		ArrayList<References> references = new ArrayList<References>();
		String idSchema = DitaTools.getSchemaId(schema.getTargetNamespace());

		CreatePortada cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_ELEMENT, "Elementos del esquema ",
				"Elements del esquema XML");
		References cover = new References(cc.create());

		for (Element element : schema.getElements()) {
			CreateElement ce = new CreateElement(idSchema);
			cover.addChild(new References(ce.create(element)));
		}

		references.add(cover);
		cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_SIMPLETYPE, "Simple Types del esquema ",
				"Tipos simples del esquema XML");
		cover = new References(cc.create());

		for (SimpleType type : schema.getSimpleTypes()) {
			CreateSimpleType cs = new CreateSimpleType(idSchema);
			cover.addChild(new References(cs.create(type)));
		}
		references.add(cover);
		cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_COMPLEXTYPE, "Complex Types del esquema ",
				"Tipos complejos del esquema XML");
		cover = new References(cc.create());

		for (ComplexType type : schema.getComplexTypes()) {
			CreateComplexType ct = new CreateComplexType(idSchema);
			cover.addChild(new References(ct.create(type)));
		}
		references.add(cover);
		cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_ATTRIBUTE + "s", "Attributes del esquema ",
				"Atributos del esquema XML");
		cover = new References(cc.create());
		for (Attribute attribute : schema.getAttributes()) {
			CreateAttribute ca = new CreateAttribute(idSchema);
			cover.addChild(new References(ca.create(attribute)));
		}
		for (AttributeGroup attributeGroup : schema.getAttributeGroups()) {
			CreateAttributeGroup cag = new CreateAttributeGroup(idSchema);
			cover.addChild(new References(cag.create(attributeGroup)));
		}
		references.add(cover);
		cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_GROUP, "Grupos del esquema ", "Grupos del esquema XML");
		cover = new References(cc.create());

		for (Group group : schema.getGroups()) {
			CreateGroup cg = new CreateGroup(idSchema);
			cover.addChild(new References(cg.create(group)));
		}
		references.add(cover);
		return references;
	}
}
