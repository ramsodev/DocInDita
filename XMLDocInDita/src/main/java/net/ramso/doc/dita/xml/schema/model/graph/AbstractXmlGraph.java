package net.ramso.doc.dita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;

import com.mxgraph.model.mxCell;

import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.tools.graph.AbstractGraph;
import net.ramso.tools.graph.GraphTools;

public abstract class AbstractXmlGraph extends AbstractGraph {
	protected mxCell createType(mxCell parent, String name) {
		return createType(parent, name, 0, 0);
	}

	protected mxCell createType(mxCell parent, String name, int x, int y) {
		return createType(parent, name, x, y, DitaConstants.SUFFIX_TYPE.toLowerCase());
	}

	protected mxCell createType(mxCell parent, String name, int x, int y, int width, int height, String icon) {
		final mxCell cell = (mxCell) getGraph().createVertex(parent, name + icon, "", x, y, width, height,
				GraphTools.getStyle(false, true));
		final Object titulo = getGraph().insertVertex(cell, "Title" + name + icon, name, 0, 0, width, height,
				GraphTools.getStyle(true, true, "LIGHTGRAY", height));
		insertIcon((mxCell) titulo, icon, height);
		return cell;
	}

	protected mxCell createType(mxCell parent, String name, int x, int y, String icon) {
		final Rectangle2D base = GraphTools.getTextSize(name);
		final int altura = (int) (base.getHeight() + (base.getHeight() / 2));
		final int anchura = (int) ((base.getWidth() + ((base.getWidth() * 25) / 100)) + altura);
		return createType(parent, name, x, y, anchura, altura, icon);
	}

	protected mxCell createType(mxCell parent, String name, String icon) {
		return createType(parent, name, 0, 0, icon);
	}

}
