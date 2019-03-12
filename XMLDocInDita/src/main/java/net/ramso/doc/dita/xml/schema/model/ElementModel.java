package net.ramso.doc.dita.xml.schema.model;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;

import groovy.xml.QName;
import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.tools.DitaTools;
import net.ramso.doc.dita.xml.schema.model.graph.ElementGraph;
import net.ramso.tools.LogManager;

public class ElementModel extends AbstractComplexContentModel {

	private boolean requiered = false;
	private int minOccurs = -1;
	private String maxOccurs = null;
	private SimpleTypeModel simpleType = null;
	private ComplexTypeModel complexType = null;
	private final Element element;
	private String diagram;

	public ElementModel(Element element) {
		super();
		this.element = element;
		init();
		LogManager.debug("Carga de Element " + getName());
	}

	public ComplexTypeModel getComplexType() {
		return complexType;
	}

	@Override
	public SchemaComponent getComponent() {
		return element;
	}

	@Override
	public String getComponentName() {
		return DitaConstants.NAME_ELEMENT;
	}

	@Override
	public String getDiagram() {
		if (diagram == null) {
			final ElementGraph graph = new ElementGraph(this);
			diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return diagram;
	}

	/**
	 * @return the maxOccurs
	 */
	@Override
	public String getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * @return the minOccurs
	 */
	@Override
	public int getMinOccurs() {
		return minOccurs;
	}

	@Override
	public QName getRef() {
		return element.getRef();
	}

	public iComponentModel getRefType() {
		final TypeDefinition t = DitaTools.getType(getRef());
		iComponentModel m = null;
		if (t != null) {
			if (t instanceof SimpleType) {
				m = new SimpleTypeModel((SimpleType) t);
			} else if (t instanceof ComplexType) {
				m = new ComplexTypeModel((ComplexType) t);
			}
		}
		return m;
	}

	/**
	 * @return the simpleType
	 */
	public SimpleTypeModel getSimpleType() {
		return simpleType;
	}

	@Override
	public QName getType() {
		if (element.getRef() != null)
			return element.getRef();
		if ((element.getType() == null) && (getSimpleType() == null) && (getComplexType() == null))
			return DitaTools.getAnyType();
		return element.getType();
	}

	private void init() {
		contentType = DitaConstants.SUFFIX_ELEMENT;
		if ((element.getMinOccurs() != null) && !element.getMinOccurs().isEmpty()) {
			setMinOccurs(Integer.parseInt(element.getMinOccurs()));
		}
		if ((element.getMaxOccurs() != null) && !element.getMaxOccurs().isEmpty()) {
			maxOccurs = element.getMaxOccurs();
		}
		if (element.getEmbeddedType() != null) {
			final TypeDefinition t = element.getEmbeddedType();
			if (t instanceof SimpleType) {
				simpleType = new SimpleTypeModel((SimpleType) t);
			} else if (t instanceof ComplexType) {
				complexType = new ComplexTypeModel((ComplexType) t);
			}
		}
	}

	/**
	 * @return the requiered
	 */
	@Override
	public boolean isRequiered() {
		return requiered;
	}

	/**
	 * @param minOccurs
	 *            the minOccurs to set
	 */
	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
		if (minOccurs > 0) {
			requiered = true;
		}
	}

}
