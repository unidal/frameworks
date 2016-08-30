package org.unidal.eunit.benchmark.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;
import org.unidal.eunit.benchmark.BenchmarkClassRunner;
import org.unidal.eunit.benchmark.CpuMeta;
import org.unidal.eunit.benchmark.MemoryMeta;

@RunWith(BenchmarkClassRunner.class)
public class ListAndMap {
   @MemoryMeta(loops = 10000)
   @CpuMeta(loops = 200000)
   public Object ArrayList() {
      List<Integer> list = new ArrayList<Integer>(60);

      for (int i = 0; i < 60; i++) {
         list.add(i);
      }

      return list;
   }

   @MemoryMeta(loops = 10000)
   @CpuMeta(loops = 200000)
   public Object LinkedList() {
      List<Integer> list = new LinkedList<Integer>();

      for (int i = 0; i < 60; i++) {
         list.add(i);
      }

      return list;
   }

   @MemoryMeta(loops = 10000)
   @CpuMeta(loops = 200000)
   public Object HashMap() {
      Map<Integer, Integer> map = new HashMap<Integer, Integer>();

      for (int i = 0; i < 60; i++) {
         map.put(i, i);
      }

      return map;
   }

   @MemoryMeta(loops = 10000)
   @CpuMeta(loops = 200000)
   public Object LinkedHashMap() {
      Map<Integer, Integer> map = new LinkedHashMap<Integer, Integer>();

      for (int i = 0; i < 60; i++) {
         map.put(i, i);
      }

      return map;
   }
}
