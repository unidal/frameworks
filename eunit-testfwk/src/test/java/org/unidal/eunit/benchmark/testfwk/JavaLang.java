package org.unidal.eunit.benchmark.testfwk;

import org.junit.runner.RunWith;

import org.unidal.eunit.annotation.Groups;
import org.unidal.eunit.benchmark.BenchmarkClassRunner;
import org.unidal.eunit.benchmark.CpuMeta;
import org.unidal.eunit.benchmark.MemoryMeta;

@RunWith(BenchmarkClassRunner.class)
@Groups("benchmark")
public class JavaLang {
   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Error newError() {
      return new Error();
   }

   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Exception newException() {
      return new Exception();
   }

   @CpuMeta(loops = 10000000)
   @MemoryMeta
   public Object newObject() {
      return new Object();
   }

   @CpuMeta(loops = 10000000)
   @MemoryMeta
   public void noop() {
   }

   @CpuMeta(loops = 10000000)
   @MemoryMeta
   public String newString() {
      return new String();
   }
   
   @CpuMeta(loops = 10000000)
   @MemoryMeta
   public StringBuilder newStringBuilder() {
   	return new StringBuilder();
   }

   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Thread newThread() {
      return new Thread();
   }

   @CpuMeta(loops = 10000000)
   @MemoryMeta
   public ThreadLocal<Object> newThreadLocal() {
      return new ThreadLocal<Object>();
   }

   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Exception throwException() {
      try {
         throw new Exception();
      } catch (Exception e) {
         return e;
      }
   }
}
