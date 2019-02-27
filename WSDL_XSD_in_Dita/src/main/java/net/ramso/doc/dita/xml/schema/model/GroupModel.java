package net.ramso.doc.dita.xml.schema.model;

import com.predic8.schema.Group;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;

public class GroupModel extends AbstractComplexContentModel {
	private Group group;
	

	public GroupModel(Group group) {
		super();
		this.group = group;
		init();
	}

	private void init() {
		procesModel(group.getModel());

		if (group.getMinOccurs() != null) {
			setMinOccurs(((Integer) group.getMinOccurs()));

		}
		if (group.getMaxOccurs() != null) {
			setMaxOccurs(group.getMaxOccurs());
		}

	}

	
	@Override
	public SchemaComponent getComponent() {

		return group;
	}

	@Override
	public QName getType() {
		return group.getQname();
	}

	
}
