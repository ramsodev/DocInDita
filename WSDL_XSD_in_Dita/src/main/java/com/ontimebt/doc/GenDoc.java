package com.ontimebt.doc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import net.ramso.doc.dita.tools.Constants;
import net.ramso.doc.dita.tools.Tools;
import net.ramso.doc.dita.xml.schema.GenerateSchema;
import net.ramso.doc.dita.xml.wsdl.GenerateWsdl;
import net.ramso.tools.ConfigurationException;
import net.ramso.tools.LogManager;

public class GenDoc {

	public static void main(String[] args) {
		Config.start();
		List<String> files;
		try {
			files = Config.getParmeters(args, 1, 1);
			if (files != null && files.size() > 0) {
				URL url = null;
				try {
					 url = new URL(files.get(0));
				} catch (MalformedURLException e1) {
					File f = new File(files.get(0));
					if(!f.exists() || !f.isFile()) {
						LogManager.error("El parametro no es una fichero existente",null);
						System.out.println("El parametro no corresponde a un fichero valido");
						System.exit(1);
					}else {
						url = f.toURI().toURL();
					}
				}
				switch (Tools.getFileType(url)) {
				case Constants.WSDL:
					GenerateWsdl gen = new GenerateWsdl();
					gen.generateWSDL(url);
					break;
				case Constants.XSD:
					GenerateSchema xsd = new GenerateSchema();
					xsd.generateSchema(url);
					break;
				default:
					break;
				}
			}
		} catch (ConfigurationException e) {
			System.exit(1);
		} catch (IOException | URISyntaxException e) {
			LogManager.error("El parametro no es una url valida", e);
			System.out.println("El parametro no es una URL o fichero valido");
			System.exit(1);
		}
		System.exit(0);
	}

}
