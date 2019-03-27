package net.ramso.docindita;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import net.ramso.docindita.db.Config;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.tools.BundleManager;
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
		return this.content;
	}

	protected VelocityContext getContext() {
		return this.context;
	}

	@Override
	public String getFileName() {
		if (this.fileName.length() > 250) {
			final String ext = this.fileName.substring(this.fileName.lastIndexOf('.'));
			setFileName(this.fileName.substring(0, 230) + hashCode() + ext);
		}
		return this.fileName;
	}

	@Override
	public String getId() {
		return this.id;
	}

	protected Template getTemplate() {
		return this.template;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	protected void init() {
		this.template = Velocity.getTemplate(this.templateFile);
		this.context = new VelocityContext();
		this.context.put("id", getId());
		this.context.put("title", getTitle());
		this.context.put("tools", DitaTools.class);
		this.context.put("text", BundleManager.class);
	}

	protected void run(VelocityContext context) throws IOException {

		final File file = new File(Config.getOutputDir() + File.separator + getFileName());
		file.getParentFile().mkdirs();
		final FileWriter fw = new FileWriter(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(fw);
			if (getTemplate() != null) {
				getTemplate().merge(context, writer);
			}
			writer.flush();
		} catch (final Exception e) {
			LogManager.warn("Problemas al crear el fichero", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

	}

	public void setContent(String content) {
		this.content = content;
	}

	protected void setFileName(String fileName) {
		this.fileName = TextTools.clean(fileName).replaceAll("\\s+", "_").replaceAll("/", "_").replaceAll("\\\\", "_");
	}

	protected void setId(String id) {
		if (!id.isEmpty() && Character.isDigit(id.charAt(0))) {
			id = "id" + id;
		}
		this.id = TextTools.cleanNonAlfaNumeric(id, "_");
		setFileName(this.id + ".dita");
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
