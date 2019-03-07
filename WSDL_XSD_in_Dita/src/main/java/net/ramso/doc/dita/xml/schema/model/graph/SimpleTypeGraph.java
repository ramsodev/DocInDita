package net.ramso.doc.dita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;

import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.xml.schema.model.SimpleTypeModel;
import net.ramso.tools.graph.GraphTools;

public class SimpleTypeGraph extends AbstractXmlGraph {

	SimpleTypeModel simpleType;

	public SimpleTypeGraph(SimpleTypeModel simpleType) {
		super();
		this.simpleType = simpleType;
		SUFFIX = DitaConstants.SUFFIX_SIMPLETYPE;
		setFileName(simpleType.getName());
	}

	public SimpleTypeGraph(SimpleTypeModel simpleType, mxGraph graph) {
		this(simpleType);
		setGraph(graph);
	}

	@Override
	public String generate() {
		setGraph(new mxGraph());
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);
		mxCell parent = (mxCell) getGraph().getDefaultParent();
		mxCell simpleTypeCell = createSimpleType(parent);
		mxCell type = null;
		if (simpleType.getDataType() != null) {
			type = createType(parent, simpleType.getDataType());
		}
		getGraph().insertEdge(parent, "", "", simpleTypeCell, type, GraphTools.getExtendEdgeStyle());
		getGraph().addCell(simpleTypeCell);
		getGraph().addCell(type);
		process(getGraph());
		return getFileName();
	}

	public mxCell createSimpleType(mxCell parent) {
		return createSimpleType(parent, simpleType.getName());
	}

	public mxCell createSimpleType(mxCell parent, String name) {
		return createSimpleType(parent, name, 0, 0);
	}

	public mxCell createSimpleType(mxCell parent, String name, int x, int y) {
		Rectangle2D base = GraphTools.getTextSize(name);
		int altura = (int) (base.getHeight() + (base.getHeight() / 2));
		int anchura = (int) ((base.getWidth() + (base.getWidth() * 25) / 100) + altura);
		return createSimpleType(parent, name, x, y, anchura, altura);
	}

	public mxCell createSimpleType(mxCell parent, int x, int y, int width, int height) {
		return createSimpleType(parent, simpleType.getName(), x, y, width, height);
	}

	public mxCell createSimpleType(mxCell parent, String name, int x, int y, int width, int height) {
		mxCell cell = (mxCell) getGraph().createVertex(parent, name + DitaConstants.SUFFIX_SIMPLETYPE, "", x, y, width,
				height, GraphTools.getStyle(false, true));
		String color = "BLUE";
		if (name.startsWith("(")) {
			color = "LIGHTGRAY";
		}
		Object titulo = getGraph().insertVertex(cell, "Title" + name + DitaConstants.SUFFIX_SIMPLETYPE, name, 0, 0, width,
				height, GraphTools.getStyle(true, true, color, height));

		insertIcon((mxCell) titulo, DitaConstants.SUFFIX_SIMPLETYPE.toLowerCase(), height);
		return cell;
	}

	@Override
	protected void morphGraph(mxGraph graph, mxGraphComponent graphComponent) {
		mxStackLayout layout = new mxStackLayout(graph, false, 50);
		layout.execute(graph.getDefaultParent());
	}

}
