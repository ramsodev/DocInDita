package net.ramso.docindita;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.predic8.schema.Annotation;
import com.predic8.schema.Documentation;

import net.ramso.docindita.xml.Config;
import net.ramso.tools.LogManager;
import net.ramso.tools.TextTools;

public abstract class BasicCreate implements ICreate {
	private String templateFile = "template/basic.vm";
	private String fileName;
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
	public String getFileName() {
		return fileName;
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
		template = Velocity.getTemplate(templateFile);
		context = new VelocityContext();
		context.put("id", getId());
		context.put("title", getTitle());
	}

	public void loadContent(Annotation annotation) {
		StringBuilder value = new StringBuilder();
		if (annotation != null && annotation.getDocumentations() != null) {
			for (final Documentation doc : annotation.getDocumentations()) {
				if (doc.getSource() != null) {
					value.append(doc.getSource() + ": ");
				}
				value.append(doc.getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;") + ". \n");
			}

		}
		if (value.length() > 0) {
			setContent(value.toString());
		}
	}

	protected void run(VelocityContext context) throws IOException {
		final File file = new File(Config.getOutputDir() + File.separator + getFileName());
		file.getParentFile().mkdirs();
		FileWriter fw = new FileWriter(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(fw);
			if (getTemplate() != null) {
				getTemplate().merge(context, writer);
			}
			writer.flush();
		} catch (Exception e) {
			LogManager.warn("Problemas al crear el fichero", e);
		} finally {
			if (writer != null)
				writer.close();
		}

	}

	public void setContent(String content) {
		this.content = content;
	}

	protected void setFileName(String fileName) {
		this.fileName = TextTools.clean(fileName).replaceAll("\\s+", "_").replaceAll("/", "_").replaceAll("\\\\", "_");
	}

	protected void setId(String id) {
		if (!Character.isAlphabetic(id.charAt(0))) {
			id = "id" + id;
		}
		this.id =id.replaceAll("\\s+", "_");
		setFileName(id + ".dita");
	}

	protected void setTemplateFile(String template) {
		this.templateFile = template;
		init();
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}
}
