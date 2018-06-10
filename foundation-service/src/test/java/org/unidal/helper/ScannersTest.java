package org.unidal.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Scanners.DirMatcher;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.helper.Scanners.ResourceMatcher;

public class ScannersTest {
   @SuppressWarnings("unchecked")
   private void addJarToClasspath(URL url) throws MalformedURLException {
      ClassLoader cl = getClass().getClassLoader();

      if (cl instanceof URLClassLoader) {
         Object ucp = Reflects.forField().getDeclaredFieldValue(cl, "ucp");
         Object path = Reflects.forField().getDeclaredFieldValue(ucp, "path");

         if (path instanceof List) {
            ((List<URL>) path).add(url);
         }

         List<URL> urls = Arrays.asList(((URLClassLoader) cl).getURLs());

         Assert.assertEquals(true, urls.contains(url)); // make sure it works
      }
   }

   private File makeFatJar() throws IOException {
      File jarFile = File.createTempFile("temp-", ".jar");
      JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile));
      Enumeration<URL> e = getClass().getClassLoader().getResources("META-INF/maven");

      while (e.hasMoreElements()) {
         URL url = e.nextElement();
         String path = url.getPath();

         if (path.startsWith("file:")) {
            int pos = path.lastIndexOf("!/");
            String file = path.substring(5, pos);

            jos.putNextEntry(new ZipEntry(file.substring(1)));
            jos.write(Files.forIO().readFrom(new FileInputStream(file)));
            jos.closeEntry();
         }
      }

      jos.flush();
      jos.close();
      jarFile.deleteOnExit();

      return jarFile;
   }

   @SuppressWarnings("unchecked")
   private void removeJarFromClasspath(URL url) throws MalformedURLException {
      ClassLoader cl = getClass().getClassLoader();

      if (cl instanceof URLClassLoader) {
         Object ucp = Reflects.forField().getDeclaredFieldValue(cl, "ucp");
         Object path = Reflects.forField().getDeclaredFieldValue(ucp, "path");

         if (path instanceof List) {
            ((List<URL>) path).remove(url);
         }

         List<URL> urls = Arrays.asList(((URLClassLoader) cl).getURLs());

         Assert.assertEquals(false, urls.contains(url)); // make sure it works
      }
   }

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
   public void testResourceFatJarForMatched() throws IOException {
      File jarFile = makeFatJar();
      URL jarUrl = jarFile.toURI().toURL();

      addJarToClasspath(jarUrl);

      final List<String> all = new ArrayList<String>();
      List<URL> result = Scanners.forResource().scan("META-INF/maven", new ResourceMatcher() {
         @Override
         public Direction matches(URL base, String path) {
            Assert.assertEquals(true, base.getPath().endsWith("!/META-INF/maven"));

            if (base.getProtocol().equals("jar")) { // jar only
               all.add(path);

               try {
                  URL url = new URL(base.toExternalForm() + "/" + path);
                  InputStream in = url.openStream(); // URL can be open

                  in.close();
                  return Direction.MATCHED;
               } catch (Throwable e) {
                  Assert.fail(e.toString());
               }
            }

            return Direction.DOWN;
         }
      });

      Assert.assertEquals(true, all.contains("log4j/log4j/pom.xml"));
      Assert.assertEquals(true, result.size() > 0);

      for (URL url : result) {
         System.out.println(url);
         url.openStream().close(); // make sure it can be open and close
      }

      removeJarFromClasspath(jarUrl);
   }

   @Test
   public void testResourceFileForDown() throws IOException {
      final List<String> all = new ArrayList<String>();
      List<URL> result = Scanners.forResource().scan("META-INF/plexus", new ResourceMatcher() {
         @Override
         public Direction matches(URL base, String path) {
            Assert.assertEquals(true, base.getPath().endsWith("META-INF/plexus"));

            if (base.getProtocol().equals("file")) { // file only
               File file = new File(base.getFile(), path);

               all.add(path);
               Assert.assertEquals(true, file.exists()); // file does exist
            }

            return Direction.DOWN;
         }
      });

      Assert.assertEquals(true, all.contains("components-foundation-service.xml"));
      Assert.assertEquals(0, result.size());
   }

   @Test
   public void testResourceFileForMatched() throws IOException {
      final List<String> all = new ArrayList<String>();
      List<URL> result = Scanners.forResource().scan("META-INF/plexus", new ResourceMatcher() {
         @Override
         public Direction matches(URL base, String path) {
            Assert.assertEquals(true, base.getPath().endsWith("META-INF/plexus"));

            if (base.getProtocol().equals("file")) { // file only
               File file = new File(base.getFile(), path);

               all.add(path);
               Assert.assertEquals(true, file.exists()); // file does exist
               return Direction.MATCHED;
            }

            return Direction.DOWN;
         }
      });

      Assert.assertEquals(true, all.contains("components-foundation-service.xml"));
      Assert.assertEquals(true, result.size() > 0);

      for (URL url : result) {
         url.openStream().close(); // make sure it can be open and close
      }
   }

   @Test
   public void testResourceJarForDown() throws IOException {
      final List<String> all = new ArrayList<String>();
      List<URL> result = Scanners.forResource().scan("META-INF/maven", new ResourceMatcher() {
         @Override
         public Direction matches(URL base, String path) {
            Assert.assertEquals(true, base.getPath().endsWith("!/META-INF/maven"));

            if (base.getProtocol().equals("jar")) { // jar only
               all.add(path);

               try {
                  URL url = new URL(base.toExternalForm() + "/" + path);
                  InputStream in = url.openStream(); // URL can be open

                  in.close();
               } catch (Throwable e) {
                  Assert.fail(e.toString());
               }
            }

            return Direction.DOWN;
         }
      });

      Assert.assertEquals(true, all.contains("log4j/log4j/pom.xml"));
      Assert.assertEquals(0, result.size());
   }

   @Test
   public void testResourceJarForMatched() throws IOException {
      final List<String> all = new ArrayList<String>();
      List<URL> result = Scanners.forResource().scan("META-INF/maven", new ResourceMatcher() {
         @Override
         public Direction matches(URL base, String path) {
            Assert.assertEquals(true, base.getPath().endsWith("!/META-INF/maven"));

            if (base.getProtocol().equals("jar")) { // jar only
               all.add(path);

               try {
                  URL url = new URL(base.toExternalForm() + "/" + path);
                  InputStream in = url.openStream(); // URL can be open

                  in.close();
                  return Direction.MATCHED;
               } catch (Throwable e) {
                  Assert.fail(e.toString());
               }
            }

            return Direction.DOWN;
         }
      });

      Assert.assertEquals(true, all.contains("log4j/log4j/pom.xml"));
      Assert.assertEquals(true, result.size() > 0);

      for (URL url : result) {
         url.openStream().close(); // make sure it can be open and close
      }
   }
}
