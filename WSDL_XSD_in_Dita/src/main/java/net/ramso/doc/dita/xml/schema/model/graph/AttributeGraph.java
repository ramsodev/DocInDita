package net.ramso.doc.dita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;

import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import net.ramso.doc.dita.tools.Constants;
import net.ramso.doc.dita.xml.schema.model.AttributeModel;
import net.ramso.tools.graph.GraphConstants;
import net.ramso.tools.graph.GraphTools;

public class AttributeGraph extends AbstractXmlGraph {

	AttributeModel attribute;

	public AttributeGraph(AttributeModel attribute) {
		super();
		this.attribute = attribute;
		SUFFIX = Constants.SUFFIX_ATTRIBUTE;
		setFileName(attribute.getName());
	}

	public AttributeGraph(AttributeModel attribute, mxGraph graph) {
		this(attribute);
		setGraph(graph);
	}

	@Override
	public String generate() {
		setGraph(new mxGraph());
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);

		mxCell parent = (mxCell) getGraph().getDefaultParent();
		mxCell simpleTypeCell = createAttribute(parent);
		mxCell type = null;

		if (attribute.getSimpleType() != null) {
			String value = attribute.getSimpleType().getName();
			if (value == null || value.isEmpty()) {
				value = "(" + attribute.getName() + Constants.SUFFIX_SIMPLETYPE + ")";
			}
			type = new SimpleTypeGraph(attribute.getSimpleType(), getGraph()).createSimpleType(parent, value);
		} else if (attribute.getType() != null) {
			type = createType(parent, attribute.getType().getLocalPart());
		}

		getGraph().insertEdge(parent, "", "", simpleTypeCell, type, mxConstants.STYLE_EDGE + "="
				+ mxConstants.EDGESTYLE_ELBOW + ";" + mxConstants.STYLE_ENDARROW + "=" + mxConstants.ARROW_OPEN);
		getGraph().addCell(simpleTypeCell);
		getGraph().addCell(type);
		process(getGraph());
		return getFileName();
	}

	public mxCell createAttribute(mxCell parent) {
		return createAttribute(parent, attribute.getName());
	}

	public mxCell createAttribute(mxCell parent, String name) {
		return createAttribute(parent, name, 0, 0);
	}

	public mxCell createAttribute(mxCell parent, String name, int x, int y) {
		Rectangle2D base = GraphTools.getTextSize(name);
		int altura = (int) (base.getHeight() + (base.getHeight() / 2));
		int anchura = (int) ((base.getWidth() + (base.getWidth() * 25) / 100) + altura);
		return createAttribute(parent, name, x, y, anchura, altura);
	}

	public mxCell createAttribute(mxCell parent, int x, int y, int width, int height) {
		return createAttribute(parent, attribute.getName(), x, y, width, height);
	}

	public mxCell createAttribute(mxCell parent, String name, int x, int y, int width, int height) {
		mxCell cell = (mxCell) getGraph().createVertex(parent, name + Constants.SUFFIX_ATTRIBUTE, "", x, y, width,
				height, GraphTools.getStyle(false, true));
		String color = "BLUE";
		if (name.startsWith("(")) {
			color = "LIGHTGRAY";
		}
		Object titulo = getGraph().insertVertex(cell, "Title" + name + Constants.SUFFIX_ATTRIBUTE, name, 0, 0, width,
				height, GraphTools.getStyle(true, true, color, height));

		insertIcon((mxCell) titulo, Constants.SUFFIX_ATTRIBUTE.toLowerCase(), height);
		return cell;
	}

	public mxCell createAttributeLine(mxCell parent, int x, int y, int width, int height, int widthType) {
		mxCell cell = (mxCell) getGraph().insertVertex(parent,
				GraphConstants.EXCLUDE_PREFIX_GROUP + parent.getId() + attribute.getName() + Constants.SUFFIX_ATTRIBUTE,
				"", x, y, width + 100-6 + widthType, height, GraphTools.getStyle(false, true));

		Object titulo = getGraph().insertVertex(cell, cell.getId() + "Name", attribute.getName(), 0, 0, width, height,
				GraphTools.getStyle(false, false, height));
		insertIcon((mxCell) titulo, Constants.SUFFIX_ATTRIBUTE.toLowerCase(), height);
		int anchura = 100-6;
		String value = "";		
		getGraph().insertVertex(cell, cell.getId() + "Size", value, width, 0, anchura, height,
				GraphTools.getStyle(false));
		value = "";
		String icon = Constants.SUFFIX_TYPE.toLowerCase();
		if (attribute.getSimpleType() != null) {
			icon = Constants.SUFFIX_SIMPLETYPE.toLowerCase();
			value = attribute.getSimpleType().getName();
			if (value == null || value.isEmpty()) {
				value = attribute.getName() + Constants.SUFFIX_SIMPLETYPE;
			}
		} else if (attribute.getType() != null) {
			value = attribute.getType().getLocalPart();
			icon = Constants.SUFFIX_TYPE.toLowerCase();
		}
		titulo = getGraph().insertVertex(cell, cell.getId() + value, value, width + anchura, 0, widthType, height,
				GraphTools.getStyle(false, false, height));
		insertIcon((mxCell) titulo, icon, height);
		return cell;
	}

	@Override
	protected void morphGraph(mxGraph graph, mxGraphComponent graphComponent) {
		mxStackLayout layout = new mxStackLayout(graph, false, 50);
		layout.execute(graph.getDefaultParent());
	}

}
