package net.ramso.docindita.xml.schema;

import java.io.IOException;

import com.predic8.schema.ComplexType;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.schema.model.ComplexTypeModel;

public class CreateComplexType extends BasicCreate {

	private final String idSchema;

	public CreateComplexType(String idSchema) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definicioón del tipo de datos simple");
		this.idSchema = idSchema;
	}

	public String create(ComplexType type) throws IOException {
		setId(idSchema + "_" + type.getName() + DitaConstants.SUFFIX_COMPLEXTYPE);
		setTitle("Complex Type " + type.getName());
		loadContent(type.getAnnotation());
		init();
		getContext().put("content", getContent());
		getContext().put("complexType", new ComplexTypeModel(type));
		getContext().put("tools", DitaTools.class);
		run(getContext());
		return getFileName();
	}

}
