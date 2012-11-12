package org.unidal.eunit.benchmark.demo;

import org.junit.runner.RunWith;
import org.unidal.eunit.benchmark.BenchmarkClassRunner;
import org.unidal.eunit.benchmark.CpuMeta;

@RunWith(BenchmarkClassRunner.class)
public class CharAccess {
   private static char[] array = new char[8192 * 11];

   private static StringBuilder sb = new StringBuilder(8192 * 11);

   private static String str;

   static {
      for (char ch : array) {
         sb.append(ch);
      }

      str = sb.toString();
   }
   
   @CpuMeta(loops = 100000000)
   public char testStringBuilder() {
   	char ch = ' ';
   	for (int i = 0, l = sb.length(); i < l; i++) {
   		ch = sb.charAt(i);
   	}
   	return ch;
   }

   @CpuMeta(loops = 100000000)
   public char testArray() {
      char ch = ' ';
      for (int i = 0, l = array.length; i < l; i++) {
         ch = array[i];
      }
      return ch;
   }

   @CpuMeta(loops = 100000000)
   public char testString() {
      char ch = ' ';
      for (int i = 0, l = str.length(); i < l; i++) {
         ch = str.charAt(i);
      }
      return ch;
   }
}
