package org.unidal.script.java;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.unidal.script.ScriptProber;

public class JavaFragmentEngineTest {
   @Test
   @Ignore
   public void showAllScriptEngines() {
      ScriptEngineManager manager = new ScriptEngineManager();

      for (ScriptEngineFactory factory : manager.getEngineFactories()) {
         ScriptEngine engine = factory.getScriptEngine();

         System.out.println(ScriptProber.INSTANCE.probe(engine));
      }
   }

   @Test(expected = ScriptException.class)
   public void testBadSyntax() throws ScriptException {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByExtension("java");
      String message = "how are you?";

      Assert.assertEquals(message, engine.eval(message));
   }

   @Test
   public void testClassInCurrentClasspath() throws ScriptException, NoSuchMethodException {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByExtension("java");
      Invocable inv = (Invocable) engine.eval( //
            "public java.util.List<String> split(String source) { return org.unidal.helper.Splitters.by('.').split(source); }");

      Assert.assertEquals("[a, b, c, d, e]", inv.invokeFunction("split", "a.b.c.d.e").toString());
   }

   /**
    * Java class defined, its methods can be called directly
    */
   @Test
   public void testJavaClass() throws Exception {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByExtension("java");
      Invocable inv = (Invocable) engine.eval( //
            "public class A {\t\n" + //
                  "public String hello(String name) { return \"Hello, \"+name+\"!\"; }\r\n" + //
                  "public String help() { return \"hello(...)\"; }\r\n" + //
                  "}\r\n");

      Assert.assertEquals("Hello, world!", inv.invokeFunction("hello", "world"));
      Assert.assertEquals("hello(...)", inv.invokeFunction("help"));

      try {
         inv.invokeFunction("unknown");

         Assert.fail("NoSuchMethodException should be thrown.");
      } catch (NoSuchMethodException e) {
         // expected
      }
   }

   /**
    * Java class defined, its methods can be called directly
    */
   @Test
   public void testJavaClass2() throws Exception {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByExtension("java");
      Invocable inv = (Invocable) engine.eval( //
            "import java.util.List;\r\n" + //
                  "import org.unidal.helper.Splitters;\r\n" + //
                  "public class A2 {\t\n" + //
                  "public List<String> split(String source) { return Splitters.by('.').split(source); }\r\n" + //
                  "}\r\n");

      Assert.assertEquals("[a, b, c, d, e]", inv.invokeFunction("split", "a.b.c.d.e").toString());
   }

   /**
    * Java class with package defined, its methods can be called directly
    */
   @Test
   public void testJavaClassWithPackage() throws Exception {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByExtension("java");
      Invocable inv = (Invocable) engine.eval( //
            "package here.is.the.packageName;\r\n" + //
                  "import java.util.List;\r\n" + //
                  "import org.unidal.helper.Splitters;\r\n" + //
                  "public class P {\t\n" + //
                  "public List<String> split(String source) { return Splitters.by('-').split(source); }\r\n" + //
                  "}\r\n");

      Assert.assertEquals("[a, b, c, d, e]", inv.invokeFunction("split", "a-b-c-d-e").toString());
   }

   /**
    * Test executing o a piece of java code directly, use System.out as result.
    */
   @Test
   public void testJavaFragment() throws Exception {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByExtension("java");

      Assert.assertEquals("Hello, world!", (String) engine.eval("System.out.print(\"Hello, world!\");"));
      Assert.assertEquals("HELLO, WORLD!", (String) engine.eval("System.out.print(\"Hello, world!\".toUpperCase());"));
      Assert.assertEquals("2", engine.eval("System.out.print(1 + 1);"));
   }

   /**
    * Java method defined, it can be called directly
    */
   @Test
   public void testJavaMethod() throws Exception {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByExtension("java");
      Invocable inv = (Invocable) engine.eval( //
            "public String hello(String name) { return \"Hello, \"+name+\"!\"; }" + //
                  "public String help() { return \"hello(...)\"; }");

      Assert.assertEquals("Hello, world!", inv.invokeFunction("hello", "world"));
      Assert.assertEquals("hello(...)", inv.invokeFunction("help"));

      try {
         inv.invokeFunction("unknown");

         Assert.fail("NoSuchMethodException should be thrown.");
      } catch (NoSuchMethodException e) {
         // expected
      }
   }

   @Test
   public void testJavaScript() throws Exception {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByExtension("js");
      String script = "function helloFunction(name) { var result='Hello, ' + name; return result;}";

      engine.eval(script);

      Invocable inv = (Invocable) engine;
      String actual = (String) inv.invokeFunction("helloFunction", "Scripting");
      String expected = "Hello, Scripting";

      Assert.assertEquals(expected, actual);
   }
}
