package net.ramso.docindita.xml.schema.model;

import java.net.MalformedURLException;

import com.predic8.schema.Documentation;

import net.ramso.docindita.tools.DitaTools;

public abstract class AbstractComponentModel implements IComponentModel {

	private boolean scaleDiagram = false;
	private String id = "";

	@Override
	public String getCode() {
		return getComponent().getAsString().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	

	@Override
	public String getDoc() {
		StringBuilder value = new StringBuilder();
		if (getComponent().getAnnotation() != null) {
			for (final Documentation doc : getComponent().getAnnotation().getDocumentations()) {
				if (doc.getSource() != null) {
					value.append(doc.getSource() + ": ");
				}
				value.append(doc.getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;") + ". \n");
			}
		}
		return value.toString();
	}

	@Override
	public String getExternalHref() {
		return DitaTools.getExternalHref(getType());
	}

	@Override
	public String getHrefType() throws MalformedURLException {
		return DitaTools.getHrefType(getType());
	}

	public String getHref() {
		return DitaTools.getHref(true, getId());
	}
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return getComponent().getName();
	}


	@Override
	public boolean isScaleDiagram() {
		return scaleDiagram;
	}

	protected void setScaleDiagram(boolean scaleDiagram) {
		this.scaleDiagram = scaleDiagram;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}

}
