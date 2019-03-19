package net.ramso.docindita.xml.schema.model;

import java.net.MalformedURLException;

import com.predic8.schema.Attribute;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.docindita.xml.schema.model.graph.AttributeGraph;
import net.ramso.tools.LogManager;

public class AttributeModel extends AbstractComponentModel {

	private SimpleTypeModel simpleType = null;

	private final Attribute attribute;

	private String defaultValue;

	private String fixedValue;

	private String form;

	private String usage;

	private String diagram;

	public AttributeModel(Attribute attribute) {
		super();
		this.attribute = attribute;
		init();

		LogManager.debug("Carga de Attribute " + getName());
	}

	@Override
	public SchemaComponent getComponent() {
		return this.attribute;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public String getDiagram() {
		if (this.diagram == null) {
			final AttributeGraph graph = new AttributeGraph(this);
			this.diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return this.diagram;
	}

	public String getFixedValue() {
		return this.fixedValue;
	}

	public String getForm() {
		return this.form;
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
		return this.attribute.getRef();
	}

	/**
	 * @return the simpleType
	 */
	public SimpleTypeModel getSimpleType() {
		return this.simpleType;
	}

	/**
	 * @return the type
	 */
	@Override
	public QName getType() {
		if (this.attribute.getType() != null) {
			return this.attribute.getType();
		}
		return this.attribute.getRef();
	}

	public String getUsage() {
		return this.usage;
	}

	private void init() {
		this.defaultValue = this.attribute.getDefaultValue();
		this.fixedValue = this.attribute.getFixedValue();
		this.form = this.attribute.getForm();
		this.usage = this.attribute.getUse();
		if (this.attribute.getSimpleType() != null) {
			this.simpleType = new SimpleTypeModel(this.attribute.getSimpleType());
		}
	}

}
