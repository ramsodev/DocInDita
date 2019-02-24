package net.ramso.doc.dita.xml.schema.model;

import java.io.StringWriter;

import com.predic8.schema.Documentation;
import com.predic8.schema.Element;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;

public class ElementModel {

	private boolean requiered = false;
	private int minOccurs = -1;
	private String maxOccurs = null;
	private SimpleTypeModel simpleType = null;
	private String type = null;
	private Element element;
	private String name;

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
			}
		}
	}

	/**
	 * @return the minOccurs
	 */
	public int getMinOccurs() {
		return minOccurs;
	}

	/**
	 * @param minOccurs the minOccurs to set
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

	public String getCode() {
		return element.getAsString();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return element.getName();
	}
	
	public String getDoc() {
		 String writer = "";
		if(element.getAnnotation()!=null) {
			for(Documentation doc : element.getAnnotation().getDocumentations()){
				writer += doc.getContent() + "\n";
			}
		}
		return writer;
	}

}
