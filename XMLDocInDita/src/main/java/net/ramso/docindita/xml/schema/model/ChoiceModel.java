package net.ramso.docindita.xml.schema.model;

import com.predic8.schema.Choice;
import com.predic8.schema.SchemaComponent;

import groovy.xml.QName;
import net.ramso.docindita.tools.DitaConstants;

public class ChoiceModel extends AbstractComplexContentModel {
	private final Choice choice;

	public ChoiceModel(Choice choice) {
		super();
		this.choice = choice;
		init();
	}

	@Override
	public SchemaComponent getComponent() {

		return choice;
	}

	@Override
	public String getComponentName() {
		return DitaConstants.CHOICE;
	}

	@Override
	public String getDiagram() {
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
		contentType = DitaConstants.CHOICE;
		procesChoice(choice);

	}

}
