package org.unidal.web.jsp.function;

import java.util.Date;

import org.junit.Assert;

import org.junit.Test;

public class FormatFunctionTest {
   @SuppressWarnings("deprecation")
   @Test
   public void testFormat() {
      Assert.assertEquals("12345", FormatFunction.format(12345, "0.#"));
      Assert.assertEquals("12345.0", FormatFunction.format(12345, "0.0"));
      Assert.assertEquals("12,345.0", FormatFunction.format(12345, "#,##0.0"));
      Assert.assertEquals(String.valueOf(new Date().getYear() + 1900), FormatFunction.format(new Date(), "yyyy"));
   }

   @Test
   public void testFormatNumber() {
      Assert.assertEquals("12.1KB", FormatFunction.formatNumber(12344, "0.#", "B"));
      Assert.assertEquals("12.05Kb", FormatFunction.formatNumber(12344, "0.0#", "b"));

      Assert.assertEquals("12.34s", FormatFunction.formatNumber(12.344, "0.##", "s"));
      Assert.assertEquals("123.4ms", FormatFunction.formatNumber(0.12344, "0.#", "s"));
      Assert.assertEquals("123.4us", FormatFunction.formatNumber(0.00012344, "0.#", "s"));
   }
}
