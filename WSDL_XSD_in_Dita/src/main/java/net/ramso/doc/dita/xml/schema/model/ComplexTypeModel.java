package net.ramso.doc.dita.xml.schema.model;

import java.util.ArrayList;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Documentation;
import com.predic8.schema.Element;
import com.predic8.schema.Sequence;

public class ComplexTypeModel {
	private boolean requiered = false;
	private int minOccurs = -1;
	private int maxOccurs = -1;
	private ArrayList<ElementModel> elements = null;

	private ComplexType type;

	public ComplexTypeModel(ComplexType type) {
		super();
		this.type = type;
		init();
	}

	private void init() {
		type.getAllAttributes();
		type.getAttributeGroups();
		Sequence s = type.getSequence();
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
	public int getMaxOccurs() {
		return maxOccurs;
	}

	public String getNameSpace() {
		return type.getNamespaceUri();
	}

	public String getCode() {
		return type.getAsString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
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
	
	public String getName() {
		return type.getName();
	}
	
	public String getDoc() {
		 String writer = "";
		if(type.getAnnotation()!=null) {
			for(Documentation doc : type.getAnnotation().getDocumentations()){
				writer += doc.getContent() + "\n";
			}
		}
		return writer;
	}

}
