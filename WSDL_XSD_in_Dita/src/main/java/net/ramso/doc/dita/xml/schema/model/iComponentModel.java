package net.ramso.doc.dita.xml.schema.model;

import java.net.MalformedURLException;

import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;

public interface iComponentModel {
	public String getHrefType() throws MalformedURLException;

	public String getExternalHref();

	public String getCode();

	public QName getType();

	public String getName();

	public String getDoc();
	
	SchemaComponent getComponent();

}
