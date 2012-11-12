package org.unidal.eunit.helper;

import java.util.List;

public class Joiners {
   public static StringJoiner noDelimiter() {
      return new StringJoiner() {
         @Override
         protected void appendDelimiter(StringBuilder sb) {
            // do nothing
         }
      };
   }

   public static StringJoiner by(final char delimiter) {
      return new StringJoiner() {
         @Override
         protected void appendDelimiter(StringBuilder sb) {
            sb.append(delimiter);
         }
      };
   }

   public static StringJoiner by(final String delimiter) {
      return new StringJoiner() {
         @Override
         protected void appendDelimiter(StringBuilder sb) {
            sb.append(delimiter);
         }
      };
   }

   public static interface IBuilder<T> {
      public String asString(T item);
   }

   public static abstract class StringJoiner {
      private boolean m_prefixDelimiter;

      private boolean m_noEmptyItem;

      protected abstract void appendDelimiter(StringBuilder sb);

      public String join(String pre, List<String> list, String post) {
         if (list == null) {
            return null;
         }

         StringBuilder sb = new StringBuilder();

         if (m_prefixDelimiter) {
            appendDelimiter(sb);
            m_prefixDelimiter = false;
         }

         if (pre != null) {
            sb.append(pre);
            appendDelimiter(sb);
         }

         join(sb, list, null);

         if (post != null) {
            appendDelimiter(sb);
            sb.append(post);
         }

         return sb.toString();
      }

      public String join(List<String> list) {
         return this.<String> join(list, null);
      }

      public <T> String join(List<T> list, IBuilder<T> builder) {
         if (list == null) {
            return null;
         }

         StringBuilder sb = new StringBuilder();

         join(sb, list, builder);

         return sb.toString();
      }

      public String join(String... array) {
         if (array == null) {
            return null;
         }

         StringBuilder sb = new StringBuilder();
         boolean first = true;

         if (m_prefixDelimiter) {
            appendDelimiter(sb);
         }

         for (String item : array) {
            if (m_noEmptyItem && (item == null || item.length() == 0)) {
               continue;
            }

            if (first) {
               first = false;
            } else {
               appendDelimiter(sb);
            }

            sb.append(item);
         }

         return sb.toString();
      }

      public <T> void join(StringBuilder sb, List<T> list, IBuilder<T> builder) {
         boolean first = true;

         if (list != null) {
            for (T item : list) {
               String str;

               if (builder == null) {
                  str = String.valueOf(item);
               } else {
                  str = builder.asString(item);
               }

               if (m_noEmptyItem && (str == null || str.length() == 0)) {
                  continue;
               }

               if (first) {
                  first = false;

                  if (m_prefixDelimiter) {
                     appendDelimiter(sb);
                  }
               } else {
                  appendDelimiter(sb);
               }

               sb.append(str);
            }
         }
      }

      public StringJoiner prefixDelimiter() {
         m_prefixDelimiter = true;
         return this;
      }

      public StringJoiner noEmptyItem() {
         m_noEmptyItem = true;
         return this;
      }
   }
}
