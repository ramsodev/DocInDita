package net.ramso.doc.dita.xml.schema;

import java.io.IOException;

import com.predic8.schema.Annotation;
import com.predic8.schema.Documentation;
import com.predic8.schema.Element;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.doc.dita.xml.schema.model.ElementModel;
import net.ramso.tools.Tools;

public class CreateElement extends BasicCreate {

	private String content;
	private String idSchema;

	public CreateElement(String idSchema) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definicioón del elemento");
		this.idSchema = idSchema;
	}

	

	public String create(Element element) throws IOException {
		setId(idSchema+"_"+element.getName() + "Element");
		setTitle("Element " + element.getName());
		loadContent(element.getAnnotation());
		init();
		getContext().put("content", getContent());
		getContext().put("element", new ElementModel(element));
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
