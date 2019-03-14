package net.ramso.docindita.xml.wadl;

import java.util.ArrayList;
import java.util.List;

import com.predic8.wadl.Method;
import com.predic8.wadl.Param;
import com.predic8.wadl.Representation;
import com.predic8.wadl.WADLElement;

public class MethodModel extends AbstracWadlModel {

	private Method method;
	private List<ParameterModel> requestParams = new ArrayList<>();
	private List<ParameterModel> responseParams = new ArrayList<>();

	public MethodModel(Method method, List<ParameterModel> parms) {
		super();
		this.method = method;
		this.requestParams = parms;
		init();
	}

	private void init() {
		if (method.getRequest() != null) {
			for (Param param : method.getRequest().getParams()) {
				requestParams.add(new ParameterModel(param));
			}
			for (Representation representation : method.getRequest().getRepresentations()) {
				for (Param param : representation.getParams()) {
					requestParams.add(new ParameterModel(param));
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
			}
		}
	}

	@Override
	public WADLElement getElement() {
		return method;
	}

	public String getRequestDoc() {
		if (method.getRequest() != null) {
			return getDoc(method.getRequest().getDocs());
		}
		return "";
	}

	public String getResponseDoc() {
		if (method.getResponse() != null) {
			return getDoc(method.getResponse().getDocs());
		}
		return "";
	}

	public List<ParameterModel> getRequestParams() {
		return requestParams;
	}

	public List<ParameterModel> getResponseParams() {
		return responseParams;
	}

	public boolean isRequest() {
		return !getRequestDoc().isEmpty() || !getRequestParams().isEmpty();
	}

	public boolean isResponse() {
		return !getResponseDoc().isEmpty() || !getResponseParams().isEmpty();
	}

}
