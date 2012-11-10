package org.unidal.web.jsp.function;

import java.io.UnsupportedEncodingException;

public class Encoder {
   public static String htmlEncode(String str) {
      int len = str == null ? 0 : str.length();
      StringBuilder sb = new StringBuilder(len + 16);

      for (int i = 0; i < len; i++) {
         char ch = str.charAt(i);

         switch (ch) {
         case '"':
            sb.append("&quot;");
            break;
         case '<':
            sb.append("&lt;");
            break;
         case '>':
            sb.append("&gt;");
            break;
         default:
            sb.append(ch);
            break;
         }
      }

      return sb.toString();
   }

   public static String urlEncode(String str) {
      if (str == null) {
         return null;
      }

      byte[] ba;

      try {
         ba = str.getBytes("utf-8");
      } catch (UnsupportedEncodingException e) {
         ba = str.getBytes();
      }

      StringBuilder sb = new StringBuilder(ba.length + 16);

      for (int i = 0; i < ba.length; i++) {
         byte b = ba[i];

         if (b == 0x20) {
            sb.append('+');
         } else if (b < 0x30 || b > 0x7E || !Character.isLetterOrDigit(b)) {
            sb.append('%').append(Integer.toHexString(b));
         } else {
            sb.append((char) b);
         }
      }

      return sb.toString();
   }
}
