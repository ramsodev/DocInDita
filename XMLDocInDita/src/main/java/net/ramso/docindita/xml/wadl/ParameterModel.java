package net.ramso.docindita.xml.wadl;

import com.predic8.wadl.Option;
import com.predic8.wadl.Param;
import com.predic8.wadl.WADLElement;

import groovy.xml.QName;

public class ParameterModel extends AbstracWadlModel {

	private Param parameter;

	public ParameterModel(Param parameter) {
		super();
		this.parameter = parameter;

	}

	public boolean isRequired() {
		return parameter.isRequired();
	}

	public boolean isRepeting() {
		return parameter.isRepeating();
	}

	public String getName() {
		return parameter.getName();
	}

	public String getStyle() {
		return parameter.getStyle() != null ? parameter.getStyle() : new String();
	}

	public QName getType() {

		if (parameter.getType() instanceof QName) {
			return (QName) parameter.getType();
		} else if (parameter.getType() == null) {
			return QName.valueOf("");
		}
		return QName.valueOf(parameter.getType().toString());
	}

	public String getDefault() {
		return parameter.getDfault() != null ? parameter.getDfault() : new String();
	}

	public String getFixed() {
		return parameter.getFixed() != null ? parameter.getFixed() : new String();
	}

	public String getOptions() {
		String result = "";
		if (parameter.getOptions() != null) {
			StringBuilder content = new StringBuilder();
			boolean coma = false;
			for (Option option : parameter.getOptions()) {
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
		return parameter;
	}

}
