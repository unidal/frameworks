package org.unidal.web.mvc;

import java.util.LinkedHashMap;

import org.junit.Assert;

import org.junit.Test;

public class ActionContextTest {
   @SuppressWarnings("serial")
   @Test
   public void testJsonPairs() {
      check("{\"code\": 0, \"message\": \"successfull\"}", "code", 0, "message", "successfull");
      check("{\"array\": [1,4.3,true,\"abc\"]}", "array", new Object[] { 1, 4.3, true, "abc" });
      check("{\"map\": {\"a\":1,\"b\":4.3,\"c\":true,\"d\":\"abc\"}}", "map", new LinkedHashMap<String, Object>() {
         {
            put("a", 1);
            put("b", 4.3);
            put("c", true);
            put("d", "abc");
         }
      });

      // environment related, just for manually check
      // check("{\"exception\": {\"name\":\"java.lang.Exception\",\"stackTrace\":\"...\"}}", "exception", new Exception("aha"));
   }

   @SuppressWarnings("rawtypes")
   private void check(String expected, Object... pairs) {
      ActionContext ctx = new ActionContext() {
      };
      String actual = ctx.toJson(pairs);

      System.out.println(actual);
      Assert.assertEquals(expected, actual);
   }
}
