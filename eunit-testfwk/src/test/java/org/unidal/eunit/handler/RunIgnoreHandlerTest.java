package org.unidal.eunit.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import org.unidal.eunit.BaseJUnit4RunnerTest;
import org.unidal.eunit.EunitJUnit4Runner;
import org.unidal.eunit.EunitSuiteRunner;
import org.unidal.eunit.annotation.RunIgnore;

@RunWith(EunitJUnit4Runner.class)
public class RunIgnoreHandlerTest extends BaseJUnit4RunnerTest {
   private static List<String> s_list = new ArrayList<String>();

   private void check(Class<?> testClass, String expectedOutput) throws Exception {
      s_list.clear();

      if (testClass.isAnnotationPresent(SuiteClasses.class)) {
         checkSuite(testClass);
      } else {
         checkClass(testClass);
      }

      Collections.sort(s_list);
      Assert.assertEquals(expectedOutput, s_list.toString());
   }

   @Test
   public void s1() throws Exception {
      check(IgnoredSuite1.class, "[g2, g3, h2]");
   }

   @Test
   @Ignore
   public void s2() throws Exception {
      check(IgnoredSuite2.class, "[g1, g2, g3, h1, h2]");
   }

   @Test
   public void t1() throws Exception {
      check(IgnoredTest1.class, "[g2, g3]");
   }

   @Test
   public void t2() throws Exception {
      check(IgnoredTest2.class, "[h2]");
   }

   @RunWith(EunitSuiteRunner.class)
   @SuiteClasses({ IgnoredTest1.class, IgnoredTest2.class })
   @RunIgnore
   public static class IgnoredSuite1 {
   }

   @RunWith(EunitSuiteRunner.class)
   @SuiteClasses({ IgnoredTest1.class, IgnoredTest2.class })
   @RunIgnore(runAll = true)
   public static class IgnoredSuite2 {
   }

   @RunWith(EunitJUnit4Runner.class)
   public static class IgnoredTest1 {
      @Test
      @Ignore
      public void g1() {
         s_list.add("g1");
      }

      @Test
      public void g2() {
         s_list.add("g2");
      }

      @Test
      public void g3() {
         s_list.add("g3");
      }
   }

   @RunWith(EunitJUnit4Runner.class)
   @RunIgnore
   public static class IgnoredTest2 {
      @Test
      public void h1() {
         s_list.add("h1");
      }

      @Test
      @Ignore
      public void h2() {
         s_list.add("h2");
      }
   }
}
