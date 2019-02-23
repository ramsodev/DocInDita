package net.ramso.doc.dita.xml.schema;

import java.io.IOException;

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
		getContext().put("content", getContent());
		getContext().put("simpleType", new SimpleTypeModel(type));
		run(getContext());
		return getFile_name();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
