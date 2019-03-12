package net.ramso.doc.dita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.doc.dita.xml.schema.model.AttributeGroupModel;
import net.ramso.doc.dita.xml.schema.model.AttributeModel;
import net.ramso.tools.graph.GraphConstants;
import net.ramso.tools.graph.GraphTools;

public class AttributeGroupGraph extends AbstractXmlGraph {

	private final AttributeGroupModel attributeGroup;
	private List<mxCell> cellTypes;
	private int contentPosition = 0;
	private mxCell typeGroup;
	private boolean addType = false;
	private int maxWidth = 0;

	public AttributeGroupGraph(AttributeGroupModel attributeGroup) {
		super();
		this.attributeGroup = attributeGroup;
		SUFFIX = DitaConstants.SUFFIX_ATTRIBUTEGROUP;
		setFileName(attributeGroup.getName());
	}

	public AttributeGroupGraph(AttributeGroupModel attributeGroup, mxGraph graph) {
		this(attributeGroup);
		setGraph(graph);
	}

	protected void addCellType(mxCell cell) {
		if (getCellTypes() == null) {
			cellTypes = new ArrayList<>();
		}
		typeGroup.insert(cell);
		getCellTypes().add(cell);
	}

	private void apppendContent(mxCell parent, Object iconParent, List<AttributeModel> attributes, int[] widths,
			int height) {
		final int x = height + (height / 2);
		for (final AttributeModel element : attributes) {
			final AttributeModel ele = element;
			final AttributeGraph eg = new AttributeGraph(ele, getGraph());
			final mxCell cellLine = eg.createAttributeLine(parent, x, getContentPosition(), widths[0], height,
					widths[1]);
			final mxCell cellType = inserType(ele);
			if (iconParent != null) {
				getGraph().insertEdge(getGraph().getDefaultParent(), "", "", iconParent, cellLine,
						GraphTools.getOrtogonalEdgeStyle());
			}
			if (isAddType()) {
				addCellType(cellType);
				getGraph().insertEdge(getGraph().getDefaultParent(), "", "", cellLine, cellType,
						GraphTools.getOrtogonalEdgeStyle(true));
			}
			contentPosition += height;
			final mxGeometry g = cellLine.getGeometry();
			g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
			g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
			cellLine.setGeometry(g);
		}
	}

	public mxCell crateAttributeGroupCell(mxCell parent) {
		return crateAttributeGroupCell(parent, attributeGroup.getName());
	}

	public mxCell crateAttributeGroupCell(mxCell parent, int x, int y, int width, int height) {
		final int[] sizes = getSizes();
		return crateAttributeGroupCell(parent, attributeGroup.getName(), x, y, width, height, sizes);
	}

	public mxCell crateAttributeGroupCell(mxCell parent, int x, int y, int width, int height, int[] sizes) {
		return crateAttributeGroupCell(parent, attributeGroup.getName(), x, y, width, height, sizes);
	}

	public mxCell crateAttributeGroupCell(mxCell parent, String name) {
		return crateAttributeGroupCell(parent, name, 0, 0);
	}

	public mxCell crateAttributeGroupCell(mxCell parent, String name, int x, int y) {
		final Rectangle2D base = GraphTools.getTextSize(name);
		final int height = (int) (base.getHeight() + (base.getHeight() / 2));

		final int[] sizes = getSizes();
		final int width = sizes[0] + 100 + sizes[1] + (height + (height / 2));
		return crateAttributeGroupCell(parent, name, x, y, width, height, sizes);
	}

	public mxCell crateAttributeGroupCell(mxCell parent, String name, int x, int y, int width, int height,
			int[] sizes) {
		String color = "GREEN";
		if (!isAddType()) {
			color = "LIGHTGRAY";
			setMaxWidth(width);
		}
		final mxCell cell = (mxCell) getGraph().createVertex(parent, name + DitaConstants.SUFFIX_ATTRIBUTEGROUP, "", x,
				y, width, height, GraphTools.getStyle(false, true, color));
		final mxCell titulo = (mxCell) getGraph().insertVertex(cell,
				"Title" + name + DitaConstants.SUFFIX_ATTRIBUTEGROUP, name, x, y, width, height,
				GraphTools.getStyle(true, true, color, height));
		super.insertIcon(titulo, DitaConstants.SUFFIX_ATTRIBUTEGROUP.toLowerCase(), height);
		y += height;
		width -= 6;
		if (attributeGroup.getAttributes().size() > 0) {
			final mxCell subCell = (mxCell) getGraph().insertVertex(cell,
					attributeGroup.getName() + DitaConstants.SUFFIX_ATTRIBUTEGROUP, "", x, y, width, height,
					GraphTools.getStyle(false, true));
			y = 0;
			contentPosition = 0;
			typeGroup = (mxCell) getGraph().createVertex(parent,
					GraphConstants.EXCLUDE_PREFIX_GROUP + DitaConstants.SUFFIX_TYPE, "", 100, 100, 300, 0,
					mxConstants.STYLE_AUTOSIZE + "=1;" + mxConstants.STYLE_RESIZABLE + "=1;"
							+ mxConstants.STYLE_STROKE_OPACITY + "=0;" + mxConstants.STYLE_FILL_OPACITY + "=0;");

			apppendContent(subCell, null, attributeGroup.getAttributes(), sizes, height);
			width = (int) resize(subCell, sizes);
			subCell.getGeometry().setWidth(width);

		}
		cell.getGeometry().setWidth(width);
		titulo.getGeometry().setWidth(width);
		return cell;
	}

