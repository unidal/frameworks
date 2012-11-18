package org.unidal.web.jsp.function;

import java.io.UnsupportedEncodingException;

import org.unidal.web.jsp.annotation.FunctionMeta;

public class CodecFunction {
	@FunctionMeta(description = "HTML encode", example = "${w:htmlEncode(str)}")
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
	
	@FunctionMeta(description = "URL decode", example = "${w:urlDecode(str)}")
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

	@FunctionMeta(description = "URL encode", example = "${w:urlEncode(str)}")
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
