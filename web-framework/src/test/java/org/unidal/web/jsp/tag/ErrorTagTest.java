package org.unidal.web.jsp.tag;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.web.mvc.ErrorObject;

public class ErrorTagTest {
   @Test
   public void testCode() {
      ErrorTag tag = new ErrorTag();

      String result = tag.processBody("Code is ${code}.", new ErrorObject("id"));
      String expected = "Code is id.";

      Assert.assertEquals(expected, result);
   }
}
