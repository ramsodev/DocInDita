package net.ramso.docindita.db.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import net.ramso.docindita.db.Config;

public abstract class BaseTest {
	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";
//	public String db = "toursdb";
	public String db = "DB/BirtSample";
	private static Validator topicValidator;
	private static Validator bookmapValidator;
	
	protected Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String path = Thread.currentThread().getContextClassLoader().getResource(db).getPath();
		Class.forName(driver).newInstance();		
		return DriverManager.getConnection(protocol + path);
	}
	
	protected void disconnect(Connection con) throws SQLException {
		con.close();
	}
	
	protected void clean() {
		clean(Config.getOutputDir());
	}

	protected void clean(String path) {
		final File dir = new File(path);
		if (dir.exists()) {
			for (final File file : dir.listFiles()) {
				if (file.isDirectory()) {
					clean(file.getAbsolutePath());
				}
				file.delete();
			}
		}
	}
	protected boolean valid() throws MalformedURLException {
		boolean valid = true;
		File files = new File(Config.getOutputDir());
		for (File file : files.listFiles()) {
			if (file.getAbsolutePath().endsWith(".dita")) {
				valid = validateXMLSchema(getTopicValidator(), file);
				if (!valid) {
					break;
				}
			} else if (file.getAbsolutePath().endsWith(".ditamap")) {
				valid = validateXMLSchema(getBookmapValidator(), file);
				if (!valid) {
					break;
				}
			}
		}
		return valid;
	}

	private Validator getTopicValidator() throws MalformedURLException {
		if (topicValidator == null) {
			URL XSD_TOPIC = new URL(
					"http://docs.oasis-open.org/dita/v1.2/os/DITA1.2-xsds/xsd1.2-url/technicalContent/xsd/topic.xsd");
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema;
			try {
				schema = factory.newSchema(XSD_TOPIC);
				topicValidator = schema.newValidator();
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}
		return topicValidator;
	}

	private Validator getBookmapValidator() throws MalformedURLException {
		if (bookmapValidator == null) {
			URL XSD_BOOKMAP = new URL(
					"http://docs.oasis-open.org/dita/v1.2/os/DITA1.2-xsds/xsd1.2-url/bookmap/xsd/bookmap.xsd");
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema;
			try {
				schema = factory.newSchema(XSD_BOOKMAP);
				bookmapValidator = schema.newValidator();
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}
		return bookmapValidator;
	}

	private static boolean validateXMLSchema(Validator validator, File xml) {
		try {
			validator.validate(new StreamSource(xml));
		} catch (IOException e) {
			System.out.println("Exception: " + e.getMessage());
			return false;
		} catch (SAXException e1) {
			System.out.println("SAX Exception: " + e1.getMessage());
			return false;
		}

		return true;

	}
	
}
