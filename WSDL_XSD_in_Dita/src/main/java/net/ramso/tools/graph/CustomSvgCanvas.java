package net.ramso.tools.graph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;

public class CustomSvgCanvas extends mxSvgCanvas {

	public CustomSvgCanvas() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CustomSvgCanvas(Document document) {
		super(document);
		// TODO Auto-generated constructor stub
	}

	@Override
	public mxPoint drawMarker(Element parent, Object type, mxPoint p0, mxPoint pe, float size, float strokeWidth,
			String color) {
		if (!type.equals(GraphConstants.ARROW_EXTENDS)) {
			return super.drawMarker(parent, type, p0, pe, size, strokeWidth, color);
		}
		mxPoint offset = null;

		// Computes the norm and the inverse norm
		double dx = pe.getX() - p0.getX();
		double dy = pe.getY() - p0.getY();

		double dist = Math.max(1, Math.sqrt(dx * dx + dy * dy));
		double absSize = size * scale;
		double nx = dx * absSize / dist;
		double ny = dy * absSize / dist;

		pe = (mxPoint) pe.clone();
		pe.setX(pe.getX() - nx * strokeWidth / (2 * size));
		pe.setY(pe.getY() - ny * strokeWidth / (2 * size));

		nx *= 0.5 + strokeWidth / 2;
		ny *= 0.5 + strokeWidth / 2;

		Element path = document.createElement("path");
		path.setAttribute("stroke-width", String.valueOf(strokeWidth * scale));
		path.setAttribute("stroke", color);
		path.setAttribute("fill", "#FFFFFF");

		String d = null;

		d = "M " + pe.getX() + " " + pe.getY()+ " L " + (pe.getX() - nx - ny / 2) + " " + (pe.getY() - ny*1.25 + nx / 2)
				+ " L " + (pe.getX() + ny / 2 - nx) + " " + (pe.getY() - ny*1.25 - nx / 2) + " z";

		if (d != null) {
			path.setAttribute("d", d);
			parent.appendChild(path);
		}
		offset = new mxPoint();
		offset.setX(0);
		offset.setY((ny*1.25)*-1);
		return offset;
	}

}
