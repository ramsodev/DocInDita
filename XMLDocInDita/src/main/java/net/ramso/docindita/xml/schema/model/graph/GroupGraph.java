package net.ramso.docindita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

import net.ramso.docindita.xml.Config;
import net.ramso.docindita.xml.DitaConstants;
import net.ramso.docindita.xml.schema.model.AttributeGroupModel;
import net.ramso.docindita.xml.schema.model.AttributeModel;
import net.ramso.docindita.xml.schema.model.ElementModel;
import net.ramso.docindita.xml.schema.model.GroupModel;
import net.ramso.docindita.xml.schema.model.IComplexContentModel;
import net.ramso.tools.graph.GraphConstants;
import net.ramso.tools.graph.GraphTools;

public class GroupGraph extends AbstractXmlGraph {

	private final GroupModel group;
	private int iconsColumn = 0;
	private List<mxCell> cellTypes;
	private int contentPosition = 0;
	private mxCell typeGroup;
	private boolean addType = false;
	private int maxWidth;

	public GroupGraph(GroupModel group) {
		super();
		this.group = group.getModel();
		SUFFIX = DitaConstants.SUFFIX_GROUP;
		setFileName(group.getName());
	}

	public GroupGraph(GroupModel group, mxGraph graph) {
		this(group);
		setGraph(graph);
	}

	protected void addCellType(mxCell cell) {
		if (getCellTypes() == null) {
			cellTypes = new ArrayList<>();
		}
		typeGroup.insert(cell);
		getCellTypes().add(cell);
	}

	private void addIconColumn() {
		iconsColumn++;
	}

