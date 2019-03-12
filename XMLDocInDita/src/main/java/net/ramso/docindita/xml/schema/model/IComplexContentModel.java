package net.ramso.docindita.xml.schema.model;

import java.util.ArrayList;

import groovy.xml.QName;

public interface IComplexContentModel extends iComponentModel {
	public String getComponentName();

	public String getContentType();

	public ArrayList<IComplexContentModel> getElements();

	public String getMaxOccurs();

	public int getMinOccurs();

	public QName getRef();

	public boolean isElement();

	public boolean isRequiered();

	public void setElements(ArrayList<IComplexContentModel> elements);

	public void setMinOccurs(Object minOccurs);
}
