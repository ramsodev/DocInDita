package net.ramso.docindita.xml;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.cli.ParseException;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;

import net.ramso.docindita.tools.DitaConstants;
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

	public static List<String> getParmeters(String[] args, int pmin, int pmax) throws ConfigurationException {
		final CommandLineProcessor cmd = new CommandLineProcessor();
		try {
			final List<String> parameters = cmd.parse(args, pmin, pmax);
			getCmdValues();
			if (((getId() != null) || (getTitle() != null) || (getDescription() != null)) && !isOne()) {
				LogManager.error("The options id, title or description are only available with the option one", null);
				System.err.println("The options id, title or description are only available with the option one");
				cmd.printHelp();
				throw new ConfigurationException(
						"The options id, title or description are only available with the option one");
			}
			return parameters;
		} catch (final ParseException e) {
			LogManager.error("Error de parametros de entrada", e);
			System.err.println("Error de parametros de entrada");
			cmd.printHelp();
			throw new ConfigurationException(e);
		}
	}

	public static String getTitle() {
		return title;
	}

	protected static Properties getVelocityConfig() {
		final Properties p = getProperties(DitaConstants.VELOCITY_PREFIX);
		final Properties velocityConfig = new Properties();
		for (final Entry<Object, Object> entry : p.entrySet()) {
			final String key = (String) entry.getKey();
			velocityConfig.put(key.substring(key.indexOf('.') + 1, key.length()), entry.getValue());
		}
		return velocityConfig;
	}

	public static boolean isOne() {
		return one;
	}

	/**
	 * @return the r
	 */
	public static boolean isR() {
		return r;
	}

	private static void load() {
		GraphConfig.loadIcons();
		GraphConfig.loadShapes();
		outputDir = getProperty(DitaConstants.OUTDIR_PROPERTY, DitaConstants.OUTDIR_DEFAULT);

	}

	public static void getCmdValues() throws ConfigurationException {
		outputDir = getProperty(DitaConstants.CMD_OUTDIR);
		if (outputDir != null) {
			final File f = new File(outputDir);
			if (f.exists() && !f.isDirectory())
				throw new ConfigurationException("El directorio de salida existe y no es un directorio");
		} else {
			outputDir = getProperty(DitaConstants.OUTDIR_PROPERTY, DitaConstants.OUTDIR_DEFAULT);
		}
		r = getBooleanProperty(DitaConstants.CMD_RECURSIVE, false);
		one = getBooleanProperty(DitaConstants.CMD_ONE, false);
		id = getProperty(DitaConstants.CMD_ID);
		title = getProperty(DitaConstants.CMD_TITLE);
		description = getProperty(DitaConstants.CMD_DESCRIPTION);
	}

	public static void start() {
		try {
			System.setProperty("java.awt.headless", "true");
			init();
			load();
			final Properties p = getVelocityConfig();
			Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_INSTANCE, LogManager.getLogger());
			Velocity.init(p);
		} catch (final ConfigurationException e) {
			LogManager.error("Error en configuración", e);
		}
	}

	public static String getDescription() {
		return description;
	}

	public static String getId() {
		return id;
	}

	public static String getOutputDir() {
		return outputDir;
	}

}