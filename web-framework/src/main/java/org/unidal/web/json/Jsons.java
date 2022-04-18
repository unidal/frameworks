package org.unidal.web.json;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.unidal.helper.Splitters;

public class Jsons {
   public static JsonMap map() {
      return new JsonMap();
   }

   public static class JsonList extends JsonNode {
      private List<JsonNode> m_list = new ArrayList<JsonNode>();

      public void add(JsonNode node) {
         m_list.add(node);
      }

      private void add(Object value) {
         if (value instanceof JsonNode) {
            m_list.add((JsonNode) value);
         } else if (value instanceof Iterable) {
            JsonList list = new JsonList();
            @SuppressWarnings("unchecked")
            Iterable<Object> items = (Iterable<Object>) value;

            for (Object item : items) {
               list.add(item);
            }

            m_list.add(list);
         } else if (value != null && value.getClass().isArray()) {
            JsonList list = new JsonList();
            int length = Array.getLength(value);

            for (int i = 0; i < length; i++) {
               Object item = Array.get(value, i);

               list.add(item);
            }

            m_list.add(list);
         } else {
            m_list.add(JsonPrimitive.of(value));
         }
      }

      @Override
      public String toString() {
         return toString(0);
      }

      @Override
      public String toString(int level) {
         StringBuilder sb = new StringBuilder();

         boolean first = true;

         sb.append("[");

         for (JsonNode item : m_list) {
            if (first) {
               first = false;
            } else {
               sb.append(", ");
            }

            sb.append(item.toString(level + 1));
         }

         sb.append("]");

         return sb.toString();
      }
   }

   public static class JsonMap extends JsonNode {
      private Map<String, JsonNode> m_map = new LinkedHashMap<String, JsonNode>();

      private JsonMap mapOf(String key) {
         JsonNode original = m_map.get(key);

         if (original != null && !(original instanceof JsonMap)) {
            throw new IllegalStateException(
                  String.format("Conflicting key(%s) found during building JsonMap(%s)!", key, this));
         }

         JsonMap map = (JsonMap) original;

         if (map == null) {
            map = new JsonMap();
            m_map.put(key, map);
         }

         return map;
      }

      private void put(List<String> keys, Object value) {
         String first = keys.remove(0);

         if (keys.isEmpty()) {
            put(first, value);
         } else {
            JsonMap map = mapOf(first);

            map.put(keys, value);
         }
      }

      private void put(String key, Object value) {
         if (value instanceof JsonNode) {
            m_map.put(key, (JsonNode) value);
         } else if (value instanceof Iterable) {
            JsonList list = new JsonList();
            @SuppressWarnings("unchecked")
            Iterable<Object> items = (Iterable<Object>) value;

            for (Object item : items) {
               list.add(item);
            }

            m_map.put(key, list);
         } else {
            m_map.put(key, JsonPrimitive.of(value));
         }
      }

      @Override
      public String toString() {
         return toString(0);
      }

      @Override
      public String toString(int level) {
         StringBuilder sb = new StringBuilder();
         boolean first = true;

         sb.append("{\r\n");

         for (Map.Entry<String, JsonNode> e : m_map.entrySet()) {
            if (first) {
               first = false;
            } else {
               sb.append(",\r\n");
            }

            indent(sb, level);
            indent(sb);
            sb.append('"').append(e.getKey()).append('"');
            sb.append(": ");
            sb.append(e.getValue().toString(level + 1));
         }

         sb.append("\r\n");
         indent(sb, level);
         sb.append("}");

         return sb.toString();
      }

      public JsonMap with(String keys, List<String> values) {
         if (values != null) {
            List<String> path = Splitters.by('.').split(keys, new LinkedList<String>());

            put(path, values);
         }

         return this;
      }

      public <T> JsonMap with(String keys, List<T> values, StringApplier<T> applier) {
         if (values != null) {
            List<String> path = Splitters.by('.').split(keys, new LinkedList<String>());
            List<String> items = new ArrayList<String>();

            for (T value : values) {
               items.add(value == null ? null : applier.apply(value));
            }

            put(path, items);
         }

         return this;
      }

      public JsonMap with(String keys, Object value) {
         if (value != null) {
            List<String> path = Splitters.by('.').split(keys, new LinkedList<String>());

            put(path, value);
         }

         return this;
      }
   }

   public static abstract class JsonNode {
      public void indent(StringBuilder sb) {
         sb.append("  ");
      }

      public void indent(StringBuilder sb, int level) {
         for (int i = 0; i < level; i++) {
            sb.append("  ");
         }
      }

      protected abstract String toString(int level);
   }

   public static class JsonPrimitive extends JsonNode {
      private Object m_value;

      public JsonPrimitive(Object value) {
         m_value = value;
      }

      public static JsonPrimitive of(Object value) {
         return new JsonPrimitive(value);
      }

      @Override
      public String toString() {
         return toString(0);
      }

      private void escape(StringBuilder sb, String value) {
         int len = value.length();

         for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);

            switch (ch) {
            case '\r':
               sb.append("\\r");
               break;
            case '\n':
               sb.append("\\n");
               break;
            case '\t':
               sb.append("\\t");
               break;
            case '\\':
            case '"':
               sb.append('\\');
               sb.append(ch);
               break;
            default:
               sb.append(ch);
               break;
            }
         }
      }

      @Override
      public String toString(int level) {
         StringBuilder sb = new StringBuilder();

         if (m_value == null) {
            sb.append("null");
         } else if (m_value instanceof String) {
            sb.append('"');
            escape(sb, (String) m_value);
            sb.append('"');
         } else if (m_value instanceof Boolean) {
            sb.append(m_value.toString());
         } else if (m_value instanceof Number) {
            sb.append(m_value.toString());
         } else {
            sb.append(m_value.toString());
         }

         return sb.toString();
      }
   }

   public static interface StringApplier<T> {
      public String apply(T value);
   }
}
