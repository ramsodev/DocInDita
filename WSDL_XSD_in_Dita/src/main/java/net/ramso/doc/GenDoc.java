package net.ramso.doc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
			switch (DitaTools.getFileType(url)) {
			case DitaConstants.WSDL:
				GenerateWsdl gen = new GenerateWsdl();
				parts.add(gen.generateWSDL(url, false));
				break;
			case DitaConstants.XSD:
				GenerateSchema xsd = new GenerateSchema();
				xsd.generateSchema(url);
				break;
			default:
				break;
			}
		}
	}
}
