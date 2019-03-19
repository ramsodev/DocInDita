package net.ramso.docindita.xml.wadl;

import java.util.List;

import com.predic8.wadl.Doc;

public abstract class AbstracWadlModel implements IWadlModel {

	@Override
	public String getDoc() {
		return getDoc(getElement().getDocs());
	}

	protected String getDoc(List<Doc> docs) {
		final StringBuilder content = new StringBuilder();
		if (docs != null) {
			for (final Doc doc : docs) {
				content.append(doc.getContent());
			}
		}
		return content.toString();
	}

}
