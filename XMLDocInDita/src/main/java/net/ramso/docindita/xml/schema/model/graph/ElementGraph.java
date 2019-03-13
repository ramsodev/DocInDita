package net.ramso.docindita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;

import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.xml.Config;
import net.ramso.docindita.xml.schema.model.ElementModel;
import net.ramso.tools.graph.GraphConstants;
import net.ramso.tools.graph.GraphTools;

public class ElementGraph extends AbstractXmlGraph {

	private final ElementModel element;

	public ElementGraph(ElementModel element) {
		this.element = element;
		suffix = DitaConstants.SUFFIX_ELEMENT;
		setFileName(element.getName());
	}

	public ElementGraph(ElementModel ele, mxGraph graph) {
		this(ele);
		setGraph(graph);
	}

	public mxCell createElement(mxCell parent) {
		return createElement(parent, element.getName());
	}

	public mxCell createElement(mxCell parent, int x, int y, int width, int height) {
		return createElement(parent, element.getName(), x, y, width, height);
	}

	public mxCell createElement(mxCell parent, String name) {
		final Rectangle2D base = GraphTools.getTextSize(element.getName());
		final int altura = (int) (base.getHeight() + (base.getHeight() / 2));
		final int anchura = (int) ((base.getWidth() + ((base.getWidth() * 25) / 100)) + altura);
		return createElement(parent, name, 0, 0, anchura, altura);
	}

	public mxCell createElement(mxCell parent, String name, int x, int y, int width, int height) {
		final mxCell cell = (mxCell) getGraph().createVertex(parent, name + DitaConstants.SUFFIX_ELEMENT, "", x, y,
				width, height, GraphTools.getStyle(false, true));
		final Object titulo = getGraph().insertVertex(cell, "Title" + name + DitaConstants.SUFFIX_ELEMENT, name, 0, 0,
				width, height, GraphTools.getStyle(true, true, "BLUE", height));
		insertIcon((mxCell) titulo, DitaConstants.SUFFIX_ELEMENT.toLowerCase(), height);
		return cell;
	}

	public mxCell createElementLine(mxCell parent, int x, int y, int width, int height, int widthType) {
		final mxCell cell = (mxCell) getGraph().insertVertex(parent,
				GraphConstants.EXCLUDE_PREFIX_GROUP + parent.getId() + element.getName() + DitaConstants.SUFFIX_ELEMENT,
				"", x, y, ((double) width + (100 - 6) + widthType), height, GraphTools.getStyle(false, true));

		Object titulo = getGraph().insertVertex(cell, cell.getId() + "Name", element.getName(), 0, 0, width, height,
				GraphTools.getStyle(false, false, height));
		insertIcon((mxCell) titulo, DitaConstants.SUFFIX_ELEMENT.toLowerCase(), height);
		final int anchura = 100;
		String value = "";
		if (element.getMinOccurs() >= 0) {
			value += "[" + element.getMinOccurs();
		}
		if (!element.getMaxOccurs().isEmpty()) {
			value += ".. ";
			if (element.getMaxOccurs().equalsIgnoreCase(DitaConstants.UNBOUNDED)) {
				value += "*";
			} else {
				value += element.getMaxOccurs();
			}
			value += "]";
		}
		getGraph().insertVertex(cell, cell.getId() + "Size", value, width, 0, anchura, height,
				GraphTools.getStyle(false));
		value = "";
		String icon = DitaConstants.SUFFIX_TYPE.toLowerCase();
		if (element.getType() != null) {
			value = element.getType().getLocalPart();
			icon = DitaConstants.SUFFIX_TYPE.toLowerCase();
		} else if (element.getSimpleType() != null) {
			icon = DitaConstants.SUFFIX_SIMPLETYPE.toLowerCase();
			value = element.getSimpleType().getName();
			if ((value == null) || value.isEmpty()) {
				value = element.getName() + DitaConstants.SUFFIX_SIMPLETYPE;
			}
		} else if (element.getComplexType() != null) {
			icon = DitaConstants.SUFFIX_COMPLEXTYPE.toLowerCase();
			value = element.getComplexType().getName();
			if ((value == null) || value.isEmpty()) {
				value = element.getName() + DitaConstants.SUFFIX_COMPLEXTYPE;
			}
		}
		titulo = getGraph().insertVertex(cell, cell.getId() + value, value, ((double) width + anchura), 0, widthType,
				height, GraphTools.getStyle(false, false, height));
		insertIcon((mxCell) titulo, icon, height);
		return cell;
	}

	@Override
	public String generate() {
		if (getGraph() == null) {
			setGraph(new mxGraph());
		}
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);

		final mxCell parent = (mxCell) getGraph().getDefaultParent();
		final mxCell elementCell = createElement(parent);
		mxCell type = null;

		if (element.getType() != null) {
			type = createType(parent, element.getType().getLocalPart());
		} else if (element.getSimpleType() != null) {
			String value = element.getSimpleType().getName();
			if ((value == null) || value.isEmpty()) {
				value = "(" + element.getName() + DitaConstants.SUFFIX_SIMPLETYPE + ")";
			}
			type = new SimpleTypeGraph(element.getSimpleType(), getGraph()).createSimpleType(parent, value);
		} else if (element.getComplexType() != null) {
			String value = element.getComplexType().getName();
			if ((value == null) || value.isEmpty()) {
				value = "(" + element.getName() + DitaConstants.SUFFIX_COMPLEXTYPE + ")";
			}
			type = new ComplexTypeGraph(element.getComplexType(), getGraph()).createComplexTypeCell(parent, value);
		}

		getGraph().insertEdge(parent, "", "", elementCell, type, GraphTools.getOrtogonalEdgeStyle(true));
		getGraph().addCell(elementCell);
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
