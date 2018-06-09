package org.unidal.helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.unidal.helper.Scanners.IMatcher.Direction;

public class Scanners {
   public static DirScanner forDir() {
      return DirScanner.INSTANCE;
   }

   public static JarScanner forJar() {
      return JarScanner.INSTANCE;
   }

   public static ResourceScanner forResource() {
      return ResourceScanner.INSTANCE;
   }

   public static abstract class DirMatcher implements IMatcher<File> {
      @Override
      public boolean isDirEligible() {
         return true;
      }

      @Override
      public boolean isFileElegible() {
         return false;
      }
   }

   public enum DirScanner {
      INSTANCE;

      public List<File> scan(File base, IMatcher<File> matcher) {
         List<File> files = new ArrayList<File>();
         StringBuilder relativePath = new StringBuilder();

         scanForFiles(base, relativePath, matcher, false, files);

         return files;
      }

      private void scanForFiles(File base, StringBuilder relativePath, IMatcher<File> matcher, boolean foundFirst,
            List<File> files) {
         int len = relativePath.length();
         File dir = len == 0 ? base : new File(base, relativePath.toString());
         String[] list = dir.list();

         if (list != null) {
            for (String item : list) {
               File child = new File(dir, item);

               if (len > 0) {
                  relativePath.append('/');
               }

               relativePath.append(item);

               IMatcher.Direction direction = matcher.matches(base, relativePath.toString());

               if (direction == null) {
                  direction = Direction.NEXT;
               }

               switch (direction) {
               case MATCHED:
                  if (matcher.isDirEligible() && child.isDirectory()) {
                     files.add(child);
                  }

                  if (matcher.isFileElegible() && child.isFile()) {
                     files.add(child);
                  }

                  break;
               case DOWN:
                  // for sub-folders
                  scanForFiles(base, relativePath, matcher, foundFirst, files);
                  break;
               default:
                  break;
               }

               relativePath.setLength(len); // reset

               if (foundFirst && files.size() > 0) {
                  break;
               }
            }
         }
      }

      public File scanForOne(File base, IMatcher<File> matcher) {
         List<File> files = new ArrayList<File>(1);
         StringBuilder relativePath = new StringBuilder();

         scanForFiles(base, relativePath, matcher, true, files);

         if (files.isEmpty()) {
            return null;
         } else {
            return files.get(0);
         }
      }
   }

   public static abstract class FileMatcher implements IMatcher<File> {
      @Override
      public boolean isDirEligible() {
         return false;
      }

      @Override
      public boolean isFileElegible() {
         return true;
      }
   }

   public static interface IMatcher<T> {
      public boolean isDirEligible();

      public boolean isFileElegible();

      public Direction matches(T base, String path);

      public enum Direction {
         MATCHED,

         DOWN,

         NEXT;

         public boolean isDown() {
            return this == DOWN;
         }

         public boolean isMatched() {
            return this == MATCHED;
         }

         public boolean isNext() {
            return this == NEXT;
         }
      }
   }

   public enum JarScanner {
      INSTANCE;

      public ZipEntry getEntry(String jarFileName, String name) {
         ZipFile zipFile = null;

         try {
            zipFile = new ZipFile(jarFileName);

            ZipEntry entry = zipFile.getEntry(name);

            return entry;
         } catch (IOException e1) {
            // ignore
         } finally {
            if (zipFile != null) {
               try {
                  zipFile.close();
               } catch (IOException e) {
                  // ignore it
               }
            }
         }

         return null;
      }

      public byte[] getEntryContent(String jarFileName, String entryPath) {
         byte[] bytes = null;
         ZipFile zipFile = null;

         try {
            zipFile = new ZipFile(jarFileName);
            ZipEntry entry = zipFile.getEntry(entryPath);

            if (entry != null) {
               InputStream inputStream = zipFile.getInputStream(entry);
               bytes = Files.forIO().readFrom(inputStream);
            }
         } catch (Exception e) {
            // ignore
         } finally {
            if (zipFile != null) {
               try {
                  zipFile.close();
               } catch (Exception e) {
               }
            }
         }

         return bytes;
      }

      public boolean hasEntry(String jarFileName, String name) {
         return getEntry(jarFileName, name) != null;
      }

      public List<String> scan(File base, IMatcher<File> matcher) {
         List<String> files = new ArrayList<String>();
         scanForFiles(base, matcher, false, files);

         return files;
      }

      public List<String> scan(ZipFile zipFile, IMatcher<ZipEntry> matcher) {
         List<String> files = new ArrayList<String>();
         scanForEntries(zipFile, matcher, false, files);

         return files;
      }

      private void scanForEntries(ZipFile zipFile, IMatcher<ZipEntry> matcher, boolean foundFirst, List<String> names) {
         Enumeration<? extends ZipEntry> entries = zipFile.entries();

         while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();

            if (matcher.isDirEligible() && entry.isDirectory()) {
               IMatcher.Direction direction = matcher.matches(entry, name);

               if (direction.isMatched()) {
                  names.add(name);
               }
            } else if (matcher.isFileElegible() && !entry.isDirectory()) {
               IMatcher.Direction direction = matcher.matches(entry, name);

               if (direction.isMatched()) {
                  names.add(name);
               }
            }

            if (foundFirst && names.size() > 0) {
               break;
            }
         }
      }

