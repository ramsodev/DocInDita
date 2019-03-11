package net.ramso.doc;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.cli.ParseException;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;

import net.ramso.doc.dita.tools.DitaConstants;
import net.ramso.tools.CommandLineProcessor;
import net.ramso.tools.ConfigurationException;
import net.ramso.tools.ConfigurationManager;
import net.ramso.tools.LogManager;
import net.ramso.tools.graph.GraphConfig;

public class Config extends ConfigurationManager {
	private static String outputDir;
	private static boolean r = false;
	private static boolean one = false;
	private static String id;
	private static String title;
	private static String description;

	public static void start() {
		try {
			init();
			load();
			Properties p = getVelocityConfig();
			Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_INSTANCE, LogManager.getLogger());
			Velocity.init(p);
		} catch (ConfigurationException e) {
			LogManager.error("Error en configuración", e);
		}
	}

	private static void load() {
		GraphConfig.loadIcons();
		GraphConfig.loadShapes();
		outputDir = getProperty(DitaConstants.OUTDIR_PROPERTY, DitaConstants.OUTDIR_DEFAULT);

	}

	public static List<String> getParmeters(String[] args, int pmin, int pmax) throws ConfigurationException {
		CommandLineProcessor cmd = new CommandLineProcessor();
		try {
			List<String> parameters = cmd.parse(args, pmin, pmax);
			if ((getId() != null || getTitle() != null || getDescription() != null) && !isOne()) {
				LogManager.error("The options id, title or description are only available with the option one", null);
				System.err.println("The options id, title or description are only available with the option one");
				cmd.printHelp();
				throw new ConfigurationException(
						"The options id, title or description are only available with the option one");
			}
			return parameters;
		} catch (ParseException e) {
			LogManager.error("Error de parametros de entrada", e);
			System.err.println("Error de parametros de entrada");
			cmd.printHelp();
			throw new ConfigurationException(e);
		}
	}

	protected static Properties getVelocityConfig() {
		Properties p = getProperties(DitaConstants.VELOCITY_PREFIX);
		Properties velocityConfig = new Properties();
		for (Entry<Object, Object> entry : p.entrySet()) {
			String key = (String) entry.getKey();
			velocityConfig.put(key.substring(key.indexOf(".") + 1, key.length()), entry.getValue());
		}
		return velocityConfig;
	}

	public static void set(String property, String value) throws ConfigurationException {
		switch (property) {
		case DitaConstants.OUTDIR_NAME:
			outputDir = value;
			File f = new File(outputDir);
			if (f.exists() && !f.isDirectory()) {
				throw new ConfigurationException("El directorio de salida existe y no es un directorio");
			}
			break;
		case DitaConstants.RECURSIVE:
			r = Boolean.parseBoolean(value);
		case DitaConstants.ONE:
			one = Boolean.parseBoolean(value);
		case DitaConstants.ID:
			id = value;
		case DitaConstants.TITLE:
			title = value;
		case DitaConstants.DESCRIPTION:
			description = value;
		default:
			break;
		}
	}

	public static String getOutputDir() {
		return outputDir;
	}

	/**
	 * @return the r
	 */
	public static boolean isR() {
		return r;
	}

	public static boolean isOne() {
		return one;
	}

	public static String getId() {

		return id;
	}

	public static String getTitle() {
		return title;
	}

	public static String getDescription() {
		return description;
	}

}