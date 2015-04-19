package org.unidal.dal.jdbc.test.function;

import java.security.MessageDigest;

public class StringFunction {
   private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

   public static String password(String text) throws Exception {
      if (text == null) {
         return null;
      }

      MessageDigest md = MessageDigest.getInstance("SHA-1");
      byte[] data = new byte[20];

      md.update(text.getBytes("utf-8"));
      md.digest(data, 0, data.length);

      StringBuilder sb = new StringBuilder();

      sb.append('*');

      for (int i = 0; i < data.length; i++) {
         byte b = data[i];

         sb.append(hexDigits[(b >> 4) & 0x0F]);
         sb.append(hexDigits[b & 0x0F]);
      }

      return sb.toString();
   }
}
