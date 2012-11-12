package org.unidal.script.java;

import java.io.File;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

class JavaSourceFromString extends SimpleJavaFileObject {
   private static URI FAKE = URI.create("fake:///nothing");

   private File m_baseDir;

   private String m_className;

   private CharSequence m_code;

   private int m_lineOffset;

   private String m_methodName;

   private URI m_uri;

   private String m_packageName;

   private String m_uriPath;

   JavaSourceFromString(File baseDir, String code) {
      super(FAKE, Kind.SOURCE);
      m_baseDir = baseDir;
      m_className = "$JavaFragment$" + Math.abs(code.hashCode());
      m_code = normalize(code);
      m_uriPath = buildUriPath();
      m_uri = URI.create("fragment:///" + m_uriPath + ".java");
   }

   private String buildUriPath() {
      StringBuilder sb = new StringBuilder(64);

      if (m_packageName != null) {
         sb.append(m_packageName.replace('.', '/'));
         sb.append('/');
      }

      sb.append(m_className);

      return sb.toString();
   }

   @Override
   public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return m_code;
   }

   public File getClassFile() {
      return new File(m_baseDir, m_uriPath + ".class");
   }

   public String getClassName() {
      if (m_packageName == null) {
         return m_className;
      } else {
         return m_packageName + '.' + m_className;
      }
   }

   public int getLineOffset() {
      return m_lineOffset;
   }

   public String getMethodName() {
      return m_methodName;
   }

   public boolean isFragment() {
      return m_lineOffset > 0;
   }

   private CharSequence normalize(String code) {
      // a whole java source file
      if (code.startsWith("package ")) {
         return normalizeForClassWithPackage(code);
      } else if (code.indexOf("public class ") >= 0) {
         return normalizeForClass(code);
      } else {
         return normalizeForFragment(code);
      }
   }

   private CharSequence normalizeForClass(String code) {
      String token = " class ";
      int pos1 = code.indexOf(token);
      int len = token.length();
      int pos2 = code.indexOf(' ', pos1 + len);

      if (pos1 > 0 && pos2 > pos1) {
         m_className = code.substring(pos1 + len, pos2);
      }

      return code;
   }

   private CharSequence normalizeForClassWithPackage(String code) {
      int off = code.indexOf(';');
      String token = " class ";
      int pos1 = code.indexOf(token);
      int len = token.length();
      int pos2 = code.indexOf(' ', pos1 + len);

      if (off >= 0) {
         m_packageName = code.substring("package ".length(), off);
      }

      if (pos1 > 0 && pos2 > pos1) {
         m_className = code.substring(pos1 + len, pos2);
      }

      return code;
   }

   private CharSequence normalizeForFragment(String code) {
      StringBuilder sb = new StringBuilder(code.length() + 64);

      if (code.startsWith("public ")) {
         sb.append("public class ").append(m_className).append(" {\r\n");
         sb.append(code).append("\r\n");
         sb.append("}\r\n");

         m_lineOffset = 1;
      } else {
         sb.append("public class ").append(m_className).append(" {\r\n");
         sb.append("   public static void fragment() {\r\n");
         sb.append(code).append("\r\n");
         sb.append("   }\r\n");
         sb.append("}\r\n");

         m_methodName = "fragment";
         m_lineOffset = 2;
      }

      return sb.toString();
   }

   @Override
   public URI toUri() {
      return m_uri;
   }

   @Override
   public String toString() {
      return m_uri.toString();
   }
}