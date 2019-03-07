package net.ramso.doc.dita.xml.schema.model;

import java.util.ArrayList;

import com.predic8.schema.Group;
import com.predic8.schema.GroupRef;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.xml.schema.model.graph.GroupGraph;

public class GroupModel extends AbstractComplexContentModel {
	private SchemaComponent component;
	private QName ref;
	private String diagram;

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
		this.contentType = DitaConstants.SUFFIX_GROUP;
		if (component instanceof Group) {
			procesGroup((Group) component);
		} else {
			procesRef((GroupRef) component);
		}

	}

	public GroupModel getModel() {
		if (getRef() == null) {
			return this;
		} else {
			return new GroupModel(getComponent().getSchema().getGroup(getRef()));
		}
	}

	private void procesRef(GroupRef group) {
		ref = group.getRef();
		setElements(new ArrayList<IComplexContentModel>());

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
		if (getRef() != null) {
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
		return DitaConstants.NAME_GROUP;
	}

	@Override
	public String getDiagram() {
		if (this.diagram == null) {
			GroupGraph graph = new GroupGraph(this);
			diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return diagram;
	}

	@Override
	public boolean isElement() {
		return component instanceof GroupRef;
	}

}
