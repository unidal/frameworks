package org.unidal.converter;

public enum ConverterPriority {
   VERY_LOW(1),

   LOW(5),

   NORMAL(10),

   HIGH(20),

   VERY_HIGH(100),

   ;

   private int m_value;

   private ConverterPriority(int value) {
      m_value = value;
   }

   public int getValue() {
      return m_value;
   }
}
