package net.ramso.docindita.xml.schema;

import java.io.IOException;
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

import net.ramso.docindita.CreateBookMap;
import net.ramso.docindita.CreatePortada;
import net.ramso.docindita.References;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.Config;

public class GenerateXSD {

	private String idWSDL;

	public GenerateXSD() {
		super();

	}

	public void append2Schema(References cover, Schema schema) throws IOException {
		final List<References> references = generate(schema);
		for (final References ref : references) {
			final References child = cover.searchChild(ref.getId());
			if (child != null) {
				for (final References c1 : ref.getChilds()) {
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
		final ArrayList<References> references = new ArrayList<>();
		final String idSchema = getIdWSDL() + DitaTools.getSchemaId(schema.getTargetNamespace());

		CreatePortada cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_ELEMENT, "Elementos del esquema ",
				"Elements del esquema XML");
		References cover = new References(cc.create());

		for (final Element element : schema.getElements()) {
			final CreateElement ce = new CreateElement(idSchema);
			cover.addChild(ce.create(element));
		}

		references.add(cover);
		cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_SIMPLETYPE, "Simple Types del esquema ",
				"Tipos simples del esquema XML");
		cover = new References(cc.create());

		for (final SimpleType type : schema.getSimpleTypes()) {
			final CreateSimpleType cs = new CreateSimpleType(idSchema);
			cover.addChild(cs.create(type));
		}
		references.add(cover);
		cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_COMPLEXTYPE, "Complex Types del esquema ",
				"Tipos complejos del esquema XML");
		cover = new References(cc.create());

		for (final ComplexType type : schema.getComplexTypes()) {
			final CreateComplexType ct = new CreateComplexType(idSchema);
			cover.addChild(ct.create(type));
		}
		references.add(cover);
		cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_ATTRIBUTE + "s", "Attributes del esquema ",
				"Atributos del esquema XML");
		cover = new References(cc.create());
		for (final Attribute attribute : schema.getAttributes()) {
			final CreateAttribute ca = new CreateAttribute(idSchema);
			cover.addChild(ca.create(attribute));
		}
		for (final AttributeGroup attributeGroup : schema.getAttributeGroups()) {
			final CreateAttributeGroup cag = new CreateAttributeGroup(idSchema);
			cover.addChild(cag.create(attributeGroup));
		}
		references.add(cover);
		cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_GROUP, "Grupos del esquema ", "Grupos del esquema XML");
		cover = new References(cc.create());

		for (final Group group : schema.getGroups()) {
			final CreateGroup cg = new CreateGroup(idSchema);
			cover.addChild(cg.create(group));
		}
		references.add(cover);
		return references;
	}

	public References generateXsd(Schema schema, String idWsdl, boolean one) throws IOException {
		if (idWsdl != null) {
			this.idWSDL = idWsdl.trim() + "_";
		} else {
			this.idWSDL = "";
		}
		final List<References> references = generate(schema);
		final String idSchema = getIdWSDL() + DitaTools.getSchemaId(schema.getTargetNamespace());
		String name = schema.getName();
		if ((name == null) || name.isEmpty()) {
			name = DitaTools.getName(schema.getTargetNamespace());
		}
		if (idWsdl != null) {

			final CreatePortada cc = new CreatePortada(idSchema + DitaConstants.SUFFIX_SERVICE, "Schema XML " + name,
					"NameSpace:" + schema.getTargetNamespace());
			final References cover = new References(cc.create());
			cover.getChilds().addAll(references);
			return cover;
		} else {
			if (!one) {
				String id = Config.getId();
				if ((id == null) || id.isEmpty()) {
					id = idSchema;
				}
				String title = Config.getTitle();
				if ((title == null) || title.isEmpty()) {
					title = "Documentación  del XSD " + name;
				}
				String description = Config.getDescription();
				if ((description == null) || description.isEmpty()) {
					description = "NameSpace:" + schema.getTargetNamespace();
				}
				final CreateBookMap cb = new CreateBookMap(id, title, description);
				cb.create(references);
			} else {
				final CreatePortada cc = new CreatePortada(idSchema, "Documentación  del XSD " + name,
						"NameSpace:" + schema.getTargetNamespace());
				final References cover = new References(cc.create());
				cover.getChilds().addAll(references);
				return cover;
			}
		}
		return null;
	}

	public References generateXsd(String url) throws IOException {
		return generateXsd(new URL(url), false);
	}

	public References generateXsd(URL url) throws IOException {
		return generateXsd(url, false);
	}

	public References generateXsd(URL url, boolean one) throws IOException {
		final SchemaParser parser = new SchemaParser();
		final SchemaParserContext ctx = new SchemaParserContext();
		if (url.getProtocol().startsWith("file")) {
			final String p = url.getPath();
			ctx.setInput(p);
		} else {
			ctx.setInput(url.toExternalForm());
		}
		final Schema schema = parser.parse(ctx);
		return generateXsd(schema, null, one);
	}

	protected String getIdWSDL() {
		return this.idWSDL;
	}
}
