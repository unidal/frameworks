package org.unidal.lookup.container.model.transform;

import static org.unidal.lookup.container.model.Constants.ELEMENT_FIELD_NAME;
import static org.unidal.lookup.container.model.Constants.ELEMENT_IMPLEMENTATION;
import static org.unidal.lookup.container.model.Constants.ELEMENT_INSTANTIATION_STRATEGY;
import static org.unidal.lookup.container.model.Constants.ELEMENT_ROLE;
import static org.unidal.lookup.container.model.Constants.ELEMENT_ROLE_HINT;
import org.unidal.lookup.container.model.entity.Any;
import static org.unidal.lookup.container.model.Constants.ENTITY_COMPONENT;
import static org.unidal.lookup.container.model.Constants.ENTITY_COMPONENTS;
import static org.unidal.lookup.container.model.Constants.ENTITY_CONFIGURATION;
import static org.unidal.lookup.container.model.Constants.ENTITY_PLEXUS;
import static org.unidal.lookup.container.model.Constants.ENTITY_REQUIREMENT;
import static org.unidal.lookup.container.model.Constants.ENTITY_REQUIREMENTS;

import java.lang.reflect.Array;
import java.util.Collection;

import org.unidal.lookup.container.model.IEntity;
import org.unidal.lookup.container.model.IVisitor;
import org.unidal.lookup.container.model.entity.ComponentModel;
import org.unidal.lookup.container.model.entity.ConfigurationModel;
import org.unidal.lookup.container.model.entity.PlexusModel;
import org.unidal.lookup.container.model.entity.RequirementModel;

public class DefaultXmlBuilder implements IVisitor {

   private IVisitor m_visitor = this;

   private int m_level;

   private StringBuilder m_sb;

   private boolean m_compact;

   public DefaultXmlBuilder() {
      this(false);
   }

   public DefaultXmlBuilder(boolean compact) {
      this(compact, new StringBuilder(4096));
   }

   public DefaultXmlBuilder(boolean compact, StringBuilder sb) {
      m_compact = compact;
      m_sb = sb;
      m_sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
   }

   public String buildXml(IEntity<?> entity) {
      entity.accept(m_visitor);
      return m_sb.toString();
   }

   protected void endTag(String name) {
      m_level--;

      indent();
      m_sb.append("</").append(name).append(">\r\n");
   }

   protected String escape(Object value) {
      return escape(value, false);
   }
   
   protected String escape(Object value, boolean text) {
      if (value == null) {
         return null;
      }

      String str = toString(value);
      int len = str.length();
      StringBuilder sb = new StringBuilder(len + 16);

      for (int i = 0; i < len; i++) {
         final char ch = str.charAt(i);

         switch (ch) {
         case '<':
            sb.append("&lt;");
            break;
         case '>':
            sb.append("&gt;");
            break;
         case '&':
            sb.append("&amp;");
            break;
         case '"':
            if (!text) {
               sb.append("&quot;");
               break;
            }
         default:
            sb.append(ch);
            break;
         }
      }

      return sb.toString();
   }
   
   protected void indent() {
      if (!m_compact) {
         for (int i = m_level - 1; i >= 0; i--) {
            m_sb.append("   ");
         }
      }
   }

   protected void startTag(String name) {
      startTag(name, false, null);
   }
   
   protected void startTag(String name, boolean closed, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      startTag(name, null, closed, dynamicAttributes, nameValues);
   }

   protected void startTag(String name, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      startTag(name, null, false, dynamicAttributes, nameValues);
   }

   protected void startTag(String name, Object text, boolean closed, java.util.Map<String, String> dynamicAttributes, Object... nameValues) {
      indent();

      m_sb.append('<').append(name);

      int len = nameValues.length;

      for (int i = 0; i + 1 < len; i += 2) {
         Object attrName = nameValues[i];
         Object attrValue = nameValues[i + 1];

         if (attrValue != null) {
            m_sb.append(' ').append(attrName).append("=\"").append(escape(attrValue)).append('"');
         }
      }

      if (dynamicAttributes != null) {
         for (java.util.Map.Entry<String, String> e : dynamicAttributes.entrySet()) {
            m_sb.append(' ').append(e.getKey()).append("=\"").append(escape(e.getValue())).append('"');
         }
      }

      if (text != null && closed) {
         m_sb.append('>');
         m_sb.append(escape(text, true));
         m_sb.append("</").append(name).append(">\r\n");
      } else {
         if (closed) {
            m_sb.append('/');
         } else {
            m_level++;
         }
   
         m_sb.append(">\r\n");
      }
   }

