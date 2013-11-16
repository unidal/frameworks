package org.unidal.eunit.testfwk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.unidal.eunit.testfwk.spi.filter.GroupFilter;
import org.unidal.eunit.testfwk.spi.filter.IGroupFilter;
import org.unidal.eunit.testfwk.spi.filter.RunOption;
import org.unidal.helper.Files;
import org.unidal.helper.Splitters;

public enum EunitRuntimeConfig {
   INSTANCE;

   private static final String OPTIONS = "options";

   private static final String GROUPS = "groups";

   private static final String ARG_FILE = "argFile";

   private RunOption m_runOption = RunOption.TEST_CASES_ONLY;

   private IGroupFilter m_groupFilter;

   private EunitProperties m_properties = new EunitProperties();

   public IGroupFilter getGroupFilter() {
      return m_groupFilter;
   }

   public EunitProperties getProperties() {
      return m_properties;
   }

   public RunOption getRunOption() {
      return m_runOption;
   }

   public void initialize() {
      // -DargFile=
      String argFileValue = System.getProperty(ARG_FILE);

      if (argFileValue != null) {
         File argFile = new File(argFileValue);

         if (argFile.isFile()) {
            try {
               String content = Files.forIO().readFrom(argFile, "utf-8");

               m_properties.loadProperties(content);
            } catch (IOException e) {
               throw new RuntimeException(String.format("Unable to read arg file(%s)!", argFile));
            }
         } else {
            throw new RuntimeException(String.format("Arg file(%s) does not exist!", argFile));
         }
      }

      // -Dgroups="P1 P2 -P3 -P4"
      // -groups P1 P2 '-P3' '-P4'
      String groupsValue = System.getProperty(GROUPS);
      List<String> groups;

      if (groupsValue != null) {
         groups = Splitters.by(' ').noEmptyItem().split(groupsValue);
      } else {
         groups = m_properties.getProperties(GROUPS);
      }

      if (!groups.isEmpty()) {
         m_groupFilter = new GroupFilter(groups);
      }

      // -Doptions=all
      // -options all
      String options = System.getProperty(OPTIONS);

      if (options == null) {
         options = m_properties.getProperty(OPTIONS);
      }

      if (options != null) {
         if ("ignored".equals(options)) {
            m_runOption = RunOption.IGNORED_CASES_ONLY;
         } else if ("test".equals(options)) {
            m_runOption = RunOption.TEST_CASES_ONLY;
         } else if ("all".equals(options)) {
            m_runOption = RunOption.ALL_CASES;
         } else {
            System.err.println(String.format("Unknown run options(%s) specified, use 'test', 'ignored' or 'all'!", options));
         }
      }
   }

   public void setGroupFilter(IGroupFilter groupFilter) {
      m_groupFilter = groupFilter;
   }

   public void setRunOption(RunOption runOption) {
      m_runOption = runOption;
   }

   static class EunitProperties {
      private Map<String, List<String>> m_properties = new LinkedHashMap<String, List<String>>();

      public List<String> getProperties(String name) {
         List<String> values = m_properties.get(name);

         if (values == null) {
            return Collections.emptyList();
         } else {
            return values;
         }
      }

      public String getProperty(String name) {
         List<String> values = m_properties.get(name);

         if (values == null || values.isEmpty()) {
            return null;
         } else {
            return values.get(values.size() - 1); // last one
         }
      }

      public boolean hasProperty(String name) {
         return m_properties.containsKey(name);
      }

      public void loadProperties(String content) {
         int len = content.length();
         StringBuilder name = new StringBuilder();
         StringBuilder value = new StringBuilder();
         int part = 0;

         for (int i = 0; i < len; i++) {
            char ch = content.charAt(i);

            switch (ch) {
            case ' ':
            case '\t':
            case '\r':
            case '\n':
               if (part % 2 != 0) {
                  part++;

                  if (part == 4) {
                     setProperty(name.toString(), value.toString());

                     value.setLength(0);
                  }
               }

               break;
            case '#': // comments
               i++;

               while (i < len) {
                  char ch2 = content.charAt(i);

                  if (ch2 == '\n') {
                     break;
                  } else {
                     i++;
                  }
               }

               break;
            case '"':
            case '\'':
               boolean matched = false;

               if ((part == 2 || part == 4) && value.length() == 0) {
                  char ch2 = 0;

                  i++;

                  while (i < len) {
                     ch2 = content.charAt(i);

                     if (ch2 == ch) {
                        break;
                     } else if (ch2 == '\\') {
                        if (i + 1 < len) {
                           i++;
                           ch2 = content.charAt(i);
                        }
                     }

                     value.append(ch2);
                     i++;
                  }

                  matched = (ch2 == ch);
                  part = 3;
               }

               if (!matched) {
                  throw new RuntimeException(String.format("Quotes in properties(%s) are not matched!", content));
               }

               break;
            case '-':
               if (part % 4 == 0) {
                  name.setLength(0);
                  part = 1;
                  break;
               } else if (part == 2 && name.length() > 0) {
                  setProperty(name.toString(), null);
                  name.setLength(0);
                  part = 1;
                  break;
               }
            default:
               switch (part) {
               case 1:
                  name.append(ch);
                  break;
               case 2:
                  part++;
               case 3:
                  value.append(ch);
                  break;
               case 4:
                  part = 3;
                  value.append(ch);
                  break;
               }
            }
         }

         if (name.length() > 0) {
            setProperty(name.toString(), value.length() == 0 ? null : value.toString());
         }
      }

      public void reset() {
         m_properties.clear();
      }

      public void setProperty(String name, String value) {
         List<String> values = m_properties.get(name);

         if (values == null) {
            values = new ArrayList<String>();
            m_properties.put(name, values);
         }

         if (value != null) {
            values.add(value);
         }
      }

      @Override
      public String toString() {
         return m_properties.toString();
      }
   }
}
