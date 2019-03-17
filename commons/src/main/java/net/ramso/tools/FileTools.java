package net.ramso.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FileTools {
	private FileTools() {
		super();
	}

	public static void backup(String origen, String pathOrigen, String pathDestino) throws FileException {
		String destino = pathDestino;
		if (!destino.endsWith(File.separator)) {
			destino += File.separator;
		}
		final String extension = origen.substring(origen.indexOf('.'));
		destino += origen.substring(0, origen.indexOf('.'));
		destino += "-"; //$NON-NLS-1$
		destino += new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()); //$NON-NLS-1$
		destino += extension;
		origen = pathOrigen + File.separator + origen;
		copy(origen, destino);
	}

	public static boolean checkPath(File file, boolean mkparentDirs) {
		if (!file.exists()) {
			final File parent = file.getParentFile();
			if (!parent.exists()) {
				if (mkparentDirs) {
					parent.mkdirs();
				} else {
					return false;
				}
			} else if (!parent.isDirectory()) {
				return false;
			}
		}
		return true;
	}

	public static boolean checkPath(String filename, boolean mkparentDirs) {

		return checkPath(new File(filename), mkparentDirs);

	}

	public static void copy(String origen, String destino) throws FileException {

		FileInputStream fOri;
		FileOutputStream fDes = null;

		int c;
		try {
			fDes = new FileOutputStream(destino);
		} catch (final FileNotFoundException e1) {
			final File f = new File(destino);
			f.getParentFile().mkdirs();

			try {
				f.createNewFile();
			} catch (final IOException e) {
				throw new FileException(
						BundleManager.getString("commons.FileTools.file_create_error") + f.getAbsolutePath(), e); //$NON-NLS-1$
			}
		}
		try {
			fOri = new FileInputStream(origen);
			fDes = new FileOutputStream(destino);
			while ((c = fOri.read()) != -1) {
				fDes.write(c);
			}
			fOri.close();
			fDes.close();
		} catch (final FileNotFoundException e) {
			throw new FileException(BundleManager.getString("commons.FileTools.finle_not_found") + origen, e); //$NON-NLS-1$
		} catch (final IOException e) {
			throw new FileException(
					BundleManager.getString("commons.FileTools.file_io_error") + origen + " en " + destino, e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public static String getPath(String name) {
		return Thread.currentThread().getContextClassLoader().getResource(name).getPath();
	}

	public static InputStream getStream(String name) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	}

	public static Document parseXML(File file) throws ParserConfigurationException, SAXException, IOException {
		return parseXML(file.getAbsolutePath());
	}

	public static Document parseXML(String file) throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(file);
	}

	public static Document parseXML(URL url)
			throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
		return parseXML(url.toURI().toString());
	}

	public static List<String> toString(File[] files) {
		final List<String> strings = new ArrayList<>();
		for (final File file : files) {
			strings.add(file.getAbsolutePath());
		}
		return strings;
	}

	public static List<File> getResourcesInFolder(String folder) throws IOException {
		List<File> files = new ArrayList<>();
		Stream<Path> walk = null;
		FileSystem fileSystem = null;
		try {
			URI uri = Thread.currentThread().getContextClassLoader().getResource(folder).toURI();

			Path myPath;
			if (uri.getScheme().equals("jar")) {
				fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
				myPath = fileSystem.getPath(File.separator + folder);
			} else {
				myPath = Paths.get(uri);
			}
			walk = Files.walk(myPath, 1);
			for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
				Path i = it.next();
				URL resource = Thread.currentThread().getContextClassLoader()
						.getResource(folder + File.separator + i.getFileName());
				if (resource != null) {
					File f = new File(resource.toURI());
					if (!f.isDirectory())
						files.add(f);
				}
			}
		} catch (IOException | URISyntaxException e) {
			LogManager.warn(BundleManager.getString("commons.folder.error", folder), e);
		} finally {
			if (walk != null) {
				walk.close();
			}
			if (fileSystem != null) {
				fileSystem.close();
			}

		}
		return files;
	}
}
