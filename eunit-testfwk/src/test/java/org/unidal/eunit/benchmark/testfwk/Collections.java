package org.unidal.eunit.benchmark.testfwk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.runner.RunWith;
import org.unidal.eunit.annotation.Groups;
import org.unidal.eunit.benchmark.BenchmarkClassRunner;
import org.unidal.eunit.benchmark.CpuMeta;
import org.unidal.eunit.benchmark.MemoryMeta;

@RunWith(BenchmarkClassRunner.class)
@Groups("benchmark")
public class Collections {
   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Object newHashMap() {
      HashMap<String, String> map = new HashMap<String, String>();

      map.put("1", "a");
      map.put("2", "b");
      map.put("3", "c");

      return map;
   }

   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Object newLinkedHashMap() {
      LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

      map.put("1", "a");
      map.put("2", "b");
      map.put("3", "c");

      return map;
   }

   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Object newArrayList() {
      return new ArrayList<String>();
   }

   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Object newArrayListSize1() {
      return new ArrayList<String>(2);
   }

   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Object newArrayListSize2() {
      return new ArrayList<String>(2);
   }

   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Object newArrayListSize3() {
      return new ArrayList<String>(3);
   }

   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Object newArrayListSize4() {
      return new ArrayList<String>(4);
   }

   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Object newArrayListSize8() {
      return new ArrayList<String>(8);
   }

   @CpuMeta(loops = 100000)
   @MemoryMeta(loops = 100000)
   public Object newArrayListSize16() {
      return new ArrayList<String>(16);
   }
}
