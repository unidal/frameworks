package org.unidal.converter.collection;

import java.util.Arrays;

import junit.framework.TestCase;

import org.unidal.converter.ConverterManager;

public class ArrayConverterTest extends TestCase {
   ConverterManager m_manager = ConverterManager.getInstance();

   @SuppressWarnings("unchecked")
   public void testArray() {
      Integer[] integerArray = (Integer[]) m_manager.convert(new String[] { "1", "2", "3" }, Integer[].class);

      assertEquals(3, integerArray.length);
      assertEquals(new Integer(1), integerArray[0]);
      assertEquals(new Integer(2), integerArray[1]);
      assertEquals(new Integer(3), integerArray[2]);

      int[] intArray = (int[]) m_manager.convert(new String[] { "1", "2", "3" }, int[].class);

      assertEquals(3, intArray.length);
      assertEquals(1, intArray[0]);
      assertEquals(2, intArray[1]);
      assertEquals(3, intArray[2]);

      Long[] fromList = (Long[]) m_manager.convert(Arrays.asList("1", "2", "3", true), Long[].class);

      assertEquals(4, fromList.length);
      assertEquals(new Long(1), fromList[0]);
      assertEquals(new Long(2), fromList[1]);
      assertEquals(new Long(3), fromList[2]);
      assertEquals(new Long(1), fromList[3]);
   }
}
