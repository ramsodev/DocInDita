package net.ramso.tools;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import net.ramso.tools.ConfigurationManager.LOG_TYPES;

public class LogManager {
	public enum LEVELS {
		INFO, WARN, ERROR, DEBUG
	}

	private static final String NAME = "LogManager";

	protected static Logger logger = null;

	public static void debug(String message) {
		if (logger == null) {
			init(NAME, LOG_TYPES.SIMPLE, "", false);
		}
		logger.debug(message);
	}

	public static void error(String message, Throwable cause) {
		if (logger == null) {
			init(NAME, LOG_TYPES.SIMPLE, "", false);
		}
		logger.error(message, cause);
	}

	public static Logger getLogger() {
		if (logger == null) {
			init(NAME, LOG_TYPES.SIMPLE, "", false);
		}
		return logger;
	}

	public static void info(String message) {
		if (logger == null) {
			init(NAME, LOG_TYPES.SIMPLE, "", false);
		}
		logger.info(message);
	}

	public static void init(Logger externalLogger) {
		logger = externalLogger;
	}

	/**
	 * Configura el logger con el nombre el tipo y las configuraciones recibidas
	 *
	 * @param name
	 *            Nombre del log
	 * @param logtype
	 *            tipo del log a configurar
	 * @param file
	 *            Path al fichero de configuración del log
	 * @param inCP
	 *            true si debe busacar en el Calsspath el fichero de configuración
	 */
	public static void init(String name, LOG_TYPES logtype, String file, boolean inCP) {
		switch (logtype) {
		case JDK:
			System.setProperty("java.util.logging.config.file", file);
			break;
		case LOGBACK:
			final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
			if (logger != null) {
				context.reset();
			}
			final JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			try {
				if (inCP) {
					final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
					configurator.doConfigure(in);
				} else {
					configurator.doConfigure(file);
				}
			} catch (final JoranException e) {
				error("Configurando el log", e);
			}
			break;
		default:
			break;
		}
		logger = LoggerFactory.getLogger(name);
		logger.info("Logger configuration ending");

	}

	public static void log(LEVELS level, String string) {
		if (logger == null) {
			init(NAME, LOG_TYPES.SIMPLE, "", false);
		}
		switch (level) {
		case INFO:
			info(string);
			break;
		case DEBUG:
			debug(string);
			break;
		case WARN:
			warn(string, null);
			break;
		case ERROR:
			error(string, null);
			break;
		default:
			info(string);
			break;
		}
	}

	public static void warn(String message, Throwable cause) {
		if (logger == null) {
			init(NAME, LOG_TYPES.SIMPLE, "", false);
		}
		logger.warn(message, cause);
	}
}