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

	private final AttributeGroup attributeGroup;

	private List<AttributeModel> attributes;

	private String diagram;

	public AttributeGroupModel(AttributeGroup attribute) {
		super();
		this.attributeGroup = attribute;
		init();
		LogManager.debug("Carga de AttributeGroup " + getName());
	}

	public List<AttributeModel> getAttributes() {
		return this.attributes;
	}

	@Override
	public SchemaComponent getComponent() {
		return this.attributeGroup;
	}

	@Override
	public String getDiagram() {
		if (this.diagram == null) {
			final AttributeGroupGraph graph = new AttributeGroupGraph(this);
			this.diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return this.diagram;
	}

	@Override
	public String getHref() {
		String href = "";
		try {
			if (getRef() != null) {

				href = getHrefType();
			} else {
				href = super.getHref();
			}
		} catch (final MalformedURLException e) {
			LogManager.warn("Error al generar url del attribute " + getName(), e);
		}
		return href;

	}

	public QName getRef() {
		return this.attributeGroup.getRef();
	}

	/**
	 * @return the type
	 */
	@Override
	public QName getType() {
		return this.attributeGroup.getRef();
	}

	private void init() {
		for (final Attribute attribute : this.attributeGroup.getAllAttributes()) {
			if (this.attributes == null) {
				this.attributes = new ArrayList<>();
			}
			this.attributes.add(new AttributeModel(attribute));
		}
	}
}
