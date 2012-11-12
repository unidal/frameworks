package org.unidal.eunit.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import org.unidal.eunit.BaseJUnit4RunnerTest;
import org.unidal.eunit.EunitJUnit4Runner;
import org.unidal.eunit.EunitSuiteRunner;
import org.unidal.eunit.annotation.Groups;
import org.unidal.eunit.annotation.RunGroups;
import org.unidal.eunit.testfwk.EunitRuntimeConfig;
import org.unidal.eunit.testfwk.spi.filter.GroupFilter;

@RunWith(EunitJUnit4Runner.class)
public class GroupsHandlerTest extends BaseJUnit4RunnerTest {
   private static List<String> s_list = new ArrayList<String>();

   private void check(Class<?> testClass, String expectedOutput, String... groups) throws Exception {
      s_list.clear();

      GroupFilter filter = groups.length == 0 ? null : new GroupFilter(Arrays.asList(groups));

      try {
         EunitRuntimeConfig.INSTANCE.setGroupFilter(filter);

         if (testClass.isAnnotationPresent(SuiteClasses.class)) {
            checkSuite(testClass);
         } else {
            checkClass(testClass);
         }
      } finally {
         EunitRuntimeConfig.INSTANCE.setGroupFilter(null);
      }

      Collections.sort(s_list);
      Assert.assertEquals(expectedOutput, s_list.toString());
   }

   @Test
   public void s1() throws Exception {
      check(GroupedSuite1.class, "[g1, g2, g3, g4, g5, h2]");
      check(GroupedSuite1.class, "[g2, h2]", "P2");
      check(GroupedSuite1.class, "[g1, g4, h1, h2]", "benchmark");
      check(GroupedSuite1.class, "[g1, g3, g4, g5, h1]", "-P2");
   }

   @Test
   public void s2() throws Exception {
      check(GroupedSuite2.class, "[g2, g3, g5]");
      check(GroupedSuite2.class, "[g2, h2]", "P2");
      check(GroupedSuite2.class, "[g1, g4, h1, h2]", "benchmark");
      check(GroupedSuite2.class, "[g1, g3, g4, g5, h1]", "-P2");
   }

   @Test
   public void t1() throws Exception {
      check(GroupedTest1.class, "[g1, g2, g3, g4, g5]");
      check(GroupedTest1.class, "[]", "unknown");
      check(GroupedTest1.class, "[g1, g2, g3, g4, g5]", "Grouped");
      check(GroupedTest1.class, "[g2]", "P2");
      check(GroupedTest1.class, "[g1, g3]", "P3");
      check(GroupedTest1.class, "[g2, g4, g5]", "-P3");
      check(GroupedTest1.class, "[g1, g2, g3]", "P2", "P3");
      check(GroupedTest1.class, "[g4, g5]", "-P2", "-P3");
      check(GroupedTest1.class, "[g4]", "-P3", "benchmark");
      check(GroupedTest1.class, "[g2, g4]", "P1", "P2", "-P3", "benchmark");
   }

   @Test
   public void t2() throws Exception {
      check(GroupedTest2.class, "[h2]");
      check(GroupedTest2.class, "[h2]", "P2");
      check(GroupedTest2.class, "[h1, h2]", "benchmark");
      check(GroupedTest2.class, "[]", "grouped");
   }

   @RunWith(EunitSuiteRunner.class)
   @SuiteClasses({ GroupedTest1.class, GroupedTest2.class })
   public static class GroupedSuite1 {
   }

   @RunWith(EunitSuiteRunner.class)
   @SuiteClasses({ GroupedTest1.class, GroupedTest2.class })
   @RunGroups(exclude = "benchmark")
   public static class GroupedSuite2 {
   }

   @RunWith(EunitJUnit4Runner.class)
   @Groups("Grouped")
   public static class GroupedTest1 {
      @Test
      @Groups({ "P3", "benchmark" })
      public void g1() {
         s_list.add("g1");
      }

      @Test
      @Groups({ "P2" })
      public void g2() {
         s_list.add("g2");
      }

      @Test
      @Groups({ "P3" })
      public void g3() {
         s_list.add("g3");
      }

      @Test
      @Groups({ "P4", "benchmark" })
      public void g4() {
         s_list.add("g4");
      }

      @Test
      public void g5() {
         s_list.add("g5");
      }
   }

   @RunWith(EunitJUnit4Runner.class)
   @Groups("benchmark")
   @RunGroups(include = "P2")
   public static class GroupedTest2 {
      @Test
      @Groups({ "P3" })
      public void h1() {
         s_list.add("h1");
      }

      @Test
      @Groups({ "P2" })
      public void h2() {
         s_list.add("h2");
      }
   }
}
