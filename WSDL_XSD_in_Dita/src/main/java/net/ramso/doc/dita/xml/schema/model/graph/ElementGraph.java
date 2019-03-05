package net.ramso.doc.dita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import net.ramso.doc.dita.tools.Constants;
import net.ramso.doc.dita.xml.schema.model.ElementModel;
import net.ramso.tools.graph.GraphConstants;
import net.ramso.tools.graph.GraphTools;

public class ElementGraph extends AbstractXmlGraph {

	private ElementModel element;

	public ElementGraph(ElementModel element) {
		this.element = element;
		SUFFIX = Constants.SUFFIX_ELEMENT;
		setFileName(element.getName());
	}

	public ElementGraph(ElementModel ele, mxGraph graph) {
		this(ele);
		setGraph(graph);
	}

	@Override
	public String generate() {
		if (getGraph() == null)
			setGraph(new mxGraph());
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);

		mxCell parent = (mxCell) getGraph().getDefaultParent();
		mxCell elementCell = createElement(parent);
		mxCell type = null;

		if (element.getType() != null) {
			type = createType(parent, element.getType().getLocalPart());
		} else if (element.getSimpleType() != null) {

			String value = element.getSimpleType().getName();
			if (value == null || value.isEmpty()) {
				value = "(" + element.getName() + Constants.SUFFIX_SIMPLETYPE + ")";
			}

			type = createType(parent, value, Constants.SUFFIX_SIMPLETYPE);
		} else if (element.getComplexType() != null) {
			String value = element.getComplexType().getName();
			if (value == null || value.isEmpty()) {
				value = "(" + element.getName() + Constants.SUFFIX_COMPLEXTYPE + ")";
			}
			type = createType(parent, value);
		}

		getGraph().insertEdge(parent, "", "", elementCell, type,
				mxConstants.STYLE_EDGE + "=" + mxConstants.EDGESTYLE_ELBOW + ";");
		getGraph().addCell(elementCell);
		getGraph().addCell(type);
		process(getGraph());
		return getFileName();
	}

	public mxCell createElement(mxCell parent) {
		return createElement(parent, element.getName());
	}

	public mxCell createElement(mxCell parent, String name) {
		Rectangle2D base = GraphTools.getTextSize(element.getName());
		int altura = (int) (base.getHeight() + (base.getHeight() / 2));
		int anchura = (int) ((base.getWidth() + (base.getWidth() * 25) / 100) + altura);
		return createElement(parent, name, 0, 0, anchura, altura);
	}

	public mxCell createElement(mxCell parent, int x, int y, int width, int height) {
		return createElement(parent, element.getName(), x, y, width, height);
	}

	public mxCell createElement(mxCell parent, String name, int x, int y, int width, int height) {
		mxCell cell = (mxCell) getGraph().createVertex(parent, name + Constants.SUFFIX_ELEMENT, "", x, y, width, height,
				GraphTools.getStyle(false, true));
		Object titulo = getGraph().insertVertex(cell, "Title" + name + Constants.SUFFIX_ELEMENT, name, 0, 0, width,
				height, GraphTools.getStyle(true, true, "BLUE", height));
		insertIcon((mxCell) titulo, Constants.SUFFIX_ELEMENT.toLowerCase(), height);
		return cell;
	}

	public mxCell createElementLine(mxCell parent, int x, int y, int width, int height, int widthType) {
		mxCell cell = (mxCell) getGraph().insertVertex(parent,
				GraphConstants.EXCLUDE_PREFIX_GROUP + parent.getId() + element.getName() + Constants.SUFFIX_ELEMENT, "",
				x, y, width + 100 + widthType, height, GraphTools.getStyle(false, true));

		Object titulo = getGraph().insertVertex(cell, cell.getId() + "Name", element.getName(), 0, 0, width, height,
				GraphTools.getStyle(false, false, height));
		insertIcon((mxCell) titulo, Constants.SUFFIX_ELEMENT.toLowerCase(), height);
		int anchura = 100;
		String value = "";
		if (element.getMinOccurs() >= 0) {
			value += "[" + element.getMinOccurs();
		}
		if (!element.getMaxOccurs().isEmpty()) {
			value += ".. ";
			if (element.getMaxOccurs().equalsIgnoreCase(Constants.UNBOUNDED)) {
				value += "*";
			} else {
				value += element.getMaxOccurs();
			}
			value += "]";
		}
		getGraph().insertVertex(cell, cell.getId() + "Size", value, width, 0, anchura, height,
				GraphTools.getStyle(false));
		value = "";
		String icon = Constants.SUFFIX_TYPE.toLowerCase();
		if (element.getType() != null) {
			value = element.getType().getLocalPart();
			icon = Constants.SUFFIX_TYPE.toLowerCase();
		} else if (element.getSimpleType() != null) {
			icon = Constants.SUFFIX_SIMPLETYPE.toLowerCase();
			value = element.getSimpleType().getName();
			if (value == null || value.isEmpty()) {
				value = element.getName() + Constants.SUFFIX_SIMPLETYPE;
			}
		} else if (element.getComplexType() != null) {
			icon = Constants.SUFFIX_COMPLEXTYPE.toLowerCase();
			value = element.getComplexType().getName();
			if (value == null || value.isEmpty()) {
				value = element.getName() + Constants.SUFFIX_COMPLEXTYPE;
			}
		}
		titulo = getGraph().insertVertex(cell, cell.getId() + value, value, width + anchura, 0, widthType, height,
				GraphTools.getStyle(false, false, height));
		insertIcon((mxCell) titulo, icon, height);
		return cell;
	}
}
