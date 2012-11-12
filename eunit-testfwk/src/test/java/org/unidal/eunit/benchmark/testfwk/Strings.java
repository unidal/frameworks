package org.unidal.eunit.benchmark.testfwk;

import org.junit.runner.RunWith;

import org.unidal.eunit.annotation.Groups;
import org.unidal.eunit.benchmark.BenchmarkClassRunner;
import org.unidal.eunit.benchmark.CpuMeta;
import org.unidal.eunit.benchmark.MemoryMeta;

@RunWith(BenchmarkClassRunner.class)
@Groups("benchmark")
public class Strings {
   private String m_id = "Kitty";

   @CpuMeta
   @MemoryMeta
   public String stringFormat() {
      return String.format("Hello, %s!", m_id);
   }
   
   @CpuMeta(loops = 10000000)
   @MemoryMeta
   public String concatenation() {
      return "Hello, " + m_id + "!";
   }
   
   @CpuMeta(loops = 10000000)
   @MemoryMeta
   public String concatenation2() {
   	String tmp = "Hello, " + m_id;
   	
		return tmp + "!";
   }

   @CpuMeta(loops = 10000000)
   @MemoryMeta
   public String constant() {
      return "Hello, Kitty!";
   }
}
