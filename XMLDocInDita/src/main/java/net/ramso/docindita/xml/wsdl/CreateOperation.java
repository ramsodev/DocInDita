package net.ramso.docindita.xml.wsdl;

import java.io.IOException;

import com.predic8.wsdl.Operation;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.tools.DitaTools;

public class CreateOperation extends BasicCreate {
	private String content;

	public CreateOperation(String id, String title, String content) {
		super(id, title);
		setTemplateFile("template/operation.vm");
		setContent(content);
	}

	public String create(Operation operation) throws IOException {
		getContext().put("content", getContent());
		getContext().put("operation", operation);
		getContext().put("tools", DitaTools.class);
		run(getContext());
		return getFileName();
	}

	/**
	 * @return the content
	 */
	@Override
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	@Override
	public void setContent(String content) {
		this.content = content;
	}

}
