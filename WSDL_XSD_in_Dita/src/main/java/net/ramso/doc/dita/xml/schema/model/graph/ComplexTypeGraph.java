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
import net.ramso.doc.dita.xml.schema.model.ComplexTypeModel;
import net.ramso.doc.dita.xml.schema.model.ElementModel;
import net.ramso.doc.dita.xml.schema.model.GroupModel;
import net.ramso.doc.dita.xml.schema.model.IComplexContentModel;
import net.ramso.doc.dita.xml.schema.model.SimpleTypeModel;
import net.ramso.doc.dita.xml.schema.model.iComponentModel;
import net.ramso.tools.graph.GraphConstants;
import net.ramso.tools.graph.GraphTools;

public class ComplexTypeGraph extends AbstractXmlGraph {

	private ComplexTypeModel complexType;
	private int iconsColumn = 0;
	private List<mxCell> cellTypes;
	private int contentPosition = 0;
	private mxCell typeGroup;
	private boolean addType = false;

	public ComplexTypeGraph(ComplexTypeModel complexType) {
		super();
		this.complexType = complexType;
		SUFFIX = DitaConstants.SUFFIX_COMPLEXTYPE;
		setFileName(complexType.getName());
	}

	public ComplexTypeGraph(ComplexTypeModel complexType, mxGraph graph) {
		this(complexType);
		setGraph(graph);
	}