   @SuppressWarnings("unchecked")
   protected String toString(Object value) {
      if (value instanceof String) {
         return (String) value;
      } else if (value instanceof Collection) {
         Collection<Object> list = (Collection<Object>) value;
         StringBuilder sb = new StringBuilder(32);
         boolean first = true;

         for (Object item : list) {
            if (first) {
               first = false;
            } else {
               sb.append(',');
            }

            if (item != null) {
               sb.append(item);
            }
         }

         return sb.toString();
      } else if (value.getClass().isArray()) {
         int len = Array.getLength(value);
         StringBuilder sb = new StringBuilder(32);
         boolean first = true;

         for (int i = 0; i < len; i++) {
            Object item = Array.get(value, i);

            if (first) {
               first = false;
            } else {
               sb.append(',');
            }

            if (item != null) {
               sb.append(item);
            }
         }
		
         return sb.toString();
      }
 
      return String.valueOf(value);
   }

   protected void tagWithText(String name, String text, Object... nameValues) {
      if (text == null) {
         return;
      }
      
      indent();

      m_sb.append('<').append(name);

      int len = nameValues.length;

      for (int i = 0; i + 1 < len; i += 2) {
         Object attrName = nameValues[i];
         Object attrValue = nameValues[i + 1];

         if (attrValue != null) {
            m_sb.append(' ').append(attrName).append("=\"").append(escape(attrValue)).append('"');
         }
      }

      m_sb.append(">");
      m_sb.append(escape(text, true));
      m_sb.append("</").append(name).append(">\r\n");
   }

   protected void element(String name, String text, String defaultValue, boolean escape) {
      if (text == null || text.equals(defaultValue)) {
         return;
      }
      
      indent();
      
      m_sb.append('<').append(name).append(">");
      
      if (escape) {
         m_sb.append(escape(text, true));
      } else {
         m_sb.append("<![CDATA[").append(text).append("]]>");
      }
      
      m_sb.append("</").append(name).append(">\r\n");
   }

   @Override
   public void visitAny(Any any) {
      if (any.getChildren().isEmpty()) {
         if (!any.getAttributes().isEmpty()) {
            if (any.hasValue() && any.getValue().length() != 0) {
               startTag(any.getName(), false, any.getAttributes());
               m_sb.setLength(m_sb.length() - 2);
               m_sb.append(any.getValue());
               endTag(any.getName());
               m_sb.setLength(m_sb.length() - 2);
            } else {
               startTag(any.getName(), true, any.getAttributes());
            }
         } else if (any.hasValue()) {
            if (any.getName() == null) {
               m_sb.append(any.getValue());
            } else {
               tagWithText(any.getName(), any.getValue());
            }
         }
      } else {
         startTag(any.getName(), false, any.getAttributes());

         if (m_compact) {
            m_sb.setLength(m_sb.length() - 2);
         }

         for (Any child : any.getChildren()) {
            child.accept(m_visitor);
         }

         endTag(any.getName());

         if (m_compact) {
            m_sb.setLength(m_sb.length() - 2);
         }
      }
   }

   @Override
   public void visitComponent(ComponentModel component) {
      startTag(ENTITY_COMPONENT, null);

      element(ELEMENT_ROLE, component.getRole(), null,  true);

      element(ELEMENT_ROLE_HINT, component.getRoleHint(), null,  true);

      element(ELEMENT_IMPLEMENTATION, component.getImplementation(), null,  true);

      element(ELEMENT_INSTANTIATION_STRATEGY, component.getInstantiationStrategy(), null,  true);

      if (component.getConfiguration() != null) {
         component.getConfiguration().accept(m_visitor);
      }

      if (!component.getRequirements().isEmpty()) {
         startTag(ENTITY_REQUIREMENTS);

         for (RequirementModel requirement : component.getRequirements()) {
            requirement.accept(m_visitor);
         }

         endTag(ENTITY_REQUIREMENTS);
      }

      for (Any any : component.getDynamicElements()) {
         any.accept(m_visitor);
      }

      endTag(ENTITY_COMPONENT);
   }

   @Override
   public void visitConfiguration(ConfigurationModel configuration) {
      startTag(ENTITY_CONFIGURATION, null);

      for (Any any : configuration.getDynamicElements()) {
         any.accept(m_visitor);
      }

      endTag(ENTITY_CONFIGURATION);
   }

   @Override
   public void visitPlexus(PlexusModel plexus) {
      startTag(ENTITY_PLEXUS, null);

      if (!plexus.getComponents().isEmpty()) {
         startTag(ENTITY_COMPONENTS);

         for (ComponentModel component : plexus.getComponents()) {
            component.accept(m_visitor);
         }

         endTag(ENTITY_COMPONENTS);
      }

      endTag(ENTITY_PLEXUS);
   }

   @Override
   public void visitRequirement(RequirementModel requirement) {
      startTag(ENTITY_REQUIREMENT, null);

      element(ELEMENT_ROLE, requirement.getRole(), null,  true);

      element(ELEMENT_ROLE_HINT, requirement.getRoleHint(), "default",  true);

      element(ELEMENT_FIELD_NAME, requirement.getFieldName(), null,  true);

      for (Any any : requirement.getDynamicElements()) {
         any.accept(m_visitor);
      }

      endTag(ENTITY_REQUIREMENT);
   }
}
