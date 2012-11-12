package org.unidal.eunit.testfwk;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Stack;

import org.junit.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.unidal.eunit.EunitJUnit4Runner;

@RunWith(EunitJUnit4Runner.class)
public class JUnitTest {
   private static Stack<StringBuilder> s_stack = new Stack<StringBuilder>();

   @BeforeClass
   public static void beforeClass() {
      s_stack.clear();
      key("beforeClass");
   }

   private static void key(String key) {
      StringBuilder sb = new StringBuilder(32);

      sb.append(key);
      s_stack.push(sb);
   }

   private static void out(String message) {
      StringBuilder sb = s_stack.peek();

      sb.append(message);
   }

   @Before
   public void before() {
      key("case:");
      out("before");
   }

   @After
   public void after() {
      out("after");
   }

   @AfterClass
   public static void afterClass() {
      key("afterClass");

      int size = s_stack.size();
      MessageFormat format = new MessageFormat("case:before#{0}#after");

      Assert.assertEquals("beforeClass", s_stack.get(0).toString());

      for (int i = 1; i < size - 1; i++) {
         try {
            format.parse(s_stack.get(i).toString());
         } catch (ParseException e) {
            e.printStackTrace();
            System.out.println(s_stack.get(i).toString());
            Assert.fail("Methods before and/or after are not called correctly! " + s_stack);
         }
      }

      Assert.assertEquals("afterClass", s_stack.get(size - 1).toString());
   }

   @Test
   public void test1() {
      out("#one#");
   }

   @Test
   public void test2() {
      out("#two#");
   }

   @Test
   @Ignore
   public void test3() {
      out("#three#");
   }

   public void test4() {
      out("#four#");
   }
}
