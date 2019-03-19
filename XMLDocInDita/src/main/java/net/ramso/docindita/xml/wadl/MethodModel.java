package net.ramso.docindita.xml.wadl;

import java.util.ArrayList;
import java.util.List;

import com.predic8.wadl.Method;
import com.predic8.wadl.Param;
import com.predic8.wadl.Representation;
import com.predic8.wadl.WADLElement;

public class MethodModel extends AbstracWadlModel {

	private final List<Method> methods;
	private List<ParameterModel> requestParams = new ArrayList<>();
	private final List<ParameterModel> responseParams = new ArrayList<>();
	private final List<String> requestMediaType = new ArrayList<>();
	private final List<String> responseMediaType = new ArrayList<>();

	public MethodModel(List<Method> methods, List<ParameterModel> parms) {
		super();
		this.methods = methods;
		this.requestParams = parms;
		init();
	}

	private void init() {
		for (final Method method : this.methods) {
			if (method.getRequest() != null) {
				for (final Param param : method.getRequest().getParams()) {
					this.requestParams.add(new ParameterModel(param));
				}
				for (final Representation representation : method.getRequest().getRepresentations()) {
					for (final Param param : representation.getParams()) {
						this.requestParams.add(new ParameterModel(param));
					}
					if ((representation.getMediaType() != null) && !representation.getMediaType().isEmpty()) {
						this.requestMediaType.add(representation.getMediaType());
					}
				}
			}
			if (method.getResponse() != null) {
				for (final Param param : method.getResponse().getParams()) {
					this.responseParams.add(new ParameterModel(param));
				}
				for (final Representation representation : method.getResponse().getRepresentations()) {
					for (final Param param : representation.getParams()) {
						this.responseParams.add(new ParameterModel(param));
					}
					if ((representation.getMediaType() != null) && !representation.getMediaType().isEmpty()) {
						this.responseMediaType.add(representation.getMediaType());
					}
				}
			}
		}
	}

	@Override
	public WADLElement getElement() {
		if (!this.methods.isEmpty()) {
			return this.methods.get(0);
		}
		return new Method();
	}

	public String getRequestDoc() {
		final StringBuilder content = new StringBuilder();
		for (final Method method : this.methods) {
			if (method.getRequest() != null) {
				content.append(getDoc(method.getRequest().getDocs()));
			}
		}
		return content.toString();
	}

	public String getResponseDoc() {
		final StringBuilder content = new StringBuilder();
		for (final Method method : this.methods) {
			if (method.getResponse() != null) {
				content.append(getDoc(method.getResponse().getDocs()));
			}
		}
		return content.toString();
	}

	public List<ParameterModel> getRequestParams() {
		return this.requestParams;
	}

	public List<ParameterModel> getResponseParams() {
		return this.responseParams;
	}

	public boolean isRequest() {
		return !getRequestDoc().isEmpty() || !getRequestParams().isEmpty() || !getRequestMediaType().isEmpty();
	}

	public boolean isResponse() {
		return !getResponseDoc().isEmpty() || !getResponseParams().isEmpty() || !getResponseMediaType().isEmpty();
	}

	public List<String> getRequestMediaType() {
		return this.requestMediaType;
	}

	public List<String> getResponseMediaType() {
		return this.responseMediaType;
	}

}
