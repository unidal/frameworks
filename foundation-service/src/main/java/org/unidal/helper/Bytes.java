package org.unidal.helper;

public class Bytes {
   public static Bits forBits() {
      return new Bits();
   }

   public static class Bits {
      public void mask(byte[] data, int k) {
         for (int i = data.length - 1; i >= 0; i--) {
            data[i] ^= k;
         }
      }

      public void swap(byte[] data, int p, int q) {
         int len = data.length * 8;

         for (int i = 0; i < len; i += p) {
            int j = i + q;

            if (j < len) {
               byte b1 = data[i / 8];
               byte b2 = data[j / 8];
               int f1 = b1 & (1 << (i % 8));
               int f2 = b2 & (1 << (j % 8));

               if ((f1 != 0) != (f2 != 0)) {
                  data[i / 8] ^= 1 << (i % 8);
                  data[j / 8] ^= 1 << (j % 8);
               }
            }
         }
      }

      public String hex(byte[] data) {
         StringBuilder sb = new StringBuilder(data.length * 2);

         for (byte d : data) {
            sb.append(Integer.toHexString(d >> 4 & 0x0F));
            sb.append(Integer.toHexString(d & 0x0F));
         }

         return sb.toString();
      }
   }
}
