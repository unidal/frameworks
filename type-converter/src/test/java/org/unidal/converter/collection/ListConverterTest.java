package org.unidal.converter.collection;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.unidal.converter.ConverterManager;

public class ListConverterTest extends TestCase {
   ConverterManager m_manager = ConverterManager.getInstance();

   @SuppressWarnings("unchecked")
   public void testList() {
      List<Object> stringArray = (List<Object>) m_manager.convert(new String[] { "1", "2", "3" }, List.class);
      assertEquals(3, stringArray.size());
      assertEquals("1", stringArray.get(0));
      assertEquals("2", stringArray.get(1));
      assertEquals("3", stringArray.get(2));

      List<Object> fromList = (List<Object>) m_manager.convert(Arrays.asList("1", 2, "3", true), List.class);
      assertEquals(4, fromList.size());
      assertEquals("1", fromList.get(0));
      assertEquals(2, fromList.get(1));
      assertEquals("3", fromList.get(2));
      assertEquals(true, fromList.get(3));
   }
}
