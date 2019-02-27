package net.ramso.doc.dita.xml.schema.model;

import java.util.ArrayList;

import com.predic8.schema.All;
import com.predic8.schema.Choice;
import com.predic8.schema.Element;
import com.predic8.schema.Sequence;

import net.ramso.tools.Constants;

public abstract class AbstractComplexContentModel extends AbstractComponentModel {

	private int minOccurs = -1;
	private String maxOccurs = "-1";
	private boolean requiered;
	private ArrayList<ElementModel> elements;
	private String contentType = Constants.SEQUENCE;

	protected void procesModel(Object model) {
		if (model instanceof Sequence) {
			procesSequence((Sequence) model);
		} else if (model instanceof All) {
			procesAll((All) model);
		} else if (model instanceof Choice) {
			procesChoice((Choice) model);
		}
	}

	private void procesChoice(Choice choice) {
		this.contentType = Constants.CHOICE;
		if (choice.getElements() != null) {
			for (Element e : choice.getElements()) {
				addElement(new ElementModel(e));
			}
		}
		if (choice.getMinOccurs() != null) {
			setMinOccurs(((Integer) choice.getMinOccurs()));
		}
		if (choice.getMaxOccurs() != null) {
			setMaxOccurs(choice.getMaxOccurs());
		}

	}

	private void procesAll(All all) {
		this.contentType = Constants.ALL;
		if (all.getElements() != null) {
			for (Element e : all.getElements()) {
				addElement(new ElementModel(e));
			}
		}
		if (all.getMinOccurs() != null) {
			setMinOccurs(((Integer) all.getMinOccurs()));
		}
		if (all.getMaxOccurs() != null) {
			setMaxOccurs(all.getMaxOccurs());
		}

	}

	private void procesSequence(Sequence sequence) {
		this.contentType = Constants.SEQUENCE;

		if (sequence.getElements() != null) {
			for (Element e : sequence.getElements()) {
				addElement(new ElementModel(e));
			}
		}
		if (sequence.getMinOccurs() != null) {
			setMinOccurs(((Integer) sequence.getMinOccurs()));
		}
		if (sequence.getMaxOccurs() != null) {
			setMaxOccurs(sequence.getMaxOccurs());
		}
	}

	private void addElement(ElementModel elementModel) {
		if (elements == null)
			elements = new ArrayList<ElementModel>();
		elements.add(elementModel);
	}

	/**
	 * @param minOccurs
	 *            the minOccurs to set
	 */
	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
		if (minOccurs > 0)
			requiered = true;
	}

	/**
	 * @return the maxOccurs
	 */
	public int getMinOccurs() {
		return minOccurs;
	}

	/**
	 * @return the requiered
	 */
	public boolean isRequiered() {
		return requiered;
	}

	/**
	 * @return the maxOccurs
	 */
	public String getMaxOccurs() {
		return maxOccurs;
	}

	protected void setMaxOccurs(Object maxOccurs) {
		if (maxOccurs instanceof Integer) {
			this.maxOccurs = ((Integer) maxOccurs).toString();
		} else if (maxOccurs instanceof String) {
			this.maxOccurs = (String) maxOccurs;
		} else {
			this.maxOccurs = maxOccurs.toString();
		}

	}

	public String getContentType() {
		return contentType;
	}

	public ArrayList<ElementModel> getElements() {
		return elements;
	}

	public void setElements(ArrayList<ElementModel> elements) {
		this.elements = elements;
	}

}
