package net.ramso.docindita.db;

import java.io.IOException;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.References;
import net.ramso.docindita.db.metadata.TableMetadata;
import net.ramso.tools.BundleManager;

public class CreateTable extends BasicCreate {

	public CreateTable() {
		super("", "");
	}

	public References create(TableMetadata table) throws IOException {
		setId(table.getId());
		setTitle(BundleManager.getString("Table.title", table.getName()));
		setContent(table.getDoc());
		setTemplateFile("template/table.vm");
		getContext().put("content", getContent());		
		getContext().put("table", table);
		run(getContext());
		return new References(getFileName());
	}

}