	private void apppendContent(mxCell parent, mxCell iconParent, ArrayList<IComplexContentModel> elements,
			int[] widths, int height) {
		final int x = height + (height / 2);
		for (final IComplexContentModel element : elements) {
			if (element.isElement()) {
				if (element instanceof ElementModel) {
					final ElementModel ele = (ElementModel) element;
					final ElementGraph eg = new ElementGraph(ele, getGraph());
					final mxCell cellLine = eg.createElementLine(parent, x, getContentPosition(), widths[0], height,
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
				} else if (element instanceof GroupModel) {
					final GroupModel ele = (GroupModel) element;
					String name = ele.getName();
					if (ele.getRef() != null) {
						name = "(" + ele.getRef().getLocalPart() + ")";
					}
					final GroupGraph eg = new GroupGraph(ele, getGraph());
					final mxCell cellLine = eg.createGroupCell(parent, name, 0, getContentPosition(),
							(widths[0] + widths[1] + 100) - 6, height, widths);
					if (iconParent != null) {
						getGraph().insertEdge(getGraph().getDefaultParent(), "", "", iconParent, cellLine,
								GraphTools.getOrtogonalEdgeStyle());
					}

					final mxGeometry g = cellLine.getGeometry();
					contentPosition += g.getHeight();
					g.setX(x);
					g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
					g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
					cellLine.setGeometry(g);
					parent.insert(cellLine);
				}
			} else {
				mxCell iCell = null;
				int move = 0;
				if (iconParent != null) {
					move = ((int) iconParent.getGeometry().getX()) + height + (height / 2);
				}
				iCell = insertIcon(parent, element.getContentType().toLowerCase(), height, move);
				if (iconParent != null) {
					getGraph().insertEdge(getGraph().getDefaultParent(), "", "", iconParent, iCell,
							GraphTools.getOrtogonalEdgeStyle());
				}
				apppendContent(parent, iCell, element.getElements(), widths, height);
			}
		}
	}

	public mxCell createGroupCell(mxCell parent) {
		return createGroupCell(parent, group.getName());
	}

	public mxCell createGroupCell(mxCell parent, int x, int y, int width, int height) {
		final int[] sizes = getSizes();
		return createGroupCell(parent, group.getName(), x, y, width, height, sizes);
	}

	public mxCell createGroupCell(mxCell parent, int x, int y, int width, int height, int[] sizes) {
		return createGroupCell(parent, group.getName(), x, y, width, height, sizes);
	}

	public mxCell createGroupCell(mxCell parent, String name) {
		return createGroupCell(parent, name, 0, 0);
	}

	public mxCell createGroupCell(mxCell parent, String name, int x, int y) {
		final Rectangle2D base = GraphTools.getTextSize(name);
		final int height = (int) (base.getHeight() + (base.getHeight() / 2));
		final int[] sizes = getSizes();
		final int width = sizes[0] + 100 + sizes[1] + (height + (height / 2));
		return createGroupCell(parent, name, x, y, width, height, sizes);
	}

	public mxCell createGroupCell(mxCell parent, String name, int x, int y, int width, int height, int[] sizes) {
		String color = "BLUE";
		if (!isAddType()) {
			color = "LIGHTGRAY";
			setMaxWidth(width);
		}
		final mxCell cell = (mxCell) getGraph().createVertex(parent, name + DitaConstants.SUFFIX_GROUP, "", x, y, width,
				height, GraphTools.getStyle(false, true, color));
		final mxCell titulo = (mxCell) getGraph().insertVertex(cell, "Title" + name + DitaConstants.SUFFIX_GROUP, name,
				0, 0, width, height, GraphTools.getStyle(true, true, color, height));
		super.insertIcon(titulo, DitaConstants.SUFFIX_GROUP.toLowerCase(), height);
		y = height;
		width -= 6;
		if (group.getElements().size() > 0) {
			final mxCell subCell = (mxCell) getGraph().insertVertex(cell, group.getName() + DitaConstants.SUFFIX_GROUP,
					"", x, y, width, height, GraphTools.getStyle(false, true));
			contentPosition = 0;
			if (isAddType()) {
				typeGroup = (mxCell) getGraph().createVertex(parent,
						GraphConstants.EXCLUDE_PREFIX_GROUP + DitaConstants.SUFFIX_TYPE, "", 100, 100, 300, 0,
						mxConstants.STYLE_AUTOSIZE + "=1;" + mxConstants.STYLE_RESIZABLE + "=1;"
								+ mxConstants.STYLE_STROKE_OPACITY + "=0;" + mxConstants.STYLE_FILL_OPACITY + "=0;");
			}
			apppendContent(subCell, null, group.getElements(), sizes, height);
			width = (int) resize(subCell, sizes);
			subCell.getGeometry().setWidth(width);
			cell.getGeometry().setHeight(height + subCell.getGeometry().getHeight());
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
		final mxCell complexTypeCell = createGroupCell(parent);
		getGraph().addCell(complexTypeCell);
		if (isAddType()) {
			getGraph().addCell(typeGroup);
		}
		process(getGraph(), Config.getOutputDir());
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

	protected int getIconsColumn() {
		return iconsColumn;
	}

	/**
	 * @return the maxWidth
	 */
	protected int getMaxWidth() {
		return maxWidth;
	}

	private int[] getSize(ElementModel element) {
		final int[] sizes = { 0, 0 };
		final int iconSize = (int) GraphTools.getTextSize(element.getName()).getHeight();
		sizes[0] = (int) GraphTools.getTextSize(element.getName()).getWidth() + iconSize + (iconSize / 2);

		String value = "";
		if (element.getType() != null) {
			value = element.getType().getLocalPart();
		} else if (element.getSimpleType() != null) {
			value = element.getSimpleType().getName();
			if ((value == null) || value.isEmpty()) {
				value = "(" + element.getName() + DitaConstants.SUFFIX_SIMPLETYPE + ")";
			} else if (element.getComplexType() != null) {
				value = element.getComplexType().getName();
				if ((value == null) || value.isEmpty()) {
					value = "(" + element.getName() + DitaConstants.SUFFIX_COMPLEXTYPE + ")";
				}
			}
		}
		sizes[1] = (int) GraphTools.getTextSize(value).getWidth() + iconSize + (iconSize / 2);
		return sizes;
	}

	private int[] getSizes() {
		final int[] sizes = { 0, 0 };
		final int[] tempSizes = getSizes(group.getElements());
		if (tempSizes[0] > sizes[0]) {
			sizes[0] = tempSizes[0];
		}
		if (tempSizes[1] > sizes[1]) {
			sizes[1] = tempSizes[1];
		}
		return sizes;
	}

	private int[] getSizes(ArrayList<IComplexContentModel> elements) {
		final int[] sizes = { 0, 0 };
		if (elements != null) {
			for (final IComplexContentModel element : elements) {
				int[] tempSizes = { 0, 0 };
				if (element instanceof AttributeGroupModel) {
					tempSizes = getSizes((AttributeGroupModel) element);
				} else if (!(element instanceof ElementModel)) {
					addIconColumn();
					tempSizes = getSizes(element.getElements());
				} else {
					tempSizes = getSize((ElementModel) element);
				}
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

	public int[] getSizes(AttributeGroupModel attributeGroup) {
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

	private mxCell inserType(ElementModel element) {
		mxCell type = null;
		if (isAddType()) {
			final mxCell parent = typeGroup;
			final int x = 0;
			int y = (int) typeGroup.getGeometry().getHeight();
			y += 21;

			if (element.getType() != null) {
				type = createType(parent, element.getType().getLocalPart(), x, y);
			} else if (element.getSimpleType() != null) {
				String value = element.getSimpleType().getName();
				if ((value == null) || value.isEmpty()) {
					value = "(" + element.getName() + DitaConstants.SUFFIX_SIMPLETYPE + ")";
				}
				type = new SimpleTypeGraph(element.getSimpleType(), getGraph()).createSimpleType(parent, value, x, y);

			} else if (element.getComplexType() != null) {
				String value = element.getComplexType().getName();
				if ((value == null) || value.isEmpty()) {
					value = "(" + element.getName() + DitaConstants.SUFFIX_COMPLEXTYPE + ")";
				}
				type = new ComplexTypeGraph(element.getComplexType(), getGraph()).createComplexTypeCell(parent, value,
						x, y);
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
		final List<Integer> icons = new ArrayList<>();
		double heigth = 0;
		double width = 0;
		double iWidth = 0;
		for (int i = 0; i < cell.getChildCount(); i++) {
			final mxCell child = (mxCell) cell.getChildAt(i);
			if (child.getId().startsWith(GraphConstants.EXCLUDE_PREFIX_ICON)) {
				if (icons.size() == 0) {
					iWidth = child.getGeometry().getWidth();
				}
				icons.add(i);
			}
		}
		double x = (iWidth * icons.size()) + ((iWidth) * (icons.size() + 1));
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
				heigth += child.getGeometry().getHeight();
			}
		}
		cell.getGeometry().setWidth(width);
		cell.getGeometry().setHeight(heigth);
		x = cell.getGeometry().getCenterX();
		final double y = (heigth / 2) - (iWidth / 2);
		int j = 0;
		for (final Integer i : icons) {
			final mxCell child = (mxCell) cell.getChildAt(i);
			if (j == 0) {

				x = (iWidth / 3);
				child.getGeometry().setY(y);
				child.getGeometry().setX(x);
				x += (iWidth + (iWidth / 2));
			} else {
				child.getGeometry().setX(x);
				x += (iWidth + (iWidth / 2));
			}
			j++;
		}

		return width;

	}

	private void setMaxWidth(int width) {
		maxWidth = width;

	}

}
