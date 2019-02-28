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
import net.ramso.tools.LogManager;

public class Tools {
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
		String idSchema = Tools.getSchemaId(element.getNamespaceUri());
		String href = idSchema + "_" + getHref(true, element.getName() + Constants.SUFFIX_ELEMENT);
		return href;
	}

	public static String getHref(QName qname) throws MalformedURLException {
		String idSchema = Tools.getSchemaId(qname.getNamespaceURI());
		String href = idSchema + "_" + getHref(true, qname.getLocalPart() + getSuffixType(qname));
		return href;
	}

	public static String getHrefType(QName type) throws MalformedURLException {
		String idSchema = Tools.getSchemaId(type.getNamespaceURI());
		String href = idSchema + "_" + type.getLocalPart() + getSuffixType(type) + ".dita";
		return href;
	}

	public static String getHref(BindingOperation operation) throws MalformedURLException {
		String href = getHref(true, operation.getName() + Constants.SUFFIX_OPERATION);
		return href;
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
					return Constants.SUFFIX_SIMPLETYPE;
				} else if (td != null && td instanceof ComplexType) {
					return Constants.SUFFIX_COMPLEXTYPE;
				} else {
					Attribute a = SCHEMA.getAttribute(type);
					if (a != null) {
						return Constants.SUFFIX_ATTRIBUTE;
					} else {
						AttributeGroup g = SCHEMA.getAttributeGroup(type);
						if (g != null) {
							return Constants.SUFFIX_ATTRIBUTEGROUP;
						} else {
							Group gr = SCHEMA.getGroup(type);
							if (gr != null) {
								return Constants.SUFFIX_GROUP;
							}
						}
					}
				}
			}
		}
		return Constants.SUFFIX_TYPE;
	}

	public static TypeDefinition getType(QName type) {
		return SCHEMA.getType(type);
	}

	public static String getFileType(URL url) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(url.toURI().toString());
			switch (doc.getDocumentElement().getNodeName()) {
			case Constants.WSDL_ELEMENTNAME:
				return Constants.WSDL;
			case Constants.WADL_ELEMENTNAME:
				return Constants.WADL;
			case Constants.XSD_ELEMENTNAME:
				return Constants.XSD;
			default:
				return "";
			}
		} catch (ParserConfigurationException | SAXException | IOException | URISyntaxException e) {
			LogManager.error("Error al buscar el tipo de definición", e);
		}
		return null;
	}
}
