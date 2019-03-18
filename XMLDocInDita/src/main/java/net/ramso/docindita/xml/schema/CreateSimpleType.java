package net.ramso.docindita.xml.schema;

import java.io.IOException;

import com.predic8.schema.SimpleType;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.References;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.schema.model.SimpleTypeModel;

public class CreateSimpleType extends BasicCreate {

	private final String idSchema;
	private String prefix;

	public CreateSimpleType(String idSchema) {
		this(idSchema, "");
	}

	public CreateSimpleType(String idSchema, String prefix) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definición del tipo de datos simple");
		this.idSchema = idSchema;
		this.prefix = prefix;
	}

	public References create(SimpleType type) throws IOException {
		return create(new SimpleTypeModel(type));
	}

	public References create(SimpleTypeModel model) throws IOException {
		return create(model, model.getName());
	}

	public References create(SimpleTypeModel model, String name) throws IOException {
		setId(prefix + idSchema + "_" + name + DitaConstants.SUFFIX_SIMPLETYPE);
		setTitle("Simple Type " + name);
		setContent(model.getDoc());
		init();
		getContext().put("content", getContent());
		getContext().put("simpleType", model);
		getContext().put("tools", DitaTools.class);
		getContext().put("child", true);
		run(getContext());
		return new References(getFileName());

	}

}
