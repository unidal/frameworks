package org.unidal.web.jsp.function;

import org.unidal.helper.Formats;
import org.unidal.web.jsp.annotation.FunctionMeta;

public class FormatFunction {
	@FunctionMeta(description = "Format object using message pattern", example = "${w:format(payload.dateFrom,'yyyy-MM-dd')}")
	public static String format(Object obj, String pattern) {
		return Formats.forObject().format(obj, pattern);
	}

	@FunctionMeta(description = "Format number with message pattern with auto scaling", example = "${w:formatNumber(12345, '0.#', 'B')}")
	public static String formatNumber(Number data, String pattern, String suffix) {
		return Formats.forNumber().format(data, pattern, suffix);
	}

	@FunctionMeta(description = "Format number in percentage", example = "${w:percentage(4, 11, 2)}")
	public static String percentage(int divisor, int divident, int precision) {
		if (divident == 0) {
			return "N/A";
		} else {
			return Formats.forNumber().percentage(divisor, divident, precision);
		}
	}

	@FunctionMeta(description = "Shorten a message", example = "${w:shorten('123456789', 5)} will be '1...9'")
	public static String shorten(String str, int maxLength) {
		return Formats.forObject().shorten(str, maxLength);
	}
}
