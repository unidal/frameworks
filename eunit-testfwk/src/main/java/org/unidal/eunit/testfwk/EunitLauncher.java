package org.unidal.eunit.testfwk;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.unidal.helper.Files;

public class EunitLauncher {
   private static double M = 1024 * 1024;

   private Class<?> m_testClass;

   private File m_gclog;

   protected static int m(long size) {
      return (int) Math.round(size / M);
   }

   public static void main(String[] args) {
      if (args.length == 1) {
         String className = args[0];

         try {
            new EunitLauncher(Class.forName(className)).enableGclog(new File("gc.log")).run();
         } catch (Exception e) {
            e.printStackTrace();
         }
      } else {
         System.out.println(String.format("java %s <test-class>", EunitLauncher.class.getName()));
      }
   }

   public EunitLauncher(Class<?> testClass) {
      m_testClass = testClass;
   }

   protected List<String> buildArgsForSunJDKAtWin() {
      List<String> list = new ArrayList<String>();
      String javaHome = System.getProperty("java.home");
      String classpath = System.getProperty("java.class.path");
      long initSize = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getInit();

      list.add(javaHome + "\\bin\\java.exe");

      if (initSize > 0) {
         list.add(String.format("-Xms%sm", m(initSize)));
      }

      if (m_gclog != null) {
         Files.forDir().delete(m_gclog);
         list.add("-Xloggc:" + m_gclog);
         list.add("-Dgclog=" + m_gclog);
      }

      list.add("-Dfork=true");
      list.add("-cp");
      list.add(classpath);
      list.add(JUnitCore.class.getName());
      list.add(m_testClass.getName());

      return list;
   }

   public EunitLauncher enableGclog(File gclog) {
      m_gclog = gclog;
      return this;
   }

   protected int redirectOutput(Process process, IHandler handler) throws IOException {
      BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      boolean inEOF = false;
      boolean errEOF = false;

      while (!inEOF && !errEOF) {
         while (!inEOF && in.ready()) {
            String line = in.readLine();

            if (line == null) {
               inEOF = true;
               in.close();
               break;
            } else if (handler != null) {
               handler.onMessage(line);
            }
         }

         while (!errEOF && err.ready()) {
            String line = err.readLine();

            if (line == null) {
               errEOF = true;
               err.close();
               break;
            } else if (handler != null) {
               handler.onError(line);
            }
         }

         try {
            return process.exitValue();
         } catch (Exception e) {
            // ignore it
         }

         try {
            Thread.sleep(10);
         } catch (InterruptedException e) {
            break;
         }
      }

      return -1;
   }

   public void run() throws IOException {
      if (m_gclog == null) { // no need to fork a process
         JUnitCore junit = new JUnitCore();

         junit.run(m_testClass);
      } else {
         String userDir = System.getProperty("user.dir");
         ProcessBuilder builder = new ProcessBuilder();

         builder.directory(new File(userDir));
         builder.command(buildArgsForSunJDKAtWin());

         Process process = builder.start();

         redirectOutput(process, ConsoleHandler.INSTANCE);
      }
   }

   public enum ConsoleHandler implements IHandler {
      INSTANCE;

      @Override
      public void onError(String error) {
         System.err.println(error);
      }

      @Override
      public void onMessage(String message) {
         System.out.println(message);
      }
   }

   public static interface IHandler {
      public void onError(String error);

      public void onMessage(String message);
   }
}
