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
import net.ramso.doc.dita.tools.Constants;
import net.ramso.doc.dita.xml.schema.model.graph.ComplexTypeGraph;

public class ComplexTypeModel extends AbstractComplexContentModel {

	private ComplexType complexType;
	private List<AttributeModel> attributes = new ArrayList<AttributeModel>();
	private ArrayList<AttributeGroupModel> attributeGroups = new ArrayList<AttributeGroupModel>();
	private List<QName> supers;
	private String diagram;

	public ComplexTypeModel(ComplexType type) {
		super();
		this.complexType = type;
		init();
	}

	private void init() {
		this.contentType = Constants.SUFFIX_COMPLEXTYPE;
		Sequence s = complexType.getSequence();
		try {
			supers = complexType.getSuperTypes();
			addAttributes(complexType.getAttributes());
			addAttributeGroups(complexType.getAttributeGroups());
		} catch (Exception e) {
			supers = null;
		}

		if ((supers != null && supers.size() > 0) || s == null) {
			procesContent(complexType.getModel());
		}
		if (s != null) {
			procesModel(complexType.getModel());
		}
	}

	private void procesContent(SchemaComponent model) {
		Derivation derivation = null;
		if (model instanceof ComplexContent) {
			ComplexContent c = (ComplexContent) model;
			if (((ComplexContent) model).getRestriction() == null) {
				derivation = c.getDerivation();
			}
		} else if (model instanceof SimpleContent) {
			SimpleContent s = ((SimpleContent) model);
			if (s.getRestriction() == null) {
				derivation = (Derivation) s.getDerivation();
			}
		}
		if (derivation != null) {
			if (derivation instanceof Extension) {
				Extension extension = (Extension) derivation;
				if (extension != null) {
					procesModel(extension.getModel());
					addAttributes(extension.getAttributes());
					addAttributeGroups(extension.getAttributeGroups());
				}
			} else if (derivation instanceof Restriction) {
				Restriction restriction = (Restriction) derivation;
				if (restriction != null) {
					procesModel(restriction.getModel());
					addAttributes(restriction.getAttributes());
					addAttributeGroups(restriction.getAttributeGroups());
				}
			}

		}

	}

	private void addAttributeGroups(List<AttributeGroup> attrGroups) {
		if (attrGroups != null) {
			for (AttributeGroup atr : attrGroups) {
				if (attributeGroups == null)
					attributeGroups = new ArrayList<AttributeGroupModel>();
				attributeGroups.add(new AttributeGroupModel(atr));
			}
		}

	}

	private void addAttributes(List<Attribute> attrs) {
		if (attrs != null) {
			for (Attribute atr : attrs) {
				if (attributes == null)
					attributes = new ArrayList<AttributeModel>();
				attributes.add(new AttributeModel(atr));
			}
		}

	}

	public String getNameSpace() {
		return complexType.getNamespaceUri();
	}

	public String getCode() {
		try {
			return complexType.getAsString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		} catch (Exception e) {
			return "";
		}
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

	public List<QName> getSupers() {
		return supers;
	}

	@Override
	public QName getRef() {
		return null;
	}

	@Override
	public String getComponentName() {

		return Constants.NAME_COMPLEXTYPE;
	}

	@Override
	public String getDiagram() {
		if (this.diagram == null) {
			ComplexTypeGraph graph = new ComplexTypeGraph(this);
			diagram = graph.generate();
		}
		return diagram;
	}

}
