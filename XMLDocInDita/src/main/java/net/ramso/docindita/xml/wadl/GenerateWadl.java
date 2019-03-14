package net.ramso.docindita.xml.wadl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.predic8.schema.Schema;
import com.predic8.wadl.Application;
import com.predic8.wadl.Doc;
import com.predic8.wadl.Method;
import com.predic8.wadl.Resource;
import com.predic8.wadl.Resources;
import com.predic8.wadl.WADLParser;
import com.predic8.wadl.WADLParserContext;

import net.ramso.docindita.CreateBookMap;
import net.ramso.docindita.CreatePortada;
import net.ramso.docindita.References;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.Config;
import net.ramso.docindita.xml.schema.GenerateSchema;
import net.ramso.tools.BundleManager;
import net.ramso.tools.LogManager;

public class GenerateWadl {

	public GenerateWadl() {
		super();
	}

	public References generateWADL(String url) throws IOException {
		return generateWADL(new URL(url), false);
	}

	public References generateWADL(URL url) throws IOException {
		return generateWADL(url, false);
	}

	public References generateWADL(URL url, boolean one) throws IOException {
		final String fileName = DitaTools.getName(url.toExternalForm());
		DitaTools.setIdPrefix(fileName);
		LogManager.info("Inicio de Procesado de " + url);
		final WADLParser parser = new WADLParser();
		final WADLParserContext ctx = new WADLParserContext();
		if (url.getProtocol().startsWith("file")) {
			final String p = url.getPath();
			ctx.setInput(p);
		} else {
			ctx.setInput(url.toExternalForm());
		}
		final ArrayList<References> index = new ArrayList<>();

		Application app = parser.parse(ctx);

		for (Resources resource : app.getRscss()) {
			String name = DitaTools.getName(resource.getBase());
			String content = BundleManager.getString("wadl.resources.basedir", resource.getBase()) + "\n"
					+ getDoc(resource.getDocs());
			CreatePortada cc = new CreatePortada(name + DitaConstants.SUFFIX_RESOURCE + "s",
					BundleManager.getString("wadl.resources.title", name), content);
			References chapter = new References(cc.create());
			chapter.getChilds().addAll(createResources(resource.getResources()));

			index.add(chapter);
		}
		if (app.getGrammars() != null) {
			final List<Schema> schemas = app.getGrammars().getSchemas();
			for (final Schema schema : schemas) {
				final String idSchema = (DitaTools.getSchemaId(schema.getTargetNamespace())
						+ DitaConstants.SUFFIX_RESOURCE).replaceAll("\\s+", "_");
				final References cover = findRef(idSchema, index);
				final GenerateSchema gs = new GenerateSchema();
				if (cover == null) {
					final References ref = gs.generateSchema(schema, fileName, false);
					index.add(ref);
				} else {
					gs.append2Schema(cover, schema);
				}
			}
		}
		LogManager.info("Fin procesado " + url);
		if (!one) {
			String id = Config.getId();
			if ((id == null) || id.isEmpty()) {
				id = fileName;
			}
			String title = Config.getTitle();
			if ((title == null) || title.isEmpty()) {
				title = "Documentación  del WADL " + fileName;
			}
			String description = Config.getDescription();

			final CreateBookMap cb = new CreateBookMap(id, title, description);
			cb.create(index);
			return null;
		} else {
			final CreatePortada cc = new CreatePortada(fileName + DitaConstants.SUFFIX_WSDL,
					"Documentación  del WADL " + fileName, getDoc(app.getDocs()));
			final References ref = new References(cc.create());
			ref.getChilds().addAll(index);
			return ref;
		}

	}

	private List<References> createResources(List<Resource> resources) throws IOException {
		List<References> index = new ArrayList<>();
		for (Resource resource : resources) {
			String name = DitaTools.getName(resource.getPath());
			if (name == null || name.isEmpty()) {
				name = resource.getPath();
			}
			for (Method method : resource.getMethods()) {
				CreateMethod cm = new CreateMethod(name + "_" + method.getName() + DitaConstants.SUFFIX_RESOURCE_METHOD,
						method.getName() + " " + resource.getPath(), getDoc(method.getDocs()));
				index.add(new References(cm.create(method, resource)));
			}

		}
		return index;
	}

	private References findRef(String idSchema, ArrayList<References> index) {
		for (final References ref : index) {
			if (ref.getId().equalsIgnoreCase(idSchema))
				return ref;
		}
		return null;
	}

	private String getDoc(List<Doc> docs) {
		StringBuilder content = new StringBuilder();
		if (docs != null) {
			for (Doc doc : docs) {
				content.append(doc.getContent());
			}
		}
		return content.toString();
	}
}
