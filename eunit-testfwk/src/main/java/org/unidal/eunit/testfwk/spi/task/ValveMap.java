package org.unidal.eunit.testfwk.spi.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.eunit.testfwk.spi.ICaseContext;

public class ValveMap {
   private Map<Priority, List<IValve<? extends ICaseContext>>> m_map = new HashMap<Priority, List<IValve<? extends ICaseContext>>>();

   private SimpleValveChain m_chain;

   public void addValve(Priority priority, IValve<? extends ICaseContext> valve) {
      addValve(priority, valve, true);
   }

   public void addValve(Priority priority, IValve<? extends ICaseContext> valve, boolean append) {
      List<IValve<? extends ICaseContext>> list = m_map.get(priority);

      if (list == null) {
         list = new ArrayList<IValve<? extends ICaseContext>>();
         m_map.put(priority, list);
      }

      if (append) {
         list.add(valve);
      } else {
         list.add(0, valve);
      }
   }

   public IValveChain getValveChain() {
      if (m_chain == null) {
         List<IValve<? extends ICaseContext>> valves = new ArrayList<IValve<? extends ICaseContext>>();

         for (Priority priority : Priority.values()) {
            List<IValve<? extends ICaseContext>> list = m_map.get(priority);

            if (list != null) {
               valves.addAll(list);
            }
         }

         m_chain = new SimpleValveChain(valves);
      } else {
         m_chain.reset();
      }

      return m_chain;
   }

   public void mergeFrom(ValveMap source) {
      if (!source.m_map.isEmpty()) {
         for (Map.Entry<Priority, List<IValve<? extends ICaseContext>>> e : source.m_map.entrySet()) {
            Priority priority = e.getKey();
            List<IValve<? extends ICaseContext>> list = m_map.get(priority);

            if (list == null) {
               list = new ArrayList<IValve<? extends ICaseContext>>();
               m_map.put(priority, list);
            }

            list.addAll(e.getValue());
         }
      }
   }

   @Override
   public String toString() {
      return m_map.toString();
   }
}