package net.ramso.doc.dita.xml.schema;

import java.io.IOException;

import com.predic8.schema.Annotation;
import com.predic8.schema.Attribute;
import com.predic8.schema.Documentation;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.doc.dita.xml.schema.model.AttributeModel;
import net.ramso.tools.Constants;

public class CreateAttribute extends BasicCreate {

	private String content;
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
	
	public void loadContent(Annotation annotation) {
		String value = "";
		if(annotation!=null) {
			if(annotation.getDocumentations()!=null) {
				for(Documentation doc:annotation.getDocumentations()) {
					value += doc.getContent();
				}
			}
		}
		if(!value.isEmpty()) setContent(value);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
