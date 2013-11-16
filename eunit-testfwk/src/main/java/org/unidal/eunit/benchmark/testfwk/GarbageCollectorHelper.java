package org.unidal.eunit.benchmark.testfwk;

import java.io.File;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.text.ParseException;

import org.unidal.helper.Files;

public enum GarbageCollectorHelper {
   INSTANCE;

   public long getGcAmount() {
      String gclog = System.getProperty("gclog");

      if (gclog != null) {
         try {
            String content = Files.forIO().readFrom(new File(gclog), "utf-8");

            return new GcLogParser().getGcAmount(content);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      return 0;
   }

   public long getGcCount() {
      long count = 0;

      for (GarbageCollectorMXBean mxbean : ManagementFactory.getGarbageCollectorMXBeans()) {
         if (mxbean.isValid()) {
            count += mxbean.getCollectionCount();
         }
      }

      return count;
   }

   public long getGcTime() {
      long time = 0;

      for (GarbageCollectorMXBean mxbean : ManagementFactory.getGarbageCollectorMXBeans()) {
         if (mxbean.isValid()) {
            time += mxbean.getCollectionTime();
         }
      }

      return time;
   }

   public void runGC() {
      // It helps to call Runtime.gc()
      // using several method calls
      for (int r = 0; r < 10; r++) {
         runGC0();
      }
   }

   private void runGC0() {
      long usedMem1 = usedMemory();
      long usedMem2 = Long.MAX_VALUE;

      // run finalization until no more garbage memory could be collected
      for (int i = 0; (usedMem1 < usedMem2) && (i < 500); i++) {
         Runtime.getRuntime().runFinalization();
         Runtime.getRuntime().gc();

         // allow other threads to execute
         Thread.yield();

         usedMem2 = usedMem1;
         usedMem1 = usedMemory();
      }
   }

   public long usedMemory() {
      return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
   }

   public static class GcLogParser {
      // 2.123: [GC 2485K->1566K(5056K), 0.0007150 secs]
      // 2.123: [Full GC 2485K->1566K(5056K), 0.0007150 secs]
      private static MessageFormat GC = new MessageFormat("{9}: [{8}GC {0,number}K->{1,number}K({7}K), {6} secs]");

      // 2.456: [GC [DefNew: 64575K->959K(64576K), 0.0457646 secs]
      // 196016K->133633K(261184K), 0.0459067 secs]]
      // 111.042: [GC 111.042: [DefNew: 8128K->8128K(8128K), 0.0000505
      // secs]111.042: [Tenured: 18154K->2311K(24576K), 0.1290354 secs]
      // 26282K->2311K(32704K), 0.1293306 secs]
      private static MessageFormat WITH_DETAILS = new MessageFormat("{9}: [{8} [{5}] {0,number}K->{1,number}K({7}K), {6} secs]");

      public synchronized long getGcAmount(String content) throws IOException {
         int offset = 0;
         int pos = content.indexOf('\n', offset);
         long amount = 0;

         while (pos >= 0) {
            String line;

            if (pos >= 1 && content.charAt(pos - 1) == '\r') {
               line = content.substring(offset, pos - 1);
            } else {
               line = content.substring(offset, pos);
            }

            offset = pos + 1;

            if (line.length() == 0) {
               continue;
            }

            Object[] parts = null;

            if (parts == null) {
               try {
                  parts = GC.parse(line);
               } catch (ParseException e) {
                  // ignore it
               }
            }

            if (parts == null) {
               try {
                  parts = WITH_DETAILS.parse(line);
               } catch (ParseException e) {
                  // ignore it
               }
            }

            if (parts == null) {
               System.err.println("Unknown GC line: " + line);
            } else {
               int index = 0;
               Number before = (Number) parts[index++];
               Number after = (Number) parts[index++];

               amount += (before.longValue() - after.longValue()) * 1000L;
            }

            pos = content.indexOf('\n', offset);
         }

         return amount;
      }
   }
}