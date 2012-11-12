package org.unidal.script.java;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;


public class CompiledJavaFragment extends CompiledScript implements Invocable {
   private ScriptEngine m_engine;

   private ClassLoader m_classLoader;

   private JavaSourceFromString m_source;

   public CompiledJavaFragment(ScriptEngine engine) {
      m_engine = engine;
   }

   @Override
   public Object eval(ScriptContext ctx) throws ScriptException {
      String className = m_source.getClassName();
      String methodName = m_source.getMethodName();

      if (className != null && methodName != null) {
         PrintStream oldOut = System.out;

         try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream newOut = new PrintStream(baos);

            System.setOut(newOut);
            Class<?> clazz = m_classLoader.loadClass(className);

            call(clazz, methodName, null, null);
            return new String(baos.toByteArray());
         } catch (Exception e) {
            e.printStackTrace();
            return null;
         } finally {
            System.setOut(oldOut);
         }
      }

      return this;
   }

   Object call(Class<?> clazz, String methodName, Object instance, Object[] args) throws ScriptException,
         NoSuchMethodException {
      Method[] methods = clazz.getMethods();
      int len = args == null ? 0 : args.length;

      for (Method method : methods) {
         if (method.getName().equals(methodName) && method.getParameterTypes().length == len)
            try {
               return method.invoke(instance, args);
            } catch (Exception e) {
               throw new ScriptException(e);
            }
      }

      throw new NoSuchMethodException(methodName);
   }

   @Override
   public ScriptEngine getEngine() {
      return m_engine;
   }

   @Override
   public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
      try {
         Class<?> clazz = m_classLoader.loadClass(m_source.getClassName());

         return call(clazz, name, clazz.newInstance(), args);
      } catch (NoSuchMethodException e) {
         throw e;
      } catch (ScriptException e) {
         throw e;
      } catch (Exception e) {
         throw new ScriptException(e);
      }
   }

   @Override
   public <T> T getInterface(Class<T> clazz) {
      throw new UnsupportedOperationException();
   }

   @Override
   public <T> T getInterface(Object instance, Class<T> clazz) {
      throw new UnsupportedOperationException();
   }

   public void setClassLoader(ClassLoader classloader) {
      m_classLoader = classloader;
   }

   public void setSource(JavaSourceFromString source) {
      m_source = source;
   }
}
