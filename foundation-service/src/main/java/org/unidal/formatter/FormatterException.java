package org.unidal.formatter;

public class FormatterException extends Exception {
	private static final long serialVersionUID = 1L;

	public FormatterException(String message) {
		super(message);
	}

	public FormatterException(String message, Throwable cause) {
		super(message, cause);
	}
}
