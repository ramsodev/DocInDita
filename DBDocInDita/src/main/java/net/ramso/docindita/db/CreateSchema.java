package net.ramso.docindita.db;

import java.io.IOException;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.References;
import net.ramso.docindita.db.metadata.SchemaMetadata;
import net.ramso.tools.BundleManager;

public class CreateSchema extends BasicCreate {

	public CreateSchema() {
		super("", "");

	}

	public References create(SchemaMetadata schema) throws IOException {
		setId(schema.getId());
		setTitle(BundleManager.getString("Schema.title", schema.getName()));
		setContent(schema.getDoc());
		setTemplateFile("template/portada.vm");
		getContext().put("content", getContent());
		run(getContext());
		return new References(getFileName());
	}

}
