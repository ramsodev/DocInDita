package net.ramso.doc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.ramso.doc.dita.CreateBookMap;
import net.ramso.doc.dita.References;
import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.tools.DitaTools;
import net.ramso.doc.dita.xml.schema.GenerateSchema;
import net.ramso.doc.dita.xml.wsdl.GenerateWsdl;
import net.ramso.tools.ConfigurationException;
import net.ramso.tools.FileTools;
import net.ramso.tools.LogManager;

public class GenDoc {
	private boolean first = true;

	public static void main(String[] args) {
		Config.start();
		List<String> files;
		try {
			files = Config.getParmeters(args, 1, 1);
			if (files == null) {
				System.exit(0);
			}
			GenDoc g = new GenDoc();
			List<URL> urls = g.processFiles(files);
			g.processUrls(urls);
		} catch (ConfigurationException e) {
			System.exit(1);
		} catch (IOException | URISyntaxException e) {
			LogManager.error("El parametro no es una url valida", e);
			System.out.println("El parametro no es una URL o fichero valido");
			System.exit(1);
		}
		System.exit(0);
	}

	protected List<URL> processFiles(List<String> list) throws IOException, URISyntaxException {
		List<URL> urls = new ArrayList<URL>();
		for (String file : list) {
			URL url = null;
			File f = new File(file);
			if (f.isDirectory()) {
				if (first || Config.isR()) {
					urls.addAll(processFiles(FileTools.toString(f.listFiles())));
				}
			} else {
				try {
					url = new URL(file);
				} catch (MalformedURLException e1) {
					if (!f.exists()) {
						LogManager.error("El parametro no es una fichero existente", null);
						System.out.println("El parametro no corresponde a un fichero valido");
						System.exit(1);
					} else {
						url = f.toURI().toURL();
					}
				}
				urls.add(url);
			}
			first = false;
		}
		return urls;
	}

	protected void processUrls(List<URL> urls) throws IOException, URISyntaxException {
		List<References> parts = new ArrayList<References>();
		for (URL url : urls) {
			LogManager.info("Procesando url " + url.toExternalForm());
			switch (DitaTools.getFileType(url)) {
			case DitaConstants.WSDL:
				GenerateWsdl gen = new GenerateWsdl();
				parts.add(gen.generateWSDL(url, Config.isOne()));
				break;
			case DitaConstants.XSD:
				GenerateSchema xsd = new GenerateSchema();
				parts.add(xsd.generateSchema(url, Config.isOne()));
				break;
			default:
				break;
			}
		}
		if (Config.isOne()) {
			String id = Config.getId();
			if (id == null || id.isEmpty()) {
				id = "XMLDocInDita";
			}
			String title = Config.getTitle();
			if (title == null || title.isEmpty()) {
				title = "Documentación XML";
			}
			String description = Config.getDescription();
			if (description == null || description.isEmpty()) {
				description = "";
			}
			CreateBookMap cb = new CreateBookMap(id, title, description);
			cb.create(parts);
		}
	}
}