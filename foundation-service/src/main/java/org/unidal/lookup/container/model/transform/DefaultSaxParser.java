package org.unidal.lookup.container.model.transform;

import static org.unidal.lookup.container.model.Constants.ELEMENT_FIELD_NAME;
import static org.unidal.lookup.container.model.Constants.ELEMENT_IMPLEMENTATION;
import static org.unidal.lookup.container.model.Constants.ELEMENT_INSTANTIATION_STRATEGY;
import static org.unidal.lookup.container.model.Constants.ELEMENT_ROLE;
import static org.unidal.lookup.container.model.Constants.ELEMENT_ROLE_HINT;

import static org.unidal.lookup.container.model.Constants.ENTITY_COMPONENT;
import static org.unidal.lookup.container.model.Constants.ENTITY_CONFIGURATION;
import static org.unidal.lookup.container.model.Constants.ENTITY_PLEXUS;
import static org.unidal.lookup.container.model.Constants.ENTITY_REQUIREMENT;
import static org.unidal.lookup.container.model.Constants.ENTITY_COMPONENTS;
import static org.unidal.lookup.container.model.Constants.ENTITY_REQUIREMENTS;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.unidal.lookup.container.model.IEntity;
import org.unidal.lookup.container.model.entity.Any;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.PlexusModel;
import org.unidal.lookup.container.model.entity.RequirementModel;

public class DefaultSaxParser extends DefaultHandler {

   private DefaultLinker m_linker = new DefaultLinker(true);

   private DefaultSaxMaker m_maker = new DefaultSaxMaker();

   private Stack<String> m_tags = new Stack<String>();

   private Stack<Object> m_objs = new Stack<Object>();

   private IEntity<?> m_entity;

   private StringBuilder m_text = new StringBuilder();

   public static PlexusModel parse(InputStream in) throws SAXException, IOException {
      return parseEntity(PlexusModel.class, new InputSource(removeBOM(in)));
   }

   public static PlexusModel parse(Reader reader) throws SAXException, IOException {
      return parseEntity(PlexusModel.class, new InputSource(removeBOM(reader)));
   }

   public static PlexusModel parse(String xml) throws SAXException, IOException {
      return parseEntity(PlexusModel.class, new InputSource(new StringReader(removeBOM(xml))));
   }

   @SuppressWarnings("unchecked")
   private static <T extends IEntity<?>> T parseEntity(Class<T> type, InputSource is) throws SAXException, IOException {
      try {
         DefaultSaxParser handler = new DefaultSaxParser();
         SAXParserFactory factory = SAXParserFactory.newInstance();

         factory.setValidating(false);
         factory.setFeature("http://xml.org/sax/features/validation", false);

         factory.newSAXParser().parse(is, handler);
         return (T) handler.getEntity();
      } catch (ParserConfigurationException e) {
         throw new IllegalStateException("Unable to get SAX parser instance!", e);
      }
   }

   public static <T extends IEntity<?>> T parseEntity(Class<T> type, InputStream in) throws SAXException, IOException {
      return parseEntity(type, new InputSource(removeBOM(in)));
   }

   public static <T extends IEntity<?>> T parseEntity(Class<T> type, String xml) throws SAXException, IOException {
      return parseEntity(type, new InputSource(new StringReader(removeBOM(xml))));
   }

   // to remove Byte Order Mark(BOM) at the head of windows utf-8 file
   @SuppressWarnings("unchecked")
   private static <T> T removeBOM(T obj) throws IOException {
      if (obj instanceof String) {
         String str = (String) obj;

         if (str.length() != 0 && str.charAt(0) == 0xFEFF) {
            return (T) str.substring(1);
         } else {
            return obj;
         }
      } else if (obj instanceof InputStream) {
         BufferedInputStream in = new BufferedInputStream((InputStream) obj);

         in.mark(3);

         if (in.read() != 0xEF || in.read() != 0xBB || in.read() != 0xBF) {
            in.reset();
         }

         return (T) in;
      } else if (obj instanceof Reader) {
         BufferedReader in = new BufferedReader((Reader) obj);

         in.mark(1);

         if (in.read() != 0xFEFF) {
            in.reset();
         }

         return (T) in;
      } else {
         return obj;
      }
   }
   protected Any buildAny(String qName, Attributes attributes) {
      Any any = new Any();
      int length = attributes == null ? 0 : attributes.getLength();

      any.setName(qName);

      if (length > 0) {
         Map<String, String> dynamicAttributes = any.getAttributes();

         for (int i = 0; i < length; i++) {
            String name = attributes.getQName(i);
            String value = attributes.getValue(i);

            dynamicAttributes.put(name, value);
         }
      }

      return any;
   }

