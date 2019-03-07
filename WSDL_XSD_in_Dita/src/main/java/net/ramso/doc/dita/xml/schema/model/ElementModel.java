package net.ramso.doc.dita.xml.schema.model;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;

import groovy.xml.QName;
import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.xml.schema.model.graph.ElementGraph;

public class ElementModel extends AbstractComplexContentModel {

	private boolean requiered = false;
	private int minOccurs = -1;
	private String maxOccurs = null;
	private SimpleTypeModel simpleType = null;
	private ComplexTypeModel complexType = null;
	private Element element;
	private String diagram;
	

	public ElementModel(Element element) {
		super();
		this.element = element;
		init();
	}

	private void init() {
		this.contentType = DitaConstants.SUFFIX_ELEMENT;
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
		if(element.getRef() != null) {
			return element.getRef();
		}
		return element.getType();
	}

	@Override
	public SchemaComponent getComponent() {
		return element;
	}
	
	public QName getRef() {
		return element.getRef();
	}

	@Override
	public String getComponentName() {
		return DitaConstants.NAME_ELEMENT;
	}

	@Override
	public String getDiagram() {
		if(this.diagram == null) {
			ElementGraph graph = new ElementGraph(this);
			diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return diagram;
	}

}
