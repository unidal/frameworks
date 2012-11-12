package org.unidal.web.jsp.function;

import org.unidal.helper.Formats;

public class Format {
	public static String format(Object data, String pattern) {
		return Formats.forObject().format(data, pattern);
	}

	public static String formatNumber(Number data, String pattern, String suffix) {
		return Formats.forNumber().format(data, pattern, suffix);
	}

	public static String percentage(int divisor, int divident, int precision) {
		if (divident == 0) {
			return "N/A";
		} else {
			return Formats.forNumber().percentage(divisor, divident, precision);
		}
	}

	public static String shorten(String str, int maxLength) {
		return Formats.forObject().shorten(str, maxLength);
	}
}
