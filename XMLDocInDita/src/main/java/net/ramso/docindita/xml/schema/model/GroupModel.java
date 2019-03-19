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
		this.component = group;
		init();
		LogManager.debug("Carga de Grupo " + getName());
	}

	public GroupModel(GroupRef group) {
		super();
		this.component = group;
		init();
	}

	@Override
	public SchemaComponent getComponent() {

		return this.component;
	}

	@Override
	public String getComponentName() {
		return DitaConstants.NAME_GROUP;
	}

	@Override
	public String getDiagram() {
		if (this.diagram == null) {
			final GroupGraph graph = new GroupGraph(this);
			this.diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return this.diagram;
	}

	public GroupModel getModel() {
		if (getRef() == null) {
			return this;
		} else {
			return new GroupModel(getComponent().getSchema().getGroup(getRef()));
		}
	}

	/**
	 * @return the ref
	 */
	@Override
	public QName getRef() {
		return this.ref;
	}

	@Override
	public QName getType() {
		if (getRef() != null) {
			return getRef();
		}
		return ((Group) this.component).getQname();
	}

	private void init() {
		this.contentType = DitaConstants.SUFFIX_GROUP;
		if (this.component instanceof Group) {
			procesGroup((Group) this.component);
		} else {
			procesRef((GroupRef) this.component);
		}

	}

	@Override
	public boolean isElement() {
		return this.component instanceof GroupRef;
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
		this.ref = group.getRef();
		setElements(new ArrayList<IComplexContentModel>());

	}

}
