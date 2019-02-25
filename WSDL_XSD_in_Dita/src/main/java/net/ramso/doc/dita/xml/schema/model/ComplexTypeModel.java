package net.ramso.doc.dita.xml.schema.model;

import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.Attribute;
import com.predic8.schema.AttributeGroup;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.Sequence;

import groovy.xml.QName;

public class ComplexTypeModel extends AbstractComponentModel {
	private boolean requiered = false;
	private int minOccurs = -1;
	private int maxOccurs = -1;
	private ArrayList<ElementModel> elements = null;

	private ComplexType complexType;
	private List<AttributeModel> attributes;
	private ArrayList<AttributeGroupModel> attributeGroups;

	public ComplexTypeModel(ComplexType type) {
		super();
		this.complexType = type;
		init();
	}

	private void init() {

		complexType.getAttributeGroups();
		Sequence s = complexType.getSequence();

		for (Attribute atr : complexType.getAttributes()) {
			if (attributes == null)
				attributes = new ArrayList<AttributeModel>();
			attributes.add(new AttributeModel(atr));
		}
		for (AttributeGroup atr : complexType.getAttributeGroups()) {
			if (attributeGroups == null)
				attributeGroups = new ArrayList<AttributeGroupModel>();
			attributeGroups.add(new AttributeGroupModel(atr));
		}
		for (Element e : s.getElements()) {
			addElement(new ElementModel(e));
		}
		if (s.getMinOccurs() != null) {
			setMinOccurs(((Integer) s.getMinOccurs()));
		}
		if (s.getMaxOccurs() != null) {
			maxOccurs = (Integer) s.getMaxOccurs();
		}

	}

	private void addElement(ElementModel elementModel) {
		if (elements == null)
			elements = new ArrayList<ElementModel>();
		elements.add(elementModel);
	}

	/**
	 * @param minOccurs
	 *            the minOccurs to set
	 */
	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
		if (minOccurs > 0)
			requiered = true;
	}

	/**
	 * @return the requiered
	 */
	public boolean isRequiered() {
		return requiered;
	}

	/**
	 * @return the maxOccurs
	 */
	public int getMaxOccurs() {
		return maxOccurs;
	}

	public String getNameSpace() {
		return complexType.getNamespaceUri();
	}

	public String getCode() {
		return complexType.getAsString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	/**
	 * @return the minOccurs
	 */
	public int getMinOccurs() {
		return minOccurs;
	}

	/**
	 * @return the elements
	 */
	public ArrayList<ElementModel> getElements() {
		return elements;
	}

	@Override
	public SchemaComponent getComponent() {
		return complexType;
	}

	@Override
	public QName getType() {

		return null;
	}

	/**
	 * @return the attributes
	 */
	public List<AttributeModel> getAttributes() {
		return attributes;
	}

	/**
	 * @return the attributeGroups
	 */
	public ArrayList<AttributeGroupModel> getAttributeGroups() {
		return attributeGroups;
	}

}