	@Override
	public String generate() {
		addType = true;
		setGraph(new mxGraph());
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);
		final mxCell parent = (mxCell) getGraph().getDefaultParent();
		final mxCell complexTypeCell = crateAttributeGroupCell(parent);
		getGraph().addCell(complexTypeCell);
		if (isAddType()) {
			getGraph().addCell(typeGroup);
		}
		process(getGraph());
		return getFileName();
	}

	protected List<mxCell> getCellTypes() {
		return cellTypes;
	}

	protected Object[] getCellTypesArray() {
		if (cellTypes == null) {
			cellTypes = new ArrayList<>();
		}
		return cellTypes.toArray();
	}

	protected int getContentPosition() {
		return contentPosition;
	}

	protected int getMaxWidth() {
		return maxWidth;
	}

	private int[] getSizes() {
		final int[] sizes = { 0, 0 };
		final int[] tempSizes = getSizes(attributeGroup.getAttributes());
		if (tempSizes[0] > sizes[0]) {
			sizes[0] = tempSizes[0];
		}
		if (tempSizes[1] > sizes[1]) {
			sizes[1] = tempSizes[1];
		}
		return sizes;
	}

	private int[] getSizes(AttributeModel atr) {
		final int[] sizes = { 0, 0 };
		final int iconSize = (int) GraphTools.getTextSize(atr.getName()).getHeight();
		sizes[0] = (int) GraphTools.getTextSize(atr.getName()).getWidth() + iconSize + (iconSize / 2);
		String value = "";
		if (atr.getType() != null) {
			value = atr.getType().getLocalPart();
		} else if (atr.getSimpleType() != null) {
			value = atr.getSimpleType().getName();
			if ((value == null) || value.isEmpty()) {
				value = "(" + atr.getName() + DitaConstants.SUFFIX_SIMPLETYPE + ")";
			}
		}
		sizes[1] = (int) GraphTools.getTextSize(value).getWidth() + iconSize + (iconSize / 2);
		return sizes;
	}

	private int[] getSizes(List<AttributeModel> attributes) {
		final int[] sizes = { 0, 0 };
		if (attributes != null) {
			for (final AttributeModel atr : attributes) {
				final int[] tempSizes = getSizes(atr);
				if (tempSizes[0] > sizes[0]) {
					sizes[0] = tempSizes[0];
				}
				if (tempSizes[1] > sizes[1]) {
					sizes[1] = tempSizes[1];
				}
			}
		}
		return sizes;
	}

	@Override
	protected mxCell insertIcon(mxCell parent, String icon, int size) {
		return insertIcon(parent, icon, size, 0);
	}

	private mxCell insertIcon(mxCell parent, String icon, int size, int move) {
		final mxCell icono = super.insertIcon(parent, icon, size);
		final mxGeometry g = icono.getGeometry();
		if (move > 0) {
			g.setX(size + move);
			icono.setId(GraphConstants.EXCLUDE_PREFIX_ICON + parent.getId() + move);
		}
		g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
		g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
		icono.setGeometry(g);
		return icono;

	}

	private mxCell inserType(AttributeModel attribute) {
		mxCell type = null;
		if (isAddType()) {
			final mxCell parent = typeGroup;
			final int x = 0;
			int y = (int) typeGroup.getGeometry().getHeight();
			y += 21;

			if (attribute.getSimpleType() != null) {
				String value = attribute.getSimpleType().getName();
				if ((value == null) || value.isEmpty()) {
					value = "(" + attribute.getName() + DitaConstants.SUFFIX_SIMPLETYPE + ")";
				}
				type = new SimpleTypeGraph(attribute.getSimpleType(), getGraph()).createSimpleType(parent, value, x, y);

			} else if (attribute.getType() != null) {
				type = createType(parent, attribute.getType().getLocalPart(), x, y);
			}
			if (type != null) {
				typeGroup.getGeometry().setHeight(y + type.getGeometry().getHeight());
			}
		}
		return type;
	}

	protected boolean isAddType() {
		return addType;
	}

	private double resize(mxCell cell, int[] sizes) {
		double width = 0;
		final double x = 0;
		if (isAddType()) {
			width = sizes[0] + 100 + sizes[1] + x;
		} else {
			width = getMaxWidth();
		}
		for (int i = 0; i < cell.getChildCount(); i++) {
			final mxCell child = (mxCell) cell.getChildAt(i);
			if (!child.getId().startsWith(GraphConstants.EXCLUDE_PREFIX_ICON)) {
				child.getGeometry().setX(x);
				child.getGeometry().setWidth(width - x);
			}
		}
		return width;

	}

	protected void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

}
