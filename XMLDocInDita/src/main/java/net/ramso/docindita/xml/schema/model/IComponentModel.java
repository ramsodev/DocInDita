package net.ramso.docindita.xml.schema.model;

import java.net.MalformedURLException;

import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;

public interface IComponentModel {
	public String getCode();

	SchemaComponent getComponent();

	String getDiagram();

	public String getDoc();

	public String getExternalHref();

	public String getHrefType() throws MalformedURLException;

	public String getName();

	public QName getType();

	boolean isScaleDiagram();

	public String getHref();

	public String getFileName();

	public void setFileName(String id);

}