      private void scanForFiles(File jarFile, IMatcher<File> matcher, boolean foundFirst, List<String> names) {
         ZipFile zipFile = null;

         try {
            zipFile = new ZipFile(jarFile);
         } catch (IOException e) {
            // ignore it
         }

         if (zipFile != null) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
               ZipEntry entry = entries.nextElement();
               String name = entry.getName();

               if (matcher.isDirEligible() && entry.isDirectory()) {
                  IMatcher.Direction direction = matcher.matches(jarFile, name);

                  if (direction.isMatched()) {
                     names.add(name);
                  }
               } else if (matcher.isFileElegible() && !entry.isDirectory()) {
                  IMatcher.Direction direction = matcher.matches(jarFile, name);

                  if (direction.isMatched()) {
                     names.add(name);
                  }
               }

               if (foundFirst && names.size() > 0) {
                  break;
               }
            }
         }
      }

      public String scanForOne(File jarFile, IMatcher<File> matcher) {
         List<String> files = new ArrayList<String>(1);

         scanForFiles(jarFile, matcher, true, files);

         if (files.isEmpty()) {
            return null;
         } else {
            return files.get(0);
         }
      }
   }

   public static abstract class ResourceMatcher implements IMatcher<URL> {
      @Override
      public boolean isDirEligible() {
         return false;
      }

      @Override
      public boolean isFileElegible() {
         return true;
      }

      public abstract Direction matches(URL base, String path);
   }

   public enum ResourceScanner {
      INSTANCE;

      @SuppressWarnings("deprecation")
      private String decode(String url) {
         try {
            return URLDecoder.decode(url, "utf-8");
         } catch (UnsupportedEncodingException e) {
            return URLDecoder.decode(url);
         }
      }

      public List<URL> scan(String resourceBase, final ResourceMatcher matcher) throws IOException {
         List<URL> urls = new ArrayList<URL>();
         Set<URL> done = new HashSet<URL>();

         // try to load from current class's classloader
         Enumeration<URL> r1 = getClass().getClassLoader().getResources(resourceBase);

         while (r1.hasMoreElements()) {
            URL url = r1.nextElement();

            scan(done, urls, url, resourceBase, matcher);
         }

         // try to load from current context's classloader
         Enumeration<URL> r2 = Thread.currentThread().getContextClassLoader().getResources(resourceBase);

         while (r2.hasMoreElements()) {
            URL url = r2.nextElement();

            scan(done, urls, url, resourceBase, matcher);
         }

         return urls;
      }

      private void scan(final List<URL> urls, final URL url, final ResourceMatcher matcher) throws IOException {
         String protocol = url.getProtocol();

         if ("file".equals(protocol)) {
            DirScanner.INSTANCE.scan(new File(decode(url.getPath())), new FileMatcher() {
               @Override
               public Direction matches(File base, String path) {
                  try {
                     URL u = new URL(url, path);
                     Direction d = matcher.matches(u, path);

                     if (d.isMatched()) {
                        urls.add(u);
                     }

                     return d;
                  } catch (MalformedURLException e) {
                     // ignore it
                  }

                  return Direction.DOWN;
               }
            });
         } else if ("jar".equals(protocol) || "wasjar".equals(protocol)) { // wasjar for WAS
            List<String> parts = Splitters.by('!').split(url.toExternalForm());
            int len = parts.size();
            String prefix = parts.remove(len - 1).substring(1);
            JarInputStream jis = null;

            for (int i = 0; i < len - 1; i++) {
               String part = parts.get(i);
               String nextPart = i + 1 < len - 1 ? parts.get(i + 1).substring(1) : null;

               if (i == 0) {
                  URL top = new URL(part + "!/");
                  JarURLConnection conn = (JarURLConnection) top.openConnection();
                  JarFile jarFile = conn.getJarFile();
                  Enumeration<JarEntry> jarEntries = jarFile.entries();

                  while (jarEntries.hasMoreElements()) {
                     JarEntry jarEntry = jarEntries.nextElement();
                     String name = jarEntry.getName();

                     if (nextPart == null && name.startsWith(prefix)) {
                        String p = name.substring(prefix.length());
                        URL u = new URL(url, p);
                        Direction d = matcher.matches(u, p);

                        if (d.isMatched()) {
                           urls.add(u);
                        }
                     } else if (name.equals(nextPart)) {
                        int size = (int) jarEntry.getSize();
                        byte[] data = Files.forIO().readFrom(jarFile.getInputStream(jarEntry), size);

                        jis = new JarInputStream(new ByteArrayInputStream(data));
                        break;
                     }
                  }

                  jarFile.close();
               } else if (jis != null) {
                  JarInputStream newJis = null;

                  while (true) {
                     JarEntry e = (JarEntry) jis.getNextEntry();

                     if (e == null) {
                        break;
                     } else if (!e.isDirectory()) {
                        String name = e.getName();

                        if (nextPart == null && name.startsWith(prefix)) {
                           String p = name.substring(prefix.length());
                           URL u = new URL(url, p);
                           Direction d = matcher.matches(u, p);

                           if (d.isMatched()) {
                              urls.add(u);
                           }
                        } else if (name.equals(nextPart)) {
                           int size = (int) e.getSize();
                           byte[] data = new byte[size];
                           int off = 0;

                           while (off < size) {
                              off += jis.read(data, off, size);
                           }

                           newJis = new JarInputStream(new ByteArrayInputStream(data));
                           break;
                        }
                     }
                  }

                  jis.close();
                  jis = newJis;
               }
            }
         }
      }
   }

   public static abstract class ZipEntryMatcher implements IMatcher<ZipEntry> {
      @Override
      public boolean isDirEligible() {
         return false;
      }

      @Override
      public boolean isFileElegible() {
         return true;
      }

      public abstract Direction matches(ZipEntry entry, String path);
   }
}
