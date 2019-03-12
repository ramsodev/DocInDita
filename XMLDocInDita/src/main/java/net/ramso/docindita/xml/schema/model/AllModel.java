package net.ramso.docindita.xml.schema.model;

import com.predic8.schema.All;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.docindita.xml.DitaConstants;

public class AllModel extends AbstractComplexContentModel {
	private final All all;

	public AllModel(All all) {
		super();
		this.all = all;
		init();
	}

	@Override
	public SchemaComponent getComponent() {

		return all;
	}

	@Override
	public String getComponentName() {
		return DitaConstants.CHOICE;
	}

	@Override
	public String getDiagram() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ramso.doc.dita.xml.schema.model.AbstractComponentModel#getName()
	 */
	@Override
	public String getName() {

		return DitaConstants.CHOICE;
	}

	/**
	 * @return the ref
	 */
	@Override
	public QName getRef() {
		return null;
	}

	@Override
	public QName getType() {

		return null;
	}

	private void init() {
		contentType = DitaConstants.ALL;
		procesAll(all);

	}

}
