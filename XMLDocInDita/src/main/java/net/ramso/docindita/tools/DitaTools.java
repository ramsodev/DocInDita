package net.ramso.docindita.tools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.predic8.schema.Attribute;
import com.predic8.schema.AttributeGroup;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.Group;
import com.predic8.schema.Schema;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;
import com.predic8.wsdl.BindingOperation;

import groovy.xml.QName;
import net.ramso.tools.FileTools;
import net.ramso.tools.LogManager;
import net.ramso.tools.TextTools;

public class DitaTools {

	private DitaTools() {
		super();
	}

	private static Schema schema = null;
	private static String idPrefix = "";

	public static QName getAnyType() {
		return new QName(DitaConstants.XSD_NAMESPACE, "anyType");
	}

	public static String getExternalHref(QName type) {
		if (type.getNamespaceURI().equalsIgnoreCase(DitaConstants.XSD_NAMESPACE))
			return "format=\"html\" scope=\"external\"";
		return "";
	}

	public static String getFileType(URL url) {
		try {
			final Document doc = FileTools.parseXML(url);
			switch (doc.getDocumentElement().getNodeName()) {
			case DitaConstants.WSDL_ELEMENTNAME:
				return DitaConstants.WSDL;
			case DitaConstants.WADL_ELEMENTNAME:
				return DitaConstants.WADL;
			case DitaConstants.XSD_ELEMENTNAME:
			case DitaConstants.XSD_ELEMENTNAME2:
				return DitaConstants.XSD;
			default:
				return "";
			}
		} catch (ParserConfigurationException | SAXException | IOException | URISyntaxException e) {
			LogManager.error("Error al buscar el tipo de definición", e);
		}
		return null;
	}

	public static String getHref(BindingOperation operation) {

		return getHref(true, operation.getName() + DitaConstants.SUFFIX_OPERATION);
	}

	public static String getHref(boolean file, String id) {
		String href = "";

		if (file) {
			href += id + ".dita";
		} else {
			href += "#" + id;
		}
		return href;

	}

	public static String getHref(Element element) throws MalformedURLException {
		final String idSchema = DitaTools.getSchemaId(element.getNamespaceUri());
		return idPrefix.trim() + idSchema + "_" + getHref(true, element.getName() + DitaConstants.SUFFIX_ELEMENT);
	}

	public static String getHref(QName qname) throws MalformedURLException {
		if (qname.getNamespaceURI().equalsIgnoreCase(DitaConstants.XSD_NAMESPACE))
			return DitaConstants.XSD_DOC_URI + qname.getLocalPart();
		final String idSchema = DitaTools.getSchemaId(qname.getNamespaceURI());
		return idPrefix.trim() + idSchema + "_" + getHref(true, qname.getLocalPart() + getSuffixType(qname));

	}

	public static String getHref(String id, String idSection) {
		return getHref(false, id + "/" + idSection);
	}

	public static String getHrefType(QName type) throws MalformedURLException {
		final String idSchema = DitaTools.getSchemaId(type.getNamespaceURI());
		return TextTools.cleanNonAlfaNumeric(idPrefix.trim() + idSchema + "_" + type.getLocalPart() + getSuffixType(type),"_") + ".dita";
	}

	public static String getName(String uri) {
		String name = "";
		if ((uri == null) || uri.isEmpty()) {
			name = "No Name";
		} else if (uri.startsWith("urn")) {
			final URI urn = URI.create(uri);
			name = urn.getSchemeSpecificPart().substring(urn.getSchemeSpecificPart().lastIndexOf(':') + 1);
		} else {
			URL url;
			try {
				url = new URL(uri);
				name = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
			} catch (MalformedURLException e) {
				name = uri.substring(uri.lastIndexOf('/') + 1);
			}
			if (name.contains("?")) {
				name = name.substring(0, name.lastIndexOf('?'));
			} else if (name.contains(".")) {
				name = name.substring(0, name.lastIndexOf('.'));
			}
		}
		return name;
	}

	public static String getSchemaId(String uri) throws MalformedURLException {
		URL url = null;
		if ((uri == null) || uri.isEmpty())
			return "noNamespace";
		else if (uri.startsWith("urn")) {
			final URI urn = URI.create(uri);
			return urn.getSchemeSpecificPart().replaceAll("\\.", "").replaceAll("\\:", "");
		} else {
			url = new URL(uri);
		}
		String idSchema = "";
		if (url.getHost() != null) {
			idSchema += url.getHost().replaceAll("\\.", "");
		}
		if (url.getPath() != null) {
			idSchema += url.getPath().replaceAll("\\/", "");
		}
		return TextTools.cleanNonAlfaNumeric(idSchema,"_");
	}

	public static String getSuffixType(QName type) {
		String value = DitaConstants.SUFFIX_TYPE;
		if (schema != null) {
			TypeDefinition td = null;
			try {
				td = schema.getType(type);
			} catch (final Exception e) {
				LogManager.debug("No Encuentra el tipo");
			} finally {
				if (td instanceof SimpleType)
					value = DitaConstants.SUFFIX_SIMPLETYPE;
				else if (td instanceof ComplexType)
					value = DitaConstants.SUFFIX_COMPLEXTYPE;
				else {

					final Attribute a = schema.getAttribute(type);
					if (a != null)
						value = DitaConstants.SUFFIX_ATTRIBUTE;
					else {
						final AttributeGroup g = schema.getAttributeGroup(type);
						if (g != null)
							value = DitaConstants.SUFFIX_ATTRIBUTEGROUP;
						else {
							final Group gr = schema.getGroup(type);
							if (gr != null)
								value = DitaConstants.SUFFIX_GROUP;
							else {
								Element el = null;
								try {
									el = schema.getElement(type);
								} catch (final Exception e) {
									LogManager.debug("No encuentra el elemento");
								}
								if (el != null)
									value = DitaConstants.SUFFIX_ELEMENT;
							}
						}
					}
				}
			}
		}
		return value;

	}

	public static TypeDefinition getType(QName type) {
		TypeDefinition td = null;
		if (schema != null) {
			try {
				td = schema.getType(type);
			} catch (final Exception e) {
				LogManager.debug("No encuentra el tipo");
			}
		}
		return td;
	}

	public static void setSchema(Schema schema) {
		DitaTools.schema = schema;
	}

	public static void setIdPrefix(String idPrefix) {
		if (!idPrefix.endsWith("_"))
			idPrefix = idPrefix.trim() + "_";
		DitaTools.idPrefix = idPrefix;
	}
}
