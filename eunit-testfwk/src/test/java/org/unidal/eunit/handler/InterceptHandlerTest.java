package org.unidal.eunit.handler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.unidal.eunit.BaseJUnit4RunnerTest;
import org.unidal.eunit.EunitJUnit4Runner;
import org.unidal.eunit.annotation.Intercept;

@RunWith(EunitJUnit4Runner.class)
public class InterceptHandlerTest extends BaseJUnit4RunnerTest {
   private static List<String> s_list = new ArrayList<String>();

   private void check(String methodName, String expectedOutput) throws Exception {
      s_list.clear();
      checkMethod(NormalTest.class, methodName);
      Assert.assertEquals(expectedOutput, s_list.toString());
   }

   @Test
   public void normal() throws Exception {
      check("b1", "[b1]");
      check("b2", "[before, b2]");
      check("b3", "[b3, after]");
      check("b4", "[before, b4, after]");
      check("b5", "[b5, onError(Exception)]");
      check("b6", "[before, b6, onError(Exception)]");
      check("b7", "[b7, onError(Exception), after]");
      check("b8", "[before, b8, onError(Exception), after]");
   }

   public static class NormalTest {
      protected void after() {
         s_list.add("after");
      }

      @Test
      @Intercept
      public void b1() {
         s_list.add("b1");
      }

      @Test
      @Intercept(beforeMethod = "before")
      public void b2() {
         s_list.add("b2");
      }

      @Test
      @Intercept(afterMethod = "after")
      public void b3() {
         s_list.add("b3");
      }

      @Test
      @Intercept(beforeMethod = "before", afterMethod = "after")
      public void b4() {
         s_list.add("b4");
      }

      @Test
      @Intercept(onErrorMethod = "onError")
      public void b5() throws Exception {
         s_list.add("b5");
         throw new Exception();
      }

      @Test
      @Intercept(beforeMethod = "before", onErrorMethod = "onError")
      public void b6() throws Exception {
         s_list.add("b6");
         throw new Exception();
      }

      @Test
      @Intercept(afterMethod = "after", onErrorMethod = "onError")
      public void b7() throws Exception {
         s_list.add("b7");
         throw new Exception();
      }

      @Test
      @Intercept(beforeMethod = "before", afterMethod = "after", onErrorMethod = "onError")
      public void b8() throws Exception {
         s_list.add("b8");
         throw new Exception();
      }

      protected void before() {
         s_list.add("before");
      }

      protected void onError(Throwable e) {
         s_list.add(String.format("onError(%s)", e.getClass().getSimpleName()));
      }
   }
}
