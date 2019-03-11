package net.ramso.doc.dita.xml.schema.model;

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
import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.xml.schema.model.graph.ComplexTypeGraph;
import net.ramso.tools.LogManager;

public class ComplexTypeModel extends AbstractComplexContentModel {

	private final ComplexType complexType;
	private List<AttributeModel> attributes = new ArrayList<>();
	private ArrayList<AttributeGroupModel> attributeGroups = new ArrayList<>();
	private QName superType;
	private String diagram;

	public ComplexTypeModel(ComplexType type) {
		super();
		complexType = type;
		init();
		LogManager.debug("Carga de ComplexType " + getName());
	}

	private void addAttributeGroups(List<AttributeGroup> attrGroups) {
		if (attrGroups != null) {
			for (final AttributeGroup atr : attrGroups) {
				if (attributeGroups == null) {
					attributeGroups = new ArrayList<>();
				}
				attributeGroups.add(new AttributeGroupModel(atr));
			}
		}

	}

	private void addAttributes(List<Attribute> attrs) {
		if (attrs != null) {
			for (final Attribute atr : attrs) {
				if (attributes == null) {
					attributes = new ArrayList<>();
				}
				attributes.add(new AttributeModel(atr));
			}
		}

	}

	/**
	 * @return the attributeGroups
	 */
	public ArrayList<AttributeGroupModel> getAttributeGroups() {
		return attributeGroups;
	}

	/**
	 * @return the attributes
	 */
	public List<AttributeModel> getAttributes() {
		return attributes;
	}

	@Override
	public String getCode() {
		try {
			return complexType.getAsString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		} catch (final Exception e) {
			return "";
		}
	}

	@Override
	public SchemaComponent getComponent() {
		return complexType;
	}

	@Override
	public String getComponentName() {

		return DitaConstants.NAME_COMPLEXTYPE;
	}

	@Override
	public String getDiagram() {
		if (diagram == null) {
			final ComplexTypeGraph graph = new ComplexTypeGraph(this);
			diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return diagram;
	}

	public String getNameSpace() {
		return complexType.getNamespaceUri();
	}

	@Override
	public QName getRef() {
		return null;
	}

	public QName getSuper() {
		return superType;
	}

	@Override
	public QName getType() {

		return null;
	}

	private void init() {
		contentType = DitaConstants.SUFFIX_COMPLEXTYPE;
		final Sequence s = complexType.getSequence();
		try {
			setSuper(complexType.getSuperTypes());
			addAttributes(complexType.getAttributes());
			addAttributeGroups(complexType.getAttributeGroups());
		} catch (final Exception e) {
			superType = null;
		}

		if ((superType != null) || (s == null)) {
			procesContent(complexType.getModel());
		}
		if (s != null) {
			procesModel(complexType.getModel());
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
		superType = null;
		if ((supers != null) && (supers.size() > 0)) {
			superType = supers.get(0);
		}
	}

}
