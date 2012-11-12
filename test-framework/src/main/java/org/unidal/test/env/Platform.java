package org.unidal.test.env;

import java.io.File;

public class Platform {
   public static boolean isWindows2K3() {
      final String os = System.getProperty("os.name");

      return os != null && os.startsWith("Windows Server 2003");
   }

   public static boolean isWindows() {
      final String os = System.getProperty("os.name");

      return os != null && os.startsWith("Windows");
   }

   public static boolean isMac() {
      final String os = System.getProperty("os.name");

      return os != null && os.indexOf("Mac") >= 0;
   }

   public static boolean isLinux() {
      final String os = System.getProperty("os.name");

      return os != null && os.indexOf("Linux") >= 0;
   }

   public static File getProgramFile(String relativePath) {
      File dir = new File(isWindows2K3() ? "c:/program files (x86)" : "c:/program files");
      File file = new File(dir, relativePath);

      return file;
   }
}
