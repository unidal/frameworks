package org.unidal.web.jsp.function;

public class Decoder {
   public static String urlDecode(String str) {
      if (str == null) {
         return null;
      }

      int len = str.length();
      StringBuilder sb = new StringBuilder(len);

      for (int i = 0; i < len; i++) {
         char ch = str.charAt(i);

         if (ch == '%') {
            if (i + 2 < len) {
               sb.append((char) (Integer.parseInt(str.substring(i + 1, i + 1 + 2), 16)));
               i += 2;
            }
         } else if (ch == '+') {
            sb.append(' ');
         } else {
            sb.append(ch);
         }
      }

      return sb.toString();
   }
}
