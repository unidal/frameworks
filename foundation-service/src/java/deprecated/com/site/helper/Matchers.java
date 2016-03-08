package com.site.helper;

public class Matchers {
	public static StringMatcher forString() {
		return StringMatcher.CASE_SENSITIVE;
	}

	public enum StringMatcher {
		CASE_SENSITIVE(true),

		CASE_INSENSITIVE(false);

		private boolean m_caseSensitive;

		private StringMatcher(boolean caseSensitive) {
			m_caseSensitive = caseSensitive;
		}

		public StringMatcher caseSensitive(boolean caseSensitive) {
			if (caseSensitive) {
				return CASE_SENSITIVE;
			} else {
				return CASE_INSENSITIVE;
			}
		}

		public StringMatcher ignoreCase() {
			return CASE_INSENSITIVE;
		}

		public boolean matches(String source, int start, String part) {
			return matches(source, start, part, 0, part.length());
		}

		public boolean matches(String source, int start, String part, int count) {
			return matches(source, start, part, 0, count);
		}

		public boolean matches(String source, int start, String part, int partStart, int count) {
			if (source == null || part == null) {
				throw new IllegalArgumentException(String.format("Source(%s) or part(%s) can't be null!", source, part));
			}

			return source.regionMatches(m_caseSensitive, start, part, 0, count);
		}
	}
}
