package org.unidal.lookup.container;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;

class MyPlexusContainerContext implements Context {
   private Map<Object, Object> m_map = new HashMap<Object, Object>();

   private Object m_container;

   public MyPlexusContainerContext(PlexusContainer container) {
      m_container = container;
   }

   @Override
   public boolean contains(Object key) {
      return m_map.containsKey(key);
   }

   @Override
   public Object get(Object key) throws ContextException {
      if ("plexus".equals(key)) {
         return m_container;
      } else {
         return m_map.get(key);
      }
   }

   @Override
   public Map<Object, Object> getContextData() {
      return m_map;
   }

   @Override
   public void put(Object key, Object value) throws IllegalStateException {
      m_map.put(key, value);
   }
}