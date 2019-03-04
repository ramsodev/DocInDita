package net.ramso.doc.dita.xml.wsdl.graph;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.predic8.wsdl.Binding;
import com.predic8.wsdl.Fault;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Part;
import com.predic8.wsdl.Port;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.Service;

import net.ramso.doc.dita.tools.Constants;
import net.ramso.tools.graph.AbstractGraph;
import net.ramso.tools.graph.GraphConstants;
import net.ramso.tools.graph.GraphTools;

public class WSDLGraph extends AbstractGraph {

	private Service service;

	private int maxWith;
	private HashMap<String, Object> binds;
	private int maxWith2;
	private HashMap<String, Object> pts;
	private int anchura;

	public WSDLGraph(Service service) {
		this.service = service;
		SUFFIX=Constants.SUFFIX_SERVICE;
		setFileName(service.getName());
	}

	public String generate() {
		setGraph(new mxGraph());
		getGraph().setAutoSizeCells(true);
		getGraph().setCellsResizable(true);

		Object parent = getGraph().getDefaultParent();
		Rectangle2D base = GraphTools.getTextSize(service.getName());
		int altura = (int) (base.getHeight() + (base.getHeight() / 2));
		int anchura = (int) (base.getWidth() + (base.getWidth() * 25) / 100);
		setMaxWith(anchura);
		setMaxWith2(anchura);
		int alt = (service.getPorts().size() * altura);
		binds = new HashMap<String, Object>();
		pts = new HashMap<String, Object>();
		mxCell ports = (mxCell) getGraph().insertVertex(parent, service.getName(), "", 100, 100, anchura, alt,
				GraphTools.getStyle(false, true));

		mxCell bindingGroup = (mxCell) getGraph().createVertex(parent,
				GraphConstants.EXCLUDE_PREFIX_GROUP + Constants.SUFFIX_BINDING, "", 100, 100, 80, alt,
				mxConstants.STYLE_AUTOSIZE + "=1;" + mxConstants.STYLE_RESIZABLE + "=1;"
						+ mxConstants.STYLE_STROKE_OPACITY + "=0;" + mxConstants.STYLE_FILL_OPACITY + "=0;");
		int y = 0;
		Object titulo = getGraph().insertVertex(ports, "title", service.getName(), 0, y, anchura, altura,
				GraphTools.getStyle(true, true, "BLUE"));
		insertIcon((mxCell) titulo, Constants.SUFFIX_SERVICE.toLowerCase(), altura);

		for (Port port : service.getPorts()) {
			mxCell adrs = createPort(ports, port, altura);
			mxCell bind = createBinding(bindingGroup, port.getBinding());
			getGraph().insertEdge(parent, "edge" + "_" + port.getName() + "_" + port.getBinding().getName(), "", adrs,
					bind);
			mxCell portType = createPortType(parent, port.getBinding().getPortType(), altura);

			getGraph().insertEdge(parent,
					"edge" + "_" + port.getBinding().getName() + "_" + port.getBinding().getPortType().getName(), "",
					bind, portType);

		}
		resizeCell(ports, getMaxWith());
		y = 15;
		mxGeometry g = ports.getGeometry();
		bindingGroup.setGeometry(new mxGeometry(g.getX() + g.getWidth() + 50, g.getY() + 15, 80, g.getHeight()));
		for (Entry<String, Object> entry : binds.entrySet()) {
			mxCell c = (mxCell) entry.getValue();
			c.setGeometry(new mxGeometry(15, y, 30, 30));
			y += 80;
			bindingGroup.insert(c);
		}
		getGraph().addCell(bindingGroup);

		for (Entry<String, Object> entry : getPts().entrySet()) {
			getGraph().addCell(entry.getValue());
			if (entry.getValue() instanceof mxCell) {
				resizeCell((mxCell) entry.getValue(), getMaxWith2());
			}
		}
		process(getGraph());
		return getFileName();

	}

