package net.ramso.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Gestion la configuración de una aplicación
 *
 * @author jescudero
 *
 */

public abstract class ConfigurationManager {
	/**
	 * Tipos admitido de log para configurar
	 *
	 * @author jescudero
	 *
	 */
	public enum LOG_TYPES {
		JDK, LOG4J, LOGBACK, SIMPLE
	}

	/**
	 * Nombre del fichero de configuración
	 */
	protected static String propertiesName = Constants.PROPERTIES_NAME;
	/**
	 * Propiedad del fichero de configuración del log
	 */
	protected static String fileConfLog = Constants.FILE_CONF_LOG;

	/**
	 * Propiedad con el nombre del log
	 */
	protected static String logName = Constants.LOG_NAME;

	/**
	 * Tipo de log a usar en la aplicación
	 */
	protected static String logType = Constants.LOG_TYPE;

	/**
	 * Propieda con el path del directorio de configuración
	 */
	protected static String confDir = null;

	protected static String bundleName = Constants.BUNDLENAME;

	/**
	 *
	 */
	protected static final Properties defaults = new Properties();
	/**
	 *
	 */
	protected static final Properties properties = new Properties(defaults);

	/**
	 * Obtiene la propiedad y la convierte a boolean si la propiedad no existe
	 * retorna false
	 *
	 * @param propName
	 * @return
	 */
	public static final boolean getBooleanProperty(String propName) {
		return getBooleanProperty(propName, false);
	}

