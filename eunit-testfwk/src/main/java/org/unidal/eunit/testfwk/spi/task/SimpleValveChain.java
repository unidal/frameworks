package org.unidal.eunit.testfwk.spi.task;

import java.util.List;

import org.unidal.eunit.testfwk.spi.ICaseContext;

public class SimpleValveChain implements IValveChain {
   private List<IValve<? extends ICaseContext>> m_valves;

   private int m_index;

   public SimpleValveChain(List<IValve<? extends ICaseContext>> valves) {
      m_valves = valves;
   }

   public List<IValve<? extends ICaseContext>> getValves() {
      return m_valves;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void executeNext(ICaseContext ctx) throws Throwable {
      if (m_index < m_valves.size()) {
         IValve<ICaseContext> valve = (IValve<ICaseContext>) m_valves.get(m_index++);

         valve.execute(ctx, this);
      }
   }

   public void reset() {
      m_index = 0;
   }
}
