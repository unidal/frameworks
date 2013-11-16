package org.unidal.eunit.codegen.xsl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.unidal.eunit.codegen.XslCodegen;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.task.IValve;
import org.unidal.eunit.testfwk.spi.task.IValveChain;
import org.unidal.helper.Files;

public class XslCodegenValve implements IValve<ICaseContext> {
   private final XslCodegen m_meta;

   private TransformerFactory m_factory;

   public XslCodegenValve(XslCodegen meta) {
      m_meta = meta;
   }

   private String evaluate(ICaseContext ctx, String xml, String template, Map<String, Object> parameters) {
      Transformer transformer = getTransformer(ctx, template);
      StringWriter writer = new StringWriter(64 * 1024);

      if (parameters != null) {
         for (Map.Entry<String, Object> e : parameters.entrySet()) {
            transformer.setParameter(e.getKey(), e.getValue());
         }
      }

      try {
         transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
      } catch (TransformerException e) {
         throw new RuntimeException(String.format("Error when evaluating template(%s)!", template), e);
      }

      return writer.toString();
   }

   @Override
   public void execute(ICaseContext ctx, IValveChain chain) throws Throwable {
      String xmlContent = getXmlSource(ctx, m_meta.source());
      List<Manifest> manifests = getManifest(ctx, xmlContent, m_meta.manifest(), m_meta.template());

      for (Manifest manifest : manifests) {
         String generated = evaluate(ctx, xmlContent, manifest.getTemplate(), manifest.getProperties());

         saveToFile(manifest, generated);
      }

      System.out.println(String.format("%s files generated!", manifests.size()));
      chain.executeNext(ctx);
   }

   private List<Manifest> getManifest(ICaseContext ctx, String contentXml, String manifestXsl, String templateXsl) {
      String content = evaluate(ctx, contentXml, manifestXsl, null);
      ManifestParser parser = new ManifestParser();
      List<Manifest> manifests = parser.parse(content);

      for (Manifest manifest : manifests) {
         if (manifest.getTemplate().length() == 0) {
            manifest.setTemplate(templateXsl);
         }
      }

      return manifests;
   }

   private InputStream getResourceFromClassPath(Class<?> testClass, String source) {
      InputStream is = testClass.getResourceAsStream(source);

      if (is == null) {
         String path;

         if (source.startsWith("/")) {
            path = source;
         } else {
            String packageName = testClass.getPackage().getName();

            path = packageName.replace('.', '/') + '/' + source;
         }

         is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
      }

      return is;
   }

   private Transformer getTransformer(final ICaseContext ctx, final String template) {
      if (m_factory == null) {
         TransformerFactory factory = TransformerFactory.newInstance();

         try {
            factory.setURIResolver(new URIResolver() {
               public Source resolve(String href, String base) throws TransformerException {
                  if (base == null) {
                     return getXslTemplate(ctx, href);
                  }

                  try {
                     URL uri = new URL(new URL(base), href);

                     return new StreamSource(uri.openStream(), uri.toString());
                  } catch (Exception e) {
                     // ignore it
                     e.printStackTrace();
                  }

                  // let the processor to resolve the URI itself
                  return null;
               }
            });
         } catch (Exception e) {
            throw new RuntimeException("Error when creating transformer factory!", e);
         }

         m_factory = factory;
      }

      try {
         Templates templates = m_factory.newTemplates(getXslTemplate(ctx, template));

         return templates.newTransformer();
      } catch (Exception e) {
         throw new RuntimeException(String.format("Fail to open XSL template(%s)!", template), e);
      }
   }

   private String getXmlSource(ICaseContext ctx, String sourceXml) throws IOException {
      Class<?> testClass = ctx.getEunitClass().getType();
      InputStream is = getResourceFromClassPath(testClass, sourceXml);

      if (is == null) {
         throw new RuntimeException(String.format("Can't find XML file(%s)!", sourceXml));
      } else {
         return Files.forIO().readFrom(is, "utf-8");
      }
   }

   private Source getXslTemplate(ICaseContext ctx, String templateXsl) {
      Class<?> testClass = ctx.getEunitClass().getType();
      InputStream is = testClass.getResourceAsStream(templateXsl);

      if (is == null) {
         is = getClass().getResourceAsStream(templateXsl);
      }

      if (is == null) {
         throw new RuntimeException(String.format("Can't find XSL template(%s)!", templateXsl));
      } else {
         return new StreamSource(is);
      }
   }

   private void saveToFile(Manifest manifest, String content) throws IOException {
      File file = new File(".", manifest.getPath()).getCanonicalFile();

      Files.forIO().writeTo(file, content);
      System.out.println(String.format("File %s generated.", file));
   }
}