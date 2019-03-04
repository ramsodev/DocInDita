package net.ramso.doc.dita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import net.ramso.doc.dita.tools.Constants;
import net.ramso.doc.dita.xml.schema.model.SimpleTypeModel;
import net.ramso.tools.graph.GraphTools;

public class SimpleTypeGraph extends AbstractXmlGraph {

	SimpleTypeModel simpleType;

	public SimpleTypeGraph(SimpleTypeModel simpleType) {
		super();
		this.simpleType = simpleType;
		SUFFIX=Constants.SUFFIX_SIMPLETYPE;
		setFileName(simpleType.getName());
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

		getGraph().insertEdge(parent, "", "", simpleTypeCell, type, mxConstants.STYLE_EDGE + "="
				+ mxConstants.EDGESTYLE_ELBOW + ";" + mxConstants.STYLE_ENDARROW + "=" + mxConstants.ARROW_OPEN);
		getGraph().addCell(simpleTypeCell);
		getGraph().addCell(type);
		process(getGraph());
		return getFileName();
	}

	public mxCell createSimpleType(mxCell parent) {
		Rectangle2D base = GraphTools.getTextSize(simpleType.getName());
		int altura = (int) (base.getHeight() + (base.getHeight() / 2));
		int anchura = (int) ((base.getWidth() + (base.getWidth() * 25) / 100) + altura);
		return createSimpleType(parent, 0, 0, anchura, altura);
	}

	public mxCell createSimpleType(mxCell parent, int x, int y, int width, int height) {
		mxCell cell = (mxCell) getGraph().createVertex(parent, simpleType.getName() + Constants.SUFFIX_SIMPLETYPE, "",
				x, y, width, height, GraphTools.getStyle(false, true));
		Object titulo = getGraph().insertVertex(cell, "Title" + simpleType.getName() + Constants.SUFFIX_SIMPLETYPE,
				simpleType.getName(), 0, 0, width, height, GraphTools.getStyle(true, true, "BLUE", height));
		insertIcon((mxCell) titulo, Constants.SUFFIX_SIMPLETYPE.toLowerCase(), height);
		return cell;
	}

}
