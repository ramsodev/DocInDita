package net.ramso.doc.dita.xml.wsdl;

import java.io.IOException;

import com.predic8.wsdl.Operation;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.tools.Tools;

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
		getContext().put("tools", Tools.class);
		run(getContext());
		return getFile_name();
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

}
