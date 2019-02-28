package com.ontimebt.doc;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.cli.ParseException;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;

import net.ramso.doc.dita.tools.Constants;
import net.ramso.tools.CommandLineProcessor;
import net.ramso.tools.ConfigurationException;
import net.ramso.tools.ConfigurationManager;
import net.ramso.tools.LogManager;

public class Config extends ConfigurationManager {
	private static String outputDir;

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
		outputDir = getProperty(Constants.OUTDIR_PROPERTY, Constants.OUTDIR_DEFAULT);

	}

	public static List<String> getParmeters(String[] args, int pmin, int pmax) throws ConfigurationException {
		CommandLineProcessor cmd = new CommandLineProcessor();
		try {
			return cmd.parse(args, pmin, pmax);
		} catch (ParseException e) {
			LogManager.error("Error de parametros de entrada", e);
			System.out.println("Error en la llamada\n" + e.getMessage());
			cmd.printHelp();
			throw new ConfigurationException(e);
		}
	}

	protected static Properties getVelocityConfig() {
		Properties p = getProperties(Constants.VELOCITY_PREFIX);
		Properties velocityConfig = new Properties();
		for (Entry<Object, Object> entry : p.entrySet()) {
			String key = (String) entry.getKey();
			velocityConfig.put(key.substring(key.indexOf(".") + 1, key.length()), entry.getValue());
		}
		return velocityConfig;
	}

	public static String getOutputDir() {
		return outputDir;
	}

	public static void set(String property, String value) throws ConfigurationException {
		switch (property) {
		case Constants.OUTDIR_NAME:
			outputDir = value;
			File f = new File(outputDir);
			if(f.exists() && !f.isDirectory()) {
				throw new ConfigurationException("El directorio de salida existe y no es un directorio");
			}
			break;
		default:
			break;
		}

	}

}