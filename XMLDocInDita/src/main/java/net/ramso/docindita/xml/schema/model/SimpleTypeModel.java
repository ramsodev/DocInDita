package net.ramso.docindita.xml.schema.model;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.predic8.schema.SchemaComponent;
import com.predic8.schema.SchemaList;
import com.predic8.schema.SimpleType;
import com.predic8.schema.Union;
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
import com.sun.org.apache.xerces.internal.impl.dv.xs.UnionDV;

import groovy.xml.QName;
import net.ramso.docindita.tools.DitaConstants;
import net.ramso.docindita.tools.DitaTools;
import net.ramso.docindita.xml.schema.model.graph.SimpleTypeGraph;
import net.ramso.tools.LogManager;

public class SimpleTypeModel extends AbstractComponentModel {

	private final BaseRestriction restriction;

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

	private final SimpleType simpleType;

	private String diagram;

	private Union union;

	private SchemaList lista;

	private String listType;

	private List<QName> unionRefs;
	List<SimpleTypeModel> unionSimpleTypes;

	private SimpleTypeModel listSimpleType;

	public SimpleTypeModel(SimpleType type) {
		super();
		simpleType = type;
		restriction = type.getRestriction();
		union = type.getUnion();
		lista = type.getList();
		init();
		LogManager.debug("Carga de SimpleType " + getName());
	}

	private void addValue(String value) {
		if (getValues() == null) {
			values = new ArrayList<>();
		}
		values.add(value);

	}

	@Override
	public SchemaComponent getComponent() {
		return simpleType;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @return the decimals
	 */
	public int getDecimals() {
		return decimals;
	}

	@Override
	public String getDiagram() {
		if (diagram == null) {
			if(getName()!=null) {
			final SimpleTypeGraph graph = new SimpleTypeGraph(this);
			diagram = graph.generate();
			setScaleDiagram(graph.scale());
			}else {
				diagram="";
			}
		}
		return diagram;
	}

	@Override
	public String getExternalHref() {
		if (restriction.getBase().getNamespaceURI().equalsIgnoreCase(DitaConstants.XSD_NAMESPACE))
			return "format=\"html\" scope=\"external\"";
		return "";
	}

	@Override
	public String getHrefType() throws MalformedURLException {
		if (restriction.getBase().getNamespaceURI().equalsIgnoreCase(DitaConstants.XSD_NAMESPACE))
			return DitaConstants.XSD_DOC_URI + restriction.getBase().getLocalPart();
		return DitaTools.getHrefType(restriction.getBase());
	}

	/**
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * @return the maxValue
	 */
	public String getMaxValue() {
		return maxValue;
	}

	/**
	 * @return the minLenght
	 */
	public int getMinLength() {
		return minLength;
	}

	/**
	 * @return the minValue
	 */
	public String getMinValue() {
		return minValue;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	@Override
	public QName getType() {
		return restriction.getBase();
	}

	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	@SuppressWarnings("unchecked")
	private void init() {
		if (restriction != null) {
			initRestriction();
		}
		if (lista != null) {
			listType = lista.getItemType();
			listSimpleType = new SimpleTypeModel(lista.getSimpleType());
		}
		if (union != null) {
			unionRefs = union.getMemberTypes();
			if (unionRefs.isEmpty()) {
				Object ust = union.getSimpleTypes();
				if (ust instanceof List<?>) {
					unionSimpleTypes = new ArrayList<>();
					for (SimpleType st : (List<SimpleType>) ust) {
						unionSimpleTypes.add(new SimpleTypeModel(st));
					}
				}
			}
		}
	}

	private void initRestriction() {
		dataType = restriction.getBase().getLocalPart();
		for (final Facet facet : restriction.getFacets()) {
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
				minInclusive = true;
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
	 * @return the whiteSpaces
	 */
	public boolean isWhiteSpaces() {
		return whiteSpaces;
	}

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

	public BaseRestriction getRestriction() {
		return restriction;
	}

	public String getListType() {
		return listType;
	}

	public List<QName> getUnionRefs() {
		if (unionRefs == null)
			unionRefs = new ArrayList<>();
		return unionRefs;
	}

	public List<SimpleTypeModel> getUnionSimpleTypes() {
		if (unionSimpleTypes == null)
			unionSimpleTypes = new ArrayList<>();
		return unionSimpleTypes;
	}

	public SimpleTypeModel getListSimpleType() {
		return listSimpleType;
	}

	public boolean isUnion() {
		return union != null;
	}

	public boolean isList() {
		return lista != null;
	}
}
