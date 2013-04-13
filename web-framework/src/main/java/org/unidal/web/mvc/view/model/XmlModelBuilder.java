package org.unidal.web.mvc.view.model;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.unidal.web.mvc.view.annotation.AttributeMeta;
import org.unidal.web.mvc.view.annotation.ElementMeta;
import org.unidal.web.mvc.view.annotation.EntityMeta;

public class XmlModelBuilder implements ModelBuilder {
   @Override
   public String build(ModelDescriptor descriptor, Object model) {
      StringBuilder sb = new StringBuilder(8192);
      String name = descriptor.getModelName();

      sb.append('<').append(name);
      buildAttributes(sb, descriptor, model);
      sb.append(">\r\n");

      buildElements(sb, descriptor, model);
      buildEntities(sb, descriptor, model);

      sb.append("</").append(name).append(">\r\n");

      return sb.toString();
   }

   private void buildAttributes(StringBuilder sb, ModelDescriptor descriptor, Object model) {
      for (Field field : descriptor.getAttributeFields()) {
         AttributeMeta attribute = field.getAnnotation(AttributeMeta.class);
         Object value = getFieldValue(field, model);

         if (value != null) {
            String name = getNormalizedName(attribute.value(), field);
            String str = getString(value, attribute.format());

            sb.append(' ').append(name).append("=\"").append(escape(str)).append("\"");
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void buildElements(StringBuilder sb, ModelDescriptor descriptor, Object model) {
      for (Field field : descriptor.getElementFields()) {
         ElementMeta element = field.getAnnotation(ElementMeta.class);
         Object value = getFieldValue(field, model);

         if (value != null) {
            String name = getNormalizedName(element.value(), field);

            if (element.multiple()) {
               String names = element.names();

               if (names.length() > 0) {
                  sb.append('<').append(names).append(">\r\n");
               }

               if (value instanceof Collection) {
                  for (Object item : (Collection<Object>) value) {
                     if (item != null) {
                        String str = getString(item, element.format());

                        sb.append('<').append(name).append('>').append(escape(str)).append("</").append(name).append(">\r\n");
                     }
                  }
               } else if (value.getClass().isArray()) {
                  int len = Array.getLength(value);

                  for (int i = 0; i < len; i++) {
                     Object item = Array.get(value, i);

                     if (item != null) {
                        String str = getString(item, element.format());

                        sb.append('<').append(name).append('>').append(escape(str)).append("</").append(name).append(">\r\n");
                     }
                  }
               } else if (value instanceof Map) {
                  for (Object item : ((Map<Object, Object>) value).values()) {
                     if (item != null) {
                        String str = getString(item, element.format());

                        sb.append('<').append(name).append('>').append(escape(str)).append("</").append(name).append(">\r\n");
                     }
                  }
               } else {
                  throw new UnsupportedOperationException(String.format("%s(multiple=true) is not support for type(%s)",
                        ElementMeta.class.getSimpleName(), value.getClass()));
               }

               if (names.length() > 0) {
                  sb.append("</").append(names).append(">\r\n");
               }
            } else {
               String str = getString(value, element.format());

               sb.append('<').append(name).append('>').append(escape(str)).append("</").append(name).append(">\r\n");
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void buildEntities(StringBuilder sb, ModelDescriptor descriptor, Object model) {
      for (Field field : descriptor.getEntityFields()) {
         EntityMeta entity = field.getAnnotation(EntityMeta.class);
         Object value = getFieldValue(field, model);

         if (value != null) {
            String name = getNormalizedName(entity.value(), field);

            if (entity.multiple()) {
               String names = entity.names();

               if (names.length() > 0) {
                  sb.append('<').append(names).append(">\r\n");
               }

               if (value instanceof Collection) {
                  for (Object item : (Collection<Object>) value) {
                     if (item != null) {
                        String str = getModel(item, name);

                        sb.append(str).append("\r\n");
                     }
                  }
               } else if (value.getClass().isArray()) {
                  int len = Array.getLength(value);

                  for (int i = 0; i < len; i++) {
                     Object item = Array.get(value, i);

                     if (item != null) {
                        String str = getModel(item, name);

                        sb.append(str).append("\r\n");
                     }
                  }
               } else if (value instanceof Map) {
                  for (Object item : ((Map<Object, Object>) value).values()) {
                     if (item != null) {
                        String str = getModel(item, name);

                        sb.append(str).append("\r\n");
                     }
                  }
               } else {
                  throw new UnsupportedOperationException(String.format("%s(multiple=true) is not support for type(%s)",
                        EntityMeta.class.getSimpleName(), value.getClass()));
               }

               if (names.length() > 0) {
                  sb.append("</").append(names).append(">\r\n");
               }
            } else {
               String str = getModel(value, name);

               sb.append(str).append("\r\n");
            }
         }
      }
   }

   private String escape(Object value) {
      return escape(value, false);
   }

   private String escape(Object value, boolean text) {
      if (value == null) {
         return null;
      }

      String str = value.toString();
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

   private Object getFieldValue(Field field, Object instance) {
      try {
         if (!field.isAccessible()) {
            field.setAccessible(true);
         }

         return field.get(instance);
      } catch (Exception e) {
         throw new RuntimeException(String.format("Error when getting value of field(%s) of %s", field.getName(),
               instance.getClass()));
      }
   }

   private String getModel(Object value, String name) {
      String model = value.toString();
      String prefix = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";

      if (model.startsWith(prefix)) {
         model = model.substring(prefix.length());
      }

      int pos1 = model.indexOf('>');
      int pos2 = model.indexOf(' ');
      int pos3 = model.lastIndexOf('/');
      int off;

      if (pos2 > 0 && pos2 < pos1) {
         off = pos2;
      } else {
         off = pos1;
      }

      return "<" + name + model.substring(off, pos3 + 1) + name + ">";
   }

   private String getNormalizedName(String name, Field field) {
      if (name.length() > 0) {
         return name;
      }

      String fieldName = field.getName();

      if (fieldName.startsWith("m_")) {
         return fieldName.substring(2);
      } else {
         return fieldName;
      }
   }

   private String getString(Object value, String format) {
      String str;

      if (format != null && format.length() > 0) {
         if (value instanceof Date) {
            str = new SimpleDateFormat(format).format(value);
         } else if (value instanceof Number) {
            str = new DecimalFormat(format).format(value);
         } else {
            str = value.toString();
         }
      } else {
         str = value.toString();
      }

      return str;
   }
}
