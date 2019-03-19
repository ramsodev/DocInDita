package net.ramso.docindita.xml.wadl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.predic8.wadl.Method;
import com.predic8.wadl.Param;
import com.predic8.wadl.Resource;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.tools.BundleManager;

public class CreateMethod extends BasicCreate {
	private String content;

	public CreateMethod(String id, String title, String content) {
		super(id, title);
		setTemplateFile("template/type.vm");
		setContent(content);
	}

	public String create(List<Method> methods, Resource resource) throws IOException {

		getContext().put("content", getContent());
		getContext().put("method", new MethodModel(methods, getParams(resource.getParams())));
		getContext().put("tools", DitaTools.class);
		getContext().put("text", BundleManager.class);
		run(getContext());
		return getFileName();
	}

	/**
	 * @return the content
	 */
	@Override
	public String getContent() {
		return this.content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	@Override
	public void setContent(String content) {
		this.content = content;
	}

	private List<ParameterModel> getParams(List<Param> params) {
		final List<ParameterModel> parameters = new ArrayList<>();
		if (params != null) {
			for (final Param param : params) {
				parameters.add(new ParameterModel(param));
			}
		}
		return parameters;
	}
}
