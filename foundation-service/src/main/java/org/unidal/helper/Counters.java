package org.unidal.helper;

public class Counters {
   public static CharCounter forChar() {
      return CharCounter.CASE_SENSITIVE;
   }

   public static StringCounter forString() {
      return StringCounter.CASE_SENSITIVE;
   }

   public enum CharCounter {
      CASE_SENSITIVE(true),

      CASE_INSENSITIVE(false);

      private boolean m_caseSensitive;

      private CharCounter(boolean caseSensitive) {
         m_caseSensitive = caseSensitive;
      }

      public int count(CharSequence source, char c) {
         int count = 0;
         int len = source == null ? 0 : source.length();
         boolean caseSensitive = m_caseSensitive;
         char lc = caseSensitive ? c : Character.toLowerCase(c);

         for (int i = 0; i < len; i++) {
            char ch = source.charAt(i);

            if (ch == c) {
               count++;
            } else if (!caseSensitive && Character.toLowerCase(ch) == lc) {
               count++;
            }
         }

         return count;
      }

      public CharCounter ignoreCase() {
         return CASE_INSENSITIVE;
      }
   }

   public enum StringCounter {
      CASE_SENSITIVE(true),

      CASE_INSENSITIVE(false);

      private boolean m_caseSensitive;

      private StringCounter(boolean caseSensitive) {
         m_caseSensitive = caseSensitive;
      }

      public int count(String source, String str) {
         int count = 0;

         if (source != null && str != null) {
            String whole = m_caseSensitive ? source : source.toLowerCase();
            String part = m_caseSensitive ? str : str.toLowerCase();
            int len = str.length();
            int offset = 0;

            while (true) {
               int index = whole.indexOf(part, offset);

               if (index < 0) {
                  break;
               } else {
                  count++;
                  offset = index + len;
               }
            }
         }

         return count;
      }

      public StringCounter ignoreCase() {
         return CASE_INSENSITIVE;
      }
   }
}
