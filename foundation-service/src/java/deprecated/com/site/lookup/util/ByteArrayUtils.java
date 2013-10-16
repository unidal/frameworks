package com.site.lookup.util;

public class ByteArrayUtils {
   public static byte[] trim(byte[] src) {
      int start = 0;
      int end = src.length;
      boolean trimed = false;

      for (int i = start; i < end; i++) {
         byte b = src[i];

         if (b == ' ' || b == '\t' || b == '\r' || b == '\n' || b == '\f') {
            start++;
            trimed = true;
         } else {
            break;
         }
      }

      for (int i = end - 1; i >= start; i--) {
         byte b = src[i];

         if (b == ' ' || b == '\t' || b == '\r' || b == '\n' || b == '\f') {
            end--;
            trimed = true;
         } else {
            break;
         }
      }

      if (trimed) {
         byte[] dst = new byte[end - start];

         System.arraycopy(src, start, dst, 0, end - start);
         return dst;
      } else {
         return src;
      }
   }
}
