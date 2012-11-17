package org.unidal.dal.jdbc;

public enum Bit {
   BIT_00(0),

   BIT_01(1),

   BIT_02(2),

   BIT_03(3),

   BIT_04(4),

   BIT_05(5),

   BIT_06(6),

   BIT_07(7),

   BIT_08(8),

   BIT_09(9),

   BIT_10(10),

   BIT_11(11),

   BIT_12(12),

   BIT_13(13),

   BIT_14(14),

   BIT_15(15),

   BIT_16(16),

   BIT_17(17),

   BIT_18(18),

   BIT_19(19),

   BIT_20(20),

   BIT_21(21),

   BIT_22(22),

   BIT_23(23),

   BIT_24(24),

   BIT_25(25),

   BIT_26(26),

   BIT_27(27),

   BIT_28(28),

   BIT_29(29),

   BIT_30(30),

   BIT_31(31);

   private int m_index;
   private int m_mask;

   private Bit(int index) {
      m_index = index;
      m_mask = 1 << index;
   }

   public int getIndex() {
      return m_index;
   }

   public int getMask() {
      return m_mask;
   }

   public static Bit valueOf(int index) {
      Bit[] bits = values();

      if (index >= 0 && index < bits.length) {
         return bits[index];
      } else {
         throw new IndexOutOfBoundsException(bits.length + " < " + index);
      }
   }
}
