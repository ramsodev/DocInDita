package net.ramso.doc.dita.xml.schema.model;


import com.predic8.schema.All;
import com.predic8.schema.Choice;
import com.predic8.schema.Group;
import com.predic8.schema.GroupRef;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.tools.Constants;

public class AllModel extends AbstractComplexContentModel {
	private All all;
	

	public AllModel(All all) {
		super();
		this.all = all;
		init();
	}

	private void init() {
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
		return Constants.CHOICE;
	}

	/* (non-Javadoc)
	 * @see net.ramso.doc.dita.xml.schema.model.AbstractComponentModel#getName()
	 */
	@Override
	public String getName() {
		
		return Constants.CHOICE;
	}
	
}
