package net.ramso.docindita.xml.schema;

import java.io.IOException;
import java.util.List;

import com.predic8.schema.SimpleType;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.CreatePortada;
import net.ramso.docindita.References;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.schema.model.SimpleTypeModel;

public class CreateSimpleType extends BasicCreate {

	private final String idParent;
	private boolean child = true;

	public CreateSimpleType(String idParent) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definición del tipo de datos simple");
		this.idParent = idParent;
	}

	public References create(SimpleType type) throws IOException {
		this.child = false;
		return create(new SimpleTypeModel(type));
	}

	public References create(SimpleTypeModel model) throws IOException {
		return create(model, model.getName());
	}

	public References create(SimpleTypeModel model, String name) throws IOException {
		setId(idParent + "_" + name + DitaConstants.SUFFIX_SIMPLETYPE);
		setTitle("Simple Type " + name);
		model.setFileName(getFileName());
		setContent(model.getDoc());
		init();
		References ref = new References(getFileName());
		if (model.getListSimpleType() != null) {
			String nameST = name + " " + DitaConstants.LIST + " " + DitaConstants.SUFFIX_SIMPLETYPE;
			if (model.getName() != null) {
				nameST = model.getName();
			} else if (model.getType() != null) {
				nameST = model.getType().getLocalPart();
			}
			CreatePortada cp = new CreatePortada(getId() + DitaConstants.LIST, DitaConstants.LIST, model.getDoc());
			References childRef = new References(cp.create());
			CreateSimpleType cs = new CreateSimpleType(getId());
			childRef.addChild(cs.create(model.getListSimpleType(), nameST));
			ref.addChild(childRef);
		}
		if (!model.getUnionSimpleTypes().isEmpty()) {
			ref.addChild(append(model.getUnionSimpleTypes(), name));
		}
		getContext().put("content", getContent());
		getContext().put("simpleType", model);
		getContext().put("tools", DitaTools.class);
		getContext().put("child", child);
		run(getContext());
		return ref;

	}

	private References append(List<SimpleTypeModel> unionSimpleTypes, String name) throws IOException {
		CreatePortada cp = new CreatePortada(getId() + DitaConstants.UNION, DitaConstants.UNION, "");
		References cover = new References(cp.create());
		for (SimpleTypeModel model : unionSimpleTypes) {
			String nameST = name + "  " + DitaConstants.UNION + " " + DitaConstants.SUFFIX_SIMPLETYPE;
			if (model.getName() != null) {
				nameST = model.getName();
			} else if (model.getType() != null) {
				nameST = model.getType().getLocalPart();
			}
			CreateSimpleType cs = new CreateSimpleType(getId());
			cover.addChild(cs.create(model, nameST));
		}
		return cover;
	}

}
