package com.site.helper;

import java.util.Arrays;
import java.util.Collection;

public class Joiners {
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

      protected abstract void appendDelimiter(StringBuilder sb);

      public String join(Collection<String> list) {
         return this.<String> join(list, null);
      }

      public <T> String join(Collection<T> list, IBuilder<T> builder) {
         if (list == null) {
            return null;
         }

         StringBuilder sb = new StringBuilder();

         join(sb, list, builder);

         return sb.toString();
      }

      public String join(String... array) {
         return join(Arrays.asList(array), null);
      }

      public <T> String join(IBuilder<T> builder, T... array) {
         return join(Arrays.asList(array), builder);
      }

      public <T> void join(StringBuilder sb, Collection<T> list, IBuilder<T> builder) {
         boolean first = true;

         if (list != null) {
            for (T item : list) {
               if (first) {
                  first = false;

                  if (m_prefixDelimiter) {
                     appendDelimiter(sb);
                  }
               } else {
                  appendDelimiter(sb);
               }

               if (builder == null) {
                  sb.append(item);
               } else {
                  sb.append(builder.asString(item));
               }
            }
         }
      }

      public StringJoiner prefixDelimiter() {
         m_prefixDelimiter = true;
         return this;
      }
   }
}
