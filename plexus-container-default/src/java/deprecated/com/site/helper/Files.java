package com.site.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Files {
   public static Dir forDir() {
      return Dir.INSTANCE;
   }

   public static IO forIO() {
      return IO.INSTANCE;
   }

   public static Zip forZip() {
      return Zip.INSTANCE;
   }

   public enum AutoClose {
      NONE,

      INPUT,

      OUTPUT,

      INPUT_OUTPUT;

      public void close(InputStream is) {
         if (this == INPUT || this == INPUT_OUTPUT) {
            if (is != null) {
               try {
                  is.close();
               } catch (IOException e) {
                  // ignore it
               }
            }
         }
      }

      public void close(OutputStream os) {
         if (this == OUTPUT || this == INPUT_OUTPUT) {
            if (os != null) {
               try {
                  os.close();
               } catch (IOException e) {
                  // ignore it
               }
            }
         }
      }
   }

   public enum Dir {
      INSTANCE;

      public void copyDir(File from, File to) throws IOException {
         String[] names = from.list();

         createDir(to);

         for (String name : names) {
            File file = new File(from, name);

            if (file.isDirectory()) {
               copyDir(file, new File(to, name));
            } else {
               copyFile(file, new File(to, name));
            }
         }
      }

      public void copyFile(File from, File to) throws IOException {
         IO.INSTANCE.copy(new FileInputStream(from), new FileOutputStream(to), AutoClose.INPUT_OUTPUT);
         to.setLastModified(from.lastModified());
      }

      public void createDir(File dir) {
         if (!dir.exists()) {
            if (!dir.mkdirs()) {
               throw new RuntimeException(String.format("Cant' create directory(%s)!", dir));
            }
         }
      }

      public boolean delete(File file) {
         return delete(file, false);
      }

      public boolean delete(File file, boolean recursive) {
         if (file.exists()) {
            if (file.isFile()) {
               return file.delete();
            } else if (file.isDirectory()) {
               if (recursive) {
                  File[] children = file.listFiles();

                  if (children != null) {
                     for (File child : children) {
                        delete(child, recursive);
                     }
                  }
               }

               return file.delete();
            }
         }

         return false;
      }
   }

   public enum IO {
      INSTANCE;

      public void copy(InputStream is, OutputStream os) throws IOException {
         copy(is, os, AutoClose.NONE);
      }

      public void copy(InputStream is, OutputStream os, AutoClose stream) throws IOException {
         byte[] content = new byte[4096];

         try {
            while (true) {
               int size = is.read(content);

               if (size == -1) {
                  break;
               } else {
                  os.write(content, 0, size);
               }
            }
         } finally {
            stream.close(is);
            stream.close(os);
         }
      }

      public byte[] readFrom(File file) throws IOException {
         return readFrom(new FileInputStream(file), (int) file.length());
      }

      public String readFrom(File file, String charsetName) throws IOException {
         byte[] content = readFrom(new FileInputStream(file), (int) file.length());

         return new String(content, charsetName);
      }

      public byte[] readFrom(InputStream is) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(16 * 1024);

         copy(is, baos, AutoClose.INPUT);
         return baos.toByteArray();
      }

      public byte[] readFrom(InputStream is, int expectedSize) throws IOException {
         byte[] content = new byte[expectedSize];

         try {
            int count = 0;

            while (count < expectedSize) {
               int size = is.read(content, count, expectedSize - count);

               if (size == -1) {
                  break;
               } else {
                  count += size;
               }
            }
         } finally {
            try {
               is.close();
            } catch (IOException e) {
               // ignore it
            }
         }

         return content;
      }

      public String readFrom(InputStream is, String charsetName) throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(16 * 1024);

         copy(is, baos, AutoClose.INPUT);
         return baos.toString(charsetName);
      }

      public void writeTo(File file, byte[] data) throws IOException {
         if (file.isDirectory()) {
            throw new IOException(String.format("Can't write to an existing directory(%s)", file));
         }

         File parent = file.getCanonicalFile().getParentFile();
         if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException(String.format("Can't create directory(%s)!", parent));
         }

         FileOutputStream fos = new FileOutputStream(file);

         try {
            fos.write(data);
         } finally {
            try {
               fos.close();
            } catch (IOException e) {
               // ignore it
            }
         }
      }

      public void writeTo(File file, String data) throws IOException {
         writeTo(file, data, "utf-8");
      }

      public void writeTo(File file, String data, String charsetName) throws IOException {
         writeTo(file, data.getBytes(charsetName));
      }
   }

   public enum Zip {
      INSTANCE;

      public List<String> copyDir(ZipInputStream zis, File baseDir) throws IOException {
         List<String> entryNames = new ArrayList<String>();

         if (!baseDir.exists()) {
            Dir.INSTANCE.createDir(baseDir);
         }

         while (true) {
            ZipEntry entry = zis.getNextEntry();

            if (entry == null) {
               break;
            }

            if (entry.isDirectory()) {
               Dir.INSTANCE.createDir(new File(baseDir, entry.getName()));
            } else {
               IO.INSTANCE.copy(zis, new FileOutputStream(new File(baseDir, entry.getName())), AutoClose.OUTPUT);
            }
         }

         return entryNames;
      }
   }
}
