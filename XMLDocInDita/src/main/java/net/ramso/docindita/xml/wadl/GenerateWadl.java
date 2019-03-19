package net.ramso.docindita.xml.wadl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import net.ramso.docindita.xml.schema.GenerateXSD;
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

		final Application app = parser.parse(ctx);

		for (final Resources resource : app.getRscss()) {
			final String name = DitaTools.getName(resource.getBase());
			final String content = BundleManager.getString("wadl.resources.basedir", resource.getBase()) + "\n"
					+ getDoc(resource.getDocs());
			final CreatePortada cc = new CreatePortada(fileName + "_" + name + DitaConstants.SUFFIX_RESOURCE + "s",
					BundleManager.getString("wadl.resources.title", name), content);
			final References chapter = new References(cc.create());
			chapter.getChilds().addAll(createResources(resource.getResources(), fileName));

			index.add(chapter);
		}
		if (app.getGrammars() != null) {
			final List<Schema> schemas = app.getGrammars().getSchemas();
			for (final Schema schema : schemas) {
				final String idSchema = (DitaTools.getSchemaId(schema.getTargetNamespace())
						+ DitaConstants.SUFFIX_RESOURCE).replaceAll("\\s+", "_");
				final References cover = findRef(idSchema, index);
				final GenerateXSD gs = new GenerateXSD();
				if (cover == null) {
					final References ref = gs.generateXsd(schema, fileName, false);
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
			final String description = Config.getDescription();

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

	private List<References> createResources(List<Resource> resources, String prefix) throws IOException {
		final List<References> index = new ArrayList<>();
		for (final Resource resource : resources) {
			String name = DitaTools.getName(resource.getPath());
			if ((name == null) || name.isEmpty()) {
				name = resource.getPath();
			}
			final Map<String, List<Method>> ms = new HashMap<>();
			for (final Method method : resource.getMethods()) {
				if (ms.containsKey(method.getName())) {
					ms.get(method.getName()).add(method);
				} else {
					final List<Method> m = new ArrayList<>();
					m.add(method);
					ms.put(method.getName(), m);
				}
			}
			for (final Entry<String, List<Method>> entry : ms.entrySet()) {
				final CreateMethod cm = new CreateMethod(
						prefix + "_" + name + "_" + entry.getKey() + DitaConstants.SUFFIX_RESOURCE_METHOD,
						entry.getKey() + " " + resource.getPath(), getDocs(entry.getValue()));
				index.add(new References(cm.create(entry.getValue(), resource)));
			}

		}
		return index;
	}

	private References findRef(String idSchema, ArrayList<References> index) {
		for (final References ref : index) {
			if (ref.getId().equalsIgnoreCase(idSchema)) {
				return ref;
			}
		}
		return null;
	}

	private String getDocs(List<Method> methods) {
		final StringBuilder content = new StringBuilder();
		for (final Method method : methods) {
			content.append(getDoc(method.getDocs()));
		}
		return content.toString();
	}

	private String getDoc(List<Doc> docs) {
		final StringBuilder content = new StringBuilder();
		if (docs != null) {
			for (final Doc doc : docs) {
				content.append(doc.getContent());
			}
		}
		return content.toString();
	}
}
