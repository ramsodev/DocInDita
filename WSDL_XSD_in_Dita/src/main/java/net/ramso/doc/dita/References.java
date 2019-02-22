package net.ramso.doc.dita;

import java.util.ArrayList;

public class References {
	private String id;
	private String href;
	private ArrayList<References> childs;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id.toLowerCase().replaceAll("\\s+","_").substring(0, id.lastIndexOf('.'));
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public ArrayList<References> getChilds() {
		return childs;
	}

	public void setChilds(ArrayList<References> childs) {
		this.childs = childs;
	}

	public References(String href) {
		super();
		setId(href);
		setHref(href);
	}

}
