package org.unidal.eunit.codegen.xsl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ManifestParser extends DefaultHandler {
   private List<Manifest> m_manifests;

   private String m_propertyName;

   public ManifestParser() {
      m_manifests = new ArrayList<Manifest>();
   }

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException {
      if (m_propertyName != null) {
         String value = new String(ch, start, length);

         if (!m_manifests.isEmpty()) {
            int size = m_manifests.size();
            Manifest file = m_manifests.get(size - 1);

            file.addProperty(m_propertyName, value);
         }
      }
   }

   @Override
   public void endElement(String namespaceURI, String localName, String rawName) throws SAXException {
      m_propertyName = null;
   }

   public void parse(InputSource input) throws SAXException, IOException {
      try {
         SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
         XMLReader reader = parser.getXMLReader();

         reader.setFeature("http://xml.org/sax/features/namespaces", true);

         if (parser.getClass().getName().equals("org.apache.xerces.jaxp.SAXParserImpl")) {
            // disable DTD validate
            String feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            reader.setFeature(feature, false);
         }

         reader.setContentHandler(this);
         reader.setErrorHandler(this);
         reader.setDTDHandler(this);
         reader.setEntityResolver(this);

         reader.parse(input);
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      }
   }

   public List<Manifest> parse(String content) {
      try {
         parse(new InputSource(new StringReader(content)));

         return m_manifests;
      } catch (Exception e) {
         throw new RuntimeException(String.format("Error when parsing manifest(%s)!", content), e);
      }
   }

   @Override
   public void startElement(String namespaceURI, String localName, String rawName, Attributes attrs) throws SAXException {
      String tag = localName;

      m_propertyName = null;

      if (tag.equals("file")) {
         String path = attrs.getValue("path");
         String template = attrs.getValue("template");
         FileMode mode = FileMode.getByName(attrs.getValue("mode"));
         Manifest file = new Manifest(template, path, mode);

         m_manifests.add(file);
      } else if (tag.equals("property")) {
         m_propertyName = attrs.getValue("name");
      }
   }
}
