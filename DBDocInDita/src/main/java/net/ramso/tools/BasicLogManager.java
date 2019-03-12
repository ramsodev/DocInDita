package net.ramso.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicLogManager {
	public enum LEVELS {
		info, warn, error, debug
	}

	public static Logger logger = null;;

	public static void debug(String message) {

		getLogger().debug(message);
	}

	public static void error(String message, Throwable cause) {

		getLogger().error(message, cause);
	}

	public static Logger getLogger() {
		if (logger == null) {
			logger = LoggerFactory.getLogger("Log");
		}

		return logger;
	}

	public static void info(String message) {

		getLogger().info(message);
	}

	public static void init(Logger externalLogger) {
		logger = externalLogger;
	}

	public static void log(LEVELS level, String message) {
		if (logger == null) {
			System.out.println(message);
		}
		switch (level) {
		case info:
			info(message);
			break;
		case debug:
			debug(message);
			break;
		case warn:
			warn(message, null);
			break;
		case error:
			error(message, null);
			break;
		default:
			info(message);
			break;
		}
	}

	public static void warn(String message, Throwable cause) {

		getLogger().warn(message, cause);
	}
}