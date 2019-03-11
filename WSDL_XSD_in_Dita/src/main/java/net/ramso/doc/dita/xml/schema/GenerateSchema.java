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

import net.ramso.doc.Config;
import net.ramso.doc.dita.CreateBookMap;
import net.ramso.doc.dita.CreatePortada;
import net.ramso.doc.dita.References;
import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.tools.DitaTools;

public class GenerateSchema {

	private String idWSDL;

	public GenerateSchema() {
		super();

	}

	public References generateSchema(String url) throws MalformedURLException, IOException, URISyntaxException {
		return generateSchema(new URL(url), false);
	}

	public References generateSchema(URL url) throws IOException, URISyntaxException {
		return generateSchema(url, false);
	}

	public References generateSchema(URL url, boolean one) throws IOException, URISyntaxException {
		SchemaParser parser = new SchemaParser();
		SchemaParserContext ctx = new SchemaParserContext();
		if (url.getProtocol().startsWith("file")) {
			String p = url.getPath();
			ctx.setInput(p);
		} else {
			ctx.setInput(url.toExternalForm());
		}
		Schema schema = parser.parse(ctx);
		return generateSchema(schema, null, one);
	}

	public References generateSchema(Schema schema, String idWsdl, boolean one) throws IOException {
		if (idWsdl != null) {
			this.idWSDL = idWsdl.trim() + "_";
		} else {
			this.idWSDL = "";
		}
		List<References> references = generate(schema);		
		String idSchema = getIdWSDL() + DitaTools.getSchemaId(schema.getTargetNamespace());
		String name = schema.getName();
		if (name == null || name.isEmpty()) {
			name = DitaTools.getName(schema.getTargetNamespace());
		}
		if (idWsdl != null) {

			CreatePortada cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_SERVICE, "Schema XML " + name,
					"NameSpace:" + schema.getTargetNamespace());
			References cover = new References(cc.create());
			cover.getChilds().addAll(references);
			return cover;
		} else {
			if (!one) {
				String id = Config.getId();
				if (id == null || id.isEmpty()) {
					id = idSchema;
				}
				String title = Config.getTitle();
				if (title == null || title.isEmpty()) {
					title = "Documentación  del XSD " + name;
				}
				String description = Config.getDescription();
				if (description == null || description.isEmpty()) {
					description = "NameSpace:" + schema.getTargetNamespace();
				}
				CreateBookMap cb = new CreateBookMap(id, title, description);
				cb.create(references);
			} else {
				CreatePortada cc = new CreatePortada(idSchema, "Documentación  del XSD " + name,
						"NameSpace:" + schema.getTargetNamespace());
				return new References(cc.create());
			}
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
		String idSchema = getIdWSDL() + DitaTools.getSchemaId(schema.getTargetNamespace());

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

	protected String getIdWSDL() {
		return idWSDL;
	}
}
