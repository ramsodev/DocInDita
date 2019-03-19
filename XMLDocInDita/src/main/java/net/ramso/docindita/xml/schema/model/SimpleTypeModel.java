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

	private final Union union;

	private final SchemaList lista;

	private String listType;

	private List<QName> unionRefs;
	List<SimpleTypeModel> unionSimpleTypes;

	private SimpleTypeModel listSimpleType;

	public SimpleTypeModel(SimpleType type) {
		super();
		this.simpleType = type;
		this.restriction = type.getRestriction();
		this.union = type.getUnion();
		this.lista = type.getList();
		init();
		LogManager.debug("Carga de SimpleType " + getName());
	}

	private void addValue(String value) {
		if (getValues() == null) {
			this.values = new ArrayList<>();
		}
		this.values.add(value);

	}

	@Override
	public SchemaComponent getComponent() {
		return this.simpleType;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return this.dataType;
	}

	/**
	 * @return the decimals
	 */
	public int getDecimals() {
		return this.decimals;
	}

	@Override
	public String getDiagram() {
		if (this.diagram == null) {
			final SimpleTypeGraph graph = new SimpleTypeGraph(this);
			this.diagram = graph.generate();
			setScaleDiagram(graph.scale());
		}
		return this.diagram;
	}

	@Override
	public String getExternalHref() {
		if (this.restriction.getBase().getNamespaceURI().equalsIgnoreCase(DitaConstants.XSD_NAMESPACE)) {
			return "format=\"html\" scope=\"external\"";
		}
		return "";
	}

	@Override
	public String getHrefType() throws MalformedURLException {
		if (this.restriction.getBase().getNamespaceURI().equalsIgnoreCase(DitaConstants.XSD_NAMESPACE)) {
			return DitaConstants.XSD_DOC_URI + this.restriction.getBase().getLocalPart();
		}
		return DitaTools.getHrefType(this.restriction.getBase());
	}

	/**
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return this.maxLength;
	}

	/**
	 * @return the maxValue
	 */
	public String getMaxValue() {
		return this.maxValue;
	}

	/**
	 * @return the minLenght
	 */
	public int getMinLength() {
		return this.minLength;
	}

	/**
	 * @return the minValue
	 */
	public String getMinValue() {
		return this.minValue;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return this.size;
	}

	@Override
	public QName getType() {
		return this.restriction.getBase();
	}

	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return this.values;
	}

	@SuppressWarnings("unchecked")
	private void init() {
		if (this.restriction != null) {
			initRestriction();
		}
		if (this.lista != null) {
			this.listType = this.lista.getItemType();
			this.listSimpleType = new SimpleTypeModel(this.lista.getSimpleType());
		}
		if (this.union != null) {
			this.unionRefs = this.union.getMemberTypes();
			if (this.unionRefs.isEmpty()) {
				final Object ust = this.union.getSimpleTypes();
				if (ust instanceof List<?>) {
					this.unionSimpleTypes = new ArrayList<>();
					for (final SimpleType st : (List<SimpleType>) ust) {
						this.unionSimpleTypes.add(new SimpleTypeModel(st));
					}
				}
			}
		}
	}

	private void initRestriction() {
		this.dataType = this.restriction.getBase().getLocalPart();
		for (final Facet facet : this.restriction.getFacets()) {
			if (facet instanceof EnumerationFacet) {
				addValue(facet.getValue());
			} else if (facet instanceof FractionDigits) {
				this.decimals = Integer.parseInt(facet.getValue());
			} else if (facet instanceof LengthFacet) {
				this.size = Integer.parseInt(facet.getValue());
			} else if (facet instanceof MaxExclusiveFacet) {
				this.maxInclusive = false;
				this.maxValue = facet.getValue();
				setSizeFromMask(facet.getValue());
			} else if (facet instanceof MaxInclusiveFacet) {
				this.maxInclusive = true;
				this.maxValue = facet.getValue();
				setSizeFromMask(facet.getValue());
			} else if (facet instanceof MaxLengthFacet) {
				this.maxLength = Integer.parseInt(facet.getValue());
				if (this.maxLength > this.size) {
					this.size = this.maxLength;
				}
			} else if (facet instanceof MinExclusiveFacet) {
				this.minInclusive = false;
				this.minValue = facet.getValue();
				setSizeFromMask(facet.getValue());
			} else if (facet instanceof MinInclusiveFacet) {
				this.minInclusive = true;
				this.minValue = facet.getValue();
				setSizeFromMask(facet.getValue());
			} else if (facet instanceof MinLengthFacet) {
				this.minLength = Integer.parseInt(facet.getValue());
				if (this.minLength > this.size) {
					this.size = this.minLength;
				}
			} else if (facet instanceof PatternFacet) {
				this.pattern = facet.getValue();
			} else if (facet instanceof TotalDigitsFacet) {
				this.size = Integer.parseInt(facet.getValue());
			} else if (facet instanceof WhiteSpaceFacet) {
				this.whiteSpaces = Boolean.parseBoolean(facet.getValue());
			}
		}

	}

	/**
	 * @return the maxInclusive
	 */
	public boolean isMaxInclusive() {
		return this.maxInclusive;
	}

	/**
	 * @return the minInclusive
	 */
	public boolean isMinInclusive() {
		return this.minInclusive;
	}

	/**
	 * @return the whiteSpaces
	 */
	public boolean isWhiteSpaces() {
		return this.whiteSpaces;
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
		if (sizes[0] > this.size) {
			this.size = sizes[0];
		}
		if (sizes[1] > this.decimals) {
			this.decimals = sizes[1];
		}
	}

	public BaseRestriction getRestriction() {
		return this.restriction;
	}

	public String getListType() {
		return this.listType;
	}

	public List<QName> getUnionRefs() {
		if (this.unionRefs == null) {
			this.unionRefs = new ArrayList<>();
		}
		return this.unionRefs;
	}

	public List<SimpleTypeModel> getUnionSimpleTypes() {
		if (this.unionSimpleTypes == null) {
			this.unionSimpleTypes = new ArrayList<>();
		}
		return this.unionSimpleTypes;
	}

	public SimpleTypeModel getListSimpleType() {
		return this.listSimpleType;
	}

	public boolean isUnion() {
		return !getUnionRefs().isEmpty();
	}

	public boolean isList() {
		return (getListType() != null) && !getListType().isEmpty();
	}
}
