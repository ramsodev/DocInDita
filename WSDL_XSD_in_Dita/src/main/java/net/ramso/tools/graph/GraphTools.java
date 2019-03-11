package net.ramso.tools.graph;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.net.URL;

import com.mxgraph.util.mxConstants;

import net.ramso.tools.LogManager;

public class GraphTools {

	public static String getDefaultColor(boolean title) {
		String color = "BLACK";
		if (title) {
			color = "BLUE";
		}
		return color;
	}

	public static String getExtendEdgeStyle() {
		// TODO: mxMarkerRegistry
		String style = mxConstants.STYLE_EDGE + "=" + mxConstants.EDGESTYLE_ELBOW + ";";
		style += mxConstants.STYLE_ENDARROW + "=" + GraphConstants.ARROW_EXTENDS + ";";
		// + mxConstants.ARROW_BLOCK + ";";
		style += mxConstants.STYLE_ENDFILL + "=" + "RED;";
		style += mxConstants.STYLE_ENDSIZE + "=25;";

		return style;
	}

	public static String getOrtogonalEdgeStyle() {
		return getOrtogonalEdgeStyle(false);
	}

	public static String getOrtogonalEdgeStyle(boolean arrow) {
		String style = mxConstants.STYLE_EDGE + "=" + mxConstants.EDGESTYLE_ORTHOGONAL + ";";
		style += mxConstants.STYLE_EXIT_X + "=1;";
		style += mxConstants.STYLE_EXIT_Y + "=0.5;";
		style += mxConstants.STYLE_ENTRY_X + "=0;";
		style += mxConstants.STYLE_ENTRY_Y + "=0.5;";
		if (arrow) {
			style += mxConstants.STYLE_ENDARROW + "=" + mxConstants.ARROW_BLOCK + ";";
		} else {
			style += mxConstants.STYLE_ENDARROW + "=" + "NONE;";
		}
		return style;
	}

	public static String getStyle(boolean title) {
		return getStyle(title, getDefaultColor(title));
	}

	public static String getStyle(boolean title, boolean border) {

		return getStyle(title, border, getDefaultColor(title), -1);
	}

	public static String getStyle(boolean title, boolean border, int space) {
		return getStyle(title, border, getDefaultColor(title), space);
	}

	public static String getStyle(boolean title, boolean border, String color) {
		return getStyle(title, border, color, -1);
	}

	public static String getStyle(boolean title, boolean border, String color, int space) {
		String style = mxConstants.STYLE_AUTOSIZE + "=1;" + mxConstants.STYLE_RESIZABLE + "=1;";
		if (title) {
			style += mxConstants.STYLE_GRADIENTCOLOR + "=" + color + ";";
		}
		if (border) {
			String borderColor = "BLACK";
			if (!title && (color != null)) {
				borderColor = color;
			}
			style += mxConstants.STYLE_STROKECOLOR + "=" + borderColor + ";";
			style += mxConstants.STYLE_SHADOW + "=true;";
		} else {
			style += mxConstants.STYLE_GLASS + "=1;";
			style += mxConstants.STYLE_STROKE_OPACITY + "=0;";
		}
		style += mxConstants.STYLE_FILLCOLOR + "=WHITE;";
		style += mxConstants.STYLE_FONTFAMILY + "=Verdana;";
		style += mxConstants.STYLE_FONTSIZE + "=12;";
		style += mxConstants.STYLE_FONTCOLOR + "=BLACK;";
		if (title) {
			style += mxConstants.STYLE_FONTSTYLE + "=" + mxConstants.FONT_BOLD + ";";
		} else {
			style += mxConstants.STYLE_FONTSTYLE + "=0;";
		}
		if (space > 0) {
			style += mxConstants.STYLE_SPACING + "=" + (space) + ";";
			style += mxConstants.STYLE_ALIGN + "=" + mxConstants.ALIGN_LEFT + ";";
		}
		return style;
	}

	public static String getStyle(boolean title, int space) {
		return getStyle(title, getDefaultColor(title), space);
	}

	public static String getStyle(boolean title, String color) {
		return getStyle(title, false, color, -1);
	}

	public static String getStyle(boolean title, String color, int space) {
		return getStyle(title, false, color, -1);
	}

	public static String getStyleImage(boolean border, int height, int witdth, String icon) {

		String style = mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_IMAGE + ";";
		final URL url = Thread.currentThread().getContextClassLoader().getResource("icons/" + icon + ".gif");
		if (url == null) {
			LogManager.warn("Icono " + icon + "No existe", null);
		} else {
			style += mxConstants.STYLE_IMAGE + "=" + url.toExternalForm() + ";";
		}
		style += mxConstants.STYLE_IMAGE_ALIGN + "=" + mxConstants.ALIGN_CENTER + ";";
		style += mxConstants.STYLE_IMAGE_VERTICAL_ALIGN + "=" + mxConstants.ALIGN_MIDDLE + ";";
		style += mxConstants.STYLE_IMAGE_HEIGHT + "=" + height + ";";
		style += mxConstants.STYLE_IMAGE_WIDTH + "=" + witdth + ";";
		style += mxConstants.STYLE_FILL_OPACITY + "=0;";
		if (border) {
			style += mxConstants.STYLE_STROKECOLOR + "=BLACK;";
			style += mxConstants.STYLE_SHADOW + "=true;";
			style += mxConstants.STYLE_STROKE_OPACITY + "=100;";
		} else {
			style += mxConstants.STYLE_GLASS + "=1;";
			style += mxConstants.STYLE_STROKE_OPACITY + "=0;";
		}
		return style;
	}

	public static String getStyleTransparent(boolean center) {
		String style = mxConstants.STYLE_AUTOSIZE + "=1;";
		style += mxConstants.STYLE_RESIZABLE + "=1;";
		style += mxConstants.STYLE_STROKE_OPACITY + "=0;";
		style += mxConstants.STYLE_FILL_OPACITY + "=0;";
		if (center) {
			style += mxConstants.STYLE_ALIGN + "=" + mxConstants.ALIGN_CENTER + ";";
		} else {
			style += mxConstants.STYLE_ALIGN + "=" + mxConstants.ALIGN_LEFT + ";";
		}
		return style;
	}

	public static Rectangle2D getTextSize(String text) {
		return getTextSize(text, Font.PLAIN);
	}

	public static Rectangle2D getTextSize(String text, int type) {
		if (text == null) {
			text = "";
		}
		final Font font = new Font("Verdana", type, 12);
		final FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
		return font.getStringBounds(text, frc);
	}

}
