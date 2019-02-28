/**
 * 
 */
package net.ramso.tools;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * @author jjescudero
 *
 */
public class PasswordManager {

	
	protected static String KEY = "zs^S#2hsSUF*=PPpsAkG+Rp52Cfe=LVJ";
	protected static String ALGORITHM = "PBEWithMD5AndDES";
	private static StandardPBEStringEncryptor encryptor;
	private static final String PREFIX="ENC:";
	

	static {
		try {
			init();
		} catch (Exception e) {
			LogManager.error("Error al configurar encryptación", e);
		}
	}

	protected static void init() throws IOException {
		encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm(ALGORITHM);
		encryptor.setPassword(KEY);
		
	}

	public static String encrypt(String pass) throws InvalidKeyException, InvalidAlgorithmParameterException,
			ShortBufferException, IllegalBlockSizeException, BadPaddingException {
		
		String result = encryptor.encrypt(pass);
		result=PREFIX+result;
		return result;
	}

	public static String decrypt(String pass) throws InvalidKeyException, InvalidAlgorithmParameterException,
			ShortBufferException, IllegalBlockSizeException, BadPaddingException {
		if(IsEncrypted(pass)){
			pass = pass.substring(4);
		}
		String result = encryptor.decrypt(pass);
		return result;
	}

	public static boolean IsEncrypted(String pass) {
		if(pass.startsWith(PREFIX)){
			return true;
		}
		return false;
	}

}
