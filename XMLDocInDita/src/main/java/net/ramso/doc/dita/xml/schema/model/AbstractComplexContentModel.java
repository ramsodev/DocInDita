package net.ramso.doc.dita.xml.schema.model;

import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.All;
import com.predic8.schema.Choice;
import com.predic8.schema.Element;
import com.predic8.schema.Group;
import com.predic8.schema.GroupRef;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.Sequence;

import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.tools.LogManager;

public abstract class AbstractComplexContentModel extends AbstractComponentModel implements IComplexContentModel {

	private int minOccurs = -1;
	private String maxOccurs = "-1";
	private boolean requiered;
	private ArrayList<IComplexContentModel> elements = new ArrayList<>();
	protected String contentType = DitaConstants.SEQUENCE;

	protected void addElement(IComplexContentModel elementModel) {
		if (elements == null) {
			elements = new ArrayList<>();
		}
		elements.add(elementModel);
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public ArrayList<IComplexContentModel> getElements() {
		return elements;
	}

	/**
	 * @return the maxOccurs
	 */
	@Override
	public String getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * @return the maxOccurs
	 */
	@Override
	public int getMinOccurs() {
		return minOccurs;
	}

	@Override
	public boolean isElement() {
		return this instanceof ElementModel;
	}

	/**
	 * @return the requiered
	 */
	@Override
	public boolean isRequiered() {
		return requiered;
	}

	protected void procesAll(All all) {

		processParticles(all.getParticles());
		if (all.getMinOccurs() != null) {
			setMinOccurs(all.getMinOccurs());
		}
		if (all.getMaxOccurs() != null) {
			setMaxOccurs(all.getMaxOccurs());
		}

	}

	protected void procesChoice(Choice choice) {

		processParticles(choice.getParticles());
		if (choice.getMinOccurs() != null) {
			setMinOccurs(choice.getMinOccurs());
		}
		if (choice.getMaxOccurs() != null) {
			setMaxOccurs(choice.getMaxOccurs());
		}

	}

	protected void procesModel(Object model) {
		if (model != null) {
			if (model instanceof Sequence) {
				addElement(new SequenceModel((Sequence) model));
			} else if (model instanceof All) {
				addElement(new AllModel((All) model));
			} else if (model instanceof Choice) {
				addElement(new ChoiceModel((Choice) model));
			} else {
				LogManager.error("Otro tipo" + model.getClass().getSimpleName(), null);
			}
		}
	}

	protected void procesSequence(Sequence sequence) {

		processParticles(sequence.getParticles());
		if (sequence.getMinOccurs() != null) {
			setMinOccurs(sequence.getMinOccurs());
		}
		if (sequence.getMaxOccurs() != null) {
			setMaxOccurs(sequence.getMaxOccurs());
		}
	}

	private void processParticles(List<SchemaComponent> particles) {
		for (final SchemaComponent par : particles) {
			if (par instanceof Element) {
				addElement(new ElementModel((Element) par));
			} else if (par instanceof Choice) {
				addElement(new ChoiceModel((Choice) par));
			} else if (par instanceof Sequence) {
				addElement(new SequenceModel((Sequence) par));
			} else if (par instanceof All) {
				addElement(new AllModel((All) par));
			} else if (par instanceof Group) {
				addElement(new GroupModel((Group) par));
			} else if (par instanceof GroupRef) {
				addElement(new GroupModel((GroupRef) par));
			}
		}

	}

	@Override
	public void setElements(ArrayList<IComplexContentModel> elements) {
		this.elements = elements;
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

	/**
	 * @param minOccurs
	 *            the minOccurs to set
	 */
	@Override
	public void setMinOccurs(Object minOccurs) {
		if (minOccurs instanceof Integer) {
			this.minOccurs = ((Integer) minOccurs).intValue();
		} else if (minOccurs instanceof String) {
			try {
				this.minOccurs = Integer.parseInt((String) minOccurs);
			} catch (final Exception e) {
				this.minOccurs = -1;
			}
		} else {
			this.minOccurs = -1;
		}
		if (this.minOccurs > 0) {
			requiered = true;
		}
	}
}
