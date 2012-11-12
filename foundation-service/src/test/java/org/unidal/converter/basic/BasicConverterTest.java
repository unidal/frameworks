package org.unidal.converter.basic;

import java.lang.annotation.ElementType;
import java.lang.management.MemoryType;

import junit.framework.TestCase;

import org.unidal.converter.ConverterManager;

public class BasicConverterTest extends TestCase {
   ConverterManager m_manager = ConverterManager.getInstance();

   public void testInteger() {
      assertEquals(1, m_manager.convert("1", Integer.class));
      assertEquals(2, m_manager.convert(2, Integer.class));
      assertEquals(3, m_manager.convert(3L, Integer.class));
      assertEquals(1, m_manager.convert(true, Integer.class));
   }

   public void testLong() {
      assertEquals((long) 1, m_manager.convert("1", Long.class));
      assertEquals((long) 2, m_manager.convert(2, Long.class));
      assertEquals((long) 3, m_manager.convert(3L, Long.class));
      assertEquals((long) 1, m_manager.convert(true, Long.class));
   }

   public void testEnum() {
      assertEquals(MemoryType.HEAP, m_manager.convert("HEAP", MemoryType.class));
      assertEquals(MemoryType.NON_HEAP, m_manager.convert("NON_HEAP", MemoryType.class));

      assertEquals(ElementType.PACKAGE, m_manager.convert("PACKAGE", ElementType.class));
      assertEquals(ElementType.TYPE, m_manager.convert("TYPE", ElementType.class));
      assertEquals(ElementType.FIELD, m_manager.convert("FIELD", ElementType.class));
      assertEquals(ElementType.METHOD, m_manager.convert("METHOD", ElementType.class));
      assertEquals(ElementType.PARAMETER, m_manager.convert("PARAMETER", ElementType.class));

      assertEquals((byte) 0, m_manager.convert(ElementType.TYPE, Byte.class));
      assertEquals((byte) 1, m_manager.convert(ElementType.FIELD, Byte.class));
      assertEquals((byte) 2, m_manager.convert(ElementType.METHOD, Byte.class));

      assertEquals((char) 0, m_manager.convert(ElementType.TYPE, Character.class));
      assertEquals((char) 1, m_manager.convert(ElementType.FIELD, Character.class));
      assertEquals((char) 2, m_manager.convert(ElementType.METHOD, Character.class));

      assertEquals((short) 0, m_manager.convert(ElementType.TYPE, Short.class));
      assertEquals((short) 1, m_manager.convert(ElementType.FIELD, Short.class));
      assertEquals((short) 2, m_manager.convert(ElementType.METHOD, Short.class));

      assertEquals(0, m_manager.convert(ElementType.TYPE, Integer.class));
      assertEquals(1, m_manager.convert(ElementType.FIELD, Integer.class));
      assertEquals(2, m_manager.convert(ElementType.METHOD, Integer.class));

      assertEquals((long) 0, m_manager.convert(ElementType.TYPE, Long.class));
      assertEquals((long) 1, m_manager.convert(ElementType.FIELD, Long.class));
      assertEquals((long) 2, m_manager.convert(ElementType.METHOD, Long.class));

      assertEquals((float) 0, m_manager.convert(ElementType.TYPE, Float.class));
      assertEquals((float) 1, m_manager.convert(ElementType.FIELD, Float.class));
      assertEquals((float) 2, m_manager.convert(ElementType.METHOD, Float.class));

      assertEquals((double) 0, m_manager.convert(ElementType.TYPE, Double.class));
      assertEquals((double) 1, m_manager.convert(ElementType.FIELD, Double.class));
      assertEquals((double) 2, m_manager.convert(ElementType.METHOD, Double.class));

      assertEquals("TYPE", m_manager.convert(ElementType.TYPE, String.class));
      assertEquals("METHOD", m_manager.convert(ElementType.METHOD, String.class));
   }

   public void testObject() {
      assertEquals("1", m_manager.convert("1", Object.class));
      assertEquals(2, m_manager.convert(2, Object.class));
      assertEquals(Boolean.TRUE, m_manager.convert(true, Object.class));
   }

   public void testString() {
      assertEquals("1", m_manager.convert("1", String.class));
      assertEquals("2", m_manager.convert(2, String.class));
      assertEquals("true", m_manager.convert(true, String.class));
   }
}
