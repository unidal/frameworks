package org.unidal.dal.jdbc.query;

import org.unidal.dal.jdbc.DataField;

public class Parameter {
   public static final int TYPE_SINGLE_VALUE = 0;
   
   public static final int TYPE_ARRAY = 1;

   public static final int TYPE_ITERABLE = 2;

   private boolean m_in;

   private boolean m_out;

   private DataField m_field;

   private int m_outType;

   private int m_outScale;

   private int m_type; // 0: Single Value, 1: Array, 2: Iterable

   // IN only
   public Parameter(DataField field) {
      m_field = field;
      m_in = true;
   }

   // OUT only
   public Parameter(DataField field, int outType, int outScale) {
      this(field, outType, outScale, false);
   }

   // IN and/or OUT
   public Parameter(DataField field, int outType, int outScale, boolean isIn) {
      m_field = field;
      m_outType = outType;
      m_outScale = outScale;
      m_in = isIn;
      m_out = true;
   }

   public DataField getField() {
      return m_field;
   }

   public int getOutScale() {
      return m_outScale;
   }

   public int getOutType() {
      return m_outType;
   }

   public int getType() {
      return m_type;
   }

   public boolean isArray() {
      return m_type == TYPE_ARRAY;
   }

   public boolean isIn() {
      return m_in;
   }

   public boolean isIterable() {
      return m_type == TYPE_ITERABLE;
   }

   public boolean isOut() {
      return m_out;
   }

   public boolean isSingleValue() {
      return m_type == TYPE_SINGLE_VALUE;
   }

   public Parameter setType(int type) {
      m_type = type;
      return this;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(64);

      if (m_out) {
         sb.append('#');
      } else {
         sb.append('$');
      }

      sb.append('{').append(m_field.getName()).append('}');
      return sb.toString();
   }
}
