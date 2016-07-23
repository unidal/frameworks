package org.unidal.web.jsp.function;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class ObjectFunctionTest {
   @Test
   public void testLength() {
      Assert.assertEquals(null, ObjectFunction.length(null));
      Assert.assertEquals(3, ObjectFunction.length("abc"));
      Assert.assertEquals(4, ObjectFunction.length(new Object[] { "abc", null, true, 3 }));

      // not recognized
      Assert.assertEquals(null, ObjectFunction.length(Arrays.<Object> asList("abc", null, true, 3)));
   }

   @Test
   public void testSize() {
      Assert.assertEquals(null, ObjectFunction.size(null));
      Assert.assertEquals(4, ObjectFunction.size(Arrays.<Object> asList("abc", null, true, 3)));

      HashMap<Object, Object> map = new HashMap<Object, Object>();

      map.put("first", true);
      map.put(1, 2);

      Assert.assertEquals(2, ObjectFunction.size(map));
   }

   @Test
   public void testIn() {
      Assert.assertEquals(false, ObjectFunction.in(null, null));
      Assert.assertEquals(false, ObjectFunction.in("abc", "ab"));
      Assert.assertEquals(true, ObjectFunction.in("abc", "abc"));
      Assert.assertEquals(true, ObjectFunction.in(Arrays.asList("a", "abc"), "abc"));
      Assert.assertEquals(true, ObjectFunction.in(new Object[] { "a", "abc" }, "abc"));
      Assert.assertEquals(false, ObjectFunction.in(new Object[] { "a", "abc" }, "abcd"));

      Assert.assertEquals(true, ObjectFunction.in(new Object[] { 1, "2", 3 }, "3"));
   }
}
