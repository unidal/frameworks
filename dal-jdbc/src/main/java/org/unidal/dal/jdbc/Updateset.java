package org.unidal.dal.jdbc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Updateset<T> {
   private List<DataField> m_fields;

   public Updateset(DataField... fields) {
      m_fields = Collections.unmodifiableList(Arrays.asList(fields));
   }

   public List<DataField> getFields() {
      return m_fields;
   }
}
