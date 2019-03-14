package net.ramso.docindita.xml.schema;

import java.io.IOException;

import com.predic8.schema.SimpleType;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.schema.model.SimpleTypeModel;

public class CreateSimpleType extends BasicCreate {

	private final String idSchema;

	public CreateSimpleType(String idSchema) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definicioón del tipo de datos simple");
		this.idSchema = idSchema;
	}

	public String create(SimpleType type) throws IOException {
		setId(idSchema + "_" + type.getName() + DitaConstants.SUFFIX_SIMPLETYPE);
		setTitle("Simple Type " + type.getName());
		init();
		loadContent(type.getAnnotation());
		getContext().put("content", getContent());
		getContext().put("simpleType", new SimpleTypeModel(type));
		getContext().put("tools", DitaTools.class);
		run(getContext());
		return getFileName();
	}

}
