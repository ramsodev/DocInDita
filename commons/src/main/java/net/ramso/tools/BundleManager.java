package net.ramso.tools;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class BundleManager {
	private BundleManager() {
		super();
	}

	private static ResourceBundle commonsBundle = null;
	private static ResourceBundle bundle = null;

	public static String getString(String key) {
		if(commonsBundle==null) init();
		String value = "";
		if (key.startsWith(Constants.COMMONS)) {
			value = commonsBundle.getString(key);
		} else {
			if (bundle != null) {
				value = bundle.getString(key);
			}
		}
		return value;
	}

	public static String getString(String key, Object... param) {
		if(commonsBundle==null) init();
		String pattern = "";
		if (key.startsWith(Constants.COMMONS)) {
			pattern = commonsBundle.getString(key);
		} else {
			if (bundle != null) {
				pattern = bundle.getString(key);
			}
		}
		return MessageFormat.format(pattern, param);
	}

	protected static void init() {
		commonsBundle = ResourceBundle.getBundle("commonsBundle"); //$NON-NLS-1$
	}
	public static void init(String bundleName) {
		
		if (bundleName != null) {
			BundleManager.bundle = ResourceBundle.getBundle(bundleName);
		}
	}
}