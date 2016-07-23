package org.unidal.web.jsp.function;

import org.junit.Assert;

import org.junit.Test;

public class MappingFunctionTest {
   @Test
   public void test() {
      String codes = "1|2|3";
      String values = "one|two|three";
      String defaultValue = "not found";

      Assert.assertEquals("one", MappingFunction.translate("1", codes, values, defaultValue));
      Assert.assertEquals("two", MappingFunction.translate("2", codes, values, defaultValue));
      Assert.assertEquals("three", MappingFunction.translate("3", codes, values, defaultValue));
      Assert.assertEquals(defaultValue, MappingFunction.translate("4", codes, values, defaultValue));

      Assert.assertEquals(defaultValue, MappingFunction.translate("1", codes, null, defaultValue));
      Assert.assertEquals(defaultValue, MappingFunction.translate("1", null, values, defaultValue));
      Assert.assertEquals(defaultValue, MappingFunction.translate("1", "", "", defaultValue));
      
      Assert.assertEquals("2", MappingFunction.translate("TWO", "ONE|TWO|THREE", "1|2", defaultValue));
      Assert.assertEquals(defaultValue, MappingFunction.translate("THREE", "ONE|TWO|THREE", "1|2", defaultValue));
   }
}
