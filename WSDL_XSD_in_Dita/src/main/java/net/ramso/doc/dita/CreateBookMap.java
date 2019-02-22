package net.ramso.doc.dita;

import java.io.IOException;
import java.util.ArrayList;

public class CreateBookMap extends BasicCreate {

	
	private String content;
	
	

	public CreateBookMap(String title, String content, String filename) {
		super(title);
		setTemplateFile("template/bookmap.vm");
		setContent(content);
		setFile_name(filename);
	}

	

	public String create(ArrayList<String> services) throws IOException {		
		getContext().put("content", getContent());
		getContext().put("services", services);
		run(getContext());
		return getFile_name();
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}



	@Override
	protected void setId(String id) {
		id="net.ramso.doc."+id;
		super.setId(id);
	}

	

	
}
