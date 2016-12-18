package org.unidal.web.mvc.view.model;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.unidal.helper.Objects;
import org.unidal.helper.Objects.JsonBuilder;
import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.view.annotation.AttributeMeta;
import org.unidal.web.mvc.view.annotation.ElementMeta;
import org.unidal.web.mvc.view.annotation.EntityMeta;
import org.unidal.web.mvc.view.annotation.PojoMeta;

@Named(type = ModelBuilder.class, value = JsonModelBuilder.ID)
public class JsonModelBuilder implements ModelBuilder {
   public static final String ID = "json";

   @Override
   public String build(ModelDescriptor descriptor, Object model) {
      JsonBuilder sb = Objects.newJsonBuilder(8192);

      sb.raw("{");
      buildAttributes(sb, descriptor, model);
      buildElements(sb, descriptor, model);
      buildEntities(sb, descriptor, model);
      buildPojos(sb, descriptor, model);

      sb.trimComma();
      sb.raw("}\r\n");

      return sb.toString();
   }

   private void buildAttributes(JsonBuilder sb, ModelDescriptor descriptor, Object model) {
      for (Field field : descriptor.getAttributeFields()) {
         AttributeMeta attribute = field.getAnnotation(AttributeMeta.class);
         Object value = getFieldValue(field, model);

         if (value != null) {
            String name = getNormalizedName(attribute.value(), field);
            String str = getString(value, attribute.format());

            sb.key(name).colon().value(str).comma();
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void buildElements(JsonBuilder sb, ModelDescriptor descriptor, Object model) {
      for (Field field : descriptor.getElementFields()) {
         ElementMeta element = field.getAnnotation(ElementMeta.class);
         Object value = getFieldValue(field, model);

         if (value != null) {
            String name = getNormalizedName(element.value(), field);

            sb.key(name).colon();

            if (element.multiple()) {
               sb.raw("[");

               if (value instanceof Collection) {
                  for (Object item : (Collection<Object>) value) {
                     if (item != null) {
                        String str = getString(item, element.format());

                        sb.value(str).comma();
                     }
                  }
               } else if (value.getClass().isArray()) {
                  int len = Array.getLength(value);

                  for (int i = 0; i < len; i++) {
                     Object item = Array.get(value, i);

                     if (item != null) {
                        String str = getString(item, element.format());

                        sb.value(str).comma();
                     }
                  }
               } else if (value instanceof Map) {
                  for (Object item : ((Map<Object, Object>) value).values()) {
                     if (item != null) {
                        String str = getString(item, element.format());

                        sb.value(str).comma();
                     }
                  }
               } else {
                  throw new UnsupportedOperationException(String.format(
                        "%s(multiple=true) is not support for type(%s)", ElementMeta.class.getSimpleName(),
                        value.getClass()));
               }

               sb.trimComma();
               sb.raw("]").comma();
            } else {
               String str = getString(value, element.format());

               sb.value(str).comma();
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void buildEntities(JsonBuilder sb, ModelDescriptor descriptor, Object model) {
      for (Field field : descriptor.getEntityFields()) {
         EntityMeta entity = field.getAnnotation(EntityMeta.class);
         Object value = getFieldValue(field, model);

         if (value != null) {
            String name = getNormalizedName(entity.value(), field);

            sb.key(name).colon();

            if (entity.multiple()) {
               sb.raw("[");

               if (value instanceof Collection) {
                  for (Object item : (Collection<Object>) value) {
                     if (item != null) {
                        sb.raw(String.format("%#s", item)).comma();
                     }
                  }
               } else if (value.getClass().isArray()) {
                  int len = Array.getLength(value);

                  for (int i = 0; i < len; i++) {
                     Object item = Array.get(value, i);

                     if (item != null) {
                        sb.raw(String.format("%#s", item)).comma();
                     }
                  }
               } else if (value instanceof Map) {
                  for (Object item : ((Map<Object, Object>) value).values()) {
                     if (item != null) {
                        sb.raw(String.format("%#s", item)).comma();
                     }
                  }
               } else {
                  throw new UnsupportedOperationException(String.format(
                        "%s(multiple=true) is not support for type(%s)", EntityMeta.class.getSimpleName(),
                        value.getClass()));
               }

               sb.trimComma();
               sb.raw("]");
            } else {
               sb.raw(String.format("%#s", value)).comma();
            }
         }
      }
   }

   private void buildPojos(JsonBuilder sb, ModelDescriptor descriptor, Object model) {
      for (Field field : descriptor.getPojoFields()) {
         PojoMeta entity = field.getAnnotation(PojoMeta.class);
         Object value = getFieldValue(field, model);

         if (value != null) {
            String name = getNormalizedName(entity.value(), field);
            String str = Objects.forJson().from(value);

            sb.key(name).colon().raw(str).comma();
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
}