package org.unidal.web.jsp.function;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;

public class FormFunctionTest {
   public List<MockObject> getObjects() {
      List<MockObject> list = new ArrayList<MockObject>();

      list.add(new MockObject("First", "1"));
      list.add(new MockObject("Second", "2"));
      list.add(new MockObject("Third", "3"));
      return list;
   }

   @Test
   public void testCheckboxs() {
      String result = FormFunction.showCheckboxes("obj", getObjects(), "2", "value", "name");
      String expected = "<input type=\"checkbox\" name=\"obj\" value=\"1\" id=\"obj-1\"><label for=\"obj-1\">First</label>\r\n" + //
            "<input type=\"checkbox\" name=\"obj\" value=\"2\" id=\"obj-2\" checked><label for=\"obj-2\">Second</label>\r\n" + //
            "<input type=\"checkbox\" name=\"obj\" value=\"3\" id=\"obj-3\"><label for=\"obj-3\">Third</label>\r\n";

      Assert.assertEquals(expected, result);
   }

   @Test
   public void testOptions() {
      String result = FormFunction.showOptions(getObjects(), "2", "value", "name");
      String expected = "<option value=\"1\">First</option>\r\n" + //
            "<option value=\"2\" selected>Second</option>\r\n" + //
            "<option value=\"3\">Third</option>\r\n";

      Assert.assertEquals(expected, result);
   }

   @Test
   public void testRadios() {
      String result = FormFunction.showRadios("obj", getObjects(), "2", "value", "name");
      String expected = "<input type=\"radio\" name=\"obj\" value=\"1\" id=\"obj-1\"><label for=\"obj-1\">First</label>\r\n" + //
            "<input type=\"radio\" name=\"obj\" value=\"2\" id=\"obj-2\" checked><label for=\"obj-2\">Second</label>\r\n" + //
            "<input type=\"radio\" name=\"obj\" value=\"3\" id=\"obj-3\"><label for=\"obj-3\">Third</label>\r\n";

      Assert.assertEquals(expected, result);
   }
   
   @Test
   public void testRadio() {
      String result = FormFunction.showRadio("obj", getObjects().get(1), "2", "value", "name");
      String expected = "<input type=\"radio\" name=\"obj\" value=\"2\" id=\"obj-2\" checked><label for=\"obj-2\">Second</label>";
      
      Assert.assertEquals(expected, result);
   }

   public static class MockObject {
      private String m_name;

      private String m_value;

      public MockObject(String name, String value) {
         m_name = name;
         m_value = value;
      }

      public String getName() {
         return m_name;
      }

      public String getValue() {
         return m_value;
      }
   }
}
