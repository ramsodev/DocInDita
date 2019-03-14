package net.ramso.docindita.xml.schema;

import java.io.IOException;

import com.predic8.schema.Attribute;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.xml.schema.model.AttributeModel;

public class CreateAttribute extends BasicCreate {

	private final String idSchema;

	public CreateAttribute(String idSchema) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definición del atributo");
		this.idSchema = idSchema;
	}

	public String create(Attribute attribute) throws IOException {
		setId(idSchema + "_" + attribute.getName() + DitaConstants.SUFFIX_ATTRIBUTE);
		setTitle("Attribute " + attribute.getName());
		loadContent(attribute.getAnnotation());
		init();
		getContext().put("content", getContent());
		getContext().put("attribute", new AttributeModel(attribute));
		run(getContext());
		return getFileName();
	}

}
