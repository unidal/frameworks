package org.unidal.script;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

public enum ScriptProber {
   INSTANCE;

   public String probe(ScriptEngine engine) {
      StringBuilder sb = new StringBuilder(2048);

      probeFactory(sb, engine.getFactory());
      probeContext(sb, engine.getContext());

      return sb.toString();
   }

   private void probeContext(StringBuilder sb, ScriptContext ctx) {
      Bindings globalBindings = ctx.getBindings(ScriptContext.GLOBAL_SCOPE);

      if (globalBindings != null && !globalBindings.isEmpty()) {
         sb.append("Global bindings: \r\n");

         for (Map.Entry<String, Object> entry : globalBindings.entrySet()) {
            sb.append("   ").append(entry.getKey()).append(": ").append(entry.getValue());
         }

         sb.append("\r\n");
      }

      Bindings engineBindings = ctx.getBindings(ScriptContext.ENGINE_SCOPE);

      if (engineBindings != null && !engineBindings.isEmpty()) {
         sb.append("Engine bindings: \r\n");

         for (Map.Entry<String, Object> entry : engineBindings.entrySet()) {
            sb.append("   ").append(entry.getKey()).append(": ").append(entry.getValue());
         }

         sb.append("\r\n");
      }
   }

   private void probeFactory(StringBuilder sb, ScriptEngineFactory factory) {
      sb.append("Script engine: ").append(factory.getEngineName()).append(' ').append(factory.getEngineVersion());
      sb.append("\r\n");
      sb.append("Script language: ").append(factory.getLanguageName()).append(' ').append(factory.getLanguageVersion());
      sb.append(", supported names: ").append(factory.getNames());
      sb.append(", supported extensions: ").append(factory.getExtensions());
      sb.append(", supported mime-types: ").append(factory.getMimeTypes());
      sb.append("\r\n");

      String syntax = factory.getMethodCallSyntax("obj", "method", "arg1", "arg2", "arg3");
      if (syntax != null) {
         sb.append("Method call syntax: ").append(syntax);
         sb.append("\r\n");
      }

      String output = factory.getOutputStatement("Hello, world!");
      if (output != null) {
         sb.append("Output statement: ").append(output);
         sb.append("\r\n");

         String program = factory.getProgram(factory.getOutputStatement("Hello"), factory.getOutputStatement("world"));
         sb.append("Sample program: ").append(program == null ? "[N/A]" : program);
         sb.append("\r\n");
      }
   }
}
