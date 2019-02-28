package net.ramso.doc.dita.xml.schema.model;

import java.net.MalformedURLException;
import groovy.xml.QName;
import net.ramso.doc.dita.tools.Constants;
import net.ramso.doc.dita.tools.Tools;

import com.predic8.schema.Documentation;
import com.predic8.schema.SchemaComponent;

public abstract class AbstractComponentModel implements iComponentModel {

	public abstract SchemaComponent getComponent();

	public String getHrefType() throws MalformedURLException {
		if (getType().getNamespaceURI().equalsIgnoreCase(Constants.XSD_NAMESPACE)) {
			return Constants.XSD_DOC_URI + getType().getLocalPart();
		}

		return Tools.getHrefType(getType());
	}

	public String getExternalHref() {
		if (getType().getNamespaceURI().equalsIgnoreCase(Constants.XSD_NAMESPACE)) {
			return "format=\"html\" scope=\"external\"";
		}
		return "";
	}

	public String getCode() {
		return getComponent().getAsString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
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
		String value = "";
		if (getComponent().getAnnotation() != null) {
			for (Documentation doc : getComponent().getAnnotation().getDocumentations()) {
				if (doc.getSource() != null)
					value += doc.getSource() + ": ";
				value += doc.getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;") + ". \n";
			}
		}
		return value;
	}

}
