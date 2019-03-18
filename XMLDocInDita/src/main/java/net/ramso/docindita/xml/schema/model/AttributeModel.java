package net.ramso.docindita.xml.schema.model;

import java.net.MalformedURLException;

import com.predic8.schema.Attribute;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
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
		return attribute;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getDiagram() {
		if (diagram == null) {
			final AttributeGraph graph = new AttributeGraph(this);
			diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return diagram;
	}

	public String getFixedValue() {
		return fixedValue;
	}

	public String getForm() {
		return form;
	}

	public String getHref() {
		String href = "";
		try {
			if (getRef() != null) {

				href = getHrefType();
			} else {
				href = super.getHref();
			}
//			return DitaTools.getHref(attribute.getQname(), DitaConstants.SUFFIX_ATTRIBUTE);
		} catch (MalformedURLException e) {
			LogManager.warn("Error al generar url del attribute " + getName(), e);
		}
		return href;
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
	@Override
	public QName getType() {
		if (attribute.getType() != null)
			return attribute.getType();
		return attribute.getRef();
	}

	public String getUsage() {
		return usage;
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

}
