package net.ramso.tools.graph;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxStencilShape;

import net.ramso.tools.BundleManager;
import net.ramso.tools.FileTools;
import net.ramso.tools.LogManager;

public class GraphConfig {
	private GraphConfig() {
		super();
	}

	public static void loadIcons() {
		try {
			List<URL> files = FileTools.getResourcesInFolder(GraphConstants.ICONS_FOLDER);
			for (URL file : files) {
				LogManager.debug(file.getFile());
			}
		} catch (IOException e) {
			LogManager.error(BundleManager.getString("commons.icons.error"), e);
		}
	}

	public static void loadShapes() {
		List<URL> files;
		try {
			files = FileTools.getResourcesInFolder(GraphConstants.SHAPES_FOLDER);
			for (URL file : files) {
				Document doc;
				doc = FileTools.parseXML(file.openStream());
				mxGraphics2DCanvas.putShape(file.getFile().substring(0, file.getFile().indexOf('.') - 1).toLowerCase(),
						new mxStencilShape(doc));
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LogManager.warn(BundleManager.getString("commons.shapes.error"), e);
		}

	}

}
