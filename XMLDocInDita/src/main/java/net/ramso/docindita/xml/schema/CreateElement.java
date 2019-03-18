package net.ramso.docindita.xml.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.Element;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.CreatePortada;
import net.ramso.docindita.References;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.xml.schema.model.AllModel;
import net.ramso.docindita.xml.schema.model.ChoiceModel;
import net.ramso.docindita.xml.schema.model.ComplexTypeModel;
import net.ramso.docindita.xml.schema.model.ElementModel;
import net.ramso.docindita.xml.schema.model.GroupModel;
import net.ramso.docindita.xml.schema.model.IComplexContentModel;
import net.ramso.docindita.xml.schema.model.SequenceModel;

public class CreateElement extends BasicCreate {

	private final String idParent;

	public CreateElement(String idParent) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definicioón del elemento");
		this.idParent = idParent;
		
	}

	public References create(Element element) throws IOException {
		return create(new ElementModel(element), element.getName());

	}

	public References create(ElementModel model) throws IOException {
		return create(model, model.getName());
	}

	public References create(ElementModel model, String name) throws IOException {
		setId(idParent + "_" + name + DitaConstants.SUFFIX_ELEMENT);
		setTitle(DitaConstants.SUFFIX_ELEMENT + " " + name);
		setContent(model.getDoc());
		model.setFileName(getFileName());
		init();
		
		References ref = new References(getFileName());
		if (model.getSimpleType() != null) {
			String nameST = name + " Embebed " + DitaConstants.SUFFIX_SIMPLETYPE;
			if (model.getName() != null) {
				name = model.getName();
			} else if (model.getType() != null) {
				name = model.getType().getLocalPart();
			}
			CreateSimpleType cs = new CreateSimpleType(getId());
			ref.addChild(cs.create(model.getSimpleType(), nameST));
		}
		if (model.getComplexType() != null) {
			String nameST = name + " Embebed " + DitaConstants.SUFFIX_COMPLEXTYPE;
			if (model.getName() != null) {
				name = model.getName();
			}
			CreateComplexType cs = new CreateComplexType(getId());
			ref.addChild(cs.create(model.getComplexType(), nameST));
		}
		ref.getChilds().addAll(append(model.getElements(), name));
		getContext().put("content", getContent());
		getContext().put("element", model);
		run(getContext());
		return ref;
	}

	private List<? extends References> append(List<IComplexContentModel> elements, String parentName)
			throws IOException {
		List<References> refs = new ArrayList<>();
		for (IComplexContentModel model : elements) {
			if (model instanceof SequenceModel) {
				CreatePortada cp = new CreatePortada(getId() + DitaConstants.SEQUENCE, DitaConstants.SEQUENCE,
						model.getDoc());
				References childRef = new References(cp.create());
				childRef.getChilds().addAll(append(model.getElements(), DitaConstants.SEQUENCE + " " + parentName));
				refs.add(childRef);
			} else if (model instanceof AllModel) {
				CreatePortada cp = new CreatePortada(getId() + DitaConstants.ALL, DitaConstants.ALL, model.getDoc());
				References childRef = new References(cp.create());
				childRef.getChilds().addAll(append(model.getElements(), DitaConstants.ALL + " " + parentName));
				refs.add(childRef);
			} else if (model instanceof ChoiceModel) {
				CreatePortada cp = new CreatePortada(getId() + DitaConstants.CHOICE, DitaConstants.CHOICE,
						model.getDoc());
				References childRef = new References(cp.create());
				childRef.getChilds().addAll(append(model.getElements(), DitaConstants.CHOICE + " " + parentName));
				refs.add(childRef);
			} else if (model instanceof ElementModel) {
				String name = parentName + " Embebed " + DitaConstants.SUFFIX_ELEMENT;
				if (model.getName() != null) {
					name = model.getName();
				} else if (model.getRef() != null) {
					name = model.getRef().getLocalPart();
				}
				CreateElement ce = new CreateElement(getId());
				refs.add(ce.create((ElementModel) model, name));
			} else if (model instanceof ComplexTypeModel) {
				String name = parentName + " Embebed " + DitaConstants.SUFFIX_COMPLEXTYPE;
				if (model.getName() != null) {
					name = model.getName();
				} else if (model.getRef() != null) {
					name = model.getRef().getLocalPart();
				}
				CreateComplexType ce = new CreateComplexType(getId());
				refs.add(ce.create((ComplexTypeModel) model, name));
			} else if (model instanceof GroupModel) {
				String name = parentName + " Embebed " + DitaConstants.SUFFIX_GROUP;
				if (model.getName() != null) {
					name = model.getName();
				} else if (model.getRef() != null) {
					name = model.getRef().getLocalPart();
				}
				CreateGroup ce = new CreateGroup(getId());
				refs.add(ce.create((GroupModel) model, name));
			}
		}
		return refs;
	}
}
