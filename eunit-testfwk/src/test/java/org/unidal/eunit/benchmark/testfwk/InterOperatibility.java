package org.unidal.eunit.benchmark.testfwk;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.unidal.eunit.EunitJUnit4Runner;
import org.unidal.eunit.benchmark.BenchmarkClassRunner;
import org.unidal.eunit.benchmark.CpuMeta;

@RunWith(BenchmarkClassRunner.class)
public class InterOperatibility {
   private EunitJUnit4Runner m_runner;

   private static int s_index;

   @After
   public void after() {
      Assert.assertEquals(100010, s_index);
      s_index = 0;
   }

   @Before
   public void before() throws Throwable {
      m_runner = new EunitJUnit4Runner(Test1.class);
   }

   @CpuMeta(loops = 100)
   public void test() throws Throwable {
      m_runner.runMethod("test");
   }

   public static class Test1 {
      @Test
      public void test() {
         s_index++;
         System.out.println("test1.test");
      }
   }
}
