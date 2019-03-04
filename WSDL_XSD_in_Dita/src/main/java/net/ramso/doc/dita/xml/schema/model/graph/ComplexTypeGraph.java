package net.ramso.doc.dita.xml.schema.model.graph;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import net.ramso.doc.dita.tools.Constants;
import net.ramso.doc.dita.xml.schema.model.AttributeGroupModel;
import net.ramso.doc.dita.xml.schema.model.AttributeModel;
import net.ramso.doc.dita.xml.schema.model.ComplexTypeModel;
import net.ramso.doc.dita.xml.schema.model.ElementModel;
import net.ramso.doc.dita.xml.schema.model.IComplexContentModel;
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

	@Override
	public String generate() {
		setGraph(new mxGraph());
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);

		mxCell parent = (mxCell) getGraph().getDefaultParent();
		mxCell simpleTypeCell = createComplexTypeCell(parent);

		// getGraph().insertEdge(parent, "", "", simpleTypeCell, type,
		// mxConstants.STYLE_EDGE + "="
		// + mxConstants.EDGESTYLE_ELBOW + ";" + mxConstants.STYLE_ENDARROW + "=" +
		// mxConstants.ARROW_OPEN);
		getGraph().addCell(simpleTypeCell);
		getGraph().addCells(getCellTypesArray());
		process(getGraph());
		return getFileName();
	}

	private mxCell createComplexTypeCell(mxCell parent) {
		Rectangle2D base = GraphTools.getTextSize(complexType.getName());
		int altura = (int) (base.getHeight() + (base.getHeight() / 2));
		int anchura = (int) ((base.getWidth() + (base.getWidth() * 25) / 100) + altura);

		return createComplexTypeCell(parent, 0, 0, anchura, altura);
	}

	public mxCell createComplexTypeCell(mxCell parent, int x, int y, int width, int height) {
		int[] sizes = getSizes();
		width = iconsColumn * 50 + sizes[0] + 100 + sizes[1];
		mxCell cell = (mxCell) getGraph().createVertex(parent, complexType.getName() + Constants.SUFFIX_COMPLEXTYPE, "",
				x, y, width, height, GraphTools.getStyle(false, true));
		Object titulo = getGraph().insertVertex(cell, "Title" + complexType.getName() + Constants.SUFFIX_COMPLEXTYPE,
				complexType.getName(), 0, 0, width, height, GraphTools.getStyle(true, true, "BLUE", height));
		insertIcon((mxCell) titulo, Constants.SUFFIX_COMPLEXTYPE.toLowerCase(), height);

		mxCell contentCell = createContent(cell, 0, y + height, sizes, width, height);
		return cell;
	}

	private mxCell createContent(mxCell parent, int x, int y, int[] widths, int width, int height) {
		boolean addIcon = true;
		mxCell cell = (mxCell) getGraph().createVertex(parent, complexType.getName() + Constants.SUFFIX_COMPLEXTYPE, "",
				x, y, width, height, GraphTools.getStyle(false, true));
		mxCell iconCell = appendIcons(cell, height);
		cell.insert(iconCell);
		for (IComplexContentModel element : complexType.getElements()) {
			if (element instanceof ElementModel) {
				ElementModel ele = (ElementModel) element;
				ElementGraph eg = new ElementGraph(ele);
				mxCell cellLine = eg.createElementLine(cell, 50, y, widths[0], height, widths[1]);
				mxCell cellType = inserType(ele);
				addCellType(cellType);
				getGraph().insertEdge(parent, "", "", cellLine, cellType);
				cell.insert(cellLine);
			}

		}
		return cell;
	}

	private mxCell inserType(ElementModel element) {
		mxCell parent = (mxCell) getGraph().getDefaultParent();
		if (element.getType() != null) {
			return createType(parent, element.getType().getLocalPart());
		} else if (element.getSimpleType() != null) {
			return new SimpleTypeGraph(element.getSimpleType()).createSimpleType(parent);

		} else if (element.getComplexType() != null) {
			return new ComplexTypeGraph(element.getComplexType()).createComplexTypeCell(parent);
		}
		return null;
	}

	private mxCell appendIcons(mxCell parent, int height) {
		mxCell cell = (mxCell) getGraph().createVertex(parent,
				complexType.getName() + "Icons" + Constants.SUFFIX_COMPLEXTYPE, "", 0, 0, 50, height,
				GraphTools.getStyle(false, true));
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
		sizes[0] = (int) GraphTools.getTextSize(element.getName()).getWidth();
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
		sizes[1] = (int) GraphTools.getTextSize(value).getWidth();
		return sizes;
	}

	private int[] getSizes(AttributeModel atr) {
		int[] sizes = { 0, 0 };
		sizes[0] = (int) GraphTools.getTextSize(atr.getName()).getWidth();
		String value = "";
		if (atr.getType() != null) {
			value = atr.getType().getLocalPart();
		} else if (atr.getSimpleType() != null) {
			value = atr.getSimpleType().getName();
			if (value == null || value.isEmpty()) {
				value = "(" + atr.getName() + Constants.SUFFIX_SIMPLETYPE + ")";
			}
		}
		sizes[1] = (int) GraphTools.getTextSize(value).getWidth();
		return sizes;
	}

	protected int getIconsColumn() {
		return iconsColumn;
	}

	protected List<mxCell> getCellTypes() {
		return cellTypes;
	}

	protected Object[] getCellTypesArray() {
		return cellTypes.toArray();
	}

	protected void addCellType(mxCell cell) {
		if (getCellTypes() == null)
			cellTypes = new ArrayList<mxCell>();
		getCellTypes().add(cell);
	}
}
