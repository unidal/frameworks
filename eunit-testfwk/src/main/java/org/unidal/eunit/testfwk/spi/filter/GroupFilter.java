package org.unidal.eunit.testfwk.spi.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.eunit.model.entity.EunitMethod;

public class GroupFilter implements IGroupFilter {
   private Map<String, Boolean> m_map = new HashMap<String, Boolean>();

   private boolean m_withInclude = false;

   public GroupFilter(List<String> groups) {
      for (String group : groups) {
         if (group.startsWith("-")) {
            m_map.put(group.substring(1), Boolean.FALSE);
         } else if (group.startsWith("+")) {
            m_map.put(group.substring(1), Boolean.TRUE);
            m_withInclude = true;
         } else {
            m_map.put(group, Boolean.TRUE);
            m_withInclude = true;
         }
      }
   }

   public GroupFilter(String[] includeGroups, String[] excludeGroups) {
      if (includeGroups != null && includeGroups.length > 0) {
         for (String group : includeGroups) {
            m_map.put(group, Boolean.TRUE);
         }

         m_withInclude = true;
      }

      if (excludeGroups != null && excludeGroups.length > 0) {
         for (String group : excludeGroups) {
            m_map.put(group, Boolean.FALSE);
         }
      }
   }

   public void exclude(String group) {
      m_map.put(group, Boolean.FALSE);
   }

   public void include(String group) {
      m_map.put(group, Boolean.TRUE);
      m_withInclude = true;
   }

   public boolean matches(EunitMethod eunitMethod) {
      boolean include = false;

      for (String group : eunitMethod.getGroups()) {
         Boolean value = m_map.get(group);

         if (value != null) {
            if (value.booleanValue()) {
               include = true;
            } else {
               return false;
            }
         }
      }

      return include || !m_withInclude;
   }

   @Override
   public String toString() {
      return String.format("GroupFilter[%s]", m_map);
   }
}