   @SuppressWarnings("unchecked")
   protected <T> T convert(Class<T> type, String value, T defaultValue) {
      if (value == null || value.length() == 0) {
         return defaultValue;
      }

      if (type == Boolean.class) {
         return (T) Boolean.valueOf(value);
      } else if (type == Integer.class) {
         return (T) Integer.valueOf(value);
      } else if (type == Long.class) {
         return (T) Long.valueOf(value);
      } else if (type == Short.class) {
         return (T) Short.valueOf(value);
      } else if (type == Float.class) {
         return (T) Float.valueOf(value);
      } else if (type == Double.class) {
         return (T) Double.valueOf(value);
      } else if (type == Byte.class) {
         return (T) Byte.valueOf(value);
      } else if (type == Character.class) {
         return (T) (Character) value.charAt(0);
      } else {
         return (T) value;
      }
   }

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException {
      m_text.append(ch, start, length);
   }

   @Override
   public void endDocument() throws SAXException {
      m_linker.finish();
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (uri == null || uri.length() == 0) {
         Object currentObj = m_objs.pop();
         String currentTag = m_tags.pop();

         if (currentObj instanceof ComponentModel) {
            ComponentModel component = (ComponentModel) currentObj;

            if (ELEMENT_ROLE.equals(currentTag)) {
               component.setRole(getText());
            } else if (ELEMENT_ROLE_HINT.equals(currentTag)) {
               component.setRoleHint(getText());
            } else if (ELEMENT_IMPLEMENTATION.equals(currentTag)) {
               component.setImplementation(getText());
            } else if (ELEMENT_INSTANTIATION_STRATEGY.equals(currentTag)) {
               component.setInstantiationStrategy(getText());
            }
         } else if (currentObj instanceof RequirementModel) {
            RequirementModel requirement = (RequirementModel) currentObj;

            if (ELEMENT_ROLE.equals(currentTag)) {
               requirement.setRole(getText());
            } else if (ELEMENT_ROLE_HINT.equals(currentTag)) {
               requirement.setRoleHint(getText());
            } else if (ELEMENT_FIELD_NAME.equals(currentTag)) {
               requirement.setFieldName(getText());
            }
         } else if (currentObj instanceof Any) {
            if (m_text.toString().length() != 0) {
               ((Any) currentObj).setValue(m_text.toString());
            }
         } else if (currentObj instanceof ComponentModel) {
            if (m_text.toString().length() != 0) {
               ((ComponentModel) currentObj).getDynamicElements().add(new Any().setValue(m_text.toString()));
            }
         } else if (currentObj instanceof ConfigurationModel) {
            if (m_text.toString().length() != 0) {
               ((ConfigurationModel) currentObj).getDynamicElements().add(new Any().setValue(m_text.toString()));
            }
         } else if (currentObj instanceof RequirementModel) {
            if (m_text.toString().length() != 0) {
               ((RequirementModel) currentObj).getDynamicElements().add(new Any().setValue(m_text.toString()));
            }
         }
      }

      m_text.setLength(0);
   }

   private IEntity<?> getEntity() {
      return m_entity;
   }

   protected String getText() {
      return m_text.toString();
   }

