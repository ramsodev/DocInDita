package net.ramso.doc.dita.xml.schema.model;

import java.net.MalformedURLException;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Documentation;
import com.predic8.schema.Element;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;

import net.ramso.tools.Constants;
import net.ramso.tools.Tools;

public class ElementModel {

	private boolean requiered = false;
	private int minOccurs = -1;
	private String maxOccurs = null;
	private SimpleTypeModel simpleType = null;
	private ComplexTypeModel complexType = null;
	private String type = null;
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
		if (element.getType() != null) {
			type = element.getType().getLocalPart();
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

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	public String getHrefType() throws MalformedURLException {
		if(element.getType().getNamespaceURI().equalsIgnoreCase(Constants.XSD_NAMESPACE)) {
			return Constants.XSD_DOC_URI+element.getType().getLocalPart();
		}
		
		return Tools.getHrefType(element.getType());
	}
	
	public String getExternalHref() {
		if(element.getType().getNamespaceURI().equalsIgnoreCase(Constants.XSD_NAMESPACE)) {
			return "format=\"html\" scope=\"external\"";
		}
		return "";
	}

	public String getCode() {
		return element.getAsString().replaceAll("<", "&lt;").replaceAll(">","&gt;");
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return element.getName();
	}

	public String getDoc() {
		String writer = "";
		if (element.getAnnotation() != null) {
			for (Documentation doc : element.getAnnotation().getDocumentations()) {
				writer += doc.getContent() + "\n";
			}
		}
		return writer;
	}

}
