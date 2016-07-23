package org.unidal.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

public class StringizersTest {
   private void check(Object obj, String expected) {
      String actual = Stringizers.forJson().compact().from(obj);

      Assert.assertEquals(expected, actual);
   }

   private void checkLimit(Object obj, String expected) {
      String actual = Stringizers.forJson().compact().from(obj, 60, 5);

      Assert.assertEquals(expected, actual);
   }

   private Map<String, Object> map(Object forth) {
      Map<String, Object> map = new LinkedHashMap<String, Object>();

      map.put("first", new Pojo(1, "x"));
      map.put("second", new Pojo(2, "y"));
      map.put("third", new Pojo(3, "z"));
      map.put("forth", forth);

      return map;
   }

   @Test
   public void testBreakLoop() {
      check(new Loop1("x0").addChild(new Loop1("x10").addChild(new Loop1("x210")).addChild(new Loop1("x310")))
            .addChild(new Loop1("x11")),
            "{\"children\":[{\"children\":[{\"children\":[],\"data\":\"x210\",\"parent\":{}},{\"children\":{},\"data\":\"x310\",\"parent\":{}}],\"data\":\"x10\",\"parent\":{}},{\"children\":{},\"data\":\"x11\",\"parent\":{}}],\"data\":\"x0\"}");
   }

   @Test
   public void testLengthLimiter() {
      checkLimit("xyz", "\"xyz\"");
      checkLimit("xyzabc", "\"x...c\"");
      checkLimit("123456789", "\"1...9\"");
      checkLimit(map("hello, world"), "{\"first\":{\"x\":1,\"y\":\"x\"},\"second\":{\"x\":2,\"y\":\"y\"},\"third...");
      checkLimit(map(map(map("hello, world"))), //
            "{\"first\":{\"x\":1,\"y\":\"x\"},\"second\":{\"x\":2,\"y\":\"y\"},\"third...");
   }

   @Test
   public void testNotSame() {
      TimeZone.setDefault(TimeZone.getTimeZone("GMT+08"));

      check(new Date(1330079278861L), "\"2012-02-24 18:27:58\"");
      check(Date.class, "\"class java.util.Date\"");
      check(new Object[] { "x", "y", new Object[] { 1, 2.3, true, map(null) } }, //
            "[\"x\",\"y\",[1,2.3,true,{\"first\":{\"x\":1,\"y\":\"x\"},\"second\":{\"x\":2,\"y\":\"y\"},\"third\":{\"x\":3,\"y\":\"z\"}}]]");
   }

   @Test
   public void testObjectWithToString() {
      check(new PojoWithToString(2, "second"), "\"Pojo[2,second]\"");
   }

   @Test
   public void testSame() {
      check(null, "");
      check(1, "1");
      check(1.2, "1.2");
      check(true, "true");
      check("xyz", "\"xyz\"");
      check(new String[] { "x", "y" }, "[\"x\",\"y\"]");
      check(new Pojo(3, null), "{\"x\":3}");
      check(new Pojo(3, "a"), "{\"x\":3,\"y\":\"a\"}");
      check(map(null),
            "{\"first\":{\"x\":1,\"y\":\"x\"},\"second\":{\"x\":2,\"y\":\"y\"},\"third\":{\"x\":3,\"y\":\"z\"}}");
      check(map(map(map(null))), //
            "{\"first\":{\"x\":1,\"y\":\"x\"},\"second\":{\"x\":2,\"y\":\"y\"},\"third\":{\"x\":3,\"y\":\"z\"},\"forth\":{\"first\":{\"x\":1,\"y\":\"x\"},\"second\":{\"x\":2,\"y\":\"y\"},\"third\":{\"x\":3,\"y\":\"z\"},\"forth\":{\"first\":{\"x\":1,\"y\":\"x\"},\"second\":{\"x\":2,\"y\":\"y\"},\"third\":{\"x\":3,\"y\":\"z\"}}}}");
   }

   public static class Loop1 {
      private Loop1 m_parent;

      private Object m_data;

      private List<Loop1> m_children = new ArrayList<Loop1>();

      public Loop1(Object data) {
         m_data = data;
      }

      public Loop1 addChild(Loop1 loop) {
         loop.m_parent = this;
         m_children.add(loop);
         return this;
      }

      public List<Loop1> getChildren() {
         return m_children;
      }

      public Object getData() {
         return m_data;
      }

      public Loop1 getParent() {
         return m_parent;
      }
   }

   public static class Pojo {
      private int x;

      private String y;

      public Pojo(int x, String y) {
         this.x = x;
         this.y = y;
      }

      public int getX() {
         return x;
      }

      public String getY() {
         return y;
      }
   }

   public static class PojoWithToString {
      private int x;

      private String y;

      public PojoWithToString(int x, String y) {
         this.x = x;
         this.y = y;
      }

      public int getX() {
         return x;
      }

      public String getY() {
         return y;
      }

      @Override
      public String toString() {
         return "Pojo[" + x + "," + y + "]";
      }
   }
}
