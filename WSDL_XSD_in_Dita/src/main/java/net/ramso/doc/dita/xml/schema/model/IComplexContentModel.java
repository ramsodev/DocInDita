package net.ramso.doc.dita.xml.schema.model;

import java.util.ArrayList;

import groovy.xml.QName;

public interface IComplexContentModel extends iComponentModel {
	public void setMinOccurs(Object minOccurs);

	public int getMinOccurs();

	public boolean isRequiered();

	public String getMaxOccurs();

	public String getContentType();

	public ArrayList<IComplexContentModel> getElements();

	public void setElements(ArrayList<IComplexContentModel> elements);
	
	public QName getRef();
	
	public String getComponentName();
	public boolean isElement();
}
