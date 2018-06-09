package org.unidal.helper;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Scanners.DirMatcher;
import org.unidal.helper.Scanners.FileMatcher;

public class ScannersTest {
   @Test
   public void testDirForDirMatched() {
      final String clazz = getClass().getSimpleName() + ".class";
      final URL url = getClass().getResource(clazz);
      final File unidalDir = new File(url.getPath()).getParentFile().getParentFile();
      final List<String> all = new ArrayList<String>();
      List<File> result = Scanners.forDir().scan(unidalDir, new DirMatcher() {
         @Override
         public Direction matches(File base, String path) {
            Assert.assertEquals(unidalDir, base);

            if (path.equals("helper")) {
               all.add(path);
               return Direction.MATCHED;
            }

            return Direction.DOWN;
         }
      });

      Assert.assertEquals(1, all.size());
      Assert.assertEquals(1, result.size());
   }

   @Test
   public void testDirForDown() {
      final String clazz = getClass().getSimpleName() + ".class";
      final URL url = getClass().getResource(clazz);
      final File unidalDir = new File(url.getPath()).getParentFile().getParentFile();
      final List<String> all = new ArrayList<String>();
      List<File> result = Scanners.forDir().scan(unidalDir, new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            Assert.assertEquals(unidalDir, base);

            all.add(path);
            return Direction.DOWN;
         }
      });

      Assert.assertEquals(true, all.contains("helper"));
      Assert.assertEquals(true, all.contains("helper/" + clazz));
      Assert.assertEquals(0, result.size());
   }

   @Test
   public void testDirForFileMatched() {
      final String clazz = getClass().getSimpleName() + ".class";
      final URL url = getClass().getResource(clazz);
      final File unidalDir = new File(url.getPath()).getParentFile().getParentFile();
      final List<String> all = new ArrayList<String>();
      List<File> result = Scanners.forDir().scan(unidalDir, new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            Assert.assertEquals(unidalDir, base);

            if (path.endsWith(".class")) {
               all.add(path);
               return Direction.MATCHED;
            }

            return Direction.DOWN;
         }
      });

      Assert.assertEquals(true, all.size() >= 2);
      Assert.assertEquals(true, result.size() >= 2);
   }

   @Test
   public void testDirForNext() {
      final String clazz = getClass().getSimpleName() + ".class";
      final URL url = getClass().getResource(clazz);
      final File unidalDir = new File(url.getPath()).getParentFile().getParentFile();
      final List<String> all = new ArrayList<String>();
      List<File> result = Scanners.forDir().scan(unidalDir, new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            Assert.assertEquals(unidalDir, base);

            all.add(path);
            return Direction.NEXT;
         }
      });

      Assert.assertEquals(true, all.contains("helper"));
      Assert.assertEquals(0, result.size());
   }
}
