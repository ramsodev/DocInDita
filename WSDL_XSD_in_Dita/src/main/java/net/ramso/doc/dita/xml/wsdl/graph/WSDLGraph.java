package net.ramso.doc.dita.xml.wsdl.graph;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.predic8.wsdl.Service;

import net.ramso.doc.Config;
import net.ramso.doc.dita.tools.Constants;
import net.ramso.tools.graph.GraphConstants;

public class WSDLGraph {

	private Service service;
	private String fileName;

	public WSDLGraph(Service service) {
		this.service = service;
		setFileName(service.getName());
	}

	public String generate() {
		final mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();
		Object ports = graph.insertVertex(parent, service.getName()+Constants.SUFFIX_PORT, service.getName()+Constants.SUFFIX_PORT,  100, 100, 80, 30);
		
		process(graph);
		return getFileName();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void morphGraph(mxGraph graph, mxGraphComponent graphComponent) {
		// define layout
		mxIGraphLayout layout = new mxFastOrganicLayout(graph);

		// layout using morphing
		graph.getModel().beginUpdate();
		try {
			layout.execute(graph.getDefaultParent());
		} finally {
			mxMorphing morph = new mxMorphing(graphComponent, 20, 1.5, 20);

			morph.addListener(mxEvent.DONE, new mxIEventListener() {

				@Override
				public void invoke(Object arg0, mxEventObject arg1) {
					graph.getModel().endUpdate();
					// fitViewport();
				}

			});

			morph.startAnimation();
		}

	}

	protected void export(mxGraph graph) throws IOException {
		String filename = Config.getOutputDir() + File.separator + getFileName();
		mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer.drawCells(graph, null, 1, null, new CanvasFactory() {
			public mxICanvas createCanvas(int width, int height) {
				mxSvgCanvas canvas = new mxSvgCanvas(mxDomUtils.createSvgDocument(width, height));
				canvas.setEmbedded(true);
				return canvas;
			}
		});
		mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()), filename);
	}

	protected void setFileName(String file_name) {
		this.fileName = (file_name + Constants.SUFFIX_SERVICE + "." + GraphConstants.SVG_EXTENSION).replaceAll("\\s+",
				"_");
	}

	public String getFileName() {
		return fileName;
	}
}
