package org.unidal.helper;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class PropertiesTest {
   @Test
   public void test() {
      Map<String, String> map = null;
      String value = Properties.forString().fromEnv("HOME").fromSystem("user.dir").fromMap(map).getProperty("userBase", null);

      Assert.assertNotNull(value);
   }
}
