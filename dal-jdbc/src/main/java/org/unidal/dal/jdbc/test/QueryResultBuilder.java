package org.unidal.dal.jdbc.test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.unidal.dal.jdbc.raw.RawDataObject;
import org.unidal.lookup.annotation.Named;

@Named
public class QueryResultBuilder {
   public String build(List<RawDataObject> rowset) {
      StringBuilder sb = new StringBuilder(4096);
      int size = rowset.size();

      if (size > 0) {
         Map<String, Integer> headers = buildHeaders(rowset);

         buildHeaders(sb, headers);

         for (RawDataObject row : rowset) {
            buildLine(sb, headers, row.getFields());
         }

         buildFooters(sb, headers);
      }

      return sb.toString();
   }

   private void buildChars(StringBuilder sb, char ch, int size) {
      for (int i = 0; i < size; i++) {
         sb.append(ch);
      }
   }

   private void buildFooters(StringBuilder sb, Map<String, Integer> headers) {
      // first line
      for (Integer len : headers.values()) {
         sb.append('+');

         buildChars(sb, '-', len + 2);
      }

      sb.append("+");
   }

   private Map<String, Integer> buildHeaders(List<RawDataObject> rowset) {
      int size = rowset.size();
      Map<String, Integer> headers = new LinkedHashMap<String, Integer>();

      for (int i = 0; i < size; i++) {
         RawDataObject row = rowset.get(i);

         for (Map.Entry<String, Object> field : row.getFields()) {
            String name = field.getKey();
            Object value = field.getValue();
            Integer len = headers.get(name);
            String val = value == null ? "" : value.toString();

            if (len == null) {
               len = Math.max(name.length(), val.length());
            } else if (val.length() > len.intValue()) {
               len = val.length();
            }

            headers.put(name, len);
         }
      }

      return headers;
   }

   private void buildHeaders(StringBuilder sb, Map<String, Integer> headers) {
      // first line
      for (Integer len : headers.values()) {
         sb.append('+');

         buildChars(sb, '-', len + 2);
      }

      sb.append("+\r\n");

      // second line
      for (Map.Entry<String, Integer> e : headers.entrySet()) {
         String name = e.getKey();
         Integer len = e.getValue();

         sb.append("| ");

         sb.append(name);
         buildChars(sb, ' ', len + 1 - name.length());
      }

      sb.append("|\r\n");

      // third line
      for (Integer len : headers.values()) {
         sb.append('+');

         buildChars(sb, '-', len + 2);
      }

      sb.append("+\r\n");
   }

   private void buildLine(StringBuilder sb, Map<String, Integer> headers, Set<Map.Entry<String, Object>> cols) {
      for (Map.Entry<String, Object> col : cols) {
         String value = col.getValue() == null ? "" : col.getValue().toString();
         Integer len = headers.get(col.getKey());

         sb.append("| ");

         sb.append(value);
         buildChars(sb, ' ', len + 1 - value.length());
      }

      sb.append("+\r\n");
   }
}