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

	public void addChild(References child) {
		if (childs == null) {
			childs = new ArrayList<>();
		}
		childs.add(child);
	}

	public ArrayList<References> getChilds() {
		if (childs == null) {
			childs = new ArrayList<>();
		}
		return childs;
	}

	public String getHref() {
		return href;
	}

	public String getId() {
		return id;
	}

	public References searchChild(String id) {
		for (final References child : getChilds()) {
			if (child.getId().equalsIgnoreCase(id))
				return child;
			else if (!child.getChilds().isEmpty()) {
				final References c = child.searchChild(id);
				if (c != null)
					return c;
			}
		}
		return null;
	}

	public void setChilds(ArrayList<References> childs) {
		this.childs = childs;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setId(String id) {
		this.id = id.toLowerCase().replaceAll("\\s+", "_").substring(0, id.lastIndexOf('.'));
	}
}
