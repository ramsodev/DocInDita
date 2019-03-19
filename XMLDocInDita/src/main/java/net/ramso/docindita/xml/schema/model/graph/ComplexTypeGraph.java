package net.ramso.docindita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.predic8.schema.ComplexType;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;

import groovy.xml.QName;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.Config;
import net.ramso.docindita.xml.schema.model.AttributeGroupModel;
import net.ramso.docindita.xml.schema.model.AttributeModel;
import net.ramso.docindita.xml.schema.model.ComplexTypeModel;
import net.ramso.docindita.xml.schema.model.ElementModel;
import net.ramso.docindita.xml.schema.model.GroupModel;
import net.ramso.docindita.xml.schema.model.IComplexContentModel;
import net.ramso.docindita.xml.schema.model.IComponentModel;
import net.ramso.docindita.xml.schema.model.SimpleTypeModel;
import net.ramso.tools.LogManager;
import net.ramso.tools.graph.GraphConstants;
import net.ramso.tools.graph.GraphTools;

public class ComplexTypeGraph extends AbstractXmlGraph {

	private final ComplexTypeModel complexType;
	private int iconsColumn = 0;
	private List<mxCell> cellTypes;
	private int contentPosition = 0;
	private mxCell typeGroup;
	private boolean addType = false;

	public ComplexTypeGraph(ComplexTypeModel complexType) {
		super();
		this.complexType = complexType;
		suffix = DitaConstants.SUFFIX_COMPLEXTYPE;
		setFileName(complexType.getName());
	}

	public ComplexTypeGraph(ComplexTypeModel complexType, mxGraph graph) {
		this(complexType);
		setGraph(graph);
	}

	protected void addCellType(mxCell cell) {
		if (getCellTypes() == null) {
			this.cellTypes = new ArrayList<>();
		}
		if (cell != null) {
			this.typeGroup.insert(cell);
			getCellTypes().add(cell);
		}
	}

	private void addIconColumn() {
		this.iconsColumn++;
	}

