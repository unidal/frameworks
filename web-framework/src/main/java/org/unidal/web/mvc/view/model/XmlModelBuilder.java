package org.unidal.web.mvc.view.model;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.unidal.helper.Objects;
import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.view.annotation.AttributeMeta;
import org.unidal.web.mvc.view.annotation.ElementMeta;
import org.unidal.web.mvc.view.annotation.EntityMeta;
import org.unidal.web.mvc.view.annotation.PojoMeta;

@Named(type = ModelBuilder.class, value = XmlModelBuilder.ID)
public class XmlModelBuilder implements ModelBuilder {
   public static final String ID = "xml";
   
   @Override
   public String build(ModelDescriptor descriptor, Object model) {
      StringBuilder sb = new StringBuilder(8192);
      String name = descriptor.getModelName();

      sb.append('<').append(name);
      buildAttributes(sb, descriptor, model);
      sb.append(">\r\n");

      buildElements(sb, descriptor, model);
      buildEntities(sb, descriptor, model);
      buildPojos(sb, descriptor, model);

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

            XmlBuilder.ATTRIBUTE.build(sb, name, str);
         }
      }
   }

   private void buildElements(StringBuilder sb, ModelDescriptor descriptor, Object model) {
      for (Field field : descriptor.getElementFields()) {
         ElementMeta element = field.getAnnotation(ElementMeta.class);
         Object value = getFieldValue(field, model);

         if (value != null) {
            String name = getNormalizedName(element.value(), field);

            if (element.multiple()) {
               buildMutlple(sb, element.names(), name, value, element.format(), ElementMeta.class, XmlBuilder.ELEMENT);
            } else {
               String str = getString(value, element.format());

               XmlBuilder.ELEMENT.build(sb, name, str);
            }
         }
      }
   }

   private void buildEntities(StringBuilder sb, ModelDescriptor descriptor, Object model) {
      for (Field field : descriptor.getEntityFields()) {
         EntityMeta entity = field.getAnnotation(EntityMeta.class);
         Object value = getFieldValue(field, model);

         if (value != null) {
            String name = getNormalizedName(entity.value(), field);

            if (entity.multiple()) {
               buildMutlple(sb, entity.names(), name, value, null, EntityMeta.class, XmlBuilder.ENTITY);
            } else {
               XmlBuilder.ENTITY.build(sb, name, value);
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void buildMutlple(StringBuilder sb, String names, String name, Object value, String format,
         Class<?> metaClass, XmlBuilder builder) {
      if (names.length() > 0) {
         sb.append('<').append(names).append(">\r\n");
      }

      if (value instanceof Collection) {
         for (Object item : (Collection<Object>) value) {
            if (item != null) {
               if (format != null) {
                  item = getString(item, format);
               }

               builder.build(sb, name, item);
            }
         }
      } else if (value.getClass().isArray()) {
         int len = Array.getLength(value);

         for (int i = 0; i < len; i++) {
            Object item = Array.get(value, i);

            if (item != null) {
               if (format != null) {
                  item = getString(item, format);
               }

               builder.build(sb, name, item);
            }
         }
      } else if (value instanceof Map) {
         for (Object item : ((Map<Object, Object>) value).values()) {
            if (item != null) {
               if (format != null) {
                  item = getString(item, format);
               }

               builder.build(sb, name, item);
            }
         }
      } else {
         throw new UnsupportedOperationException(String.format("%s(multiple=true) is not support for type(%s) on %s!",
               metaClass.getSimpleName(), value.getClass(), name));
      }

      if (names.length() > 0) {
         sb.append("</").append(names).append(">\r\n");
      }
   }

   private void buildPojos(StringBuilder sb, ModelDescriptor descriptor, Object model) {
      for (Field field : descriptor.getPojoFields()) {
         PojoMeta pojo = field.getAnnotation(PojoMeta.class);
         Object value = getFieldValue(field, model);

         if (value != null) {
            String name = getNormalizedName(pojo.value(), field);

            if (pojo.multiple()) {
               buildMutlple(sb, pojo.names(), name, value, null, PojoMeta.class, XmlBuilder.POJO);
            } else {
               XmlBuilder.POJO.build(sb, name, value);
            }
         }
      }
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

   enum XmlBuilder {
      ENTITY {
         @Override
         public void build(StringBuilder sb, String tag, Object value) {
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

            if (model.trim().endsWith("/>")) {
               sb.append("<").append(tag).append(model.substring(off, pos3 + 1)).append(">\r\n");
            } else if (off >= 0) {
               sb.append("<").append(tag).append(model.substring(off, pos3 + 1)).append(tag).append(">\r\n");
            } else {
               sb.append("<").append(tag).append(">").append(model).append("</").append(tag).append(">\r\n");
            }
         }
      },

      POJO {
         @Override
         public void build(StringBuilder sb, String tag, Object value) {
            sb.append(Objects.forXml().from(tag, value));
         }
      },

      ATTRIBUTE {
         @Override
         public void build(StringBuilder sb, String tag, Object value) {
            sb.append(' ').append(tag).append("=\"").append(escape((String) value, false)).append("\"");
         }
      },

      ELEMENT {
         @Override
         public void build(StringBuilder sb, String tag, Object value) {
            sb.append('<').append(tag).append('>').append(escape((String) value, true)).append("</").append(tag)
                  .append(">\r\n");
         }
      };

      private static String escape(Object value, boolean text) {
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

      public abstract void build(StringBuilder sb, String tag, Object value);
   }
}
