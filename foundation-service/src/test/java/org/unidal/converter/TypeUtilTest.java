package org.unidal.converter;

import junit.framework.TestCase;

public class TypeUtilTest extends TestCase {

   public void testUserDefinedClass() {
      assertEquals(false, TypeUtil.isUserDefinedClass(int.class));
      assertEquals(false, TypeUtil.isUserDefinedClass(Integer.class));
      assertEquals(false, TypeUtil.isUserDefinedClass(String.class));
      assertEquals(true, TypeUtil.isUserDefinedClass(getClass()));
   }

   public void testWrapClass() {
      assertEquals(Boolean.class, TypeUtil.getWrapClass(boolean.class));
      assertEquals(Byte.class, TypeUtil.getWrapClass(byte.class));
      assertEquals(Character.class, TypeUtil.getWrapClass(char.class));
      assertEquals(Short.class, TypeUtil.getWrapClass(short.class));
      assertEquals(Integer.class, TypeUtil.getWrapClass(int.class));
      assertEquals(Long.class, TypeUtil.getWrapClass(long.class));
      assertEquals(Float.class, TypeUtil.getWrapClass(float.class));
      assertEquals(Double.class, TypeUtil.getWrapClass(Double.class));

      assertEquals(Integer.class, TypeUtil.getWrapClass(Integer.TYPE));
      assertEquals(Integer.class, TypeUtil.getWrapClass(Integer.class));
      assertEquals(String.class, TypeUtil.getWrapClass(String.class));
   }
}