	private void apppendContent(mxCell parent, mxCell iconParent, List<IComplexContentModel> elements, int[] widths,
			int height) {
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
					this.contentPosition += height;

				} else if (element instanceof GroupModel) {
					final GroupModel ele = (GroupModel) element;
					String name = ele.getName();
					if (ele.getRef() != null) {
						name = "(" + ele.getRef().getLocalPart() + ")";
					}
					final GroupGraph eg = new GroupGraph(ele, getGraph());
					final mxCell cellLine = eg.createGroupCell(parent, name, 0, getContentPosition(),
							widths[0] + widths[1] + 100, height, widths);
					if (iconParent != null) {
						getGraph().insertEdge(getGraph().getDefaultParent(), "", "", iconParent, cellLine,
								GraphTools.getOrtogonalEdgeStyle());
					}
					final mxGeometry g = cellLine.getGeometry();
					this.contentPosition += g.getHeight();
					this.contentPosition += 3;
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

	private void apppendContent(mxCell parent, List<AttributeModel> attributes, int[] widths, int height) {
		final int x = height + (height / 2);
		for (final AttributeModel attribute : attributes) {
			final AttributeGraph eg = new AttributeGraph(attribute, getGraph());
			final mxCell cellLine = eg.createAttributeLine(parent, x, getContentPosition(), widths[0], height,
					widths[1]);
			final mxCell cellType = inserType(attribute);
			if (isAddType()) {
				addCellType(cellType);
				getGraph().insertEdge(getGraph().getDefaultParent(), "", "", cellLine, cellType,
						GraphTools.getOrtogonalEdgeStyle());
			}
			this.contentPosition += height;
			final mxGeometry g = cellLine.getGeometry();
			g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
			g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
			cellLine.setGeometry(g);
		}
		if (!attributes.isEmpty()) {
			this.contentPosition += 3;
		}
	}

	private void apppendContentAttributeGroup(mxCell parent, List<AttributeGroupModel> attributes, int[] widths,
			int height) {
		final int x = height + (height / 2);
		for (final AttributeGroupModel attribute : attributes) {
			String name = attribute.getName();
			if (attribute.getRef() != null) {
				name = "(" + attribute.getRef().getLocalPart() + ")";
			}
			final AttributeGroupGraph eg = new AttributeGroupGraph(attribute, getGraph());
			final mxCell cellLine = eg.crateAttributeGroupCell(parent, name, 0, getContentPosition(),
					widths[0] + widths[1] + 100, height, widths);
			final mxGeometry g = cellLine.getGeometry();
			this.contentPosition += g.getHeight();
			g.setX(x);
			g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
			g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
			cellLine.setGeometry(g);
			parent.insert(cellLine);
		}
		if (!attributes.isEmpty()) {
			this.contentPosition += 3;
		}

	}

	public mxCell createComplexTypeCell(mxCell parent) {
		return createComplexTypeCell(parent, this.complexType.getName());
	}

	public mxCell createComplexTypeCell(mxCell parent, int x, int y, int width, int height) {
		final int[] sizes = getSizes();
		return createComplexTypeCell(parent, this.complexType.getName(), x, y, width, height, sizes);
	}

	public mxCell createComplexTypeCell(mxCell parent, int x, int y, int width, int height, int[] sizes) {
		return createComplexTypeCell(parent, this.complexType.getName(), x, y, width, height, sizes);
	}

	public mxCell createComplexTypeCell(mxCell parent, String name) {
		return createComplexTypeCell(parent, name, 0, 0);
	}

	public mxCell createComplexTypeCell(mxCell parent, String name, int x, int y) {
		final Rectangle2D base = GraphTools.getTextSize(name);
		final int height = (int) (base.getHeight() + (base.getHeight() / 2));

		final int[] sizes = getSizes();
		final int width = sizes[0] + 100 + sizes[1] + (height + (height / 2));
		return createComplexTypeCell(parent, name, x, y, width, height, sizes);
	}

	public mxCell createComplexTypeCell(mxCell parent, String name, int x, int y, int width, int height, int[] sizes) {
		String color = "BLUE";
		if (!isAddType()) {
			color = "LIGHTGRAY";
		}

		final mxCell superGroup = (mxCell) getGraph().createVertex(parent,
				GraphConstants.EXCLUDE_PREFIX_GROUP + DitaConstants.EXTENDED, "", x, y, width, height,
				GraphTools.getStyleTransparent(true));
		final mxCell cell = (mxCell) getGraph().insertVertex(superGroup, name + DitaConstants.SUFFIX_COMPLEXTYPE, "", x,
				y, width, height, GraphTools.getStyle(false, true));
		if (isAddType() && (this.complexType.getSuper() != null)) {
			int h = 0;
			final mxCell sc = insertSupers(superGroup, this.complexType.getSuper());
			superGroup.insert(sc);
			getGraph().insertEdge(getGraph().getDefaultParent(), "", "", cell, sc, GraphTools.getExtendEdgeStyle());
			h += sc.getGeometry().getHeight();
			superGroup.getGeometry().setWidth(sc.getGeometry().getWidth() + 100);
			cell.getGeometry().setY((double) h + (height * 3));

		}
		final mxCell titulo = (mxCell) getGraph().insertVertex(cell, "Title" + name + DitaConstants.SUFFIX_COMPLEXTYPE,
				name, 0, 0, width, height, GraphTools.getStyle(true, true, color, height));
		super.insertIcon(titulo, DitaConstants.SUFFIX_COMPLEXTYPE.toLowerCase(), height);

		y += height;
		width -= 6;
		if ((!this.complexType.getElements().isEmpty()) || (!this.complexType.getAttributes().isEmpty())) {
			final mxCell subCell = (mxCell) getGraph().insertVertex(cell,
					this.complexType.getName() + DitaConstants.SUFFIX_COMPLEXTYPE, "", x, y, width, height,
					GraphTools.getStyle(false, false));

			this.contentPosition = 0;
			this.typeGroup = (mxCell) getGraph().createVertex(parent,
					GraphConstants.EXCLUDE_PREFIX_GROUP + DitaConstants.SUFFIX_TYPE, "",
					superGroup.getGeometry().getWidth() + 100, cell.getGeometry().getY() + 100, 300, 0,
					GraphTools.getStyleTransparent(false));
			apppendContentAttributeGroup(subCell, this.complexType.getAttributeGroups(), sizes, height);
			apppendContent(subCell, this.complexType.getAttributes(), sizes, height);
			apppendContent(subCell, null, this.complexType.getElements(), sizes, height);
			width = (int) resize(subCell, sizes);
		}

		cell.getGeometry().setWidth(width);
		titulo.getGeometry().setWidth(width);
		if (superGroup.getGeometry().getWidth() < width) {
			superGroup.getGeometry().setWidth(width);
		}
		if (this.typeGroup != null) {
			this.typeGroup.getGeometry().setX(superGroup.getGeometry().getWidth() + 100);
			this.typeGroup.getGeometry().setY(cell.getGeometry().getY() + height);
		}
		return superGroup;
	}

	@Override
	public String generate() {
		this.addType = true;
		setGraph(new mxGraph());
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);
		final mxCell parent = (mxCell) getGraph().getDefaultParent();
		final mxCell complexTypeCell = createComplexTypeCell(parent);
		getGraph().addCell(complexTypeCell);
		if (isAddType()) {
			getGraph().addCell(this.typeGroup);
		}
		process(getGraph(), Config.getOutputDir());
		return getFileName();
	}

