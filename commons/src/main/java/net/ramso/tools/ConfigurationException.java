package net.ramso.tools;

public class ConfigurationException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -4531673954836716449L;

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String message) {
		super(message);

	}

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}

}
