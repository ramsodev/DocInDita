package net.ramso.doc.dita.xml.schema.model;

import java.net.MalformedURLException;

import com.predic8.schema.Attribute;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.doc.dita.xml.schema.model.graph.AttributeGraph;

public class AttributeModel extends AbstractComponentModel {

	private SimpleTypeModel simpleType = null;

	private Attribute attribute;

	private String defaultValue;

	private String fixedValue;

	private String form;

	private String usage;

	private String diagram;

	public AttributeModel(Attribute attribute) {
		super();
		this.attribute = attribute;
		init();
	}

	private void init() {
		defaultValue = attribute.getDefaultValue();
		fixedValue = attribute.getFixedValue();
		form = attribute.getForm();
		usage = attribute.getUse();
		if (attribute.getSimpleType() != null) {
			simpleType = new SimpleTypeModel(attribute.getSimpleType());
		}
	}

	public QName getRef() {
		return attribute.getRef();
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
	public QName getType() {
		if (attribute.getType() != null) {
			return attribute.getType();
		}
		return attribute.getRef();
	}

	@Override
	public SchemaComponent getComponent() {
		return attribute;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getFixedValue() {
		return fixedValue;
	}

	public String getForm() {
		return form;
	}

	public String getUsage() {
		return usage;
	}

	public String getHref() throws MalformedURLException {
		if (getRef() != null) {
			return getHrefType();
		}
		return null;

	}

	@Override
	public String getDiagram() {
		if(this.diagram == null) {
			AttributeGraph graph = new AttributeGraph(this);
			diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return diagram;
	}

}