	@Override
	public String generate() {
		this.addType = true;
		setGraph(new mxGraph());
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);
		mxCell parent = (mxCell) getGraph().getDefaultParent();
		mxCell complexTypeCell = createComplexTypeCell(parent);
		getGraph().addCell(complexTypeCell);
		if (isAddType()) {
			getGraph().addCell(typeGroup);
		}
		process(getGraph());
		return getFileName();
	}

	public mxCell createComplexTypeCell(mxCell parent) {
		return createComplexTypeCell(parent, complexType.getName());
	}

	public mxCell createComplexTypeCell(mxCell parent, String name) {
		return createComplexTypeCell(parent, name, 0, 0);
	}

	public mxCell createComplexTypeCell(mxCell parent, String name, int x, int y) {
		Rectangle2D base = GraphTools.getTextSize(name);
		int height = (int) (base.getHeight() + (base.getHeight() / 2));

		int[] sizes = getSizes();
		int width = sizes[0] + 100 + sizes[1] + (height + (height / 2));
		return createComplexTypeCell(parent, name, x, y, width, height, sizes);
	}

	public mxCell createComplexTypeCell(mxCell parent, int x, int y, int width, int height) {
		int[] sizes = getSizes();
		return createComplexTypeCell(parent, complexType.getName(), x, y, width, height, sizes);
	}

	public mxCell createComplexTypeCell(mxCell parent, int x, int y, int width, int height, int[] sizes) {
		return createComplexTypeCell(parent, complexType.getName(), x, y, width, height, sizes);
	}

	public mxCell createComplexTypeCell(mxCell parent, String name, int x, int y, int width, int height, int[] sizes) {
		String color = "BLUE";
		if (name.startsWith("("))
			color = "LIGHTGRAY";
		mxCell cell = (mxCell) getGraph().createVertex(parent, name + DitaConstants.SUFFIX_COMPLEXTYPE, "", x, y, width,
				height, GraphTools.getStyle(false, true));
		mxCell titulo = (mxCell) getGraph().insertVertex(cell, "Title" + name + DitaConstants.SUFFIX_COMPLEXTYPE, name,
				0, 0, width, height, GraphTools.getStyle(true, true, color, height));
		super.insertIcon((mxCell) titulo, DitaConstants.SUFFIX_COMPLEXTYPE.toLowerCase(), height);
		y += height;
		width -= 6;
		if (complexType.getElements().size() > 0 || complexType.getAttributes().size() > 0) {
			mxCell subCell = (mxCell) getGraph().insertVertex(cell,
					complexType.getName() + DitaConstants.SUFFIX_COMPLEXTYPE, "", x, y, width, height,
					GraphTools.getStyle(false, false));

			contentPosition = 0;
			typeGroup = (mxCell) getGraph().createVertex(parent,
					GraphConstants.EXCLUDE_PREFIX_GROUP + DitaConstants.SUFFIX_TYPE, "", 100, 100, 300, 0,
					mxConstants.STYLE_AUTOSIZE + "=1;" + mxConstants.STYLE_RESIZABLE + "=1;"
							+ mxConstants.STYLE_STROKE_OPACITY + "=0;" + mxConstants.STYLE_FILL_OPACITY + "=0;");
			apppendContentAttributeGroup(subCell, null, complexType.getAttributeGroups(), sizes, height);
			apppendContent(subCell, null, complexType.getAttributes(), sizes, height);
			apppendContent(subCell, null, complexType.getElements(), sizes, height);
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
		double x = (iWidth * icons.size()) + (iWidth * (icons.size() + 1));
		width = sizes[0] + 100 + sizes[1] + x;
		for (int i = 0; i < cell.getChildCount(); i++) {
			mxCell child = (mxCell) cell.getChildAt(i);
			if (!child.getId().startsWith(GraphConstants.EXCLUDE_PREFIX_ICON)) {
				child.getGeometry().setX(x);
				heigth += child.getGeometry().getHeight();
				child.getGeometry().setWidth(width - x);
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
				x += (iWidth + (iWidth / 2));
			} else {
				child.getGeometry().setX(x);
				x += (iWidth + (iWidth / 2));
			}
			j++;
		}

		return width;

	}

	private void apppendContent(mxCell parent, mxCell iconParent, ArrayList<IComplexContentModel> elements,
			int[] widths, int height) {
		int x = height + (height / 2);
		for (IComplexContentModel element : elements) {
			if (element.isElement()) {
				if (element instanceof ElementModel) {
					ElementModel ele = (ElementModel) element;
					ElementGraph eg = new ElementGraph(ele, getGraph());
					mxCell cellLine = eg.createElementLine(parent, x, getContentPosition(), widths[0], height,
							widths[1]);
					mxCell cellType = inserType(ele);
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

				} else if (element instanceof GroupModel) {
					GroupModel ele = (GroupModel) element;
					String name = ele.getName();
					if (ele.getRef() != null) {
						name = "(" + ele.getRef().getLocalPart() + ")";
					}
					GroupGraph eg = new GroupGraph(ele, getGraph());
					mxCell cellLine = eg.createGroupCell(parent, name, 0, getContentPosition(),
							widths[0] + widths[1] + 100, height, widths);
					if (iconParent != null) {
						getGraph().insertEdge(getGraph().getDefaultParent(), "", "", iconParent, cellLine,
								GraphTools.getOrtogonalEdgeStyle());
					}
					mxGeometry g = cellLine.getGeometry();
					contentPosition += g.getHeight();
					contentPosition += 3;
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

	private void apppendContent(mxCell parent, Object iconParent, List<AttributeModel> attributes, int[] widths,
			int height) {
		int x = height + (height / 2);
		for (AttributeModel attribute : attributes) {
			AttributeGraph eg = new AttributeGraph(attribute, getGraph());
			mxCell cellLine = eg.createAttributeLine(parent, x, getContentPosition(), widths[0], height, widths[1]);
			mxCell cellType = inserType(attribute);
			if (isAddType()) {
				addCellType(cellType);
				getGraph().insertEdge(getGraph().getDefaultParent(), "", "", cellLine, cellType,
						GraphTools.getOrtogonalEdgeStyle());
			}
			contentPosition += height;
			mxGeometry g = cellLine.getGeometry();
			g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
			g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
			cellLine.setGeometry(g);
		}
		if (attributes.size() > 0)

			contentPosition += 3;
	}

	private void apppendContentAttributeGroup(mxCell parent, Object iconParent, List<AttributeGroupModel> attributes,
			int[] widths, int height) {
		int x = height + (height / 2);
		for (AttributeGroupModel attribute : attributes) {
			String name = attribute.getName();
			if (attribute.getRef() != null) {
				name = "(" + attribute.getRef().getLocalPart() + ")";
			}
			AttributeGroupGraph eg = new AttributeGroupGraph(attribute, getGraph());
			mxCell cellLine = eg.crateAttributeGroupCell(parent, name, 0, getContentPosition(),
					widths[0] + widths[1] + 100, height, widths);
			mxGeometry g = cellLine.getGeometry();
			contentPosition += g.getHeight();
			g.setX(x);
			g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
			g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
			cellLine.setGeometry(g);
			parent.insert(cellLine);
		}
		if (attributes.size() > 0)
			contentPosition += 3;

	}

	private mxCell inserType(ElementModel element) {
		mxCell type = null;
		if (isAddType()) {
			mxCell parent = typeGroup;
			int x = 0;
			int y = (int) typeGroup.getGeometry().getHeight();
			y += 21;

			if (element.getSimpleType() != null) {
				type = insertType(parent, element.getSimpleType(), element.getName(), x, y);
			} else if (element.getComplexType() != null) {
				type = insertType(parent, element.getComplexType(), element.getName(), x, y);
			} else if (element.getRefType() != null) {
				type = insertType(parent, element.getRefType(), element.getName(), x, y);
			} else if (element.getType() != null) {
				type = createType(parent, element.getType().getLocalPart(), x, y);
			}
			if (type != null) {
				typeGroup.getGeometry().setHeight(y + type.getGeometry().getHeight());
			}
		}
		return type;
	}

	private mxCell insertType(mxCell parent, iComponentModel model, String name, int x, int y) {
		mxCell type = null;
		if (model instanceof SimpleTypeModel) {
			SimpleTypeModel st = (SimpleTypeModel) model;
			String value = st.getName();
			if (value == null || value.isEmpty()) {
				value = "(" + name + DitaConstants.SUFFIX_SIMPLETYPE + ")";
			}
			type = new SimpleTypeGraph(st, getGraph()).createSimpleType(parent, value, x, y);
		} else if (model instanceof ComplexTypeModel) {
			ComplexTypeModel ct = (ComplexTypeModel) model;
			String value = ct.getName();
			if (value == null || value.isEmpty()) {
				value = "(" + name + DitaConstants.SUFFIX_COMPLEXTYPE + ")";
			}
			type = new ComplexTypeGraph(ct, getGraph()).createComplexTypeCell(parent, value, x, y);
		}
		return type;
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

	private int[] getSizes() {
		int[] sizes = { 0, 0 };
		int[] tempSizes = getSizesG(complexType.getAttributeGroups());
		if (tempSizes[0] > sizes[0]) {
			sizes[0] = tempSizes[0];
		}
		if (tempSizes[1] > sizes[1]) {
			sizes[1] = tempSizes[1];
		}
		tempSizes = getSizes(complexType.getAttributes());
		if (tempSizes[0] > sizes[0]) {
			sizes[0] = tempSizes[0];
		}
		if (tempSizes[1] > sizes[1]) {
			sizes[1] = tempSizes[1];
		}
		tempSizes = getSizes(complexType.getElements());
		if (tempSizes[0] > sizes[0]) {
			sizes[0] = tempSizes[0];
		}
		if (tempSizes[1] > sizes[1]) {
			sizes[1] = tempSizes[1];
		}
		return sizes;
	}

	private int[] getSizes(ArrayList<IComplexContentModel> elements) {
		int[] sizes = { 0, 0 };
		if (elements != null) {
			for (IComplexContentModel element : elements) {
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

	private int[] getSizesG(ArrayList<AttributeGroupModel> elements) {
		int[] sizes = { 0, 0 };
		if (elements != null) {
			for (AttributeGroupModel element : elements) {
				int[] tempSizes = { 0, 0 };
				tempSizes = getSizes(element);
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

	private void addIconColumn() {
		iconsColumn++;
	}

	private int[] getSizes(AttributeGroupModel attributeGroup) {
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

	private int[] getSize(ElementModel element) {
		int[] sizes = { 0, 0 };
		int iconSize = (int) GraphTools.getTextSize(element.getName()).getHeight();
		sizes[0] = (int) GraphTools.getTextSize(element.getName()).getWidth() + iconSize + (iconSize / 2);

		String value = "";
		if (element.getSimpleType() != null) {
			value = element.getSimpleType().getName();
			if (value == null || value.isEmpty()) {
				value = "(" + element.getName() + DitaConstants.SUFFIX_SIMPLETYPE + ")";
			}
		} else if (element.getComplexType() != null) {
			value = element.getComplexType().getName();
			if (value == null || value.isEmpty()) {
				value = "(" + element.getName() + DitaConstants.SUFFIX_COMPLEXTYPE + ")";
			}
		} else if (element.getType() != null) {
			value = element.getType().getLocalPart();
		}
		sizes[1] = (int) GraphTools.getTextSize(value).getWidth() + iconSize + (iconSize / 2);
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
				value = "(" + atr.getName() + DitaConstants.SUFFIX_SIMPLETYPE + ")";
			}
		}
		sizes[1] = (int) GraphTools.getTextSize(value).getWidth() + iconSize + (iconSize / 2);
		return sizes;
	}

	protected int getIconsColumn() {
		return iconsColumn;
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
		if (cell != null) {
			typeGroup.insert(cell);
			getCellTypes().add(cell);
		}
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
			g.setY(getContentPosition());
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
