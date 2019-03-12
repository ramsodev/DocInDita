package net.ramso.docindita;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import net.ramso.docindita.db.Config;

public abstract class BasicCreate implements iCreate {
	private static String TEMPLATE = "template/basic.vm";
	private String file_name;
	private String id;
	private String title;
	private Template template;
	private VelocityContext context;
	private String content;

	public BasicCreate(String id, String title) {
		super();
		setId(id);
		setTitle(title);

	}

	public String getContent() {
		return content;
	}

	protected VelocityContext getContext() {
		return context;
	}

	@Override
	public String getFile_name() {
		return file_name;
	}

	@Override
	public String getId() {
		return id;
	}

	protected Template getTemplate() {
		return template;
	}

	@Override
	public String getTitle() {
		return title;
	}

	protected void init() {
		template = Velocity.getTemplate(TEMPLATE);
		context = new VelocityContext();
		context.put("id", getId());
		context.put("title", getTitle());
	}

	

	protected void run(VelocityContext context) throws IOException {
		final File file = new File(Config.getOutputDir() + File.separator + getFile_name());
		file.getParentFile().mkdirs();
		final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		if (getTemplate() != null) {
			getTemplate().merge(context, writer);
		}
		writer.flush();
		writer.close();
	}

	public void setContent(String content) {
		this.content = content;
	}

	protected void setFile_name(String file_name) {
		this.file_name = file_name.replaceAll("\\s+", "_");
	}

	protected void setId(String id) {
		this.id = id.replaceAll("\\s+", "_");
		setFile_name(id + ".dita");
	}

	protected void setTemplateFile(String template) {
		TEMPLATE = template;
		init();
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}
}
