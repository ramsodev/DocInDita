package net.ramso.doc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.ramso.doc.dita.CreateBookMap;
import net.ramso.tools.ConfigurationException;
import net.ramso.tools.FileTools;
import net.ramso.tools.LogManager;

public class GenDoc {
	public static void main(String[] args) {
		Config.start();
		List<String> files;
		try {
			files = Config.getParmeters(args, 1, 1);
			if (files == null) {
				System.exit(0);
			}
			final GenDoc g = new GenDoc();
			final List<URL> urls = g.processFiles(files);
			g.processUrls(urls);
		} catch (final ConfigurationException e) {
			System.exit(1);
		} catch (IOException | URISyntaxException e) {
			LogManager.error("El parametro no es una url valida", e);
			System.out.println("El parametro no es una URL o fichero valido");
			System.exit(1);
		}
		System.exit(0);
	}

	private boolean first = true;

	protected List<URL> processFiles(List<String> list) throws IOException, URISyntaxException {
		final List<URL> urls = new ArrayList<>();
		for (final String file : list) {
			URL url = null;
			final File f = new File(file);
			if (f.isDirectory()) {
				if (first || Config.isR()) {
					urls.addAll(processFiles(FileTools.toString(f.listFiles())));
				}
			} else {
				try {
					url = new URL(file);
				} catch (final MalformedURLException e1) {
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
		
		for (final URL url : urls) {
			LogManager.info("Procesando url " + url.toExternalForm());
			
		}
		if (Config.isOne()) {
			String id = Config.getId();
			if ((id == null) || id.isEmpty()) {
				id = "XMLDocInDita";
			}
			String title = Config.getTitle();
			if ((title == null) || title.isEmpty()) {
				title = "Documentación XML";
			}
			String description = Config.getDescription();
			if ((description == null) || description.isEmpty()) {
				description = "";
			}
			final CreateBookMap cb = new CreateBookMap(id, title, description);
//			cb.create(parts);
		}
	}
}
