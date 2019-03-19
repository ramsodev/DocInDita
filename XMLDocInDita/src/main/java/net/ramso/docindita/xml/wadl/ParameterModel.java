package net.ramso.docindita.xml.wadl;

import com.predic8.wadl.Option;
import com.predic8.wadl.Param;
import com.predic8.wadl.WADLElement;

import groovy.xml.QName;

public class ParameterModel extends AbstracWadlModel {

	private final Param parameter;

	public ParameterModel(Param parameter) {
		super();
		this.parameter = parameter;

	}

	public boolean isRequired() {
		return this.parameter.isRequired();
	}

	public boolean isRepeting() {
		return this.parameter.isRepeating();
	}

	public String getName() {
		return this.parameter.getName();
	}

	public String getStyle() {
		return this.parameter.getStyle() != null ? this.parameter.getStyle() : "";
	}

	public QName getType() {

		if (this.parameter.getType() instanceof QName) {
			return (QName) this.parameter.getType();
		} else if (this.parameter.getType() == null) {
			return QName.valueOf("");
		}
		return QName.valueOf(this.parameter.getType().toString());
	}

	public String getDefault() {
		return this.parameter.getDfault() != null ? this.parameter.getDfault() : "";
	}

	public String getFixed() {
		return this.parameter.getFixed() != null ? this.parameter.getFixed() : "";
	}

	public String getOptions() {
		String result = "";
		if (this.parameter.getOptions() != null) {
			final StringBuilder content = new StringBuilder();
			boolean coma = false;
			for (final Option option : this.parameter.getOptions()) {
				if (coma) {
					content.append(", ");
				} else {
					coma = true;
				}
				content.append(option.getValue());
			}
			result = content.toString();
		}
		return result;
	}

	@Override
	public WADLElement getElement() {
		return this.parameter;
	}

}
