package org.unidal.helper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Scanners.DirMatcher;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.helper.Scanners.ResourceMatcher;

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

   @Test
   public void testResourceFileForDown() throws IOException {
      final List<String> all = new ArrayList<String>();
      List<URL> result = Scanners.forResource().scan("META-INF/plexus", new ResourceMatcher() {
         @Override
         public Direction matches(URL base, String path) {
            Assert.assertEquals(true, base.getPath().endsWith("META-INF/plexus"));

            File file = new File(base.getFile(), path);

            if (base.getProtocol().equals("file")) { // file only
               all.add(path);
               Assert.assertEquals(true, file.exists());
            }

            return Direction.DOWN;
         }
      });

      Assert.assertEquals(true, all.contains("components-foundation-service.xml"));
      Assert.assertEquals(0, result.size());
   }
}
