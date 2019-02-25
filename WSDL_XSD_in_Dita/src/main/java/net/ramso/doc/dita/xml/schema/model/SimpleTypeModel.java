package net.ramso.doc.dita.xml.schema.model;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.Documentation;
import com.predic8.schema.SimpleType;
import com.predic8.schema.restriction.BaseRestriction;
import com.predic8.schema.restriction.facet.EnumerationFacet;
import com.predic8.schema.restriction.facet.Facet;
import com.predic8.schema.restriction.facet.FractionDigits;
import com.predic8.schema.restriction.facet.LengthFacet;
import com.predic8.schema.restriction.facet.MaxExclusiveFacet;
import com.predic8.schema.restriction.facet.MaxInclusiveFacet;
import com.predic8.schema.restriction.facet.MaxLengthFacet;
import com.predic8.schema.restriction.facet.MinExclusiveFacet;
import com.predic8.schema.restriction.facet.MinInclusiveFacet;
import com.predic8.schema.restriction.facet.MinLengthFacet;
import com.predic8.schema.restriction.facet.PatternFacet;
import com.predic8.schema.restriction.facet.TotalDigitsFacet;
import com.predic8.schema.restriction.facet.WhiteSpaceFacet;

import net.ramso.tools.Constants;
import net.ramso.tools.Tools;

public class SimpleTypeModel {

	private BaseRestriction restriction;

	private String dataType = null;
	private int maxLength = -1;
	private int minLength = -1;
	private String maxValue = null;
	private String minValue = null;
	private boolean maxInclusive = false;
	private boolean minInclusive = false;
	private boolean whiteSpaces = false;
	private int size = -1;
	private int decimals = -1;
	private String pattern = null;
	private List<String> values;

	private String code;

	private String doc;

	public SimpleTypeModel(SimpleType type) {
		super();
		this.restriction = type.getRestriction();
		this.code = type.getAsString();
		this.doc = "";
		if (type.getAnnotation() != null) {
			for (Documentation doce : type.getAnnotation().getDocumentations()) {
				doc += doce.getContent() + "\n";
			}
		}
		init();
	}

	private void init() {
		dataType = restriction.getBase().getLocalPart();

		for (Facet facet : restriction.getFacets()) {
			if (facet instanceof EnumerationFacet) {
				addValue(facet.getValue());
			} else if (facet instanceof FractionDigits) {
				decimals = Integer.parseInt(facet.getValue());
			} else if (facet instanceof LengthFacet) {
				size = Integer.parseInt(facet.getValue());
			} else if (facet instanceof MaxExclusiveFacet) {
				maxInclusive = false;
				maxValue = facet.getValue();
				setSizeFromMask(facet.getValue());
			} else if (facet instanceof MaxInclusiveFacet) {
				maxInclusive = true;
				maxValue = facet.getValue();
				setSizeFromMask(facet.getValue());
			} else if (facet instanceof MaxLengthFacet) {
				maxLength = Integer.parseInt(facet.getValue());
				if (maxLength > size) {
					size = maxLength;
				}
			} else if (facet instanceof MinExclusiveFacet) {
				minInclusive = false;
				minValue = facet.getValue();
				setSizeFromMask(facet.getValue());
			} else if (facet instanceof MinInclusiveFacet) {
				minInclusive = false;
				minValue = facet.getValue();
				setSizeFromMask(facet.getValue());
			} else if (facet instanceof MinLengthFacet) {
				minLength = Integer.parseInt(facet.getValue());
				if (minLength > size) {
					size = minLength;
				}
			} else if (facet instanceof PatternFacet) {
				pattern = facet.getValue();
			} else if (facet instanceof TotalDigitsFacet) {
				size = Integer.parseInt(facet.getValue());
			} else if (facet instanceof WhiteSpaceFacet) {
				whiteSpaces = Boolean.parseBoolean(facet.getValue());
			}
		}

	}

	/**
	 * Retorna el tamaño de un campo numerico en base a la mascara de maxValue
	 *
	 * @param mask la mascara a traducir
	 * @return un array con dos elementos longitud, decimales
	 */
	protected void setSizeFromMask(final String mask) {
		final String[] cuts = mask.split("\\.");
		final int[] sizes = new int[2];
		sizes[0] = 0;
		sizes[1] = -1;
		if (cuts.length == 1) {
			sizes[0] = cuts[0].trim().length();

		} else if (cuts.length > 1) {

			sizes[1] = cuts[1].trim().length();
			sizes[0] = cuts[0].trim().length() + sizes[1];
		}
		if (sizes[0] > size) {
			size = sizes[0];
		}
		if (sizes[1] > decimals) {
			decimals = sizes[1];
		}
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}
	public String getHrefType() throws MalformedURLException {
		if(restriction.getBase().getNamespaceURI().equalsIgnoreCase(Constants.XSD_NAMESPACE)) {
			return Constants.XSD_DOC_URI+restriction.getBase().getLocalPart();
		}
		return Tools.getHrefType(restriction.getBase());
	}
	
	public String getExternalHref() {
		if(restriction.getBase().getNamespaceURI().equalsIgnoreCase(Constants.XSD_NAMESPACE)) {
			return "format=\"html\" scope=\"external\"";
		}
		return "";
	}
	/**
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * @return the minLenght
	 */
	public int getMinLength() {
		return minLength;
	}

	/**
	 * @return the maxValue
	 */
	public String getMaxValue() {
		return maxValue;
	}

	/**
	 * @return the minValue
	 */
	public String getMinValue() {
		return minValue;
	}

	/**
	 * @return the maxInclusive
	 */
	public boolean isMaxInclusive() {
		return maxInclusive;
	}

	/**
	 * @return the minInclusive
	 */
	public boolean isMinInclusive() {
		return minInclusive;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return the decimals
	 */
	public int getDecimals() {
		return decimals;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	private void addValue(String value) {
		if (getValues() == null)
			values = new ArrayList<String>();
		values.add(value);

	}

	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * @return the whiteSpaces
	 */
	public boolean isWhiteSpaces() {
		return whiteSpaces;
	}

	public String getCode() {
		return code.replaceAll("<", "&lt;").replaceAll(">","&gt;");
	}

	/**
	 * @return the doc
	 */
	public String getDoc() {
		return doc;
	}

}
