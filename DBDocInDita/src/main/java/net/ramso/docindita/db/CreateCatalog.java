package net.ramso.docindita.db;

import java.io.IOException;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.References;
import net.ramso.docindita.db.metadata.CatalogMetadata;
import net.ramso.tools.BundleManager;

public class CreateCatalog extends BasicCreate{

	public CreateCatalog() {
		super("", "");
		setTemplateFile("template/portada.vm");
	}

	public References create(CatalogMetadata catalog) throws IOException {
		setId(catalog.getId());
		setTitle(BundleManager.getString("Catalog.title", catalog.getName()));
		setContent(catalog.getDoc());
		setTemplateFile("template/portada.vm");
		getContext().put("content", getContent());
		run(getContext());
		return new References(getFileName());
	}

}
