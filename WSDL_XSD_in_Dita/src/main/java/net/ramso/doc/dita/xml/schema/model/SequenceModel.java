package net.ramso.doc.dita.xml.schema.model;

import com.predic8.schema.Choice;
import com.predic8.schema.Group;
import com.predic8.schema.GroupRef;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.Sequence;

import groovy.xml.QName;
import net.ramso.doc.dita.tools.DitaConstants;

public class SequenceModel extends AbstractComplexContentModel {
	private Sequence sequence;
	

	public SequenceModel(Sequence choice) {
		super();
		this.sequence = choice;
		init();
	}

	private void init() {
		this.contentType = DitaConstants.SEQUENCE;
		procesSequence(sequence);
		
	}

	@Override
	public SchemaComponent getComponent() {

		return sequence;
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
		return DitaConstants.SEQUENCE;
	}

	/* (non-Javadoc)
	 * @see net.ramso.doc.dita.xml.schema.model.AbstractComponentModel#getName()
	 */
	@Override
	public String getName() {
		
		return DitaConstants.SEQUENCE;
	}

	@Override
	public String getDiagram() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
