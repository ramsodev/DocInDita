package net.ramso.docindita.xml.schema;

import java.io.IOException;

import com.predic8.schema.Attribute;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.References;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.xml.schema.model.AttributeModel;

public class CreateAttribute extends BasicCreate {

	private final String idSchema;
	private String prefix;

	public CreateAttribute(String idSchema) {
		this(idSchema, "");
	}

	public CreateAttribute(String idSchema, String prefix) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definición del atributo");
		this.idSchema = idSchema;
		this.prefix = prefix;
	}

	public References create(Attribute attribute) throws IOException {
		return create(new AttributeModel(attribute), attribute.getName());
	}

	public References create(AttributeModel model) throws IOException {
		return create(model, model.getName());
	}

	public References create(AttributeModel model, String name) throws IOException {
		setId(prefix + idSchema + "_" + name + DitaConstants.SUFFIX_ATTRIBUTE);
		setTitle("Attribute " + name);
		setContent(model.getDoc());
		init();
		getContext().put("content", getContent());
		getContext().put("attribute", model);
		getContext().put("child", true);
		run(getContext());
		References ref = new References(getFileName());
		if (model.getSimpleType() != null) {
			CreateSimpleType cs = new CreateSimpleType(idSchema);
			ref.addChild(cs.create(model.getSimpleType(), name));
		}
		return ref;
	}

}
