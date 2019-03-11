package net.ramso.doc.dita.xml.schema.model;

import java.net.MalformedURLException;

import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;

public interface iComponentModel {
	public String getCode();

	SchemaComponent getComponent();

	String getDiagram();

	public String getDoc();

	public String getExternalHref();

	public String getHrefType() throws MalformedURLException;

	public String getName();

	public QName getType();

	boolean isScaleDiagram();

}
