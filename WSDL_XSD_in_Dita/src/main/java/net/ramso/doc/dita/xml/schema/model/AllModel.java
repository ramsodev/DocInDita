package net.ramso.doc.dita.xml.schema.model;


import com.predic8.schema.All;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.doc.dita.tools.DitaConstants;

public class AllModel extends AbstractComplexContentModel {
	private All all;
	

	public AllModel(All all) {
		super();
		this.all = all;
		init();
	}

	private void init() {
		this.contentType = DitaConstants.ALL;
		procesAll(all);
		
	}

	@Override
	public SchemaComponent getComponent() {

		return all;
	}

	@Override
	public QName getType() {

		return null;
	}

	/**
	 * @return the ref
	 */
	public QName getRef() {
		return null;
	}

	@Override
	public String getComponentName() {
		return DitaConstants.CHOICE;
	}

	/* (non-Javadoc)
	 * @see net.ramso.doc.dita.xml.schema.model.AbstractComponentModel#getName()
	 */
	@Override
	public String getName() {
		
		return DitaConstants.CHOICE;
	}

	@Override
	public String getDiagram() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
