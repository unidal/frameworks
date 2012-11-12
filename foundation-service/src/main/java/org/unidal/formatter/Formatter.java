package org.unidal.formatter;

public interface Formatter<T> {
	public String format(String format, T object) throws FormatterException;

	public T parse(String format, String text) throws FormatterException;
}
