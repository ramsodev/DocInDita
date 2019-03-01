package net.ramso.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FileTools {

	public static InputStream getStream(String name) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	}

	public static String getPath(String name) {
		return Thread.currentThread().getContextClassLoader().getResource(name).getPath();
	}

	public static void copy(String origen, String destino) throws FileException {

		FileInputStream _FOri;
		FileOutputStream _FDes = null;

		int c;
		try {
			_FDes = new FileOutputStream(destino);
		} catch (FileNotFoundException e1) {
			File f = new File(destino);
			f.getParentFile().mkdirs();

			try {
				f.createNewFile();
			} catch (IOException e) {
				throw new FileException("Error al crear archivo " + f.getAbsolutePath(), e);
			}
		}
		try {
			_FOri = new FileInputStream(origen);
			_FDes = new FileOutputStream(destino);
			while ((c = _FOri.read()) != -1) {
				_FDes.write(c);
			}
			_FOri.close();
			_FDes.close();
		} catch (FileNotFoundException e) {
			throw new FileException("Error Fichero no encontrado " + origen, e);
		} catch (IOException e) {
			throw new FileException("Error Entrada/Salida al copiar" + origen + " en " + destino, e);
		}
	}

	public static void backup(String origen, String pathOrigen, String pathDestino) throws FileException {
		String destino = pathDestino;
		if (!destino.endsWith(File.separator)) {
			destino += File.separator;
		}
		String extension = origen.substring(origen.indexOf("."));
		destino += origen.substring(0, origen.indexOf("."));
		destino += "-";
		destino += new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
		destino += extension;
		origen = pathOrigen + File.separator + origen;
		copy(origen, destino);
	}

	public static Document parseXML(String file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(file);
	}

	public static Document parseXML(URL url)
			throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
		return parseXML(url.toURI().toString());
	}

	public static Document parseXML(File file) throws ParserConfigurationException, SAXException, IOException {
		return parseXML(file.getAbsolutePath());
	}
}
