package org.unidal.script.java;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

public class JavaFragmentEngineFactory implements ScriptEngineFactory {
   private Map<String, String> m_map = new HashMap<String, String>();

   public JavaFragmentEngineFactory() {
      m_map.put(ScriptEngine.ENGINE, getEngineName());
      m_map.put(ScriptEngine.ENGINE_VERSION, getEngineVersion());
      m_map.put(ScriptEngine.FILENAME, getEngineName());
      m_map.put(ScriptEngine.LANGUAGE, getLanguageName());
      m_map.put(ScriptEngine.LANGUAGE_VERSION, getLanguageVersion());
      m_map.put(ScriptEngine.NAME, "");
      m_map.put(ScriptEngine.ARGV, "");
      m_map.put("THREADING", "STATELESS");
   }

   @Override
   public String getEngineName() {
      return "Java Fragment Engine";
   }

   @Override
   public String getEngineVersion() {
      return "1.0";
   }

   @Override
   public List<String> getExtensions() {
      return Arrays.asList("java", "javaf");
   }

   @Override
   public List<String> getMimeTypes() {
      return Arrays.asList("text/java");
   }

   @Override
   public List<String> getNames() {
      return Arrays.asList("JavaFragment");
   }

   @Override
   public String getLanguageName() {
      return "Java";
   }

   @Override
   public String getLanguageVersion() {
      return "1.6";
   }

   @Override
   public Object getParameter(String key) {
      return m_map.get(key);
   }

   @Override
   public String getMethodCallSyntax(String obj, String m, String... args) {
      StringBuilder sb = new StringBuilder(256);
      boolean first = true;

      sb.append(obj).append('.').append(m).append('(');

      for (String arg : args) {
         if (first) {
            first = false;
         } else {
            sb.append(',');
         }

         sb.append(arg);
      }

      sb.append(')');

      return sb.toString();
   }

   @Override
   public String getOutputStatement(String toDisplay) {
      return "System.out.println(\"" + toDisplay + "\")";
   }

   @Override
   public String getProgram(String... statements) {
      StringBuilder sb = new StringBuilder(1024);

      sb.append("\r\n");

      for (String statement : statements) {
         sb.append("   ").append(statement).append(";\r\n");
      }

      return sb.toString();
   }

   @Override
   public ScriptEngine getScriptEngine() {
      return new JavaFragmentEngine(this);
   }
}
