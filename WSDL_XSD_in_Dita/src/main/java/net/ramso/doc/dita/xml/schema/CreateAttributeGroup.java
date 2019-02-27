package net.ramso.doc.dita.xml.schema;

import java.io.IOException;

import com.predic8.schema.AttributeGroup;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.doc.dita.xml.schema.model.AttributeGroupModel;
import net.ramso.tools.Constants;

public class CreateAttributeGroup extends BasicCreate {

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
	
}
