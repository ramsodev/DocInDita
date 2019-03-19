package net.ramso.docindita.xml.schema;

import java.io.IOException;

import com.predic8.schema.AttributeGroup;
import com.predic8.schema.ComplexType;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.References;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.xml.schema.model.AttributeGroupModel;
import net.ramso.docindita.xml.schema.model.AttributeModel;
import net.ramso.docindita.xml.schema.model.ComplexTypeModel;

public class CreateAttributeGroup extends BasicCreate {

	private final String idParent;
	private boolean child = true;
	

	public CreateAttributeGroup(String idParent) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definición del grupo de atributos");
		this.idParent = idParent;
	}

	public References create(AttributeGroup attributeGroup) throws IOException {
		this.child    = false;
		return create(new AttributeGroupModel(attributeGroup), attributeGroup.getName());

	}

	public References create(AttributeGroupModel model) throws IOException {
		return create(model, model.getName());
	}

	public References create(AttributeGroupModel model, String name) throws IOException {
		setId( idParent + "_" + name + DitaConstants.SUFFIX_ATTRIBUTEGROUP);
		setTitle("Attribute Group " + name);
		setContent(model.getDoc());
		model.setFileName(getFileName());
		init();
		
		References ref = new References(getFileName());
		for (AttributeModel attribute : model.getAttributes()) {
			if (attribute.getRef() == null) {
				CreateAttribute ca = new CreateAttribute(getId());
				ref.addChild(ca.create(attribute));
			}
		}
		getContext().put("content", getContent());
		getContext().put("attributeGroup", model);
		getContext().put("child", child);
		run(getContext());
		return ref;
	}

}
