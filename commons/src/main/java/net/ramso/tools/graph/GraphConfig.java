package net.ramso.tools.graph;

import java.io.File;
import java.io.IOException;
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
			List<File> files = FileTools.getResourcesInFolder(GraphConstants.ICONS_FOLDER);
			for (File file : files) {
				LogManager.debug(file.getName());
			}
		} catch (IOException e) {
			LogManager.error(BundleManager.getString("commons.icons.error"), e);
		}
	}

	public static void loadShapes() {
		List<File> files;
		try {
			files = FileTools.getResourcesInFolder(GraphConstants.SHAPES_FOLDER);
			for (File file : files) {
				Document doc;
				doc = FileTools.parseXML(file);
				mxGraphics2DCanvas.putShape(file.getName().substring(0, file.getName().indexOf('.') - 1).toLowerCase(),
						new mxStencilShape(doc));
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LogManager.warn(BundleManager.getString("commons.shapes.error"), e);
		}

	}

}
