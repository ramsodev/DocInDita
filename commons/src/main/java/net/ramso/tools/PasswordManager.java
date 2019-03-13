/**
 *
 */
package net.ramso.tools;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * @author jjescudero
 *
 */
public class PasswordManager {
	private PasswordManager() {
		super();
	}

	protected static String key = "zs^S#2hsSUF*=PPpsAkG+Rp52Cfe=LVJ"; //$NON-NLS-1$
	protected static String algorithm = "PBEWithMD5AndDES"; //$NON-NLS-1$
	private static StandardPBEStringEncryptor encryptor;
	private static final String PREFIX = "ENC:"; //$NON-NLS-1$

	static {
		try {
			init();
		} catch (final Exception e) {
			LogManager.error(BundleManager.getString("commons.PasswordManager.crypt_config_error"), e); //$NON-NLS-1$
		}
	}

	public static String decrypt(String pass)  {
		if (isEncrypted(pass)) {
			pass = pass.substring(4);
		}
		return encryptor.decrypt(pass);
	}

	public static String encrypt(String pass) {
		String result = encryptor.encrypt(pass);
		return PREFIX + result;
	}

	protected static void init()  {
		encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm(algorithm);
		encryptor.setPassword(key);
	}

	public static boolean isEncrypted(String pass) {
		return pass.startsWith(PREFIX);
	}

}
