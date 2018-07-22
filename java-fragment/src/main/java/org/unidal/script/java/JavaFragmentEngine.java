package org.unidal.script.java;

import java.io.Reader;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 * Java Fragment engine to compile and execute Java source code on the fly.
 * 
 * Following use cases are supported
 * <ul>
 * <li>a piece of java code</li>
 * <li>one java method</li>
 * <li>one java class</li>
 * </ul>
 * 
 * @author Frankie Wu
 * @since March 17, 2012
 * @version 0.1.0
 */
public class JavaFragmentEngine extends AbstractScriptEngine {
   public static final String OUTPUT_DIRECTORY = "java.fragment.output.directory";

   private ScriptEngineFactory m_factory;

   private JavaFragmentCompiler m_compiler;

   JavaFragmentEngine(JavaFragmentEngineFactory factory) {
      m_factory = factory;
      m_compiler = new JavaFragmentCompiler(this);
   }

   @Override
   public Bindings createBindings() {
      return new SimpleBindings();
   }

   @Override
   public Object eval(Reader script, ScriptContext context) throws ScriptException {
      CompiledScript compiledScript = getCompilable().compile(script);

      return compiledScript.eval(context);
   }

   @Override
   public Object eval(String script, ScriptContext context) throws ScriptException {
      CompiledScript compiledScript = getCompilable().compile(script);

      return compiledScript.eval(context);
   }

   private Compilable getCompilable() {
      return m_compiler;
   }

   @Override
   public ScriptEngineFactory getFactory() {
      return m_factory;
   }
}
