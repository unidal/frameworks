package org.unidal.helper;

import java.util.HashMap;
import java.util.Map;

public class Matchers {
   public static StringMatcher forString() {
      return StringMatcher.CASE_SENSITIVE;
   }

   public static StringTrie forTrie() {
      return new StringTrie();
   }

   public enum StringMatcher {
      CASE_SENSITIVE(true),

      CASE_INSENSITIVE(false);

      private boolean m_caseSensitive;

      private StringMatcher(boolean caseSensitive) {
         m_caseSensitive = caseSensitive;
      }

      public StringMatcher caseSensitive(boolean caseSensitive) {
         if (caseSensitive) {
            return CASE_SENSITIVE;
         } else {
            return CASE_INSENSITIVE;
         }
      }

      public StringMatcher ignoreCase() {
         return CASE_INSENSITIVE;
      }

      public boolean matches(String source, int start, String part) {
         return matches(source, start, part, 0, part.length());
      }

      public boolean matches(String source, int start, String part, int count) {
         return matches(source, start, part, 0, count);
      }

      public boolean matches(String source, int start, String part, int partStart, int count) {
         if (source == null || part == null) {
            throw new IllegalArgumentException(String.format("Source(%s) or part(%s) can't be null!", source, part));
         }

         return source.regionMatches(m_caseSensitive, start, part, 0, count);
      }
   }

   public static class StringTrie {
      private Map<Integer, TrieHandler> m_handlers = new HashMap<Integer, Matchers.TrieHandler>();

      private byte[] m_flags = new byte[8192];

      public void addHandler(String part, TrieHandler handler, boolean prefixOrSuffix) {
         int key = hash(part, prefixOrSuffix);
         int size = m_flags.length;
         int index = key > 0 ? key : Integer.MAX_VALUE + key;

         m_handlers.put(key, handler);
         m_flags[index % size] = 1;
      }

      public boolean handle(String str, Object... arguments) throws Exception {
         int len = str == null ? 0 : str.length();
         int size = m_flags.length;
         int key = 0;
         TrieHandler last = null;
         int lastStart = 0;
         int lastEnd = len;
         boolean flag = true;
         int index;

         for (int i = 0; i < len; i++) {
            key = key * 31 + str.charAt(i);
            index = key > 0 ? key : Integer.MAX_VALUE + key;

            if (m_flags[index % size] > 0) {
               TrieHandler handler = m_handlers.get(key);

               if (handler != null) {
                  last = handler;
                  lastEnd = i + 1;
               } else if (last != null) {
                  break;
               }
            }
         }

         if (last == null) {
            key = 0;
            lastEnd = len;
            flag = false;

            for (int i = len - 1; i >= 0; i--) {
               key = key * 31 + str.charAt(i);
               index = key > 0 ? key : Integer.MAX_VALUE + key;

               if (m_flags[index % size] > 0) {
                  TrieHandler handler = m_handlers.get(key);

                  if (handler != null) {
                     last = handler;
                     lastStart = i;
                  } else if (last != null) {
                     break;
                  }
               }
            }
         }

         if (last != null) {
            last.handle(str, lastStart, lastEnd, flag, arguments);
            return true;
         }

         return false;
      }

      private int hash(String str, boolean flag) {
         int len = str == null ? 0 : str.length();
         int hash = 0;

         if (flag) {
            for (int i = 0; i < len; i++) {
               hash = hash * 31 + str.charAt(i);
            }
         } else {
            for (int i = len - 1; i >= 0; i--) {
               hash = hash * 31 + str.charAt(i);
            }
         }

         return hash;
      }
   }

   public static interface TrieHandler {
      public void handle(String str, int start, int end, boolean prefixOrSuffix, Object[] arguments) throws Exception;
   }
}
