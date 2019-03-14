package net.ramso.docindita.xml.schema.model;

import java.util.List;

import groovy.xml.QName;

public interface IComplexContentModel extends IComponentModel {
	public String getComponentName();

	public String getContentType();

	public List<IComplexContentModel> getElements();

	public String getMaxOccurs();

	public int getMinOccurs();

	public QName getRef();

	public boolean isElement();

	public boolean isRequiered();

	public void setElements(List<IComplexContentModel> elements);

	public void setMinOccurs(Object minOccurs);
}
