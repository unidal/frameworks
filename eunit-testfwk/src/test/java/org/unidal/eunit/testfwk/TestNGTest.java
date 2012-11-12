package org.unidal.eunit.testfwk;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Stack;

import org.junit.Assert;

import org.junit.runner.RunWith;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.unidal.eunit.EunitTestNGRunner;
import org.unidal.eunit.annotation.testng.ConfigurationFile;

@RunWith(EunitTestNGRunner.class)
@ConfigurationFile("TestNGTest.xml")
public class TestNGTest {
   private static Stack<StringBuilder> s_stack = new Stack<StringBuilder>();

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
            Assert.fail("Methods before and/or after are not called correctly!");
         }
      }

      Assert.assertEquals("afterClass", s_stack.get(size - 1).toString());
   }

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

   @AfterMethod
   public void after() {
      out("after");
   }

   @BeforeMethod
   public void before() {
      key("case:");
      out("before");
   }

   @DataProvider(name = "dataProvider")
   protected Object[][] provideData() {
      Object[][] rows = new Object[10][2];

      for (int i = 0; i < 10; i++) {
         rows[i][0] = "name" + i;
         rows[i][1] = (i % 2 == 0 ? true : false);
      }

      return rows;
   }

   @Test
   public void test1() {
      out("#one#");
   }

   @Test
   public void test2() {
      out("#two#");
   }

   @Test(enabled = false)
   public void test3() {
      out("#three#");
   }

   @Test(dataProvider = "dataProvider")
   public void test4(String first, Boolean second) {
      out("#four#");
   }

   public void testx() {
      out("#x#");
   }
}
