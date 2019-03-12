package net.ramso.docindita.xml.schema.model;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.Attribute;
import com.predic8.schema.AttributeGroup;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.docindita.xml.schema.model.graph.AttributeGroupGraph;
import net.ramso.tools.LogManager;

public class AttributeGroupModel extends AbstractComponentModel {

	private final SimpleTypeModel simpleType = null;

	private final AttributeGroup attributeGroup;

	private List<AttributeModel> attributes;

	private String diagram;

	public AttributeGroupModel(AttributeGroup attribute) {
		super();
		attributeGroup = attribute;
		init();
		LogManager.debug("Carga de AttributeGroup " + getName());
	}

	public List<AttributeModel> getAttributes() {
		return attributes;
	}

	@Override
	public SchemaComponent getComponent() {
		return attributeGroup;
	}

	@Override
	public String getDiagram() {
		if (diagram == null) {
			final AttributeGroupGraph graph = new AttributeGroupGraph(this);
			diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return diagram;
	}

	public String getHref() throws MalformedURLException {
		if (getRef() != null)
			return getHrefType();
		return null;

	}

	public QName getRef() {
		return attributeGroup.getRef();
	}

	/**
	 * @return the simpleType
	 */
	public SimpleTypeModel getSimpleType() {
		return simpleType;
	}

	/**
	 * @return the type
	 */
	@Override
	public QName getType() {
		return attributeGroup.getRef();
	}

	private void init() {
		for (final Attribute attribute : attributeGroup.getAllAttributes()) {
			if (attributes == null) {
				attributes = new ArrayList<>();
			}
			attributes.add(new AttributeModel(attribute));
		}
	}
}
