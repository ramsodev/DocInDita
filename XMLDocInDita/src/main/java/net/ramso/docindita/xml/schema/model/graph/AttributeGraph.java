package net.ramso.docindita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;

import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.xml.Config;
import net.ramso.docindita.xml.schema.model.AttributeModel;
import net.ramso.tools.graph.GraphConstants;
import net.ramso.tools.graph.GraphTools;

public class AttributeGraph extends AbstractXmlGraph {

	AttributeModel attribute;

	public AttributeGraph(AttributeModel attribute) {
		super();
		this.attribute = attribute;
		suffix = DitaConstants.SUFFIX_ATTRIBUTE;
		setFileName(attribute.getName());
	}

	public AttributeGraph(AttributeModel attribute, mxGraph graph) {
		this(attribute);
		setGraph(graph);
	}

	public mxCell createAttribute(mxCell parent) {
		String name = this.attribute.getName();
		if (((name == null) || name.isEmpty()) && (this.attribute.getRef() != null)) {
			name = this.attribute.getRef().getLocalPart();
		} else {
			name = "NoName" + hashCode();
		}
		return createAttribute(parent, name);
	}

	public mxCell createAttribute(mxCell parent, int x, int y, int width, int height) {
		return createAttribute(parent, this.attribute.getName(), x, y, width, height);
	}

	public mxCell createAttribute(mxCell parent, String name) {
		return createAttribute(parent, name, 0, 0);
	}

	public mxCell createAttribute(mxCell parent, String name, int x, int y) {
		final Rectangle2D base = GraphTools.getTextSize(name);
		final int altura = (int) (base.getHeight() + (base.getHeight() / 2));
		final int anchura = (int) ((base.getWidth() + ((base.getWidth() * 25) / 100)) + altura);
		return createAttribute(parent, name, x, y, anchura, altura);
	}

	public mxCell createAttribute(mxCell parent, String name, int x, int y, int width, int height) {
		final mxCell cell = (mxCell) getGraph().createVertex(parent, name + DitaConstants.SUFFIX_ATTRIBUTE, "", x, y,
				width, height, GraphTools.getStyle(false, true));
		String color = "BLUE";
		if (name.startsWith("(")) {
			color = "LIGHTGRAY";
		}
		final Object titulo = getGraph().insertVertex(cell, "Title" + name + DitaConstants.SUFFIX_ATTRIBUTE, name, 0, 0,
				width, height, GraphTools.getStyle(true, true, color, height));

		insertIcon((mxCell) titulo, DitaConstants.SUFFIX_ATTRIBUTE.toLowerCase(), height);
		return cell;
	}

	public mxCell createAttributeLine(mxCell parent, int x, int y, int width, int height, int widthType) {
		final mxCell cell = (mxCell) getGraph().insertVertex(parent,
				GraphConstants.EXCLUDE_PREFIX_LINE + parent.getId() + this.attribute.getName()
						+ DitaConstants.SUFFIX_ATTRIBUTE,
				"", x, y, ((double) ((width + 100) - 6) + widthType), height, GraphTools.getStyle(false, true));

		Object titulo = getGraph().insertVertex(cell, cell.getId() + "Name", this.attribute.getName(), 0, 0, width,
				height, GraphTools.getStyle(false, false, height));
		insertIcon((mxCell) titulo, DitaConstants.SUFFIX_ATTRIBUTE.toLowerCase(), height);
		final int anchura = 100 - 6;
		String value = "";
		getGraph().insertVertex(cell, cell.getId() + "Size", value, width, 0, anchura, height,
				GraphTools.getStyle(false));
		value = "";
		String icon = DitaConstants.SUFFIX_TYPE.toLowerCase();
		if (this.attribute.getSimpleType() != null) {
			icon = DitaConstants.SUFFIX_SIMPLETYPE.toLowerCase();
			value = this.attribute.getSimpleType().getName();
			if ((value == null) || value.isEmpty()) {
				value = this.attribute.getName() + DitaConstants.SUFFIX_SIMPLETYPE;
			}
		} else if (this.attribute.getType() != null) {
			value = this.attribute.getType().getLocalPart();
			icon = DitaConstants.SUFFIX_TYPE.toLowerCase();
		}
		titulo = getGraph().insertVertex(cell, cell.getId() + value, value, ((double) width + anchura), 0, widthType,
				height, GraphTools.getStyle(false, false, height));
		insertIcon((mxCell) titulo, icon, height);
		return cell;
	}

	@Override
	public String generate() {
		setGraph(new mxGraph());
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);

		final mxCell parent = (mxCell) getGraph().getDefaultParent();
		final mxCell simpleTypeCell = createAttribute(parent);
		mxCell type = null;

		if (this.attribute.getSimpleType() != null) {
			String value = this.attribute.getSimpleType().getName();
			if ((value == null) || value.isEmpty()) {
				value = "(" + this.attribute.getName() + DitaConstants.SUFFIX_SIMPLETYPE + ")";
			}
			type = new SimpleTypeGraph(this.attribute.getSimpleType(), getGraph()).createSimpleType(parent, value);
		} else if (this.attribute.getType() != null) {
			type = createType(parent, this.attribute.getType().getLocalPart());
		}

		getGraph().insertEdge(parent, "", "", simpleTypeCell, type, GraphTools.getExtendEdgeStyle());
		getGraph().addCell(simpleTypeCell);
		getGraph().addCell(type);
		process(getGraph(), Config.getOutputDir());
		return getFileName();
	}

	@Override
	protected void morphGraph(mxGraph graph) {
		final mxStackLayout layout = new mxStackLayout(graph, false, 50);
		layout.execute(graph.getDefaultParent());
	}

}
