package net.ramso.doc.dita.xml.schema;

import java.io.IOException;

import com.predic8.schema.ComplexType;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.doc.dita.xml.schema.model.ComplexTypeModel;
import net.ramso.tools.Constants;
import net.ramso.tools.Tools;

public class CreateComplexType extends BasicCreate {

	private String idSchema;

	public CreateComplexType(String idSchema) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definicioón del tipo de datos simple");
		this.idSchema = idSchema;
	}

	

	public String create(ComplexType type) throws IOException {
		setId(idSchema+"_"+type.getName() + Constants.SUFFIX_COMPLEXTYPE);
		setTitle("Complex Type " + type.getName());
		loadContent(type.getAnnotation());
		init();		
		getContext().put("content", getContent());
		getContext().put("complexType", new ComplexTypeModel(type));
		getContext().put("tools", Tools.class);
		run(getContext());
		return getFile_name();
	}
	
	
}
