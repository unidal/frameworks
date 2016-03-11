package org.unidal.helper;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Date;

public class Formats {
	public static NumberFormat forNumber() {
		return NumberFormat.INSTANCE;
	}

	public static ObjectFormat forObject() {
		return ObjectFormat.INSTANCE;
	}

	public enum NumberFormat {
		INSTANCE;

		public String percentage(double divisor, double divident, int precision) {
			if (divident == 1e-6) {
				return "N/A";
			} else {
				StringBuilder sb = new StringBuilder();

				sb.append('0');

				if (precision > 0) {
					sb.append('.');
					for (int i = 0; i < precision; i++) {
						sb.append('0');
					}
				}

				sb.append('%');

				return new DecimalFormat(sb.toString()).format(divisor / divident);
			}
		}

		public String format(Number data, String pattern, String suffix) {
			if (pattern == null || pattern.length() == 0 || data == null) {
				return "";
			}

			StringBuilder sb = new StringBuilder(32);
			long base = 1000;

			if (suffix != null && suffix.length() > 0) {
				char ch = suffix.charAt(0);

				if (ch == 'B' || ch == 'b') {
					base = 1024;
				}
			}

			sb.append("{0,number,").append(pattern);
			sb.append('}');

			double value = data.doubleValue();
			long scale1 = 1;
			long scale2 = -1;
			boolean flag = value > 0;

			if (!flag) {
				value = -value;
			}

			while (value >= base) {
				scale1 *= base;
				value /= base;
			}

			// only available to base 1000
			if (value > 0 && value < 1 && base == 1000) {
				while (value < 1) {
					scale2 *= base;
					value *= base;
				}
			}

			if (scale1 == 1) {
				// add nothing
			} else if (scale1 == base) {
				sb.append('K');
			} else if (scale1 == base * base) {
				sb.append('M');
			} else if (scale1 == base * base * base) {
				sb.append('G');
			} else if (scale1 == base * base * base * base) {
				sb.append('T');
			} else if (scale1 == base * base * base * base * base) {
				sb.append('P');
			}

			if (scale2 == -1) {
				// add nothing
			} else if (scale2 == -base) {
				sb.append('m');
			} else if (scale2 == -base * base) {
				sb.append('u');
			} else if (scale2 == -base * base * base) {
				sb.append('n');
			}

			if (suffix != null) {
				sb.append(suffix);
			}

			MessageFormat format = new MessageFormat(sb.toString());

			return format.format(new Object[] { flag ? value : -value });
		}
	}

	public enum ObjectFormat {
		INSTANCE;

		public String shorten(String str, int maxLength) {
			int len = (str == null ? 0 : str.length());

			if (len < maxLength || len <= 3) {
				return str;
			}

			StringBuilder sb = new StringBuilder(maxLength);
			String dots = "...";
			int dotsLen = dots.length();
			int left = (maxLength - dotsLen) / 2;

			sb.append(str.substring(0, left));
			sb.append(dots);
			sb.append(str.substring(len - maxLength + left + dotsLen));

			return sb.toString();
		}

		public String format(Object data, String pattern) {
			if (pattern == null || pattern.length() == 0 || data == null) {
				return "";
			}

			StringBuilder sb = new StringBuilder(32);

			sb.append("{0");

			if (pattern.startsWith("number") || pattern.startsWith("date") || pattern.startsWith("choice")) {
				sb.append(',').append(pattern);
			} else if (data instanceof Date) {
				sb.append(",date,").append(pattern);
			} else if (data instanceof Number) {
				sb.append(",number,").append(pattern);
			}

			sb.append('}');

			MessageFormat format = new MessageFormat(sb.toString());

			return format.format(new Object[] { data });
		}
	}
}
