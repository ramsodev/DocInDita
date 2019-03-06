package net.ramso.doc.dita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

import net.ramso.doc.dita.tools.Constants;
import net.ramso.doc.dita.xml.schema.model.AttributeGroupModel;
import net.ramso.doc.dita.xml.schema.model.AttributeModel;
import net.ramso.tools.graph.GraphConstants;
import net.ramso.tools.graph.GraphTools;

public class AttributeGroupGraph extends AbstractXmlGraph {

	private AttributeGroupModel attributeGroup;
	private List<mxCell> cellTypes;
	private int contentPosition = 0;
	private mxCell typeGroup;
	private boolean addType = false;

	public AttributeGroupGraph(AttributeGroupModel attributeGroup) {
		super();
		this.attributeGroup = attributeGroup;
		SUFFIX = Constants.SUFFIX_ATTRIBUTEGROUP;
		setFileName(attributeGroup.getName());
	}

	public AttributeGroupGraph(AttributeGroupModel attributeGroup, mxGraph graph) {
		this(attributeGroup);
		setGraph(graph);
	}

	@Override
	public String generate() {
		this.addType = true;
		setGraph(new mxGraph());
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);
		mxCell parent = (mxCell) getGraph().getDefaultParent();
		mxCell complexTypeCell = crateAttributeGroupCell(parent);
		getGraph().addCell(complexTypeCell);
		if (isAddType()) {
			getGraph().addCell(typeGroup);
		}
		process(getGraph());
		return getFileName();
	}

	public mxCell crateAttributeGroupCell(mxCell parent) {
		return crateAttributeGroupCell(parent, attributeGroup.getName());
	}

	public mxCell crateAttributeGroupCell(mxCell parent, String name) {
		return crateAttributeGroupCell(parent, name, 0, 0);
	}

	public mxCell crateAttributeGroupCell(mxCell parent, String name, int x, int y) {
		Rectangle2D base = GraphTools.getTextSize(name);
		int height = (int) (base.getHeight() + (base.getHeight() / 2));

		int[] sizes = getSizes();
		int width = sizes[0] + 100 + sizes[1] + (height + (height / 2));
		return crateAttributeGroupCell(parent, name, x, y, width, height, sizes);
	}

	public mxCell crateAttributeGroupCell(mxCell parent, int x, int y, int width, int height) {
		int[] sizes = getSizes();
		return crateAttributeGroupCell(parent, attributeGroup.getName(), x, y, width, height, sizes);
	}

	public mxCell crateAttributeGroupCell(mxCell parent, int x, int y, int width, int height, int[] sizes) {
		return crateAttributeGroupCell(parent, attributeGroup.getName(), x, y, width, height, sizes);
	}

	public mxCell crateAttributeGroupCell(mxCell parent, String name, int x, int y, int width, int height,
			int[] sizes) {
		String color = "BLUE";
		if (name.startsWith("("))
			color = "LIGHTGRAY";
		mxCell cell = (mxCell) getGraph().createVertex(parent, name + Constants.SUFFIX_ATTRIBUTEGROUP, "", x, y, width,
				height, GraphTools.getStyle(false, true, "GREEN"));
		mxCell titulo = (mxCell) getGraph().insertVertex(cell, "Title" + name + Constants.SUFFIX_ATTRIBUTEGROUP, name, x,
				y, width, height, GraphTools.getStyle(true, true, color, height));
		super.insertIcon((mxCell) titulo, Constants.SUFFIX_ATTRIBUTEGROUP.toLowerCase(), height);
		y += height;
		width -= 6;
		if (attributeGroup.getAttributes().size() > 0) {
			mxCell subCell = (mxCell) getGraph().insertVertex(cell,
					attributeGroup.getName() + Constants.SUFFIX_ATTRIBUTEGROUP, "", x+3, y, width, height,
					GraphTools.getStyle(false, true));
			y = 0;
			contentPosition = 0;
			typeGroup = (mxCell) getGraph().createVertex(parent,
					GraphConstants.EXCLUDE_PREFIX_GROUP + Constants.SUFFIX_TYPE, "", 100, 100, 300, 0,
					mxConstants.STYLE_AUTOSIZE + "=1;" + mxConstants.STYLE_RESIZABLE + "=1;"
							+ mxConstants.STYLE_STROKE_OPACITY + "=0;" + mxConstants.STYLE_FILL_OPACITY + "=0;");
			
			apppendContent(subCell, null, attributeGroup.getAttributes(), sizes, height);
			width = (int) resize(subCell, sizes);

		}
		cell.getGeometry().setWidth(width);
		titulo.getGeometry().setWidth(width);
		return cell;
	}

	private double resize(mxCell cell, int[] sizes) {
		List<Integer> icons = new ArrayList<Integer>();
		double heigth = 0;
		double width = 0;
		double iWidth = 0;
		for (int i = 0; i < cell.getChildCount(); i++) {
			mxCell child = (mxCell) cell.getChildAt(i);
			if (child.getId().startsWith(GraphConstants.EXCLUDE_PREFIX_ICON)) {
				if (icons.size() == 0) {
					iWidth = child.getGeometry().getWidth();
				}
				icons.add(i);
			}
		}
		double x = (iWidth * icons.size()) + ((iWidth / 3) * icons.size()) + (iWidth / 3);
		width = sizes[0] + 100 + sizes[1] + x;
		for (int i = 0; i < cell.getChildCount(); i++) {
			mxCell child = (mxCell) cell.getChildAt(i);
			if (!child.getId().startsWith(GraphConstants.EXCLUDE_PREFIX_ICON)) {
				child.getGeometry().setX(x);
				heigth += child.getGeometry().getHeight();
			}
		}
		cell.getGeometry().setWidth(width);
		cell.getGeometry().setHeight(heigth);
		x = cell.getGeometry().getCenterX();
		double y = (heigth / 2) - (iWidth / 2);
		int j = 0;
		for (Integer i : icons) {
			mxCell child = (mxCell) cell.getChildAt(i);
			if (j == 0) {

				x = (iWidth / 3);
				child.getGeometry().setY(y);
				child.getGeometry().setX(x);
				x += (iWidth + (iWidth / 3));
			} else {
				child.getGeometry().setX(x);
				x += (iWidth + (iWidth / 3));
			}
			j++;
		}

		return width;

	}

	private void apppendContent(mxCell parent, Object iconParent, List<AttributeModel> attributes, int[] widths,
			int height) {
		int x = height + (height / 2);
		for (AttributeModel element : attributes) {
			AttributeModel ele = (AttributeModel) element;
			AttributeGraph eg = new AttributeGraph(ele, getGraph());
			mxCell cellLine = eg.createAttributeLine(parent, x, getContentPosition(), widths[0], height, widths[1]);
			mxCell cellType = inserType(ele);
			if (iconParent != null) {
				getGraph().insertEdge(getGraph().getDefaultParent(), "", "", iconParent, cellLine,
						mxConstants.STYLE_EDGE + "=" + mxConstants.EDGESTYLE_ORTHOGONAL + ";");
			}
			if (isAddType()) {
				addCellType(cellType);
				getGraph().insertEdge(getGraph().getDefaultParent(), "", "", cellLine, cellType,
						mxConstants.STYLE_EDGE + "=" + mxConstants.EDGESTYLE_ORTHOGONAL + ";");
			}
			contentPosition += height;
			mxGeometry g = cellLine.getGeometry();
			g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
			g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
			cellLine.setGeometry(g);
		}
	}

	private mxCell inserType(AttributeModel attribute) {
		mxCell type = null;
		if (isAddType()) {
			mxCell parent = typeGroup;
			int x = 0;
			int y = (int) typeGroup.getGeometry().getHeight();
			y += 21;

			if (attribute.getSimpleType() != null) {
				String value = attribute.getSimpleType().getName();
				if (value == null || value.isEmpty()) {
					value = "(" + attribute.getName() + Constants.SUFFIX_SIMPLETYPE + ")";
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

	private int[] getSizes() {
		int[] sizes = { 0, 0 };
		int[] tempSizes = getSizes(attributeGroup.getAttributes());
		if (tempSizes[0] > sizes[0]) {
			sizes[0] = tempSizes[0];
		}
		if (tempSizes[1] > sizes[1]) {
			sizes[1] = tempSizes[1];
		}
		return sizes;
	}

	private int[] getSizes(List<AttributeModel> attributes) {
		int[] sizes = { 0, 0 };
		if (attributes != null) {
			for (AttributeModel atr : attributes) {
				int[] tempSizes = getSizes(atr);
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

	private int[] getSizes(AttributeModel atr) {
		int[] sizes = { 0, 0 };
		int iconSize = (int) GraphTools.getTextSize(atr.getName()).getHeight();
		sizes[0] = (int) GraphTools.getTextSize(atr.getName()).getWidth() + iconSize + (iconSize / 2);
		String value = "";
		if (atr.getType() != null) {
			value = atr.getType().getLocalPart();
		} else if (atr.getSimpleType() != null) {
			value = atr.getSimpleType().getName();
			if (value == null || value.isEmpty()) {
				value = "(" + atr.getName() + Constants.SUFFIX_SIMPLETYPE + ")";
			}
		}
		sizes[1] = (int) GraphTools.getTextSize(value).getWidth() + iconSize + (iconSize / 2);
		return sizes;
	}

	protected List<mxCell> getCellTypes() {
		return cellTypes;
	}

	protected Object[] getCellTypesArray() {
		if (cellTypes == null)
			cellTypes = new ArrayList<mxCell>();
		return cellTypes.toArray();
	}

	protected void addCellType(mxCell cell) {
		if (getCellTypes() == null)
			cellTypes = new ArrayList<mxCell>();
		typeGroup.insert(cell);
		getCellTypes().add(cell);
	}

	@Override
	protected mxCell insertIcon(mxCell parent, String icon, int size) {
		return insertIcon(parent, icon, size, 0);
	}

	private mxCell insertIcon(mxCell parent, String icon, int size, int move) {
		mxCell icono = super.insertIcon(parent, icon, size);
		mxGeometry g = icono.getGeometry();
		if (move > 0) {
			g.setX(size + move);
			icono.setId(GraphConstants.EXCLUDE_PREFIX_ICON + parent.getId() + move);
		}
		g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
		g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
		icono.setGeometry(g);
		return icono;

	}

	protected int getContentPosition() {
		return contentPosition;
	}

	protected boolean isAddType() {
		return addType;
	}

}
