package net.ramso.doc.dita.xml.schema.model;

import java.net.MalformedURLException;

import com.predic8.schema.Documentation;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.doc.dita.tools.DitaTools;

public abstract class AbstractComponentModel implements iComponentModel {

	private boolean scaleDiagram = false;

	@Override
	public String getCode() {
		return getComponent().getAsString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	@Override
	public abstract SchemaComponent getComponent();

	@Override
	public String getDoc() {
		String value = "";
		if (getComponent().getAnnotation() != null) {
			for (final Documentation doc : getComponent().getAnnotation().getDocumentations()) {
				if (doc.getSource() != null) {
					value += doc.getSource() + ": ";
				}
				value += doc.getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;") + ". \n";
			}
		}
		return value;
	}

	@Override
	public String getExternalHref() {
		return DitaTools.getExternalHref(getType());
	}

	@Override
	public String getHrefType() throws MalformedURLException {
		return DitaTools.getHrefType(getType());
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return getComponent().getName();
	}

	/**
	 * @return the type
	 */
	@Override
	public abstract QName getType();

	@Override
	public boolean isScaleDiagram() {
		return scaleDiagram;
	}

	protected void setScaleDiagram(boolean scaleDiagram) {
		this.scaleDiagram = scaleDiagram;
	}

}
