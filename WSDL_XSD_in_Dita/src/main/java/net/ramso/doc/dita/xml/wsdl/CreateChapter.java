package net.ramso.doc.dita.xml.wsdl;

import java.io.IOException;

public class CreateChapter extends BasicCreate {

	
	private String content;
	
	

	public CreateChapter(String title, String content) {
		super(title);
		setTemplateFile("template/portada.vm");
		setContent(content);
	}

	

	public String create() throws IOException {		
		getContext().put("content", getContent());
		run(getContext());
		return getFile_name() + "#" + getId();
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
}