	/**
	 * Obtiene la propiedad y la convierte a boolean si la propiedad no existe
	 * retorna defaultValue
	 *
	 * @param propName
	 * @param defaultValue
	 * @return
	 */
	public static final boolean getBooleanProperty(String propName, boolean defaultValue) {
		try {
			return Boolean.parseBoolean(properties.getProperty(propName, Boolean.toString(defaultValue)));
		} catch (final Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Obtiene la propiedad y la convierte a byte si la propiedad no existe retorna
	 * (byte)0
	 *
	 * @param propName
	 * @return
	 */
	public static final byte getByteProperty(String propName) {
		return getByteProperty(propName, (byte) 0);
	}

	/**
	 * Obtiene la propiedad y la convierte a byte si la propiedad no existe retorna
	 * defaultValue
	 *
	 * @param propName
	 * @param defaultValue
	 * @return
	 */
	public static final byte getByteProperty(String propName, byte defaultValue) {
		try {
			return Byte.parseByte(properties.getProperty(propName));
		} catch (final Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Obtiene la propiedad y la convierte a double si la propiedad no existe
	 * retorna (short)0
	 *
	 * @param propName
	 * @return
	 */
	public static final double getDoubleProperty(String propName) {
		return getDoubleProperty(propName, 0.0);
	}

	/**
	 * Obtiene la propiedad y la convierte a double si la propiedad no existe
	 * retorna defaultValue
	 *
	 * @param propName
	 * @param defaultValue
	 * @return
	 */
	public static final double getDoubleProperty(String propName, double defaultValue) {
		try {
			return Double.parseDouble(properties.getProperty(propName));
		} catch (final Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Obtiene la propiedad y la convierte a double si la propiedad no existe
	 * retorna (short)0
	 *
	 * @param propName
	 * @return
	 */
	public static final float getFloatProperty(String propName) {
		return getFloatProperty(propName, (float) 0.0);
	}

	/**
	 * Obtiene la propiedad y la convierte a double si la propiedad no existe
	 * retorna defaultValue
	 *
	 * @param propName
	 * @param defaultValue
	 * @return
	 */
	public static final float getFloatProperty(String propName, float defaultValue) {
		try {
			return Float.parseFloat(properties.getProperty(propName));
		} catch (final Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Obtiene la propiedad y la convierte a int si la propiedad no existe retorna
	 * (int)0
	 *
	 * @param propName
	 * @return
	 */
	public static final int getIntProperty(String propName) {
		return getIntProperty(propName, 0);
	}

	/**
	 * Obtiene la propiedad y la convierte a int si la propiedad no existe retorna
	 * defaultValue
	 *
	 * @param propName
	 * @param defaultValue
	 * @return
	 */
	public static final int getIntProperty(String propName, int defaultValue) {
		try {
			return Integer.parseInt(properties.getProperty(propName));
		} catch (final Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Obtiene la propiedad y la convierte a long si la propiedad no existe retorna
	 * (long)0
	 *
	 * @param propName
	 * @return
	 */
	public static final long getLongProperty(String propName) {
		return getLongProperty(propName, 0L);
	}

	/**
	 * Obtiene la propiedad y la convierte a short si la propiedad no existe retorna
	 * defaultValue
	 *
	 * @param propName
	 * @param defaultValue
	 * @return
	 */
	public static final long getLongProperty(String propName, long defaultValue) {
		try {
			return Long.parseLong(properties.getProperty(propName));
		} catch (final Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * @param propName
	 * @return
	 */
	public static final String getPassword(String propName) {
		String pass = properties.getProperty(propName);
		if (PasswordManager.isEncrypted(pass)) {
			try {
				return PasswordManager.decrypt(pass);
			} catch (final Exception e) {
				LogManager.error(BundleManager.getString("commons.ConfigurationManager.error_decrypt_password"), e); //$NON-NLS-1$
				pass = ""; //$NON-NLS-1$
			}
		} else {
			try {
				setProperty(propName, PasswordManager.encrypt(pass));
				save();
			} catch (final Exception e) {
				LogManager.error(BundleManager.getString("commons.ConfigurationManager.error_crypt_password"), e); //$NON-NLS-1$
			}
		}
		return pass;
	}

	/**
	 * @param prefix
	 * @return
	 */
	public static final Properties getProperties(String prefix) {
		final Properties prop = new Properties();
		for (final Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
			final String name = (String) e.nextElement();
			if ((name).startsWith(prefix)) {
				prop.put(name, properties.get(name));
			}
		}
		return prop;
	}

	/**
	 * Obtiene una Propiedad como un String si no Existe devuelve ""
	 *
	 * @param key
	 * @return
	 */
	public static final String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Obtiene una Propiedad como un String sino su valor por defecto
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static final String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	/**
	 * Obtiene la propiedad y la convierte a short si la propiedad no existe retorna
	 * (short)0
	 *
	 * @param propName
	 * @return
	 */
	public static final short getShortProperty(String propName) {
		return getShortProperty(propName, (short) 0);
	}

	/**
	 * Obtiene la propiedad y la convierte a short si la propiedad no existe retorna
	 * defaultValue
	 *
	 * @param propName
	 * @param defaultValue
	 * @return
	 */
	public static final short getShortProperty(String propName, short defaultValue) {
		try {
			return Short.parseShort(properties.getProperty(propName));
		} catch (final Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Metodo de inicialición del gestor de configuraciones. Configura el log y la
	 * base de datos en base a los parametros recibidos
	 *
	 * @throws ConfigurationException
	 */
	public static void init() throws ConfigurationException {
		String path = ""; //$NON-NLS-1$
		boolean inCP = true;
		if (confDir != null) {
			path = confDir;
			if (!path.endsWith(File.separator)) {
				path += File.separator;
			}
			inCP = false;
		}
		if (!load(inCP, path + propertiesName)) {
			throw new ConfigurationException(
					BundleManager.getString("commons.ConfigurationManager.conf_not_found", path + propertiesName)); //$NON-NLS-1$
		}
		
		BundleManager.init(getProperty(bundleName));
		final String name = getProperty(logName);
		final LOG_TYPES logtype = LOG_TYPES.valueOf(getProperty(logType).toUpperCase());

		LogManager.init(name, logtype, path + getProperty(fileConfLog), inCP);
		LogManager.info(BundleManager.getString("commons.ConfigurationManager.conf_load_ok")); //$NON-NLS-1$
		LogManager.info(BundleManager.getString("commons.ConfigurationManager.app_init_ok")); //$NON-NLS-1$
	}

	public static boolean load(boolean inCp, String file) {
		boolean result = false;
		if (inCp) {
			result = load(FileTools.getStream(file));
		} else {
			result = load(new File(file));
		}
		return result;
	}

	@SuppressWarnings("null")
	static final boolean load(File file) {
		boolean result = true;
		FileInputStream propsFile = null;
		try {
			propsFile = new FileInputStream(file);
			properties.load(propsFile);
		} catch (final Exception e) {
			result = false;
		} finally {
			try {
				propsFile.close();
			} catch (final Exception e) {
				LogManager.debug(BundleManager.getString("commons.ConfigurationManager.fail_close_prop")); //$NON-NLS-1$
			}
		}
		return result;
	}

	@SuppressWarnings("null")
	static final boolean load(FileReader file) {
		boolean result = true;
		final FileInputStream propsFile = null;
		try {
			properties.load(file);
		} catch (final Exception e) {
			result = false;
		} finally {
			try {
				propsFile.close();
			} catch (final Exception e) {
				LogManager.debug(BundleManager.getString("commons.ConfigurationManager.fail_close_prop")); //$NON-NLS-1$
			}
		}
		return result;
	}

	@SuppressWarnings("null")
	static final boolean load(InputStream propsFile) {
		boolean result = true;
		try {
			properties.load(propsFile);
		} catch (final Exception e) {
			result = false;
		} finally {
			try {
				propsFile.close();
			} catch (final Exception e) {
				LogManager.debug(BundleManager.getString("commons.ConfigurationManager.fail_close_prop")); //$NON-NLS-1$
			}
		}
		return result;
	}

	public static final void putdefault(Object key, Object value) {
		defaults.put(key, value);
	}

	public static final void save() throws IOException {
		String path = ""; //$NON-NLS-1$
		if (confDir != null) {
			path = confDir;
			if (!path.endsWith(File.separator)) {
				path += File.separator;
			}
		}
		properties.store(new FileOutputStream(path + propertiesName),
				BundleManager.getString("commons.ConfigurationManager.prop_mod", propertiesName)); //$NON-NLS-1$
	}

	public static final void setProperty(String propName, String value) {
		properties.setProperty(propName, value);

	}

	public static void set(String key, String value) {
		properties.setProperty(key, value);
	}
}