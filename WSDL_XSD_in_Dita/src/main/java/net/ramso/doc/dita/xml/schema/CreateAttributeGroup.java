package net.ramso.doc.dita.xml.schema;

import java.io.IOException;

import com.predic8.schema.Annotation;
import com.predic8.schema.AttributeGroup;
import com.predic8.schema.Documentation;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.doc.dita.xml.schema.model.AttributeGroupModel;
import net.ramso.tools.Constants;

public class CreateAttributeGroup extends BasicCreate {

	private String content;
	private String idSchema;

	public CreateAttributeGroup(String idSchema) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definición del grupo de atributos");
		this.idSchema = idSchema;
	}

	

	public String create(AttributeGroup attributeGroup) throws IOException {
		setId(idSchema+"_"+attributeGroup.getName() + Constants.SUFFIX_ATTRIBUTEGROUP);
		setTitle("Attribute Group" + attributeGroup.getName());
		loadContent(attributeGroup.getAnnotation());
		init();
		getContext().put("content", getContent());
		getContext().put("attributeGroup", new AttributeGroupModel(attributeGroup));
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