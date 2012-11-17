package org.unidal.dal.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Readset<T> {
   private List<DataField> m_fields;

   private List<Readset<T>> m_readsets;

   public Readset(DataField... fields) {
      m_fields = Collections.unmodifiableList(Arrays.asList(fields));
      m_readsets = Collections.emptyList();
   }

   @SuppressWarnings("unchecked")
   public Readset(Readset<?>... readsets) {
      List<Readset<T>> children = new ArrayList<Readset<T>>();

      for (Readset<?> readset : readsets) {
         Readset<T> rs = (Readset<T>) readset;

         children.add(rs);
         children.addAll(rs.getChildren());
      }

      m_readsets = children;
      m_fields = Collections.emptyList();
   }

   public List<DataField> getFields() {
      return m_fields;
   }

   public List<Readset<T>> getChildren() {
      return m_readsets;
   }
}
