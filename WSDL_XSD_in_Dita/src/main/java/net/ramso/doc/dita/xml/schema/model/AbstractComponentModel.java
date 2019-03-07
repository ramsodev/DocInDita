package net.ramso.doc.dita.xml.schema.model;

import java.net.MalformedURLException;

import com.predic8.schema.Documentation;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.doc.dita.tools.DitaTools;

public abstract class AbstractComponentModel implements iComponentModel {

	public abstract SchemaComponent getComponent();

	private boolean scaleDiagram = false;

	public String getHrefType() throws MalformedURLException {
		return DitaTools.getHrefType(getType());
	}

	public String getExternalHref() {
		return DitaTools.getExternalHref(getType());
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

	public boolean isScaleDiagram() {
		return scaleDiagram;
	}

	protected void setScaleDiagram(boolean scaleDiagram) {
		this.scaleDiagram = scaleDiagram;
	}

}
