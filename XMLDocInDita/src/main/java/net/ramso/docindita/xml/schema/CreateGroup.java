package net.ramso.docindita.xml.schema;

import java.io.IOException;

import com.predic8.schema.Group;

import net.ramso.docindita.BasicCreate;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.schema.model.GroupModel;

public class CreateGroup extends BasicCreate {

	private final String idSchema;

	public CreateGroup(String idSchema) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definición del Grupo");
		this.idSchema = idSchema;
	}

	public String create(Group group) throws IOException {
		setId(idSchema + "_" + group.getName() + DitaConstants.SUFFIX_GROUP);
		setTitle("Complex Type " + group.getName());
		loadContent(group.getAnnotation());
		init();
		getContext().put("content", getContent());
		getContext().put("group", new GroupModel(group));
		getContext().put("tools", DitaTools.class);
		run(getContext());
		return getFile_name();
	}

}
