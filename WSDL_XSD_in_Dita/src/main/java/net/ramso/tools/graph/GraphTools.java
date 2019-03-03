package net.ramso.tools.graph;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.net.URL;

import com.mxgraph.util.mxConstants;

public class GraphTools {

	public static Rectangle2D getTextSize(String text) {
		return getTextSize(text, Font.PLAIN);
	}

	public static Rectangle2D getTextSize(String text, int type) {
		Font font = new Font("Verdana", type, 12);
		FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
		return font.getStringBounds(text, frc);
	}

	public static String getStyle(boolean title, boolean border) {
		return getStyle(title, border, "BLUE");
	}

	public static String getStyle(boolean title) {
		return getStyle(title, "BLUE");
	}

	public static String getStyle(boolean title, String color) {
		return getStyle(title, false, color);
	}

	public static String getStyle(boolean title, boolean border, String color) {
		String style = mxConstants.STYLE_AUTOSIZE + "=1;" + mxConstants.STYLE_RESIZABLE + "=1;";
		if (title) {
			style += mxConstants.STYLE_GRADIENTCOLOR + "=" + color + ";";
		}
		if (border) {
			style += mxConstants.STYLE_STROKECOLOR + "=BLACK;";
			style += mxConstants.STYLE_SHADOW + "=true;";
		} else {
			style += mxConstants.STYLE_GLASS + "=1;";
			style += mxConstants.STYLE_STROKE_OPACITY + "=0;";
		}
		style += mxConstants.STYLE_FILLCOLOR + "=WHITE;";
		style += mxConstants.STYLE_FONTFAMILY + "=Verdana;";
		style += mxConstants.STYLE_FONTSIZE + "=12;";
		style += mxConstants.STYLE_FONTCOLOR + "=BLANCK;";
		if (title) {
			style += mxConstants.STYLE_FONTSTYLE + "=" + mxConstants.FONT_BOLD + ";";
		} else {
			style += mxConstants.STYLE_FONTSTYLE + "=0;";
		}
		return style;
	}

	public static String getStyleImage(boolean border, int height, int witdth, String icon) {

		String style = mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_IMAGE + ";";
		URL url = Thread.currentThread().getContextClassLoader().getResource("icons/" + icon + ".gif");
		style += mxConstants.STYLE_IMAGE + "=" + url.toExternalForm() + ";";
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

}
