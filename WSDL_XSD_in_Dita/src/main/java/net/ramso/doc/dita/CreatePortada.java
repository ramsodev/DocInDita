package net.ramso.doc.dita;

import java.io.IOException;

public class CreatePortada extends BasicCreate {

	private String content;
	private String diagram;

	public CreatePortada(String id, String title, String content) {
		super(id, title);
		setTemplateFile("template/portada.vm");
		setContent(content);
	}

	public String create() throws IOException {
		getContext().put("content", getContent());
		getContext().put("diagram", getDiagram());

		run(getContext());
		return getFile_name();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setDiagram(String diagram) {
		this.diagram = diagram;

	}

	/**
	 * @return the diagram
	 */
	public String getDiagram() {
		return diagram;
	}

}
