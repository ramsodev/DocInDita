package net.ramso.doc.dita.tools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

public class DitaTools {
	public static Schema SCHEMA = null;

	public static String getHref(boolean file, String id) {
		String href = "";

		if (file) {
			href += id + ".dita";
		} else {
			href += "#" + id;
		}
		return href;

	}

	public static String getHref(String id, String idSection) {
		return getHref(false, id + "/" + idSection);
	}

	public static String getHref(Element element) throws MalformedURLException {
		String idSchema = DitaTools.getSchemaId(element.getNamespaceUri());
		String href = idSchema + "_" + getHref(true, element.getName() + DitaConstants.SUFFIX_ELEMENT);
		return href;
	}

	public static String getHref(QName qname) throws MalformedURLException {
		if (qname.getNamespaceURI().equalsIgnoreCase(DitaConstants.XSD_NAMESPACE)) {
			return DitaConstants.XSD_DOC_URI + qname.getLocalPart();
		}
		String idSchema = DitaTools.getSchemaId(qname.getNamespaceURI());
		String href = idSchema + "_" + getHref(true, qname.getLocalPart() + getSuffixType(qname));
		return href;
	}

	public static String getHrefType(QName type) throws MalformedURLException {
		String idSchema = DitaTools.getSchemaId(type.getNamespaceURI());
		String href = idSchema + "_" + type.getLocalPart() + getSuffixType(type) + ".dita";
		return href;
	}

	public static String getHref(BindingOperation operation) throws MalformedURLException {
		String href = getHref(true, operation.getName() + DitaConstants.SUFFIX_OPERATION);
		return href;
	}

	public static String getExternalHref(QName type) {
		if (type.getNamespaceURI().equalsIgnoreCase(DitaConstants.XSD_NAMESPACE)) {
			return "format=\"html\" scope=\"external\"";
		}
		return "";
	}

	public static String getSchemaId(String uri) throws MalformedURLException {
		URL url = null;
		if (uri == null || uri.isEmpty()) {
			return "noNamespace";
		} else if (uri.startsWith("urn")) {
			URI urn = URI.create(uri);
			String a = urn.getSchemeSpecificPart().replaceAll("\\.", "").replaceAll("\\:", "");
			return a;
		} else {
			url = new URL(uri);
		}
		String idSchema = "";
		if (url.getHost() != null)
			idSchema += url.getHost().replaceAll("\\.", "");
		if (url.getPath() != null) {
			idSchema += url.getPath().replaceAll("\\/", "");
		}
		return idSchema;
	}

	public static void setSchema(Schema schema) {
		SCHEMA = schema;
	}

	public static String getSuffixType(QName type) {
		if (SCHEMA != null) {
			TypeDefinition td = null;
			try {
				td = SCHEMA.getType(type);
			} catch (Exception e) {
			} finally {
				if (td != null && td instanceof SimpleType) {
					return DitaConstants.SUFFIX_SIMPLETYPE;
				} else if (td != null && td instanceof ComplexType) {
					return DitaConstants.SUFFIX_COMPLEXTYPE;
				} else {

					Attribute a = SCHEMA.getAttribute(type);
					if (a != null) {
						return DitaConstants.SUFFIX_ATTRIBUTE;
					} else {
						AttributeGroup g = SCHEMA.getAttributeGroup(type);
						if (g != null) {
							return DitaConstants.SUFFIX_ATTRIBUTEGROUP;
						} else {
							Group gr = SCHEMA.getGroup(type);
							if (gr != null) {
								return DitaConstants.SUFFIX_GROUP;
							} else {
								Element el = null;
								try {
									el = SCHEMA.getElement(type);
								} catch (Exception e) {
								}
								if (el != null) {
									return DitaConstants.SUFFIX_ELEMENT;
								}
							}
						}
					}
				}
			}
		}
		return DitaConstants.SUFFIX_TYPE;

	}

	public static TypeDefinition getType(QName type) {
		TypeDefinition td = null;
		if (SCHEMA != null) {
			try {
				td = SCHEMA.getType(type);
			} catch (Exception e) {
			}
		}
		return td;
	}

	public static String getFileType(URL url) {
		try {
			Document doc = FileTools.parseXML(url);
			switch (doc.getDocumentElement().getNodeName()) {
			case DitaConstants.WSDL_ELEMENTNAME:
				return DitaConstants.WSDL;
			case DitaConstants.WADL_ELEMENTNAME:
				return DitaConstants.WADL;
			case DitaConstants.XSD_ELEMENTNAME:
				return DitaConstants.XSD;
			default:
				return "";
			}
		} catch (ParserConfigurationException | SAXException | IOException | URISyntaxException e) {
			LogManager.error("Error al buscar el tipo de definición", e);
		}
		return null;
	}

	public static QName getAnyType() {
		QName q = new QName(DitaConstants.XSD_NAMESPACE, "anyType");
		return q;
	}
}
