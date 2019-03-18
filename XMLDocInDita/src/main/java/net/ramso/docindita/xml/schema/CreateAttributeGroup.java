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

	private final String idSchema;
	private String prefix;

	public CreateAttributeGroup(String idSchema) {
		this(idSchema, "");
	}

	public CreateAttributeGroup(String idSchema, String prefix) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definición del grupo de atributos");
		this.idSchema = idSchema;
		this.prefix = prefix;
	}

	public References create(AttributeGroup attributeGroup) throws IOException {
		return create(new AttributeGroupModel(attributeGroup), attributeGroup.getName());

	}

	public References create(AttributeGroupModel model) throws IOException {
		return create(model, model.getName());
	}

	public References create(AttributeGroupModel model, String name) throws IOException {
		setId(prefix + idSchema + "_" + name + DitaConstants.SUFFIX_ATTRIBUTEGROUP);
		setTitle("Attribute Group " + name);
		setContent(model.getDoc());
		init();
		getContext().put("content", getContent());
		getContext().put("attributeGroup", model);
		run(getContext());
		References ref = new References(getFileName());
		for (AttributeModel attribute : model.getAttributes()) {
			if (attribute.getRef() == null) {
				CreateAttribute ca = new CreateAttribute(idSchema);
				ref.addChild(ca.create(attribute));
			}
		}
		return ref;
	}

}
