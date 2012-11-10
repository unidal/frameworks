package org.unidal.web.jsp.function;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class FormatTest {
	@Test
	public void testFormat() {
		Assert.assertEquals("12345", Format.format(12345, "0.#"));
		Assert.assertEquals("12345.0", Format.format(12345, "0.0"));
		Assert.assertEquals("12,345.0", Format.format(12345, "#,##0.0"));
		Assert.assertEquals("2012", Format.format(new Date(), "yyyy"));
	}

	@Test
	public void testFormatNumber() {
		Assert.assertEquals("12.1KB", Format.formatNumber(12345, "0.#", "B"));
		Assert.assertEquals("12.06Kb", Format.formatNumber(12345, "0.0#", "b"));

		Assert.assertEquals("12.34s", Format.formatNumber(12.345, "0.##", "s"));
		Assert.assertEquals("123.4ms", Format.formatNumber(0.12345, "0.#", "s"));
		Assert.assertEquals("123.4us", Format.formatNumber(0.00012345, "0.#", "s"));
	}
}
