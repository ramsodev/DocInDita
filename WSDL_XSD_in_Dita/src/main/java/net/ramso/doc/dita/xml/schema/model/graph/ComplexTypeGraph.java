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
import net.ramso.doc.dita.xml.schema.model.ComplexTypeModel;
import net.ramso.doc.dita.xml.schema.model.ElementModel;
import net.ramso.doc.dita.xml.schema.model.IComplexContentModel;
import net.ramso.tools.graph.GraphConstants;
import net.ramso.tools.graph.GraphTools;

public class ComplexTypeGraph extends AbstractXmlGraph {

	private ComplexTypeModel complexType;
	private int iconsColumn = 0;
	private List<mxCell> cellTypes;

	public ComplexTypeGraph(ComplexTypeModel complexType) {
		super();
		this.complexType = complexType;
		SUFFIX = Constants.SUFFIX_COMPLEXTYPE;
		setFileName(complexType.getName());
	}

	public ComplexTypeGraph(ComplexTypeModel complexType, mxGraph graph) {
		this(complexType);
		setGraph(graph);
	}

	@Override
	public String generate() {
		setGraph(new mxGraph());
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);
		mxCell parent = (mxCell) getGraph().getDefaultParent();
		mxCell complexTypeCell = createComplexTypeCell(parent);
		getGraph().addCell(complexTypeCell);
		getGraph().addCells(getCellTypesArray());
		process(getGraph());
		return getFileName();
	}

	private mxCell createComplexTypeCell(mxCell parent) {
		return createComplexTypeCell(parent, complexType.getName());
	}

	private mxCell createComplexTypeCell(mxCell parent, String name) {
		Rectangle2D base = GraphTools.getTextSize(name);
		int altura = (int) (base.getHeight() + (base.getHeight() / 2));
		int anchura = (int) ((base.getWidth() + (base.getWidth() * 25) / 100) + altura);
		return createComplexTypeCell(parent, name, 0, 0, anchura, altura);
	}

	public mxCell createComplexTypeCell(mxCell parent, int x, int y, int width, int height) {
		return createComplexTypeCell(parent, complexType.getName(), x, y, width, height);
	}

	public mxCell createComplexTypeCell(mxCell parent, String name, int x, int y, int width, int height) {
		int[] sizes = getSizes();
		width = sizes[0] + 100 + sizes[1];
		mxCell cell = (mxCell) getGraph().createVertex(parent, name + Constants.SUFFIX_COMPLEXTYPE, "", x, y,
				width + 50, height, GraphTools.getStyle(false, true));
		Object titulo = getGraph().insertVertex(cell, "Title" + name + Constants.SUFFIX_COMPLEXTYPE,
				complexType.getName(), x, y, width + 50, height, GraphTools.getStyle(true, true, "BLUE", height));
		super.insertIcon((mxCell) titulo, Constants.SUFFIX_COMPLEXTYPE.toLowerCase(), height);
		createContent(cell, x, y + height, sizes, width, height);

		return cell;
	}

	private mxCell createContent(mxCell parent, int x, int y, int[] widths, int width, int height) {
		mxCell cell = (mxCell) getGraph().insertVertex(parent, complexType.getName() + Constants.SUFFIX_COMPLEXTYPE, "",
				x, y, width + 50, height, GraphTools.getStyle(false, true));
		mxCell iconCell = appendIcons(cell, height);
		mxCell cell2 = (mxCell) getGraph().insertVertex(cell,
				GraphConstants.EXCLUDE_PREFIX_GROUP + complexType.getName() + Constants.SUFFIX_COMPLEXTYPE, "", 50, 0,
				width, height, GraphTools.getStyle(false, true));
		y = 0;
		apppendContent(parent, cell2, iconCell, null, complexType.getElements(), y, widths, height);
		iconCell.getChildAt(0).getGeometry().setY(cell2.getGeometry().getCenterY());
		;
		return cell;
	}

	private void apppendContent(mxCell parent, mxCell cell2, mxCell iconCell, mxCell iconParent,
			ArrayList<IComplexContentModel> elements, int y, int[] widths, int height) {
		for (IComplexContentModel element : elements) {
			if (element.isElement()) {
				if (element instanceof ElementModel) {
					ElementModel ele = (ElementModel) element;
					ElementGraph eg = new ElementGraph(ele, getGraph());
					mxCell cellLine = eg.createElementLine(cell2, 0, y, widths[0], height, widths[1]);
					mxCell cellType = inserType(ele);
					if (iconParent != null) {
						getGraph().insertEdge(cell2.getParent(), "", "", iconParent, cellLine,
								mxConstants.STYLE_EDGE + "=" + mxConstants.EDGESTYLE_ORTHOGONAL + ";");
					}
					addCellType(cellType);
					getGraph().insertEdge(parent, "", "", cellLine, cellType,
							mxConstants.STYLE_EDGE + "=" + mxConstants.EDGESTYLE_ORTHOGONAL + ";");
					y += height;
					mxGeometry g = cellLine.getGeometry();
					g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
					g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
					cellLine.setGeometry(g);
				}
			} else {
				mxCell iCell = null;
				mxCell gCell = cell2;
				if (iconCell.getChildCount() > 0) {
					gCell = (mxCell) getGraph().insertVertex(cell2,
							GraphConstants.EXCLUDE_PREFIX_GROUP + parent.getId() + element.getName()
									+ Constants.SUFFIX_ELEMENT,
							"", 0, y, widths[0] + 100 + widths[1] + height, height, GraphTools.getStyle(true, true, "RED"));
					y += height;
//					iCell = insertIcon(gCell, element.getContentType().toLowerCase(), height);
					getGraph().insertEdge(cell2.getParent(), "", "", iconParent, iCell,
							mxConstants.STYLE_EDGE + "=" + mxConstants.EDGESTYLE_ORTHOGONAL + ";");
				} else {
					iCell = insertIcon(iconCell, element.getContentType().toLowerCase(), 30);

				}
				apppendContent(parent, gCell, iconCell, iCell, element.getElements(), y, widths, height);
			}
		}
	}

	private mxCell inserType(ElementModel element) {
		mxCell parent = (mxCell) getGraph().getDefaultParent();
		if (element.getType() != null) {
			return createType(parent, element.getType().getLocalPart());
		} else if (element.getSimpleType() != null) {
			String value = element.getSimpleType().getName();
			if (value == null || value.isEmpty()) {
				value = "(" + element.getName() + Constants.SUFFIX_SIMPLETYPE + ")";
			}
			return new SimpleTypeGraph(element.getSimpleType(), getGraph()).createSimpleType(parent, value);

		} else if (element.getComplexType() != null) {
			String value = element.getComplexType().getName();
			if (value == null || value.isEmpty()) {
				value = "(" + element.getName() + Constants.SUFFIX_COMPLEXTYPE + ")";
			}
			return new ComplexTypeGraph(element.getComplexType(), getGraph()).createComplexTypeCell(parent, value);
		}
		return null;
	}

	private mxCell appendIcons(mxCell parent, int height) {
		mxCell cell = (mxCell) getGraph().insertVertex(parent,
				complexType.getName() + "Icons" + Constants.SUFFIX_COMPLEXTYPE, "", 0, 0, 50, height,
				mxConstants.STYLE_ALIGN + "=" + mxConstants.ALIGN_CENTER + ";" + mxConstants.STYLE_VERTICAL_ALIGN + "="
						+ mxConstants.ALIGN_MIDDLE + ";" + GraphTools.getStyle(false, true));
		return cell;
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

	public int[] getSizes(AttributeGroupModel attributeGroup) {
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
		sizes[0] = (int) GraphTools.getTextSize(element.getName()).getWidth() + iconSize;

		String value = "";
		if (element.getType() != null) {
			value = element.getType().getLocalPart();
		} else if (element.getSimpleType() != null) {
			value = element.getSimpleType().getName();
			if (value == null || value.isEmpty()) {
				value = "(" + element.getName() + Constants.SUFFIX_SIMPLETYPE + ")";
			} else if (element.getComplexType() != null) {
				value = element.getComplexType().getName();
				if (value == null || value.isEmpty()) {
					value = "(" + element.getName() + Constants.SUFFIX_COMPLEXTYPE + ")";
				}
			}
		}
		sizes[1] = (int) GraphTools.getTextSize(value).getWidth() + iconSize;
		return sizes;
	}

	private int[] getSizes(AttributeModel atr) {
		int[] sizes = { 0, 0 };
		int iconSize = (int) GraphTools.getTextSize(atr.getName()).getHeight();
		sizes[0] = (int) GraphTools.getTextSize(atr.getName()).getWidth() + iconSize;
		String value = "";
		if (atr.getType() != null) {
			value = atr.getType().getLocalPart();
		} else if (atr.getSimpleType() != null) {
			value = atr.getSimpleType().getName();
			if (value == null || value.isEmpty()) {
				value = "(" + atr.getName() + Constants.SUFFIX_SIMPLETYPE + ")";
			}
		}
		sizes[1] = (int) GraphTools.getTextSize(value).getWidth() + iconSize;
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
		getCellTypes().add(cell);
	}

	@Override
	protected mxCell insertIcon(mxCell parent, String icon, int size) {
		mxCell icono = super.insertIcon(parent, icon, 30);
		mxGeometry g = icono.getGeometry();
		g.setTerminalPoint(new mxPoint(0, g.getHeight() / 2), false);
		g.setTerminalPoint(new mxPoint(g.getWidth(), g.getHeight() / 2), true);
		icono.setGeometry(g);
		return icono;
	}

}
