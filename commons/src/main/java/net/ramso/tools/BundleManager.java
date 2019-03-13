package net.ramso.tools;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class BundleManager {
	private BundleManager() {
		super();
	}

	private static ResourceBundle commonsBundle = ResourceBundle.getBundle("messages"); //$NON-NLS-1$
	private static ResourceBundle bundle = null;

	public static String getString(String key) {
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

	public static void init(String bundleName) {
		if (bundleName != null) {
			BundleManager.bundle = ResourceBundle.getBundle(bundleName);
		}
	}
}