package org.unidal.script.java;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class JavaFragmentCompiler implements Compilable {
   private JavaFragmentEngine m_engine;

   private File m_outputDir;

   public JavaFragmentCompiler(JavaFragmentEngine engine) {
      m_engine = engine;
   }

   private void buildClasspathEntries(ClassLoader loader, Set<File> files) {
      if (loader instanceof URLClassLoader) {
         URL[] urLs = ((URLClassLoader) loader).getURLs();

         if (urLs.length == 0) {
            // work around for JBoss 4.2.2 GA
            if (loader.getClass().getName().equals("org.jboss.mx.loading.UnifiedClassLoader3")) {
               try {
                  Method method = loader.getClass().getMethod("getAllURLs");

                  urLs = (URL[]) method.invoke(loader);
               } catch (Throwable e) {
                  System.err.println("[ERROR] Error when invoking method getAllURLs of " + loader.getClass() + "!");
                  e.printStackTrace();
               }
            }
         }

         for (URL url : urLs) {
            files.add(new File(url.getPath()));
         }

         buildClasspathEntries(loader.getParent(), files);
      } else if (loader == null) {
         String classpath = System.getProperty("java.class.path", "");
         String[] entries = classpath.split(Pattern.quote(File.pathSeparator));

         for (String entry : entries) {
            if (entry.length() > 0) {
               files.add(new File(entry));
            }
         }
      } else {
         System.err.println("[WARN] Unrecognized classloader: " + loader.getClass().getName());
      }
   }

   private URL[] buildUrls(File outputDir) {
      URL[] urls = new URL[1];

      try {
         urls[0] = outputDir.toURI().toURL();
      } catch (MalformedURLException e) {
         throw new RuntimeException("Error when building URLs for class loader!", e);
      }

      return urls;
   }

   @Override
   public CompiledScript compile(Reader reader) throws ScriptException {
      StringBuilder sb = new StringBuilder(4096);
      char[] buf = new char[2048];

      try {
         while (true) {
            int size = reader.read(buf);

            if (size < 0) {
               break;
            }

            sb.append(buf, 0, size);
         }
      } catch (IOException e) {
         throw new RuntimeException("Error when reading script from " + reader + "!");
      } finally {
         try {
            reader.close();
         } catch (IOException e) {
            // ignore it
         }
      }

      return compile(sb.toString());
   }

   @Override
   public CompiledScript compile(String script) throws ScriptException {
      File outputDir = getOutputDirectory();
      CompiledJavaFragment compiledScript = new CompiledJavaFragment(m_engine);
      JavaSourceFromString source = new JavaSourceFromString(outputDir, script);

      compileInternal(script, outputDir, source);

      URL[] urls = buildUrls(outputDir);
      ClassLoader parent = Thread.currentThread().getContextClassLoader();
      URLClassLoader classloader = new URLClassLoader(urls, parent);

      compiledScript.setClassLoader(classloader);
      compiledScript.setSource(source);
      return compiledScript;
   }

   private void compileInternal(String script, File outputDir, JavaSourceFromString source) throws ScriptException {
      Locale locale = Locale.getDefault();
      Charset charset = Charset.defaultCharset();
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
      StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, locale, charset);

      outputDir.mkdirs();

      try {
         manager.setLocation(StandardLocation.CLASS_PATH, getClasspathEntries());
         manager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(outputDir.getCanonicalFile()));

         Boolean result = compiler.getTask(null, manager, diagnostics, null, null, Arrays.asList(source)).call();

         if (!Boolean.TRUE.equals(result)) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
               throw new ScriptException(diagnostic.getMessage(locale), script, (int) diagnostic.getLineNumber()
                     - source.getLineOffset(), (int) diagnostic.getColumnNumber());
            }
         }

         manager.close();
      } catch (ScriptException e) {
         throw e;
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private Set<File> getClasspathEntries() {
      Set<File> files = new HashSet<File>(64);

      buildClasspathEntries(Thread.currentThread().getContextClassLoader(), files);
      return files;
   }

   private File getOutputDirectory() {
      if (m_outputDir == null) {
         Object outputDirectory = m_engine.get(JavaFragmentEngine.OUTPUT_DIRECTORY);

         if (outputDirectory != null) {
            m_outputDir = new File(String.valueOf(outputDirectory));
         } else {
            String tmpDir = System.getProperty("java.io.tmpdir");

            m_outputDir = new File(tmpDir, "JavaFragment");

            if (!m_outputDir.exists()) {
               if (!m_outputDir.mkdirs()) {
                  throw new IllegalStateException("Unable to create directory: " + m_outputDir);
               }
            }
         }

         System.out.println("[INFO] Java Fragment output directory is " + m_outputDir);
      }

      return m_outputDir;
   }
}
