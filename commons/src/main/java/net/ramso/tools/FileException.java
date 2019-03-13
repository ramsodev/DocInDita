package net.ramso.tools;

public class FileException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 748911255109233891L;

	public FileException() {
	}

	public FileException(String message) {
		super(message);
	}

	public FileException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileException(Throwable cause) {
		super(cause);
	}

}
