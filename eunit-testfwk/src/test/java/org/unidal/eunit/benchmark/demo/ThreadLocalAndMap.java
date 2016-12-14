package org.unidal.eunit.benchmark.demo;

import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;
import org.unidal.eunit.benchmark.BenchmarkClassRunner;
import org.unidal.eunit.benchmark.CpuMeta;

@RunWith(BenchmarkClassRunner.class)
public class ThreadLocalAndMap {
   private static ThreadLocal<String> TL = new ThreadLocal<String>() {
      @Override
      protected String initialValue() {
         return "thread-local-value";
      }
   };

   @SuppressWarnings("serial")
   private static Map<String, String> MAP = new HashMap<String, String>() {
      {
         for (int i = 0; i < 10; i++) {
            put("key" + i, "map-value");
         }
      }
   };

   @CpuMeta(loops = 2000000)
   public void map() {
      for (int i = 0; i < 100; i++) {
         MAP.get("key5");
      }
   }

   @CpuMeta(loops = 2000000)
   public void threadLocal() {
      for (int i = 0; i < 100; i++) {
         TL.get();
      }
   }

   @CpuMeta(loops = 2000000)
   public void map2() {
      for (int i = 0; i < 100; i++) {
         MAP.get("map-key");
      }
   }

   @CpuMeta(loops = 2000000)
   public void threadLocal2() {
      for (int i = 0; i < 100; i++) {
         TL.get();
      }
   }
   
   @CpuMeta(loops = 2000000)
   public void map3() {
      for (int i = 0; i < 100; i++) {
         MAP.get("map-key");
      }
   }
   
   @CpuMeta(loops = 2000000)
   public void threadLocal3() {
      for (int i = 0; i < 100; i++) {
         TL.get();
      }
   }
}
