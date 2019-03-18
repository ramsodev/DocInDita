package net.ramso.docindita.xml.schema;

import java.io.IOException;

import com.predic8.schema.SimpleType;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.References;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.schema.model.SimpleTypeModel;

public class CreateSimpleType extends BasicCreate {

	private final String idParent;


	public CreateSimpleType(String idParent) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definición del tipo de datos simple");
		this.idParent = idParent;
	}

	public References create(SimpleType type) throws IOException {
		return create(new SimpleTypeModel(type));
	}

	public References create(SimpleTypeModel model) throws IOException {
		return create(model, model.getName());
	}

	public References create(SimpleTypeModel model, String name) throws IOException {
		setId( idParent + "_" + name + DitaConstants.SUFFIX_SIMPLETYPE);
		setTitle("Simple Type " + name);
		model.setFileName(getFileName());
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
