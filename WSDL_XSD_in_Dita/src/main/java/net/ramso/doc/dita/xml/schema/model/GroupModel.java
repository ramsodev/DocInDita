package net.ramso.doc.dita.xml.schema.model;

import com.predic8.schema.Group;
import com.predic8.schema.GroupRef;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.doc.dita.tools.Constants;

public class GroupModel extends AbstractComplexContentModel {
	private SchemaComponent component;
	private QName ref;
	

	public GroupModel(Group group) {
		super();
		this.component = group;
		init();
	}
	
	public GroupModel(GroupRef group) {
		super();
		this.component = group;
		init();
	}

	private void init() {
		if(component instanceof Group) {
			procesGroup((Group) component);
		}else {
			procesRef((GroupRef) component);
		}

	}

	
	private void procesRef(GroupRef group) {
		ref = group.getRef();
		
	}

	private void procesGroup(Group group) {
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

		return component;
	}

	@Override
	public QName getType() {
		if(getRef()!= null) {
			return getRef();
		}
		return ((Group) component).getQname();
	}

	/**
	 * @return the ref
	 */
	public QName getRef() {
		return ref;
	}

	@Override
	public String getComponentName() {
		return Constants.NAME_GROUP;
	}

	
}
