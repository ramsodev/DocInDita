package net.ramso.doc.dita.xml.schema.model;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;

import groovy.xml.QName;

public class ElementModel extends AbstractComponentModel{

	private boolean requiered = false;
	private int minOccurs = -1;
	private String maxOccurs = null;
	private SimpleTypeModel simpleType = null;
	private ComplexTypeModel complexType = null;
	private Element element;
	

	public ElementModel(Element element) {
		super();
		this.element = element;
		init();
	}

	private void init() {
		if (element.getMinOccurs() != null && !element.getMinOccurs().isEmpty()) {
			setMinOccurs(Integer.parseInt(element.getMinOccurs()));
		}
		if (element.getMaxOccurs() != null && !element.getMaxOccurs().isEmpty()) {
			maxOccurs = element.getMaxOccurs();
		}
		if (element.getEmbeddedType() != null) {
			TypeDefinition t = element.getEmbeddedType();
			if (t instanceof SimpleType) {
				simpleType = new SimpleTypeModel((SimpleType) t);
			} else if (t instanceof ComplexType) {
				complexType = new ComplexTypeModel((ComplexType) t);
			}
		}
	}

	public ComplexTypeModel getComplexType() {
		return complexType;
	}

	/**
	 * @return the minOccurs
	 */
	public int getMinOccurs() {
		return minOccurs;
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
	public String getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * @return the simpleType
	 */
	public SimpleTypeModel getSimpleType() {
		return simpleType;
	}

	@Override
	public QName getType() {		
		return element.getType();
	}

	@Override
	public SchemaComponent getComponent() {
		return element;
	}

}
