package org.unidal.helper;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

public class FormatsTest {
	@Test
	public void testFormatObject() {
		Assert.assertEquals("12345", Formats.forObject().format(12345, "0.#"));
		Assert.assertEquals("12345.0", Formats.forObject().format(12345, "0.0"));
		Assert.assertEquals("12,345.0", Formats.forObject().format(12345, "#,##0.0"));
		Assert.assertEquals("2012", Formats.forObject().format(new Date(), "yyyy"));
	}

	@Test
	public void testFormatNumber() {
		Assert.assertEquals("12.3K", Formats.forNumber().format(12345, "0.#", null));
		Assert.assertEquals("114.98GB", Formats.forNumber().format(123456789012L, "0.##", "B"));
		Assert.assertEquals("-114.98GB", Formats.forNumber().format(-123456789012L, "0.##", "B"));
		Assert.assertEquals("-1B", Formats.forNumber().format(-1, "0.##", "B"));
		
		Assert.assertEquals("12.1KB", Formats.forNumber().format(12345, "0.#", "B"));
		Assert.assertEquals("12.06Kbps", Formats.forNumber().format(12345, "0.0#", "bps"));

		Assert.assertEquals("12.34s", Formats.forNumber().format(12.345, "0.##", "s"));
		Assert.assertEquals("123.4ms", Formats.forNumber().format(0.12345, "0.#", "s"));
		Assert.assertEquals("123.4us", Formats.forNumber().format(0.00012345, "0.#", "s"));

		Assert.assertEquals("11.21GB", Formats.forNumber().format(11.21, "0.##", "GB"));
		Assert.assertEquals("-11.21GB", Formats.forNumber().format(-11.21, "0.##", "GB"));
	}
}
