package net.ramso.doc.dita.xml.schema;

import java.io.IOException;

import com.predic8.schema.Annotation;
import com.predic8.schema.Documentation;
import com.predic8.schema.SimpleType;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.doc.dita.xml.schema.model.SimpleTypeModel;
import net.ramso.tools.Constants;
import net.ramso.tools.Tools;

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
		setId(idSchema+"_"+type.getName() + Constants.SUFFIX_SIMPLETYPE);
		setTitle("Simple Type " + type.getName());
		init();
		loadContent(type.getAnnotation());
		getContext().put("content", getContent());
		getContext().put("simpleType", new SimpleTypeModel(type));
		getContext().put("tools", Tools.class);
		run(getContext());
		return getFile_name();
	}
	

}
