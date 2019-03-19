package net.ramso.docindita.xml.schema.model;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;

import groovy.xml.QName;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.schema.model.graph.ElementGraph;
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
		return this.complexType;
	}

	@Override
	public SchemaComponent getComponent() {
		return this.element;
	}

	@Override
	public String getComponentName() {
		return DitaConstants.NAME_ELEMENT;
	}

	@Override
	public String getDiagram() {
		if (this.diagram == null) {
			final ElementGraph graph = new ElementGraph(this);
			this.diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return this.diagram;
	}

	/**
	 * @return the maxOccurs
	 */
	@Override
	public String getMaxOccurs() {
		return this.maxOccurs;
	}

	/**
	 * @return the minOccurs
	 */
	@Override
	public int getMinOccurs() {
		return this.minOccurs;
	}

	@Override
	public QName getRef() {
		return this.element.getRef();
	}

	public IComponentModel getRefType() {
		final TypeDefinition t = DitaTools.getType(getRef());
		IComponentModel m = null;
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
		return this.simpleType;
	}

	@Override
	public QName getType() {
		if (this.element.getRef() != null) {
			return this.element.getRef();
		}
		if ((this.element.getType() == null) && (getSimpleType() == null) && (getComplexType() == null)) {
			return DitaTools.getAnyType();
		}
		return this.element.getType();
	}

	private void init() {
		this.contentType = DitaConstants.SUFFIX_ELEMENT;
		if ((this.element.getMinOccurs() != null) && !this.element.getMinOccurs().isEmpty()) {
			setMinOccurs(Integer.parseInt(this.element.getMinOccurs()));
		}
		if ((this.element.getMaxOccurs() != null) && !this.element.getMaxOccurs().isEmpty()) {
			this.maxOccurs = this.element.getMaxOccurs();
		}
		if (this.element.getEmbeddedType() != null) {
			final TypeDefinition t = this.element.getEmbeddedType();
			if (t instanceof SimpleType) {
				this.simpleType = new SimpleTypeModel((SimpleType) t);
			} else if (t instanceof ComplexType) {
				this.complexType = new ComplexTypeModel((ComplexType) t);
			}
		}
	}

	/**
	 * @return the requiered
	 */
	@Override
	public boolean isRequiered() {
		return this.requiered;
	}

	/**
	 * @param minOccurs
	 *            the minOccurs to set
	 */
	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
		if (minOccurs > 0) {
			this.requiered = true;
		}
	}

}
