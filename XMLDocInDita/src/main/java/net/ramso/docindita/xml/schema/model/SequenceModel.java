package net.ramso.docindita.xml.schema.model;

import com.predic8.schema.SchemaComponent;
import com.predic8.schema.Sequence;

import groovy.xml.QName;
import net.ramso.docindita.tools.DitaConstants;

public class SequenceModel extends AbstractComplexContentModel {
	private final Sequence sequence;

	public SequenceModel(Sequence choice) {
		super();
		sequence = choice;
		init();
	}

	@Override
	public SchemaComponent getComponent() {

		return sequence;
	}

	@Override
	public String getComponentName() {
		return DitaConstants.SEQUENCE;
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

		return DitaConstants.SEQUENCE;
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
		contentType = DitaConstants.SEQUENCE;
		procesSequence(sequence);

	}

}
