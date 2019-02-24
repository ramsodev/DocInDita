package net.ramso.doc.dita.xml.schema;

import java.io.IOException;

import com.predic8.schema.Annotation;
import com.predic8.schema.Documentation;
import com.predic8.schema.SimpleType;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.doc.dita.xml.schema.model.SimpleTypeModel;

public class CreateSimpleType extends BasicCreate {

	private String content;
	private String idSchema;

	public CreateSimpleType(String idSchema) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definicioón del tipo de datos simple");
		this.idSchema = idSchema;
	}

	

	public String create(SimpleType type) throws IOException {
		setId(idSchema+"_"+type.getName() + "SimpleType");
		setTitle("Simple Type " + type.getName());
		init();
		loadContent(type.getAnnotation());
		getContext().put("content", getContent());
		getContext().put("simpleType", new SimpleTypeModel(type));
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