   private void parseForComponent(ComponentModel parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ELEMENT_ROLE.equals(qName) || ELEMENT_ROLE_HINT.equals(qName) || ELEMENT_IMPLEMENTATION.equals(qName) || ELEMENT_INSTANTIATION_STRATEGY.equals(qName) || ENTITY_REQUIREMENTS.equals(qName)) {
         m_objs.push(parentObj);
      } else if (ENTITY_CONFIGURATION.equals(qName)) {
         ConfigurationModel configuration = m_maker.buildConfiguration(attributes);

         m_linker.onConfiguration(parentObj, configuration);
         m_objs.push(configuration);
      } else if (ENTITY_REQUIREMENT.equals(qName)) {
         RequirementModel requirement = m_maker.buildRequirement(attributes);

         m_linker.onRequirement(parentObj, requirement);
         m_objs.push(requirement);
      } else {
         if (m_text.toString().length() != 0) {
            Any any = new Any().setValue(m_text.toString());

            parentObj.getDynamicElements().add(any);
         }

         Any any = buildAny(qName, attributes);

         parentObj.getDynamicElements().add(any);
         m_objs.push(any);
      }

      m_tags.push(qName);
   }

   private void parseForConfiguration(ConfigurationModel parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (m_text.toString().length() != 0) {
         Any any = new Any().setValue(m_text.toString());

         parentObj.getDynamicElements().add(any);
      }

      Any any = buildAny(qName, attributes);

      parentObj.getDynamicElements().add(any);
      m_objs.push(any);
      m_tags.push(qName);
   }

   private void parseForPlexus(PlexusModel parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ENTITY_COMPONENTS.equals(qName)) {
         m_objs.push(parentObj);
      } else if (ENTITY_COMPONENT.equals(qName)) {
         ComponentModel component = m_maker.buildComponent(attributes);

         m_linker.onComponent(parentObj, component);
         m_objs.push(component);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under plexus!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForRequirement(RequirementModel parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ELEMENT_ROLE.equals(qName) || ELEMENT_ROLE_HINT.equals(qName) || ELEMENT_FIELD_NAME.equals(qName)) {
         m_objs.push(parentObj);
      } else {
         if (m_text.toString().length() != 0) {
            Any any = new Any().setValue(m_text.toString());

            parentObj.getDynamicElements().add(any);
         }

         Any any = buildAny(qName, attributes);

         parentObj.getDynamicElements().add(any);
         m_objs.push(any);
      }

      m_tags.push(qName);
   }

   private void parseRoot(String qName, Attributes attributes) throws SAXException {
      if (ENTITY_PLEXUS.equals(qName)) {
         PlexusModel plexus = m_maker.buildPlexus(attributes);

         m_entity = plexus;
         m_objs.push(plexus);
         m_tags.push(qName);
      } else if (ENTITY_COMPONENT.equals(qName)) {
         ComponentModel component = m_maker.buildComponent(attributes);

         m_entity = component;
         m_objs.push(component);
         m_tags.push(qName);
      } else if (ENTITY_CONFIGURATION.equals(qName)) {
         ConfigurationModel configuration = m_maker.buildConfiguration(attributes);

         m_entity = configuration;
         m_objs.push(configuration);
         m_tags.push(qName);
      } else if (ENTITY_REQUIREMENT.equals(qName)) {
         RequirementModel requirement = m_maker.buildRequirement(attributes);

         m_entity = requirement;
         m_objs.push(requirement);
         m_tags.push(qName);
      } else {
         throw new SAXException("Unknown root element(" + qName + ") found!");
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (uri == null || uri.length() == 0) {
         if (m_objs.isEmpty()) { // root
            parseRoot(qName, attributes);
         } else {
            Object parent = m_objs.peek();
            String tag = m_tags.peek();

            if (parent instanceof Any) {
               Any any = buildAny(qName, attributes);

               ((Any) parent).addChild(any);
               m_objs.push(any);
               m_tags.push(qName);
            } else if (parent instanceof PlexusModel) {
               parseForPlexus((PlexusModel) parent, tag, qName, attributes);
            } else if (parent instanceof ComponentModel) {
               parseForComponent((ComponentModel) parent, tag, qName, attributes);
            } else if (parent instanceof ConfigurationModel) {
               parseForConfiguration((ConfigurationModel) parent, tag, qName, attributes);
            } else if (parent instanceof RequirementModel) {
               parseForRequirement((RequirementModel) parent, tag, qName, attributes);
            } else {
               throw new RuntimeException(String.format("Unknown entity(%s) under %s!", qName, parent.getClass().getName()));
            }
         }

         m_text.setLength(0);
        } else {
         throw new SAXException(String.format("Namespace(%s) is not supported by %s.", uri, this.getClass().getName()));
      }
   }
}
