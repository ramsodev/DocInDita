package net.ramso.doc.dita.xml.schema;

import java.io.IOException;

import com.predic8.schema.Attribute;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.doc.dita.tools.Constants;
import net.ramso.doc.dita.xml.schema.model.AttributeModel;

public class CreateAttribute extends BasicCreate {

	
	private String idSchema;

	public CreateAttribute(String idSchema) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definición del atributo");
		this.idSchema = idSchema;
	}

	

	public String create(Attribute attribute) throws IOException {
		setId(idSchema+"_"+attribute.getName() + Constants.SUFFIX_ATTRIBUTE);
		setTitle("Attribute " + attribute.getName());
		loadContent(attribute.getAnnotation());
		init();
		getContext().put("content", getContent());
		getContext().put("attribute", new AttributeModel(attribute));
		run(getContext());
		return getFile_name();
	}
	
	

}
