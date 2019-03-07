package net.ramso.doc.dita.xml.schema.model;

import com.predic8.schema.Choice;
import com.predic8.schema.Group;
import com.predic8.schema.GroupRef;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.doc.dita.tools.DitaConstants;

public class ChoiceModel extends AbstractComplexContentModel {
	private Choice choice;
	

	public ChoiceModel(Choice choice) {
		super();
		this.choice = choice;
		init();
	}

	private void init() {
		this.contentType = DitaConstants.CHOICE;
		procesChoice(choice);
		
	}

	@Override
	public SchemaComponent getComponent() {

		return choice;
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
