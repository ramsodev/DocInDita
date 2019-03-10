package net.ramso.tools.graph;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.shape.mxMarkerRegistry;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;

import net.ramso.doc.Config;
import net.ramso.tools.FileTools;
import net.ramso.tools.LogManager;

public class GraphConfig {
	public static void loadShapes() {
		File files = new File(
				Thread.currentThread().getContextClassLoader().getResource(GraphConstants.SHAPES_FOLDER).getPath());

		for (File file : files.listFiles()) {
			Document doc;
			try {
				doc = FileTools.parseXML(file);
				mxGraphics2DCanvas.putShape(file.getName().substring(0, file.getName().indexOf(".") - 1).toLowerCase(),
						new mxStencilShape(doc));
			} catch (ParserConfigurationException | SAXException | IOException e) {
				LogManager.warn("No se ha añadido el Shape " + file.getName(), e);
			}

		}

	}

	public static void loadIcons() {
		File files = new File(
				Thread.currentThread().getContextClassLoader().getResource(GraphConstants.SHAPES_FOLDER).getPath());

		for (File file : files.listFiles()) {

		}

	}

	

	

	
}