	protected List<mxCell> getCellTypes() {
		return this.cellTypes;
	}

	protected Object[] getCellTypesArray() {
		if (this.cellTypes == null) {
			this.cellTypes = new ArrayList<>();
		}
		return this.cellTypes.toArray();
	}

	protected int getContentPosition() {
		return this.contentPosition;
	}

	protected int getIconsColumn() {
		return this.iconsColumn;
	}

	private int[] getSize(ElementModel element) {
		final int[] sizes = { 0, 0 };
		final int iconSize = (int) GraphTools.getTextSize(element.getName()).getHeight();
		sizes[0] = (int) GraphTools.getTextSize(element.getName()).getWidth() + iconSize + (iconSize / 2);

		String value = "";
		if (element.getSimpleType() != null) {
			value = element.getSimpleType().getName();
			if ((value == null) || value.isEmpty()) {
				value = "(" + element.getName() + DitaConstants.SUFFIX_SIMPLETYPE + ")";
			}
		} else if (element.getComplexType() != null) {
			value = element.getComplexType().getName();
			if ((value == null) || value.isEmpty()) {
				value = "(" + element.getName() + DitaConstants.SUFFIX_COMPLEXTYPE + ")";
			}
		} else if (element.getType() != null) {
			value = element.getType().getLocalPart();
		}
		sizes[1] = (int) GraphTools.getTextSize(value).getWidth() + iconSize + (iconSize / 2);
		return sizes;
	}

	private int[] getSizes() {
		final int[] sizes = { 0, 0 };
		int[] tempSizes = getSizesG(this.complexType.getAttributeGroups());
		if (tempSizes[0] > sizes[0]) {
			sizes[0] = tempSizes[0];
		}
		if (tempSizes[1] > sizes[1]) {
			sizes[1] = tempSizes[1];
		}
		tempSizes = getSizesAttr(this.complexType.getAttributes());
		if (tempSizes[0] > sizes[0]) {
			sizes[0] = tempSizes[0];
		}
		if (tempSizes[1] > sizes[1]) {
			sizes[1] = tempSizes[1];
		}
		tempSizes = getSizes(this.complexType.getElements());
		if (tempSizes[0] > sizes[0]) {
			sizes[0] = tempSizes[0];
		}
		if (tempSizes[1] > sizes[1]) {
			sizes[1] = tempSizes[1];
		}
		return sizes;
	}

