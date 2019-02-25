package net.ramso.doc.dita.xml.schema.model;

import java.net.MalformedURLException;

import com.predic8.schema.Attribute;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Documentation;
import com.predic8.schema.Element;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;

import groovy.xml.QName;
import net.ramso.tools.Constants;
import net.ramso.tools.Tools;

public class AttributeModel extends AbstractComponentModel{

	
	private SimpleTypeModel simpleType = null;
	
	private String type = null;
	private Attribute attribute;

	private String defaultValue;

	private String fixedValue;

	private String form;

	private String usage;
	

	public AttributeModel(Attribute attribute) {
		super();
		this.attribute = attribute;
		init();
	}

	private void init() {
		defaultValue = attribute.getDefaultValue();
		fixedValue = attribute.getFixedValue();
		form = attribute.getForm();
		usage = attribute.getUse();
		
		if (attribute.getType() != null) {
			type = attribute.getType().getLocalPart();
		}
		
		if (attribute.getSimpleType() != null) {			
				simpleType = new SimpleTypeModel(attribute.getSimpleType());			
		}
	}

	public QName  getRef() {
		return attribute.getRef();
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
	public QName getType() {
		return attribute.getType();
	}

	

	@Override
	public SchemaComponent getComponent() {		
		return attribute;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getFixedValue() {
		return fixedValue;
	}

	public String getForm() {
		return form;
	}

	public String getUsage() {
		return usage;
	}

}
