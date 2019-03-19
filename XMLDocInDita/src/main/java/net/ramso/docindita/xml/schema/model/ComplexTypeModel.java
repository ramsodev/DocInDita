package net.ramso.docindita.xml.schema.model;

import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.Attribute;
import com.predic8.schema.AttributeGroup;
import com.predic8.schema.ComplexContent;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Derivation;
import com.predic8.schema.Extension;
import com.predic8.schema.Restriction;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.Sequence;
import com.predic8.schema.SimpleContent;

import groovy.xml.QName;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.xml.schema.model.graph.ComplexTypeGraph;
import net.ramso.tools.LogManager;

public class ComplexTypeModel extends AbstractComplexContentModel {

	private final ComplexType complexType;
	private List<AttributeModel> attributes = new ArrayList<>();
	private ArrayList<AttributeGroupModel> attributeGroups = new ArrayList<>();
	private QName superType;
	private String diagram;

	public ComplexTypeModel(ComplexType type) {
		super();
		this.complexType = type;
		init();
		LogManager.debug("Carga de ComplexType " + getName());
	}

	private void addAttributeGroups(List<AttributeGroup> attrGroups) {
		if (attrGroups != null) {
			for (final AttributeGroup atr : attrGroups) {
				if (this.attributeGroups == null) {
					this.attributeGroups = new ArrayList<>();
				}
				this.attributeGroups.add(new AttributeGroupModel(atr));
			}
		}

	}

	private void addAttributes(List<Attribute> attrs) {
		if (attrs != null) {
			for (final Attribute atr : attrs) {
				if (this.attributes == null) {
					this.attributes = new ArrayList<>();
				}
				this.attributes.add(new AttributeModel(atr));
			}
		}

	}

	/**
	 * @return the attributeGroups
	 */
	public List<AttributeGroupModel> getAttributeGroups() {
		return this.attributeGroups;
	}

	/**
	 * @return the attributes
	 */
	public List<AttributeModel> getAttributes() {
		return this.attributes;
	}

	@Override
	public String getCode() {
		try {
			return this.complexType.getAsString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		} catch (final Exception e) {
			return "";
		}
	}

	@Override
	public SchemaComponent getComponent() {
		return this.complexType;
	}

	@Override
	public String getComponentName() {

		return DitaConstants.NAME_COMPLEXTYPE;
	}

	@Override
	public String getDiagram() {
		if (this.diagram == null) {
			final ComplexTypeGraph graph = new ComplexTypeGraph(this);
			this.diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return this.diagram;
	}

	public String getNameSpace() {
		return this.complexType.getNamespaceUri();
	}

	@Override
	public QName getRef() {
		return null;
	}

	public QName getSuper() {
		return this.superType;
	}

	@Override
	public QName getType() {

		return null;
	}

	private void init() {
		this.contentType = DitaConstants.SUFFIX_COMPLEXTYPE;
		final Sequence s = this.complexType.getSequence();
		try {
			setSuper(this.complexType.getSuperTypes());
			addAttributes(this.complexType.getAttributes());
			addAttributeGroups(this.complexType.getAttributeGroups());
		} catch (final Exception e) {
			this.superType = null;
		}

		if ((this.superType != null) || (s == null)) {
			procesContent(this.complexType.getModel());
		}
		if (s != null) {
			procesModel(this.complexType.getModel());
		}
	}

	private void procesContent(SchemaComponent model) {
		Derivation derivation = null;
		if (model instanceof ComplexContent) {
			final ComplexContent c = (ComplexContent) model;
			if (((ComplexContent) model).getRestriction() == null) {
				derivation = c.getDerivation();
			}
		} else if (model instanceof SimpleContent) {
			final SimpleContent s = ((SimpleContent) model);
			if (s.getRestriction() == null) {
				derivation = (Derivation) s.getDerivation();
			}
		}
		if (derivation != null) {
			if (derivation instanceof Extension) {
				final Extension extension = (Extension) derivation;
				if (extension != null) {
					procesModel(extension.getModel());
					addAttributes(extension.getAttributes());
					addAttributeGroups(extension.getAttributeGroups());
				}
			} else if (derivation instanceof Restriction) {
				final Restriction restriction = (Restriction) derivation;
				if (restriction != null) {
					procesModel(restriction.getModel());
					addAttributes(restriction.getAttributes());
					addAttributeGroups(restriction.getAttributeGroups());
				}
			}

		}

	}

	private void setSuper(List<QName> supers) {
		this.superType = null;
		if ((supers != null) && (!supers.isEmpty())) {
			this.superType = supers.get(0);
		}
	}

}