	private int[] getSizes(List<IComplexContentModel> elements) {
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

	private int[] getSizes(AttributeGroupModel attributeGroup) {
		final int[] sizes = { 0, 0 };
		final int[] tempSizes = getSizesAttr(attributeGroup.getAttributes());
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

	private int[] getSizesAttr(List<AttributeModel> attributes) {
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

	private int[] getSizesG(List<AttributeGroupModel> elements) {
		final int[] sizes = { 0, 0 };
		if (elements != null) {
			for (final AttributeGroupModel element : elements) {
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

	@Override
	protected mxCell insertIcon(mxCell parent, String icon, int size) {

		return insertIcon(parent, icon, size, 0);
	}

	private mxCell insertIcon(mxCell parent, String icon, int size, int move) {
		final mxCell icono = super.insertIcon(parent, icon, size);
		final mxGeometry g = icono.getGeometry();
		if (move > 0) {
			g.setX((double) size + move);
			g.setY(getContentPosition());
			icono.setId(GraphConstants.EXCLUDE_PREFIX_ICON + parent.getId() + move);
		}

		g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
		g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
		icono.setGeometry(g);

		return icono;

	}

	private mxCell insertSupers(mxCell parent, QName extend) {
		final TypeDefinition t = DitaTools.getType(extend);
		IComponentModel model = null;
		if (t != null) {
			if (t instanceof SimpleType) {
				model = new SimpleTypeModel((SimpleType) t);
			} else if (t instanceof ComplexType) {
				model = new ComplexTypeModel((ComplexType) t);
			} else {
				LogManager.warn("No se encuentra el tipo " + extend.getLocalPart() + " del que hereda", null);
				return createType(parent, extend.getLocalPart());
			}
		}
		String name = "(" + extend.getLocalPart() + ")";
		if ((model.getName() != null) && !model.getName().isEmpty()) {
			name = model.getName();
		}
		return insertType(parent, model, name, 0, 0);
	}

	private mxCell insertType(mxCell parent, IComponentModel model, String name, int x, int y) {
		mxCell type = null;
		if (model instanceof SimpleTypeModel) {
			final SimpleTypeModel st = (SimpleTypeModel) model;
			String value = st.getName();
			if ((value == null) || value.isEmpty()) {
				value = "(" + name + DitaConstants.SUFFIX_SIMPLETYPE + ")";
			}
			type = new SimpleTypeGraph(st, getGraph()).createSimpleType(parent, value, x, y);
		} else if (model instanceof ComplexTypeModel) {
			final ComplexTypeModel ct = (ComplexTypeModel) model;
			String value = ct.getName();
			if ((value == null) || value.isEmpty()) {
				value = "(" + name + DitaConstants.SUFFIX_COMPLEXTYPE + ")";
			}
			type = new ComplexTypeGraph(ct, getGraph()).createComplexTypeCell(parent, value, x, y);
		}
		return type;
	}

	private mxCell inserType(AttributeModel attribute) {
		mxCell type = null;
		if (isAddType()) {
			final mxCell parent = this.typeGroup;
			final int x = 0;
			int y = (int) this.typeGroup.getGeometry().getHeight();
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
				this.typeGroup.getGeometry().setHeight(y + type.getGeometry().getHeight());
			}
		}
		return type;
	}

	private mxCell inserType(ElementModel element) {
		mxCell type = null;
		if (isAddType()) {
			final mxCell parent = this.typeGroup;
			final int x = 0;
			int y = (int) this.typeGroup.getGeometry().getHeight();
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
				this.typeGroup.getGeometry().setHeight(y + type.getGeometry().getHeight());
			}
		}
		return type;
	}

	protected boolean isAddType() {
		return this.addType;
	}

	private double resize(mxCell cell, int[] sizes) {
		final List<Integer> icons = new ArrayList<>();
		double heigth = 0;
		double width = 0;
		double iWidth = 0;
		for (int i = 0; i < cell.getChildCount(); i++) {
			final mxCell child = (mxCell) cell.getChildAt(i);
			if (child.getId().startsWith(GraphConstants.EXCLUDE_PREFIX_ICON)) {
				if (icons.isEmpty()) {
					iWidth = child.getGeometry().getWidth();
				}
				icons.add(i);
			}
		}
		double x = (iWidth * icons.size()) + (iWidth * (icons.size() + 1));
		width = sizes[0] + 100 + sizes[1] + x;
		for (int i = 0; i < cell.getChildCount(); i++) {
			final mxCell child = (mxCell) cell.getChildAt(i);
			if (!child.getId().startsWith(GraphConstants.EXCLUDE_PREFIX_ICON)) {
				child.getGeometry().setX(x);
				heigth += child.getGeometry().getHeight();
				child.getGeometry().setWidth(width - x);
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

}
