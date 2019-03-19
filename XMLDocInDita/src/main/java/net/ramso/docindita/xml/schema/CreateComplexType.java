package net.ramso.docindita.xml.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.ComplexType;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.CreatePortada;
import net.ramso.docindita.References;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.schema.model.AllModel;
import net.ramso.docindita.xml.schema.model.AttributeGroupModel;
import net.ramso.docindita.xml.schema.model.AttributeModel;
import net.ramso.docindita.xml.schema.model.ChoiceModel;
import net.ramso.docindita.xml.schema.model.ComplexTypeModel;
import net.ramso.docindita.xml.schema.model.ElementModel;
import net.ramso.docindita.xml.schema.model.GroupModel;
import net.ramso.docindita.xml.schema.model.IComplexContentModel;
import net.ramso.docindita.xml.schema.model.SequenceModel;

public class CreateComplexType extends BasicCreate {

	private final String idParent;
	private boolean child = true;
	private int level;

	public CreateComplexType(String idParent) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definicioón del tipo de datos simple");
		this.idParent = idParent;
	}

	public References create(ComplexType complexType) throws IOException {
		this.child = false;
		return create(new ComplexTypeModel(complexType), complexType.getName());

	}

	public References create(ComplexTypeModel model) throws IOException {

		return create(model, model.getName());
	}

	public References create(ComplexTypeModel model, String name) throws IOException {
		setId(this.idParent + "_" + name + DitaConstants.SUFFIX_COMPLEXTYPE);
		setTitle("Complex Type " + name);
		setContent(model.getDoc());
		model.setFileName(getFileName());
		init();

		final References ref = new References(getFileName());
		References r1 = appendAttributesGroups(model.getAttributeGroups(), name);
		if (r1 != null) {
			ref.addChild(r1);
		}
		r1 = appendAttributes(model.getAttributes(), name);
		if (r1 != null) {
			ref.addChild(r1);
		}
		this.level = 0;
		ref.getChilds().addAll(append(model.getElements(), name));
		getContext().put("content", getContent());
		getContext().put("complexType", model);
		getContext().put("tools", DitaTools.class);
		getContext().put("child", this.child);
		run(getContext());
		return ref;
	}

	private References appendAttributes(List<AttributeModel> attributes, String parentName) throws IOException {
		if (attributes.isEmpty()) {
			return null;
		}
		final CreatePortada cp = new CreatePortada(parentName + DitaConstants.SUFFIX_ATTRIBUTEGROUP, "Attribute groups",
				"");
		final References childRef = new References(cp.create());
		for (final AttributeModel attribute : attributes) {
			String name = parentName + " Embebed " + DitaConstants.SUFFIX_ATTRIBUTEGROUP;
			if (attribute.getName() != null) {
				name = attribute.getName();
			} else if (attribute.getRef() != null) {
				name = attribute.getRef().getLocalPart();
			}
			final CreateAttribute cg = new CreateAttribute(getId());
			childRef.addChild(cg.create(attribute, name));
		}
		return childRef;
	}

	private References appendAttributesGroups(List<AttributeGroupModel> groups, String parentName) throws IOException {
		if (groups.isEmpty()) {
			return null;
		}
		final CreatePortada cp = new CreatePortada(parentName + DitaConstants.SUFFIX_ATTRIBUTEGROUP, "Attribute groups",
				"");
		final References childRef = new References(cp.create());
		for (final AttributeGroupModel group : groups) {
			String name = parentName + " Embebed " + DitaConstants.SUFFIX_ATTRIBUTEGROUP;
			if (group.getName() != null) {
				name = group.getName();
			} else if (group.getRef() != null) {
				name = group.getRef().getLocalPart();
			}
			final CreateAttributeGroup cg = new CreateAttributeGroup(getId());
			childRef.addChild(cg.create(group, name));
		}
		return childRef;
	}

	private List<? extends References> append(List<IComplexContentModel> elements, String parentName)
			throws IOException {
		this.level++;
		final List<References> refs = new ArrayList<>();
		for (final IComplexContentModel model : elements) {
			if (model instanceof SequenceModel) {
				final CreatePortada cp = new CreatePortada(getId() + this.level + DitaConstants.SEQUENCE,
						DitaConstants.SEQUENCE, model.getDoc());
				final References childRef = new References(cp.create());
				childRef.getChilds().addAll(append(model.getElements(), DitaConstants.SEQUENCE + " " + parentName));
				refs.add(childRef);
			} else if (model instanceof AllModel) {
				final CreatePortada cp = new CreatePortada(getId() + this.level + DitaConstants.ALL, DitaConstants.ALL,
						model.getDoc());
				final References childRef = new References(cp.create());
				childRef.getChilds().addAll(append(model.getElements(), DitaConstants.ALL + " " + parentName));
				refs.add(childRef);
			} else if (model instanceof ChoiceModel) {
				final CreatePortada cp = new CreatePortada(getId() + this.level + DitaConstants.CHOICE,
						DitaConstants.CHOICE, model.getDoc());
				final References childRef = new References(cp.create());
				childRef.getChilds().addAll(append(model.getElements(), DitaConstants.CHOICE + " " + parentName));
				refs.add(childRef);
			} else if (model instanceof ElementModel) {
				String name = parentName + " Embebed " + DitaConstants.SUFFIX_ELEMENT;
				if (model.getName() != null) {
					name = model.getName();
				} else if (model.getRef() != null) {
					name = model.getRef().getLocalPart();
				}
				final CreateElement ce = new CreateElement(getId());
				refs.add(ce.create((ElementModel) model, name));
			} else if (model instanceof ComplexTypeModel) {
				String name = parentName + " Embebed " + DitaConstants.SUFFIX_COMPLEXTYPE;
				if (model.getName() != null) {
					name = model.getName();
				} else if (model.getRef() != null) {
					name = model.getRef().getLocalPart();
				}
				final CreateComplexType ce = new CreateComplexType(getId());
				refs.add(ce.create((ComplexTypeModel) model, name));
			} else if (model instanceof GroupModel) {
				String name = parentName + " Embebed " + DitaConstants.SUFFIX_GROUP;
				if (model.getName() != null) {
					name = model.getName();
				} else if (model.getRef() != null) {
					name = model.getRef().getLocalPart();
				}
				final CreateGroup ce = new CreateGroup(getId());
				refs.add(ce.create((GroupModel) model, name));
			}
		}
		return refs;
	}
}
