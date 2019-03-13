package net.ramso.docindita.xml.schema.model;

import java.util.ArrayList;

import com.predic8.schema.Group;
import com.predic8.schema.GroupRef;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.xml.schema.model.graph.GroupGraph;
import net.ramso.tools.LogManager;

public class GroupModel extends AbstractComplexContentModel {
	private final SchemaComponent component;
	private QName ref;
	private String diagram;

	public GroupModel(Group group) {
		super();
		component = group;
		init();
		LogManager.debug("Carga de Grupo " + getName());
	}

	public GroupModel(GroupRef group) {
		super();
		component = group;
		init();
	}

	@Override
	public SchemaComponent getComponent() {

		return component;
	}

	@Override
	public String getComponentName() {
		return DitaConstants.NAME_GROUP;
	}

	@Override
	public String getDiagram() {
		if (diagram == null) {
			final GroupGraph graph = new GroupGraph(this);
			diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return diagram;
	}

	public GroupModel getModel() {
		if (getRef() == null)
			return this;
		else
			return new GroupModel(getComponent().getSchema().getGroup(getRef()));
	}

	/**
	 * @return the ref
	 */
	@Override
	public QName getRef() {
		return ref;
	}

	@Override
	public QName getType() {
		if (getRef() != null)
			return getRef();
		return ((Group) component).getQname();
	}

	private void init() {
		contentType = DitaConstants.SUFFIX_GROUP;
		if (component instanceof Group) {
			procesGroup((Group) component);
		} else {
			procesRef((GroupRef) component);
		}

	}

	@Override
	public boolean isElement() {
		return component instanceof GroupRef;
	}

	private void procesGroup(Group group) {
		procesModel(group.getModel());

		if (group.getMinOccurs() != null) {
			setMinOccurs(group.getMinOccurs());

		}
		if (group.getMaxOccurs() != null) {
			setMaxOccurs(group.getMaxOccurs());
		}

	}

	private void procesRef(GroupRef group) {
		ref = group.getRef();
		setElements(new ArrayList<IComplexContentModel>());

	}

}
