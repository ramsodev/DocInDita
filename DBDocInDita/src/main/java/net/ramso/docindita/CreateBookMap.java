package net.ramso.docindita;

import java.io.IOException;
import java.util.List;

public class CreateBookMap extends BasicCreate {

	private String content;

	public CreateBookMap(String id, String title, String content) {
		super(id, title);
		setTemplateFile("template/bookmap.vm");
		setContent(content);
		setFileName(id + ".ditamap");
	}

	public String create(List<References> references) throws IOException {
		return create(references, false);
	}

	public String create(List<References> references, boolean parts) throws IOException {
		getContext().put("content", getContent());
		getContext().put("references", references);
		getContext().put("parts", parts);
		run(getContext());
		return getFileName();
	}

	@Override
	public String getContent() {
		return this.content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	protected void setId(String id) {
		id = "net.ramso.doc." + id;
		super.setId(id);
	}

}