	private mxCell createPortType(Object parent, PortType portType, int altura) {
		int alt = ((service.getPorts().size() * 3) * altura);

		mxCell portTypeCell = (mxCell) pts.get(portType.getName());

		if (portTypeCell == null) {
			Rectangle2D base = GraphTools.getTextSize(portType.getName());
			int anchura = (int) (base.getWidth() + (base.getWidth() * 25) / 100);
			setMaxWith2(anchura);
			portTypeCell = (mxCell) getGraph().createVertex(parent, portType.getName(), "", 500, 100, anchura, alt,
					GraphTools.getStyle(false, true));
			int y = 0;
			Object titulo = getGraph().insertVertex(portTypeCell, "Title" + portType.getName(), portType.getName(), 0,
					y, anchura, altura, GraphTools.getStyle(true, true, "BLUE"));
			insertIcon((mxCell) titulo, Constants.SUFFIX_PORT_TYPE.toLowerCase(), altura);
			y += altura;
			for (Operation operation : portType.getOperations()) {
				y += createOperation(portTypeCell, operation, y, altura);
			}
			pts.put(portType.getName(), portTypeCell);
		}
		return (mxCell) portTypeCell;
	}

	private int createOperation(mxCell parent, Operation operation, int y, int altura) {
		int alt = (altura * 2) + (altura * operation.getFaults().size());
		Rectangle2D base = GraphTools.getTextSize(operation.getName());
		int anchura = (int) (base.getWidth() + (base.getWidth() * 25) / 100);
		if (anchura > getMaxWith2())
			setMaxWith2(anchura);
		Object oprg = getGraph().insertVertex(parent, operation.getName() + Constants.SUFFIX_OPERATION,
				operation.getName(), 0, y, anchura, alt, GraphTools.getStyle(false, true));

		Object titulo = getGraph().insertVertex(oprg, operation.getName() + Constants.SUFFIX_OPERATION,
				operation.getName(), 0, 0, anchura, altura, GraphTools.getStyle(true, true, "GREEN"));
		insertIcon((mxCell) titulo, Constants.SUFFIX_OPERATION.toLowerCase(), altura);
		int alturaTotal = altura;
		alturaTotal = createOperationLine(oprg, operation.getName(), "Input",
				operation.getInput().getMessage().getParts(), alturaTotal, altura);
		alturaTotal = createOperationLine(oprg, operation.getName(), "Output",
				operation.getOutput().getMessage().getParts(), alturaTotal, altura);
		for (Fault fault : operation.getFaults()) {
			String title = fault.getName();
			if (title == null || title.isEmpty())
				title = "Fault";
			alturaTotal = createOperationLine(oprg, operation.getName(), title,
					operation.getOutput().getMessage().getParts(), alturaTotal, altura);
		}
		return alturaTotal;
	}

	private int createOperationLine(Object oprg, String operation, String title, List<Part> parts, int posicion,
			int altura) {
		Rectangle2D base = GraphTools.getTextSize(title, Font.BOLD);
		anchura = 50 + altura;
		Object input = getGraph().insertVertex(oprg,
				GraphConstants.EXCLUDE_PREFIX_GROUP + title + operation + Constants.SUFFIX_OPERATION, "", 0, posicion,
				200, altura, GraphTools.getStyle(false, true));
		Object titulo = getGraph().insertVertex(input,
				GraphConstants.EXCLUDE_PREFIX_GROUP + title + "Title" + operation + Constants.SUFFIX_OPERATION, title,
				0, 0, anchura, altura, GraphTools.getStyle(true, true, "LIGHT_GRAY", altura));
		String icon = "fault";
		switch (title) {
		case "Input":
		case "Output":
			icon = title.toLowerCase();
			break;
		default:
			break;
		}
		insertIcon((mxCell) titulo, icon, altura);
		int groupWith = anchura;
		for (Part part : parts) {
			base = GraphTools.getTextSize(part.getName());
			anchura = (int) (base.getWidth() + (base.getWidth() * 25) / 100);

			getGraph().insertVertex(input,
					GraphConstants.EXCLUDE_PREFIX_GROUP + part.getName() + Constants.SUFFIX_ELEMENT, part.getName(),
					groupWith, 0, anchura, altura, GraphTools.getStyle(false, true));
			groupWith += anchura;
			base = GraphTools.getTextSize(part.getElement().getName().trim());
			anchura = (int) (base.getWidth() + (altura * 2));

			titulo = getGraph().insertVertex(input,
					GraphConstants.EXCLUDE_PREFIX_GROUP + part.getElement().getName() + Constants.SUFFIX_ELEMENT,
					part.getElement().getName().trim(), groupWith, 0, anchura, altura,
					GraphTools.getStyle(false, altura));

			insertIcon((mxCell) titulo, Constants.SUFFIX_ELEMENT.toLowerCase(), altura);
			groupWith += anchura;
		}
		if (groupWith > getMaxWith2())
			setMaxWith2(groupWith);
		posicion += altura;
		return posicion;
	}

