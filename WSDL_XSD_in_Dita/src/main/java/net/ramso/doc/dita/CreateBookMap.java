package net.ramso.doc.dita;

import java.io.IOException;
import java.util.ArrayList;

public class CreateBookMap extends BasicCreate {

	
	private String content;
	
	

	public CreateBookMap(String id,String title, String content) {
		super(id,title);
		setTemplateFile("template/bookmap.vm");
		setContent(content);
		setFile_name(id+".ditamap");
	}

	

	public String create(ArrayList<References> references) throws IOException {		
		getContext().put("content", getContent());
		getContext().put("references", references);
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
