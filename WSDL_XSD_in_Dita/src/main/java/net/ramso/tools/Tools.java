package net.ramso.tools;

import java.net.MalformedURLException;
import java.net.URL;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;
import com.predic8.wsdl.BindingOperation;

import groovy.xml.QName;

public class Tools {
	public static Schema SCHEMA = null;

	public static String getHref(boolean file, String id) {
		String href = "";

		if (file) {
			href += id + ".dita";
		}
		href += "#" + id;
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

	public static String getHrefType(QName type) throws MalformedURLException {
		String idSchema = Tools.getSchemaId(type.getNamespaceURI());
		String q = type.getQualifiedName();
		String href = idSchema + "_" + type.getLocalPart() + getSuffixType(type)+".dita";
		return href;
	}

	public static String getHref(BindingOperation operation) throws MalformedURLException {
		String href = getHref(true, operation.getName() + Constants.SUFFIX_OPERATION);
		return href;
	}

	public static String getSchemaId(String uri) throws MalformedURLException {
		URL url = new URL(uri);
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
			TypeDefinition td = SCHEMA.getType(type);
			if (td instanceof SimpleType) {
				return Constants.SUFFIX_SIMPLETYPE;
			} else if (td instanceof ComplexType) {
				return Constants.SUFFIX_COMPLEXTYPE;
			}
		}
		return Constants.SUFFIX_TYPE;
	}
}
