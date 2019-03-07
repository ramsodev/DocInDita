package net.ramso.doc.dita.xml.schema;

import java.io.IOException;

import com.predic8.schema.Annotation;
import com.predic8.schema.Documentation;
import com.predic8.schema.Element;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.doc.dita.tools.DitaTools;
import net.ramso.doc.dita.xml.schema.model.ElementModel;

public class CreateElement extends BasicCreate {

	private String idSchema;

	public CreateElement(String idSchema) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definicioón del elemento");
		this.idSchema = idSchema;
	}

	public String create(Element element) throws IOException {
		setId(idSchema + "_" + element.getName() + "Element");
		setTitle("Element " + element.getName());
		loadContent(element.getAnnotation());
		init();
		getContext().put("content", getContent());
		getContext().put("element", new ElementModel(element));
		run(getContext());
		return getFile_name();
	}
}
