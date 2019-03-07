package net.ramso.doc.dita.xml.schema;

import java.io.IOException;

import com.predic8.schema.Annotation;
import com.predic8.schema.Documentation;
import com.predic8.schema.Group;

import net.ramso.doc.dita.BasicCreate;
import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.tools.DitaTools;
import net.ramso.doc.dita.xml.schema.model.GroupModel;

public class CreateGroup extends BasicCreate {

	private String content;
	private String idSchema;

	public CreateGroup(String idSchema) {
		super("", "");
		setTemplateFile("template/type.vm");
		setContent("Definición del Grupo");
		this.idSchema = idSchema;
	}

	

	public String create(Group group) throws IOException {
		setId(idSchema+"_"+group.getName() + DitaConstants.SUFFIX_GROUP);
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
