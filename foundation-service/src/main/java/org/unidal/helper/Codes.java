package org.unidal.helper;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Codes {
   public static Decoder forDecode() {
      return new Decoder();
   }

   public static class Decoder {
      public String decode(String src) {
         int len = src.length();
         ByteBuffer bb = ByteBuffer.allocate((len - 3) / 2);
         int p = Character.digit(src.charAt(0), 16) & 0x07;
         int q = Character.digit(src.charAt(1), 16);
         int k = Character.digit(src.charAt(2), 16);

         for (int i = 3; i < len; i += 2) {
            byte high = (byte) (Character.digit(src.charAt(i), 16) & 0xFF);
            byte low = (byte) (Character.digit(src.charAt(i + 1), 16) & 0xFF);

            bb.put((byte) (high << 4 | low));
         }

         byte[] data = (byte[]) bb.flip().array();

         Bytes.forBits().mask(data, k);
         Bytes.forBits().swap(data, p, q);

         try {
            return new String(data, 0, data.length - 13, "utf-8");
         } catch (IOException e) {
            return new String(data, 0, data.length - 13);
         }
      }
   }
}
