package net.ramso.tools.graph;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.view.mxGraph;

import net.ramso.doc.Config;
import net.ramso.doc.dita.tools.Constants;
import net.ramso.tools.FileTools;

public abstract class AbstractGraph {
	protected static String SUFFIX = "";
	private String fileName;
	private mxGraph graph;

	public abstract String generate();

	protected void insertIcon(mxCell parent, String icon, int size) {
		getGraph().insertVertex(parent, GraphConstants.EXCLUDE_PREFIX_ICON+parent.getId(), "", 0, 0, size, size,
				GraphTools.getStyleImage(true, size - 2, size - 2, icon));
	}
	
	protected void resizeCell(mxCell cell, int maxWith) {
		if (!cell.getId().startsWith(GraphConstants.EXCLUDE_PREFIX_ICON)) {
			mxGeometry g = cell.getGeometry();
			if (cell.getId().endsWith(Constants.SUFFIX_ADDRESS)) {
				maxWith -= 20;
			}
			g.setWidth(maxWith);
			int e = cell.getChildCount();
			if (!cell.getId().startsWith(GraphConstants.EXCLUDE_PREFIX_GROUP)) {
				for (int i = 0; i < e; i++) {
					if (!cell.getId().startsWith(GraphConstants.EXCLUDE_PREFIX_GROUP)) {
						resizeCell((mxCell) cell.getChildAt(i), maxWith);
					} else {

					}
				}
			}
		}
	}

	protected void process(mxGraph graph) {
		JFrame f = new JFrame();
		f.setSize(800, 800);
		f.setLocation(300, 200);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		f.getContentPane().add(graphComponent, BorderLayout.CENTER);
		f.setVisible(false);
		morphGraph(graph, graphComponent);
		try {
			export(graph);
		} catch (IOException e) {
			net.ramso.tools.LogManager.warn("Error al exportar el diagrama " + getFileName(), e);
		}
	}

	private static void morphGraph(mxGraph graph, mxGraphComponent graphComponent) {
		mxStackLayout layout = new mxStackLayout(graph, true, 50, 100, 100, 100);
		layout.execute(graph.getDefaultParent());
	}

	protected void export(mxGraph graph) throws IOException {
		String filename = Config.getOutputDir() + File.separator + getFileName();
		if (FileTools.checkPath(filename, true)) {
			mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer.drawCells(graph, null, 1, null, new CanvasFactory() {
				public mxICanvas createCanvas(int width, int height) {
					mxSvgCanvas canvas = new mxSvgCanvas(mxDomUtils.createSvgDocument(width, height));
					canvas.setEmbedded(true);
					return canvas;
				}
			});
			mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()), filename);
		}
	}

	protected void setFileName(String file_name) {
		this.fileName = (GraphConstants.IMAGE_PATH + File.separator + file_name + SUFFIX + "."
				+ GraphConstants.SVG_EXTENSION).replaceAll("\\s+", "_");
	}

	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the graph
	 */
	protected mxGraph getGraph() {
		return graph;
	}

	protected void setGraph(mxGraph graph) {
		this.graph = graph;
	}
}
