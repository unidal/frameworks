package org.unidal.eunit.benchmark.testfwk;

import java.io.IOException;

import org.junit.Assert;

import org.junit.Test;

import org.unidal.eunit.benchmark.testfwk.GarbageCollectorHelper.GcLogParser;

public class GarbageCollectorHelperTest {
   @Test
   public void testGcAmount() throws IOException {
      String content = "0.315: [GC 4590K->496K(125632K), 0.0016484 secs]\r\n" + //
            "0.317: [Full GC 496K->404K(125632K), 0.0094975 secs]\r\n" + //
            "0.327: [GC 1060K->404K(125632K), 0.0071055 secs]\r\n" + //
            "0.335: [Full GC 404K->404K(125632K), 0.0101381 secs]\r\n" + //
            "0.345: [GC 1060K->404K(125632K), 0.0003045 secs]\r\n" + //
            "0.346: [Full GC 404K->404K(125632K), 0.0089652 secs]\r\n";

      GcLogParser parser = new GcLogParser();
      long amount = parser.getGcAmount(content);

      Assert.assertEquals(5498000L, amount);
   }
}
