package net.ramso.doc.dita.xml.schema.model;

import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.Attribute;
import com.predic8.schema.AttributeGroup;
import com.predic8.schema.ComplexContent;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Extension;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.Sequence;
import com.predic8.schema.SimpleContent;

import groovy.xml.QName;
import net.ramso.tools.Constants;

public class ComplexTypeModel extends AbstractComplexContentModel {

	private ComplexType complexType;
	private List<AttributeModel> attributes;
	private ArrayList<AttributeGroupModel> attributeGroups;
	private List<QName> supers;

	public ComplexTypeModel(ComplexType type) {
		super();
		this.complexType = type;
		init();
	}

	private void init() {
		Sequence s = complexType.getSequence();
		supers = complexType.getSuperTypes();
		addAttributes(complexType.getAllAttributes());
		addAttributeGroups(complexType.getAttributeGroups());
		if (supers != null && supers.size() > 0) {
			procesContent(complexType.getModel());
		}
		if (s != null) {
			procesModel(s);
		} else {
			setElements(null);
		}
	}

	private void procesContent(SchemaComponent model) {
		if (model instanceof ComplexContent) {
			ComplexContent c = (ComplexContent) model;
			if (((ComplexContent) model).getRestriction() == null) {
				Extension extension = (Extension) c.getDerivation();
				if (extension != null) {
					procesModel(extension.getModel());
					addAttributes(extension.getAttributes());
					addAttributeGroups(extension.getAttributeGroups());
				}
			}
		} else if (model instanceof SimpleContent) {
			SimpleContent s = ((SimpleContent) model);
			if (s.getRestriction() == null) {
				Extension extension = s.getExtension();
				if (extension != null) {
					procesModel(extension.getModel());
					addAttributes(extension.getAttributes());
					addAttributeGroups(extension.getAttributeGroups());
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
		return complexType.getAsString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
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

}
