package net.ramso.docindita.xml.wadl;

import java.util.ArrayList;
import java.util.List;

import com.predic8.wadl.Method;
import com.predic8.wadl.Param;
import com.predic8.wadl.Representation;
import com.predic8.wadl.WADLElement;

public class MethodModel extends AbstracWadlModel {

	private List<Method> methods;
	private List<ParameterModel> requestParams = new ArrayList<>();
	private List<ParameterModel> responseParams = new ArrayList<>();
	private List<String> requestMediaType = new ArrayList<>();
	private List<String> responseMediaType = new ArrayList<>();

	public MethodModel(List<Method> methods, List<ParameterModel> parms) {
		super();
		this.methods = methods;
		this.requestParams = parms;
		init();
	}

	private void init() {
		for (Method method : methods) {
			if (method.getRequest() != null) {
				for (Param param : method.getRequest().getParams()) {
					requestParams.add(new ParameterModel(param));
				}
				for (Representation representation : method.getRequest().getRepresentations()) {
					for (Param param : representation.getParams()) {
						requestParams.add(new ParameterModel(param));
					}
					if (representation.getMediaType() != null && !representation.getMediaType().isEmpty()) {
						requestMediaType.add(representation.getMediaType());
					}
				}
			}
			if (method.getResponse() != null) {
				for (Param param : method.getResponse().getParams()) {
					responseParams.add(new ParameterModel(param));
				}
				for (Representation representation : method.getResponse().getRepresentations()) {
					for (Param param : representation.getParams()) {
						responseParams.add(new ParameterModel(param));
					}
					if (representation.getMediaType() != null && !representation.getMediaType().isEmpty()) {
						responseMediaType.add(representation.getMediaType());
					}
				}
			}
		}
	}

	@Override
	public WADLElement getElement() {
		if (!methods.isEmpty()) {
			return methods.get(0);
		}
		return new Method();
	}

	public String getRequestDoc() {
		StringBuilder content = new StringBuilder();
		for (Method method : methods) {
			if (method.getRequest() != null) {
				content.append(getDoc(method.getRequest().getDocs()));
			}
		}
		return content.toString();
	}

	public String getResponseDoc() {
		StringBuilder content = new StringBuilder();
		for (Method method : methods) {
			if (method.getResponse() != null) {
				content.append(getDoc(method.getResponse().getDocs()));
			}
		}
		return content.toString();
	}

	public List<ParameterModel> getRequestParams() {
		return requestParams;
	}

	public List<ParameterModel> getResponseParams() {
		return responseParams;
	}

	public boolean isRequest() {
		return !getRequestDoc().isEmpty() || !getRequestParams().isEmpty() || !getRequestMediaType().isEmpty();
	}

	public boolean isResponse() {
		return !getResponseDoc().isEmpty() || !getResponseParams().isEmpty() || !getResponseMediaType().isEmpty();
	}

	public List<String> getRequestMediaType() {
		return requestMediaType;
	}

	public List<String> getResponseMediaType() {
		return responseMediaType;
	}

}
