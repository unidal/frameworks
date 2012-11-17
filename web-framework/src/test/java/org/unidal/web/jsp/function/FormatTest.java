package org.unidal.web.jsp.function;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class FormatTest {
	@Test
	public void testFormat() {
		Assert.assertEquals("12345", FormatterFunction.format(12345, "0.#"));
		Assert.assertEquals("12345.0", FormatterFunction.format(12345, "0.0"));
		Assert.assertEquals("12,345.0", FormatterFunction.format(12345, "#,##0.0"));
		Assert.assertEquals("2012", FormatterFunction.format(new Date(), "yyyy"));
	}

	@Test
	public void testFormatNumber() {
		Assert.assertEquals("12.1KB", FormatterFunction.formatNumber(12345, "0.#", "B"));
		Assert.assertEquals("12.06Kb", FormatterFunction.formatNumber(12345, "0.0#", "b"));

		Assert.assertEquals("12.34s", FormatterFunction.formatNumber(12.345, "0.##", "s"));
		Assert.assertEquals("123.4ms", FormatterFunction.formatNumber(0.12345, "0.#", "s"));
		Assert.assertEquals("123.4us", FormatterFunction.formatNumber(0.00012345, "0.#", "s"));
	}
}
