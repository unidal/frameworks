package org.unidal.script.java;

import java.io.File;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Assert;

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

   @Test
   public void test() throws ScriptException {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByExtension("js");
      String json = "{\"status\":1,\"list\":{\"1\":{\"id\":\"111868\",\"name\":\"\\u989c\\u826f\",\"quality\":\"1\",\"level\":\"23\",\"exp\":\"41976\",\"levelupexp\":\"42320\",\"image\":\"21\",\"type\":\"1\",\"turn\":\"2\",\"isturn\":0,\"ratio\":1,\"pid\":\"76053\",\"ispractice\":1},\"2\":{\"id\":\"133789\",\"name\":\"\\u6587\\u4e11\",\"quality\":\"1\",\"level\":\"19\",\"exp\":\"2492\",\"levelupexp\":\"28880\",\"image\":\"18\",\"type\":\"1\",\"turn\":\"1\",\"isturn\":0,\"ratio\":1,\"pid\":0,\"ispractice\":0},\"3\":{\"id\":\"150693\",\"name\":\"\\u7530\\u4e30\",\"quality\":\"1\",\"level\":\"19\",\"exp\":\"2462\",\"levelupexp\":\"28880\",\"image\":\"17\",\"type\":\"2\",\"turn\":\"1\",\"isturn\":0,\"ratio\":1,\"pid\":0,\"ispractice\":0},\"4\":{\"id\":\"228506\",\"name\":\"\\u8881\\u7ecd\",\"quality\":\"1\",\"level\":\"5\",\"exp\":\"1134\",\"levelupexp\":\"2000\",\"image\":\"22\",\"type\":\"1\",\"turn\":\"1\",\"isturn\":0,\"ratio\":1,\"pid\":0,\"ispractice\":0},\"5\":{\"id\":\"204208\",\"name\":\"\\u8521\\u6587\\u59ec\",\"quality\":\"1\",\"level\":\"44\",\"exp\":\"108954\",\"levelupexp\":\"154880\",\"image\":\"5\",\"type\":\"2\",\"turn\":\"0\",\"isturn\":0,\"ratio\":1,\"pid\":0,\"ispractice\":0}},\"rule\":{\"1\":{\"paytype\":1,\"hour\":8,\"cost\":6300},\"2\":{\"paytype\":1,\"hour\":24,\"cost\":16065},\"3\":{\"paytype\":2,\"hour\":24,\"cost\":2},\"4\":{\"paytype\":2,\"hour\":72,\"cost\":20}},\"place\":{\"1\":{\"id\":\"76053\",\"gid\":\"111868\",\"image\":\"21\",\"squence\":\"1\",\"level\":\"1\",\"type\":\"2\",\"quality\":\"1\",\"cd\":84210},\"2\":{\"id\":\"76054\",\"gid\":\"0\",\"image\":0,\"squence\":\"2\",\"level\":\"1\",\"type\":\"0\",\"quality\":0,\"cd\":0},\"3\":{\"id\":\"128898\",\"gid\":\"0\",\"image\":0,\"squence\":\"3\",\"level\":\"1\",\"type\":\"0\",\"quality\":0,\"cd\":0},\"4\":{\"id\":\"188678\",\"gid\":\"0\",\"image\":0,\"squence\":\"4\",\"level\":\"1\",\"type\":\"0\",\"quality\":0,\"cd\":0}},\"leap\":0,\"mop\":[],\"freetimes\":\"12\"}";
      String script = "var gs='',ps=''; for (var i in o.list) gs+=o.list[i].id+','; for (var i in o.place) ps+=o.place[i].id+':'+o.place[i].gid+','; gs+'|'+ps;";

      Object result = engine.eval("var o=" + json + ";" + script);

      System.out.println(result);
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

   /**
    * Test executing o a piece of java code directly, use System.out as result.
    */
   @Test
   public void testOutputDirectory() throws Exception {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByExtension("java");

      engine.put(JavaFragmentEngine.OUTPUT_DIRECTORY, "target/out");
      Invocable inv = (Invocable) engine.eval("public class HelloWorld {public String hello() {return \"Hello, world!\";}}");

      Assert.assertEquals("Hello, world!", inv.invokeFunction("hello"));
      Assert.assertTrue("Output directory does not work!", new File("target/out/HelloWorld.class").exists());
   }
}
