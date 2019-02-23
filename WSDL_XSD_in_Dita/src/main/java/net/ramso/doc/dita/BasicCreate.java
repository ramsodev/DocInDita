package net.ramso.doc.dita;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import net.ramso.doc.dita.xml.wsdl.iCreate;

public abstract class BasicCreate implements iCreate {
	private String file_name;
	private String id;
	private String title;
	private static String TEMPLATE = "template/basic.vm";
	private Template template;
	private VelocityContext context;

	public BasicCreate(String id, String title) {
		super();
		setId(id);
		setTitle(title);
	
	}

	protected void init() {
		template = Velocity.getTemplate(TEMPLATE);
		context = new VelocityContext();
		context.put("id", getId());
		context.put("title", getTitle());
	}

	protected void run(VelocityContext context) throws IOException {
		File file = new File("out" + File.separator + getFile_name());
		file.getParentFile().mkdirs();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		if (getTemplate() != null)
			getTemplate().merge(context, writer);
		writer.flush();
		writer.close();
	}

	public String getFile_name() {
		return file_name;
	}

	protected void setFile_name(String file_name) {
		this.file_name = file_name.replaceAll("\\s+","_");
	}

	public String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id.replaceAll("\\s+","_");
		setFile_name(id + ".dita");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {		
		this.title = title;
	}

	protected Template getTemplate() {
		return template;
	}
	
	protected void setTemplateFile(String template) {
		TEMPLATE = template;
		init();
	}

	protected VelocityContext getContext() {
		return context;
	}
}