	private mxCell createBinding(mxCell parent, Binding bind) {
		Object b = getBinds().get(bind.getName());
		if (b == null) {
			String icon = "httpbinding";
			if (((String) bind.getProtocol()).startsWith("SOAP")) {
				icon = "soapbinding";
			}
			b = getGraph().createVertex(parent, bind.getName() + Constants.SUFFIX_BINDING, "", 0, 15, 30, 30,
					GraphTools.getStyleImage(true, 25, 25, icon));
			getBinds().put(bind.getName(), b);
		}
		return (mxCell) b;
	}

	private mxCell createPort(mxCell parent, Port port, int altura) {

		int y = (altura * 2) * parent.getChildCount();
		if (parent.getChildCount() == 1) {
			y = altura;
		} else {
			y -= altura;
		}
		Rectangle2D base = GraphTools.getTextSize(port.getName());
		int anchura = (int) (base.getWidth() + (base.getWidth() * 25) / 100);
		if (anchura > getMaxWith())
			setMaxWith(anchura);
		Object p = getGraph().insertVertex(parent, port.getName() + Constants.SUFFIX_PORT, "", 0, y, anchura,
				altura * 2, GraphTools.getStyle(false, true));
		getGraph().insertVertex(p, port.getName() + "Title" + Constants.SUFFIX_ADDRESS, port.getName(), 0, 0, anchura,
				altura, GraphTools.getStyle(false));
		y += altura;
		String url = port.getAddress().getLocation();
		if (port.getAddress().getLocation().length() > 30) {
			url = port.getAddress().getLocation().substring(0, 30);
		}
		base = GraphTools.getTextSize(url);
		anchura = (int) (base.getWidth() + (base.getWidth() * 25) / 100);
		if ((anchura + 20) > getMaxWith())
			setMaxWith(anchura + 20);
		mxCell adrs = (mxCell) getGraph().insertVertex(p, port.getName() + Constants.SUFFIX_ADDRESS, url, 10, altura,
				anchura, altura, GraphTools.getStyle(false, true));

		return adrs;
	}

	/**
	 * @return the maxWith
	 */
	protected int getMaxWith() {
		return maxWith;
	}

	/**
	 * @param maxWith
	 *            the maxWith to set
	 */
	protected void setMaxWith(int maxWith) {
		this.maxWith = maxWith;
	}

	/**
	 * @return the binds
	 */
	protected HashMap<String, Object> getBinds() {
		return binds;
	}

	/**
	 * @return the maxWith2
	 */
	protected int getMaxWith2() {
		return maxWith2;
	}

	/**
	 * @param maxWith2
	 *            the maxWith2 to set
	 */
	protected void setMaxWith2(int maxWith2) {
		this.maxWith2 = maxWith2;
	}

	/**
	 * @return the pts
	 */
	protected HashMap<String, Object> getPts() {
		return pts;
	}

}
