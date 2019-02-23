package net.ramso.doc.dita;

import java.util.ArrayList;

public class References {
	private String id;
	private String href;
	private ArrayList<References> childs;

	public References(String href) {
		super();
		setId(href);
		setHref(href);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id.toLowerCase().replaceAll("\\s+", "_").substring(0, id.lastIndexOf('.'));
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public ArrayList<References> getChilds() {
		if (childs == null)
			childs = new ArrayList<References>();
		return childs;
	}

	public void setChilds(ArrayList<References> childs) {
		this.childs = childs;
	}

	public void addChild(References child) {
		if (childs == null) {
			childs = new ArrayList<References>();
		}
		childs.add(child);
	}

}
