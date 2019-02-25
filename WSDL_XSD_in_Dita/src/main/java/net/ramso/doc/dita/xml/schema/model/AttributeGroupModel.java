package net.ramso.doc.dita.xml.schema.model;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.Attribute;
import com.predic8.schema.AttributeGroup;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;

public class AttributeGroupModel extends AbstractComponentModel {

	private SimpleTypeModel simpleType = null;

	private AttributeGroup attributeGroup;

	private List<AttributeModel> attributes;

	public AttributeGroupModel(AttributeGroup attribute) {
		super();
		this.attributeGroup = attribute;
		init();
	}

	private void init() {
		for (Attribute attribute : attributeGroup.getAllAttributes()) {
			if (attributes == null)
				attributes = new ArrayList<AttributeModel>();
			attributes.add(new AttributeModel(attribute));
		}
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
		return attributeGroup.getRef();
	}

	@Override
	public SchemaComponent getComponent() {
		return attributeGroup;
	}

	public List<AttributeModel> getAttributes() {
		return attributes;
	}

	public QName getRef() {
		return attributeGroup.getRef();
	}

	public String getHref() throws MalformedURLException {
		if (getRef() != null) {
			return getHrefType();
		}
		return null;

	}
}
