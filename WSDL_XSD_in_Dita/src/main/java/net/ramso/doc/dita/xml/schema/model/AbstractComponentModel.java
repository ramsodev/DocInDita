package net.ramso.doc.dita.xml.schema.model;

import java.net.MalformedURLException;
import groovy.xml.QName;

import com.predic8.schema.Documentation;
import com.predic8.schema.SchemaComponent;

import net.ramso.tools.Constants;
import net.ramso.tools.Tools;

public abstract class AbstractComponentModel implements iComponentModel {
	
	public abstract SchemaComponent getComponent();

	public String getHrefType() throws MalformedURLException {
		if(getType().getNamespaceURI().equalsIgnoreCase(Constants.XSD_NAMESPACE)) {
			return Constants.XSD_DOC_URI+getType().getLocalPart();
		}
		
		return Tools.getHrefType(getType());
	}
	
	public String getExternalHref() {
		if(getType().getNamespaceURI().equalsIgnoreCase(Constants.XSD_NAMESPACE)) {
			return "format=\"html\" scope=\"external\"";
		}
		return "";
	}

	public String getCode() {
		return getComponent().getAsString().replaceAll("<", "&lt;").replaceAll(">","&gt;");
	}

	/**
	 * @return the type
	 */
	public abstract QName getType();

	/**
	 * @return the name
	 */
	public String getName() {
		return getComponent().getName();
	}

	public String getDoc() {
		String writer = "";
		if (getComponent().getAnnotation() != null) {
			for (Documentation doc : getComponent().getAnnotation().getDocumentations()) {
				writer += doc.getContent() + "\n";
			}
		}
		return writer;
	}

}
