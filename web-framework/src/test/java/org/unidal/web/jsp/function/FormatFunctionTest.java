package org.unidal.web.jsp.function;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class FormatFunctionTest {
	@Test
	public void testFormat() {
		Assert.assertEquals("12345", FormatFunction.format(12345, "0.#"));
		Assert.assertEquals("12345.0", FormatFunction.format(12345, "0.0"));
		Assert.assertEquals("12,345.0", FormatFunction.format(12345, "#,##0.0"));
		Assert.assertEquals("2013", FormatFunction.format(new Date(), "yyyy"));
	}

	@Test
	public void testFormatNumber() {
		Assert.assertEquals("12.1KB", FormatFunction.formatNumber(12345, "0.#", "B"));
		Assert.assertEquals("12.06Kb", FormatFunction.formatNumber(12345, "0.0#", "b"));

		Assert.assertEquals("12.34s", FormatFunction.formatNumber(12.345, "0.##", "s"));
		Assert.assertEquals("123.4ms", FormatFunction.formatNumber(0.12345, "0.#", "s"));
		Assert.assertEquals("123.4us", FormatFunction.formatNumber(0.00012345, "0.#", "s"));
	}
}
